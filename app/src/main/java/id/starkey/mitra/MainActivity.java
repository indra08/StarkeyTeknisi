package id.starkey.mitra;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import id.starkey.mitra.History.HistoryActivity;
import id.starkey.mitra.Home.FragmentHome;
import id.starkey.mitra.Kunci.OrderKunciActivity;
import id.starkey.mitra.Login.LoginActivity;
import id.starkey.mitra.PedomanMitra.PedomanMitraActivity;
import id.starkey.mitra.Service.IntervalService;
import id.starkey.mitra.Statistik.StatistikActivity;
import id.starkey.mitra.Stempel.OrderStempelActivity;
import id.starkey.mitra.TransaksiBahan.TransaksiBahanActivity;
import id.starkey.mitra.UbahPassword.UbahPasswordActivity;
import id.starkey.mitra.Utilities.GPSTracker;

import id.starkey.mitra.BuildConfig;
import id.starkey.mitra.R;
import id.starkey.mitra.Utilities.ItemValidation;
import id.starkey.mitra.Utilities.StatusMitra;

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
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MenuItem menu_on, menu_off;
    Menu menu;
    protected static String TAG_MENU;
    private NavigationView navigationView;
    Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
    Toolbar toolbar;
    private String userChoosenTask, tokennyaUser, sLat, sLng, sFirebaseToken, sRoleMitra;
    private TextView headerName, headerEmail, headerPhone;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private Context mContext;
    private GPSTracker gps;
    private double latFromClass = -7.0160395, lngFromClass = 110.4630368;
    //private ToggleButton mSwitchStatus;
    private SwitchButton switchButtonStatus;
    public static String sStatusAvail = "0";
    private GoogleApiClient googleApiClient;

    //interval
    //Handler h = new Handler();
    //int delay = 5*1000; //1 second=1000 milisecond, 5*1000=5seconds
    //Runnable runnable;

    Runnable runnablestatus = new Runnable() {
        @Override
        public void run() {
            runnablestatus = this;
            MyHandler.resumeMyHandler(runnablestatus);
            //Toast.makeText(MainActivity.this, "handler main Act", Toast.LENGTH_SHORT).show();
            updatePosisiMitra(sStatusAvail, sLat, sLng, sFirebaseToken);
        }
    };

    //to kill
    static MainActivity mainActivity;
    private boolean dialogActive = false;
    public static boolean isBatal = false;
    private String version = "", latestVersion = "", link = "";
    private AlertDialog builderVersion;
    private boolean updateRequired = false;
    private ItemValidation iv = new ItemValidation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu");

        mContext = this;

        MyHandler.getHandler();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
         */


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //tv versioning
        String versionName = BuildConfig.VERSION_NAME;
        TextView tvVersion = navigationView.findViewById(R.id.lblVersionMain);
        tvVersion.setText("Versi "+versionName);

        //backlight selected item nav bar no color
        navigationView.setItemBackground(transparentDrawable);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] {
                Color.BLACK,
                Color.RED,
                Color.GREEN,
                Color.BLUE
        };
        ColorStateList myList = new ColorStateList(states, colors);
        navigationView.setItemTextColor(myList);
        navigationView.setItemIconTintList(myList);

        //click navdraw header
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //Toast.makeText(MainActivity.this, "header clicked", Toast.LENGTH_SHORT).show();
            }
        });


        //init tv nav header
        headerName = headerview.findViewById(R.id.namaHeader);
        headerEmail = headerview.findViewById(R.id.emailHeader);
        headerPhone = headerview.findViewById(R.id.phoneHeader);

        //from gpstracker class
       /* if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            //Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            gps = new GPSTracker(mContext, MainActivity.this);

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                latFromClass = gps.getLatitude();
                lngFromClass = gps.getLongitude();

                // \n is for new line
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }*/

        //get detail from preference
        getPref();
        getFirebaseToken();
        getLocation();

        //sStatusAvail = "0";
        updatePosisiMitra(sStatusAvail, sLat, sLng, sFirebaseToken);
        displaySelectedScreen(R.id.nav_home);

        mainActivity = this;
        dialogActive = false;
        //isBatal = false;
    }

    public static MainActivity getInstance(){
        return mainActivity;
    }

    @Override
    protected void onRestart() {
        //Toast.makeText(mContext, "onRestart", Toast.LENGTH_SHORT).show();
        //MyHandler.restartMyHandler(runnablestatus);
        //MyHandler.resumeMyHandler(runnablestatus);
        startService(new Intent(MainActivity.this, IntervalService.class));
        super.onRestart();
    }

    @Override
    protected void onResume() {

        locationAccessChecker();

        if(isBatal){
            isBatal = false;
            sStatusAvail = "0";
            updatePosisiMitra(sStatusAvail, sLat, sLng, sFirebaseToken);
        }

        StatusMitra.status = 1;

        MyHandler.resumeMyHandler(runnablestatus);
        checkVersion();
        super.onResume();
    }

    private void checkVersion(){

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;
        //getSupportActionBar().setSubtitle(getResources().getString(R.string.app_name) + " v "+ version);
        //tvVersion.setText(getResources().getString(R.string.app_name) + " v "+ version);
        latestVersion = "";
        link = "";

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("flag_app", "mitra");

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST ,ConfigLink.getVersion, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if(builderVersion != null){
                            if(builderVersion.isShowing()) builderVersion.dismiss();
                        }

                        try {

                            String status = response.getString("status");
                            if(status.equals("success")){

                                JSONArray ja = response.getJSONArray("data");
                                if(ja.length() > 0){

                                    JSONObject jo = ja.getJSONObject(0);
                                    latestVersion = jo.getString("versi");
                                    link = jo.getString("link");
                                    updateRequired = (iv.parseNullInteger(jo.getString("status")) == 1) ? true : false;
                                    if(!version.trim().equals(latestVersion.trim()) && link.length() > 0){

                                        if(updateRequired){

                                            builderVersion = new AlertDialog.Builder(mContext)
                                                    .setIcon(R.mipmap.icon_starkey_mitra_curved)
                                                    .setTitle("Update")
                                                    .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon update ke versi terbaru.")
                                                    .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                            startActivity(browserIntent);
                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .show();
                                        }else{

                                            builderVersion = new AlertDialog.Builder(mContext)
                                                    .setIcon(R.mipmap.icon_starkey_mitra_curved)
                                                    .setTitle("Update")
                                                    .setMessage("Versi terbaru "+latestVersion+" telah tersedia, mohon update ke versi terbaru.")
                                                    .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {

                                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                                            startActivity(browserIntent);
                                                        }
                                                    })
                                                    .setNegativeButton("Update Nanti", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            //dialogInterface.dismiss();
                                                        }
                                                    }).show();
                                        }
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    public void locationAccessChecker() {
        try {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                buildAlertMessageNoGps();
            }
        }catch (Exception e){e.printStackTrace();}

    }

    private void buildAlertMessageNoGps() {
        if(!dialogActive){
            dialogActive = true;
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mohon Hidupkan Akses Lokasi (GPS) Anda.")
                    .setCancelable(false)
                    .setPositiveButton("Hidupkan", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

            alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    dialogActive = false;
                }
            });
        }

    }

    @Override
    protected void onPause() {
        //MyHandler.pauseMyHandler(runnablestatus);
        //stopService(new Intent(MainActivity.this, IntervalService.class));
        super.onPause();
    }

    @Override
    protected void onStop() {
        MyHandler.stopMyHandler();
        super.onStop();
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");
    }

    private void getPref(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String nama = custDetails.getString("namaUser", "");
        String email = custDetails.getString("emailUser", "");
        String hp = custDetails.getString("phoneUser", "");
        String tokenId = custDetails.getString("tokenIdUser", "");
        String role = custDetails.getString("roleUser", "");



        headerName.setText(nama);
        headerEmail.setText(email);
        headerPhone.setText(hp);
        tokennyaUser = tokenId;
        sRoleMitra = role;
        //Log.d("rolenyamitra", sRoleMitra);
        if (sRoleMitra.equals("1")){
            //Toast.makeText(MainActivity.this, "Akses API Kunci", Toast.LENGTH_SHORT).show();
            cekTrxOutstandingKunci();
        } else {
            //Toast.makeText(MainActivity.this, "Akses API Stempel", Toast.LENGTH_SHORT).show();
            cekTrxOutstandingStempel();
        }
    }

    private void cekTrxOutstandingKunci(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.trx_outstanding_mitra, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("outKunci", response.toString());
                        try {
                            JSONObject joData = response.getJSONObject("data");

                            if (joData.length() != 0){
                                //to act outstanding trx stempel
                                Intent toTrx = new Intent(MainActivity.this, OrderKunciActivity.class);
                                toTrx.putExtra("datatrxkunci", joData.toString());
                                startActivity(toTrx);


                            } else {

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
                error.printStackTrace();

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
                Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

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

    private void cekTrxOutstandingStempel(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.trx_outstanding_mitra, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("outStempel", response.toString());
                        try {
                            JSONObject joData = response.getJSONObject("data");

                            if (joData.length() != 0){
                                //to act outstanding trx stempel
                                Intent toTrx = new Intent(MainActivity.this, OrderStempelActivity.class);
                                toTrx.putExtra("datatrxstempel", joData.toString());
                                startActivity(toTrx);
                                /*
                                Intent toTrxoutStemp = new Intent(MainActivity.this, OutstandingStempelActivity.class);
                                toTrxoutStemp.putExtra("datatrxstempel", joData.toString());
                                startActivity(toTrxoutStemp);
                                 */


                            } else {

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
                error.printStackTrace();

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
                Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            askExitApps();
        }

    }

    //boolean doubleBackToExitPressedOnce = false;
    /*
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }

    }
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);
        switchButtonStatus = menu.findItem(R.id.show_status).getActionView().findViewById(R.id.toggleStatus);
        int putihColor = Color.parseColor("#FFFFFF");
        switchButtonStatus.setText("Online", "Offline");
        switchButtonStatus.setTextColor(putihColor);
        switchButtonStatus.setTintColor(putihColor);
        switchButtonStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    sStatusAvail = "1";
                    updatePosisiMitra(sStatusAvail, sLat, sLng, sFirebaseToken);
                    //MyHandler.resumeMyHandler(runnablestatus);
                    startService(new Intent(MainActivity.this, IntervalService.class));

                    /*
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runnable=this;
                            h.postDelayed(runnable, delay);
                            updatePosisiMitra("1", sLat, sLng);
                        }
                    }, delay);
                     */

                } else {
                    sStatusAvail = "0";
                    updatePosisiMitra(sStatusAvail, sLat, sLng, sFirebaseToken);
                    stopService(new Intent(MainActivity.this, IntervalService.class));

                    //MyHandler.stopMyHandler();

                    /*
                    h.removeCallbacks(runnable);
                    updatePosisiMitra("0", sLat, sLng);
                     */

                }
            }
        });

        /*
        menu_on = menu.findItem(R.id.menu_on);
        menu_on.setVisible(false);
        menu_off = menu.findItem(R.id.menu_off);
        menu_off.setVisible(true);
         */

        if(isBatal){
            isBatal = false;
            sStatusAvail = "0";
        }
        try {
            if(switchButtonStatus != null){

                if(sStatusAvail.equals("1")){
                    switchButtonStatus.setChecked(true);
                }else{
                    switchButtonStatus.setChecked(false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.menu_on) {
            //kondisi off

            Toast.makeText(mContext, "Status anda OFF", Toast.LENGTH_SHORT).show();
            menu_on = menu.findItem(R.id.menu_on);
            menu_on.setVisible(false);
            menu_off = menu.findItem(R.id.menu_off);
            menu_off.setVisible(true);
            h.removeCallbacks(runnable);
            updatePosisiMitra("0", sLat, sLng);



            return true;
        } else if (id == R.id.menu_off) {
            // kondisi on
            //selectOrder();


            Toast.makeText(mContext, "Status anda ON", Toast.LENGTH_SHORT).show();
            menu_on = menu.findItem(R.id.menu_on);
            menu_on.setVisible(true);
            menu_off = menu.findItem(R.id.menu_off);
            menu_off.setVisible(false);

            updatePosisiMitra("1", sLat, sLng);
            h.postDelayed(new Runnable() {
                public void run() {
                    //do something

                    runnable=this;
                    h.postDelayed(runnable, delay);
                    //Toast.makeText(MainActivity.this, "post interval", Toast.LENGTH_SHORT).show();
                    updatePosisiMitra("1", sLat, sLng);
                }
            }, delay);

            return true;
        }
         */


        return super.onOptionsItemSelected(item);
    }

    private void updatePosisiMitra(String avail, String latnya, String lngnya, String fbaseToken){
        //final ProgressDialog loading = new ProgressDialog(this);
        //loading.setMessage("Mohon tunggu...");
        //loading.setCancelable(false);
        //loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", latnya);
        params.put("long", lngnya);
        params.put("available", avail);
        params.put("firebase_token", fbaseToken);

        JSONObject jalparam = new JSONObject(params);
        Log.d("jalparamfb", jalparam.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.PATCH, ConfigLink.update_location_user, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("fullres", response.toString());
                        try {
                            //Process os success response
                            //loading.dismiss();
                            String konmsg = response.getString("status");
                            if (konmsg.equals("success")){
                                String hasil = response.getString("message");
                                Log.d("hasilupdate", hasil);
                                //Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                            } else {
                                String hasilelse = response.getString("message");
                                Log.d("elseupdate", hasilelse);
                                //Toast.makeText(mContext, hasilelse, Toast.LENGTH_SHORT).show();
                                infoDeviceNotMatch(hasilelse);
                            }

                            //Toast.makeText(MainActivity.this, hasil, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();

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
                Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

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

    private void updatePosisiMitraForLogout(String avail, String latnya, String lngnya, String fbaseToken){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("lat", latnya);
        params.put("long", lngnya);
        params.put("available", avail);
        params.put("firebase_token", fbaseToken);

        JSONObject jalparam = new JSONObject(params);
        Log.d("jalparamfb", jalparam.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.PATCH, ConfigLink.update_location_user, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("fullres", response.toString());
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konmsg = response.getString("status");
                            if (konmsg.equals("success")){
                                String hasil = response.getString("message");
                                Log.d("hasilupdate", hasil);
                                //Toast.makeText(mContext, hasil, Toast.LENGTH_SHORT).show();
                            } else {
                                String hasilelse = response.getString("message");
                                Log.d("elseupdate", hasilelse);
                                //Toast.makeText(mContext, hasilelse, Toast.LENGTH_SHORT).show();
                                infoDeviceNotMatch(hasilelse);
                            }
                            finish();
                            //Toast.makeText(MainActivity.this, hasil, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                //VolleyLog.e("Err Volley: ", error.getMessage());
                error.printStackTrace();

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
                Toast.makeText(MainActivity.this,message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
                return params;
            }
        };

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

    private void infoDeviceNotMatch(String titleMsg){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(titleMsg)
                .setCancelable(false)
                .setPositiveButton("Oke",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                clearAttributeUser();
                                sStatusAvail = "0";
                                updatePosisiMitra(sStatusAvail, sLat, sLng, sFirebaseToken);
                                Intent aw = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(aw);
                                finish();
                                //turnGPSOn();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void selectOrder() {
        final CharSequence[] items = { "Kunci", "Laundry","Stempel", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Pilih Demo Order");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result= PermissionChecker.checkPermission(IsiDetailKunciActivity.this, CAMERA_SERVICE, 1,1);
                //boolean bSDisAvalaible = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
                // boolean result=Utility.checkPermission(EmotionActivity.this);
                //boolean result = ActivityCompat.checkSelfPermission(IsiDetailKunciActivity.this,CAMERA_SERVICE);

                if (items[item].equals("Kunci")) {
                    userChoosenTask="Kunci";
                    orderKunci();
                } else if (items[item].equals("Laundry")) {
                    userChoosenTask="Laundry";
                    Toast.makeText(MainActivity.this, "Under Construction", Toast.LENGTH_SHORT).show();
                } else if (items[item].equals("Stempel")) {
                    userChoosenTask="Stempel";
                    orderStempel();
                } else if (items[item].equals("Cancel")){
                    dialog.dismiss();
                }


            }
        });
        builder.show();
    }

    Dialog dialog;
    private void orderKunci(){
        //Toast.makeText(this, "void hidup", Toast.LENGTH_SHORT).show();
        dialog = new Dialog(MainActivity.this,R.style.Theme_AppCompat_Light_NoActionBar_FullScreen);
        dialog.setTitle("Update Data");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.notifikasi_order);
        dialog.show();

        final Button bTerima = dialog.findViewById(R.id.btnTerimaOrder);

        new CountDownTimer(1*15000, 100){
            public void onTick(long millisUntilFinished) {
                String jdl = "Terima Order"+" "+" "+" "+" "+" "+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished));
                //bTerima.setText("Terima Order"+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished)));
                bTerima.setText(jdl);
            }

            public void onFinish() {
                //bTerima.setText("done!");
                //Toast.makeText(MainActivity.this, "Order masuk terlewatkan", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }.start();

        bTerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, "Action Terima", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, OrderKunciActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
    }

    private void orderStempel(){
        //Toast.makeText(this, "void hidup", Toast.LENGTH_SHORT).show();
        dialog = new Dialog(MainActivity.this,R.style.Theme_AppCompat_Light_NoActionBar_FullScreen);
        dialog.setTitle("Update Data");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.notifikasi_order_stempel);
        dialog.show();

        final Button bTerimaStempel = dialog.findViewById(R.id.btnTerimaOrderStempel);

        new CountDownTimer(1*15000, 100){
            public void onTick(long millisUntilFinished) {
                String jdl = "Terima Order"+" "+" "+" "+" "+" "+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished));
                //bTerima.setText("Terima Order"+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished)));
                bTerimaStempel.setText(jdl);
            }

            public void onFinish() {
                //bTerima.setText("done!");
                //Toast.makeText(MainActivity.this, "Order masuk terlewatkan", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }.start();

        bTerimaStempel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderStempelActivity.class);
                startActivity(intent);
                dialog.dismiss();
                finish();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);

        return true;
    }

    private void displaySelectedScreen(int id){
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (id){
            case R.id.nav_home:
                TAG_MENU = "TAG_HOME";
                fragment = new FragmentHome();
                break;
            case R.id.nav_history:
                TAG_MENU = "TAG_HISTORY";
                //Toast.makeText(this, "History", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                //fragment = new category();
                //Intent i = new Intent(this, CategoryAllActivity.class);
                //startActivity(i);
                break;
            case R.id.nav_statistik:
                TAG_MENU = "TAG_STATISTIK";
                Intent intenBan = new Intent(MainActivity.this, StatistikActivity.class);
                startActivity(intenBan);
                //Toast.makeText(this, "Statistik", Toast.LENGTH_SHORT).show();
                //fragment = new help();
                break;

            case R.id.nav_trans:
                TAG_MENU = "TAG_TRANS";
                Intent intenSyarat = new Intent(MainActivity.this, TransaksiBahanActivity.class);
                startActivity(intenSyarat);
                //Toast.makeText(this, "Transaksi Bahan", Toast.LENGTH_SHORT).show();
                //fragment = new help();
                break;

            case R.id.nav_ubahpass:
                TAG_MENU = "TAG_UBAHPASS";
                Intent intentUbahpass = new Intent(MainActivity.this, UbahPasswordActivity.class);
                startActivity(intentUbahpass);
                break;

            case R.id.nav_pedoman:
                TAG_MENU = "TAG_PEDOMAN";
                Intent intenPenga = new Intent(MainActivity.this, PedomanMitraActivity.class);
                startActivity(intenPenga);
                //Toast.makeText(this, "Pedoman Mitra", Toast.LENGTH_SHORT).show();
                //fragment = new help();
                break;

            case R.id.nav_logout:
                TAG_MENU = "TAG_LOGOUT";
                //Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                askLogout();
                break;
        }


        if (fragment != null){
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment, TAG_MENU);
            fragmentTransaction.commit();
            //fragmentTransaction.commitAllowingStateLoss();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void askLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan Logout?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                clearAttributeUser();
                                sStatusAvail = "0";
                                updatePosisiMitra(sStatusAvail, sLat, sLng, sFirebaseToken);
                                Intent aw = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(aw);
                                finish();
                                //turnGPSOn();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void askExitApps() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan keluar aplikasi?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                sStatusAvail = "0";
                                updatePosisiMitraForLogout(sStatusAvail, sLat, sLng, sFirebaseToken);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void clearAttributeUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigLink.loginPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void getLocation() {

        //new check gps google
        final LocationManager manager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            //Toast.makeText(MainActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
            //finish();
        }

        // Todo Location Already on  ... end

        if(!hasGPSDevice(MainActivity.this)){
            Toast.makeText(MainActivity.this,"Gps not Supported",Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(MainActivity.this)) {
            Log.e("keshav","Gps already enabled");
            //Toast.makeText(MainActivity.this,"Gps not enabled",Toast.LENGTH_SHORT).show();
            enableLoc();
        }else{
            Log.e("keshav","Gps already enabled");
            //Toast.makeText(MainActivity.this,"Gps already enabled",Toast.LENGTH_SHORT).show();
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                //map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                sLat = String.valueOf(latti);
                sLng = String.valueOf(longi);

                //LatLng myPos = new LatLng(latti, longi);
                saveLocationGps(sLat, sLng);
            } else {
                Location locationNet = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationNet != null){
                    double lattiNet = locationNet.getLatitude();
                    double longiNet = locationNet.getLongitude();
                    sLat = String.valueOf(lattiNet);
                    sLng = String.valueOf(longiNet);
                    //LatLng myPos = new LatLng(latti, longi);
                    saveLocationNetwork(sLat, sLng);
                } else {
                    double lattiNetElse = latFromClass;
                    double longiNetElse = lngFromClass;
                    sLat = String.valueOf(lattiNetElse);
                    sLng = String.valueOf(longiNetElse);
                    saveLocationNetwork(sLat, sLng);
                }

            }
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
            googleApiClient = new GoogleApiClient.Builder(MainActivity.this)
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
                                status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
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
                displaySelectedScreen(R.id.nav_home);
            }
        }
        if (resultCode == Activity.RESULT_CANCELED){
            Toast.makeText(mContext, "Anda harus mengaktifkan GPS anda untuk menjalankan aplikasi ini", Toast.LENGTH_SHORT).show();
            //finish();
        }

    }

    private void saveLocationGps(String lat, String lng){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.locationbyGps, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latGps", lat);
        editor.putString("lngGps", lng);
        editor.commit();
    }

    private void saveLocationNetwork(String lat, String lng){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.locationbyNetwork, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latNet", lat);
        editor.putString("lngNet", lng);
        editor.commit();
    }
}
