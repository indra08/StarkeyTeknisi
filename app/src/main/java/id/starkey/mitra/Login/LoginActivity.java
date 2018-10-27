package id.starkey.mitra.Login;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import id.starkey.mitra.BuildConfig;
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.Firebase.MyFirebaseInstanceIdService;
import id.starkey.mitra.Firebase.SharedPrefManager;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;
import id.starkey.mitra.Utilities.GPSTracker;
import id.starkey.mitra.Utilities.RuntimePermissionsActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LoginActivity extends RuntimePermissionsActivity implements View.OnClickListener {

    private EditText etUsername, etPass;
    private Button bLogin;
    private TextView tampungToken;
    private BroadcastReceiver broadcastReceiver;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    private Context mContext;
    private GPSTracker gps;
    private static final int REQUEST_PERMISSIONS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProgressDialog loadToken = new ProgressDialog(this);
        loadToken.setMessage("Otentifikasi...");
        loadToken.setCancelable(false);
        loadToken.show();

        mContext = this;

        //init textview
        tampungToken = findViewById(R.id.tvTampungTokenFb);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                tampungToken.setText(SharedPrefManager.getInstance(LoginActivity.this).getToken());
                loadToken.dismiss();
            }
        };
        if (SharedPrefManager.getInstance(this).getToken() != null) {
            tampungToken.setText(SharedPrefManager.getInstance(LoginActivity.this).getToken());
            loadToken.dismiss();
            Log.d("JALTOKENBRO",SharedPrefManager.getInstance(this).getToken());
        }

        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIdService.TOKEN_BROADCAST));

        if (ContextCompat.checkSelfPermission(
                mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                mContext, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                mContext, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED) {

            LoginActivity.super.requestAppPermissions(new
                            String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WAKE_LOCK}, R.string
                            .runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }

        final LocationManager manager = (LocationManager) LoginActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(LoginActivity.this)) {
            //Toast.makeText(MainActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            //finish();
        }

        if(!hasGPSDevice(LoginActivity.this)){
            Toast.makeText(LoginActivity.this,"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(LoginActivity.this)) {
            Log.e("keshav","Gps already enabled");
            //Toast.makeText(MainActivity.this,"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Log.e("keshav","Gps already enabled");
            //Toast.makeText(MainActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }

        //init edit text
        etUsername = findViewById(R.id.edtUsername);
        etPass = findViewById(R.id.edtPassword);

        //init btn
        bLogin = findViewById(R.id.btnLogin);
        bLogin.setOnClickListener(this);

        getPref();
        prefNotNull();
        //checkGPS();
        cekManufactureXiaomi();

    }

    private void getPref(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String userEmail = custDetails.getString("emailUser", "");
        etUsername.setText(userEmail);
    }

    private void cekManufactureXiaomi(){
        /*
        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(Build.MANUFACTURER)){
            Intent intent1 = new Intent();
            intent1.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent1);
        }
         */

        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if  (list.size() > 0) {
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }

    }

    private void prefNotNull(){
        String cek1 = etUsername.getText().toString().trim();

        if (cek1.length() > 0){
            //Intent i = new Intent(LoginActivity.this, WelcomeBackActivity.class);
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error","Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LoginActivity.this, REQUEST_LOCATION);
                                //finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOCATION) {
                //Toast.makeText(mContext, "user Oke location", Toast.LENGTH_SHORT).show();
            }
        }
        if (resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(LoginActivity.this, "Anda harus mengaktifkan GPS", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the

                    // contacts-related task you need to do.

                    /*gps = new GPSTracker(mContext, LoginActivity.this);

                    // Check if GPS enabled
                    if (gps.canGetLocation()) {

                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();

                        // \n is for new line
                        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                    } else {
                        // Can't get location.
                        // GPS or network is not enabled.
                        // Ask user to enable GPS/network in settings.
                        gps.showSettingsAlert();
                    }*/

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(mContext, "You need to grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void checkGPS(){
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            //Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }else{
            showGPSDisabledAlertToUser();
        }
    }

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Untuk melanjutkan silahkan aktifkan GPS")
                .setCancelable(false)
                .setPositiveButton("Aktifkan",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                                //turnGPSOn();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onClick(View v) {
        if (v == bLogin){

            cekLogin();

            /*
            Intent intent = new Intent(LoginActivity.this, WelcomeBackActivity.class);
            startActivity(intent);
            finish();
             */

        }
    }

    private void cekLogin(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        String user = etUsername.getText().toString();
        String pass = etPass.getText().toString();
        final String tokenfirebase = tampungToken.getText().toString();

        //get versionname x.x.xx
        String versionName = BuildConfig.VERSION_NAME;

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user", user);
        params.put("password", pass);
        params.put("firebase_token", tokenfirebase);
        params.put("v_app", versionName);

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.login, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                //String cobatoken = response.getString("token");

                                JSONObject dataJO = new JSONObject();
                                dataJO = response.getJSONObject("data");
                                String id = dataJO.getString("id");
                                String nama = dataJO.getString("name");
                                String phone = dataJO.getString("phone");
                                String email = dataJO.getString("email");
                                String token = dataJO.getString("token");
                                String role = dataJO.getString("role");

                                saveAttUser(id, nama, phone, email, token, role);
                                saveFirebaseToken(tokenfirebase);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                                //Toast.makeText(LoginActivity.this, "Login sukses dan save pref", Toast.LENGTH_SHORT).show();


                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                //error.printStackTrace();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ParseError) {
                    message = "Parsing data Error";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut";
                }
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        });

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void saveAttUser(String id, String nama, String phone, String email, String tokenId, String role){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idUser", id);
        editor.putString("namaUser", nama);
        editor.putString("phoneUser", phone);
        editor.putString("emailUser", email);
        editor.putString("tokenIdUser", tokenId);
        editor.putString("roleUser", role);
        editor.commit();
    }

    private void saveFirebaseToken(String firebaseToken){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firebaseUser", firebaseToken);
        editor.commit();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }
}
