package id.starkey.mitra.Stempel;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.MyHandler;
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;
import id.starkey.mitra.Stempel.PilihProdusenStemp.PilihProdusenStempelActivity;
import id.starkey.mitra.Stempel.RincianTrxStempel.RincianTrxStempelActivity;
import id.starkey.mitra.Utilities.NumberTextWatcherForThousand;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderStempelActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap map;
    LocationManager locationManager;
    private Button bTerima, bHijau, bMerahTidak;
    private GoogleApiClient googleApiClient;
    static final int REQUEST_LOCATION = 1;
    private TextView tvDP, tvDetail;
    private TextView tNamaCust, tAlamatCust;

    private String sAlamatCus, sNamaCus, sIdTrxStemp, sNoHpUser, sEstimasiHarga, tokennyaMitra, sFirebaseToken;
    private String sLayananStemp, sTypeStempel, sUkuranStemp, sQtyStemp, sKetStemp;
    private String sLatUser, sLngUser, sIdProdusenStempel, sLatitudeProdusenStempel, sLongitudeProdusenStempel;
    private double latCus, lngCus;
    private ImageView iTelpon, iSms, iCancel, iNavStempel, iNavProdusenStempel;
    private LinearLayout linearLayoutStatus;
    private String sLatTrackMitra, sLngTrackMitra;
    private double latMitra, lngMitra;
    private String userChoosenTask, sDataAllTrxStempel, sJenisTransaksi, sStatusCode;
    private double latCustMap, lngCustMap, latProdusenMap, lngProdusenMap;
    private String sRincianTotalAwal, sRincianGrandTotal, sRincianBiayaLain, sRincianTips, sRincianDp;

    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    Runnable runnablestatus2 = new Runnable() {
        @Override
        public void run() {
            runnablestatus2 = this;
            MyHandler.resumeMyHandler(runnablestatus2);
            //Toast.makeText(MainActivity.this, "handler main Act", Toast.LENGTH_SHORT).show();
            updatePosisiMitra("2", sLatTrackMitra, sLngTrackMitra, sFirebaseToken);
        }
    };

    //to kill
    static OrderStempelActivity orderStempelActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_stempel);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        orderStempelActivity = this;

        MyHandler.getHandler();

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarOrderStempel);
        setSupportActionBar(toolbar);
        setTitle("Order Stempel");

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        //init linearlayout
        linearLayoutStatus = findViewById(R.id.layoutStatusProdusen);

        googleApiClient = new GoogleApiClient.Builder(OrderStempelActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMap();
        //Toast.makeText(this, "Klik detail untuk melihat detail\nAtau Klik DP untuk masukkan nilai DP", Toast.LENGTH_SHORT).show();

        //get param from notif act
        Bundle bundle = getIntent().getExtras();
        sDataAllTrxStempel = bundle.getString("datatrxstempel"); //jsonobject
        //Log.d("alltrx", sDataAllTrxStempel);
        sJenisTransaksi = bundle.getString("jenisTrxStempel");

        try {
            JSONObject joDataStempel = new JSONObject(sDataAllTrxStempel);
            //Log.d("jostempel", joDataStempel.toString());
            sAlamatCus = joDataStempel.getString("user_location");
            sNamaCus = joDataStempel.getString("nama_user");
            sIdTrxStemp = joDataStempel.getString("id");
            sNoHpUser = joDataStempel.getString("nohp_user");
            sEstimasiHarga = joDataStempel.getString("total_awal");
            sStatusCode = joDataStempel.getString("status_code");

            //params for dialog detail
            sLayananStemp = sJenisTransaksi;
            sTypeStempel = joDataStempel.getString("type_stempel");
            sUkuranStemp = joDataStempel.getString("ukuran");
            sQtyStemp = joDataStempel.getString("qty");
            sKetStemp = joDataStempel.getString("keterangan");
            sLatUser = joDataStempel.getString("user_lat");
            sLngUser = joDataStempel.getString("user_long");
            latCus = Double.parseDouble(sLatUser);
            lngCus = Double.parseDouble(sLngUser);

        } catch (JSONException ex){

        }


        /*
        sAlamatCus = bundle.getString("alamatUser");
        sNamaCus = bundle.getString("namaUser");
        sIdTrxStemp = bundle.getString("idtrx");
        sNoHpUser = bundle.getString("noHpUser");
        sEstimasiHarga = bundle.getString("estimasiHargaStemp");
         */



        //params for dialog detail
        /*
        sLayananStemp = bundle.getString("jenisTrx");
        sTypeStempel = bundle.getString("typeStempel");
        sUkuranStemp = bundle.getString("ukuranStempel");
        sQtyStemp = bundle.getString("qty");
        sKetStemp = bundle.getString("ketStemp");
        sLatUser = bundle.getString("latUser");
        sLngUser = bundle.getString("lngUser");
        latCus = Double.parseDouble(sLatUser);
        lngCus = Double.parseDouble(sLngUser);
         */


        //getlocationmitra
        getLocation();

        //init btn
        bTerima = findViewById(R.id.btnTerimaStempel);
        bTerima.setEnabled(false);
        bTerima.setOnClickListener(this);
        bHijau = findViewById(R.id.btnAdaProdusen);
        bHijau.setOnClickListener(this);
        bMerahTidak = findViewById(R.id.btnTidakAdaProdusen);
        bMerahTidak.setOnClickListener(this);

        //cek status code for outstanding trx
        if (sStatusCode.equals("1")){
            bTerima.setEnabled(true);
            bTerima.setBackgroundResource(R.drawable.background_button_kotak);
        } else if (sStatusCode.equals("2")){
            bTerima.setText("Menuju Lokasi Customer");
        } else if (sStatusCode.equals("3")){
            bTerima.setText("Menuju Produsen");
        } else if (sStatusCode.equals("4")){
            linearLayoutStatus.setVisibility(View.VISIBLE);
            bTerima.setVisibility(View.INVISIBLE);
        } else if (sStatusCode.equals("5")){
            linearLayoutStatus.setVisibility(View.VISIBLE);
            bTerima.setVisibility(View.INVISIBLE);
            bHijau.setText("Antar");
        } else if (sStatusCode.equals("6")){
            linearLayoutStatus.setVisibility(View.VISIBLE);
            bTerima.setVisibility(View.INVISIBLE);
            bHijau.setText("Sampai");
            bMerahTidak.setVisibility(View.GONE);
        } else if (sStatusCode.equals("7")){
            linearLayoutStatus.setVisibility(View.VISIBLE);
            bTerima.setVisibility(View.INVISIBLE);
            bHijau.setText("Selesai");
            bMerahTidak.setVisibility(View.GONE);
        }

        //init tv
        tvDP = findViewById(R.id.DP);
        tvDP.setOnClickListener(this);
        tvDP.setEnabled(false);
        tvDetail = findViewById(R.id.detailTrxStemp);
        tvDetail.setOnClickListener(this);

        tNamaCust = findViewById(R.id.tvNamaCusStempel);
        tNamaCust.setText(sNamaCus);
        tAlamatCust = findViewById(R.id.tvAlamatCustStemp);
        tAlamatCust.setText(sAlamatCus);

        //init iv
        iTelpon = findViewById(R.id.imgTelpon);
        iTelpon.setOnClickListener(this);
        iSms = findViewById(R.id.ivSmsStemp);
        iSms.setOnClickListener(this);
        iCancel = findViewById(R.id.ivCancelMitraStempel);
        iCancel.setOnClickListener(this);
        iCancel.setEnabled(false);
        iNavStempel = findViewById(R.id.imageViewNavDestStempel);
        iNavStempel.setOnClickListener(this);
        iNavProdusenStempel = findViewById(R.id.imageViewNavDestProdusen);
        iNavProdusenStempel.setOnClickListener(this);
        iNavProdusenStempel.setEnabled(false);

        //get id from pref
        getPref();
        getFirebaseToken();

        telpUserConfirmation();
    }

    public static OrderStempelActivity getInstance(){
        return orderStempelActivity;
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");
        //Log.d("jalfbtoken", sFirebaseToken);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyHandler.resumeMyHandler(runnablestatus2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyHandler.stopMyHandler();
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    private void initializeMap() {
        if (map == null) {
            MapFragment mapFragment = (MapFragment)getFragmentManager()
                    .findFragmentById(R.id.mapOrderStempel);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            // check if map is created successfully or not
            if (null == map) {
                //Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
            return;
        }

        getLocationStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocationStart();
                break;
            case 2:
                getLocationStart();
                break;
        }
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

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.PATCH, ConfigLink.update_location_user, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("ikiopo", response.toString());

                        //String logrespon = response.toString();
                        //Log.d("jalrespon", logrespon);
                        try {
                            //Process os success response
                            //loading.dismiss();
                            String hasil = response.getString("message");

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
                Toast.makeText(OrderStempelActivity.this,message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
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

    private void getLocationStart() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            //LatLng from = new LatLng(-7.0049918, 110.4551927);
            //map.addMarker(new MarkerOptions().position(from).title("Pengguna").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker)));
            //map.moveCamera(CameraUpdateFactory.newLatLng(from));

            /*
            LatLng sydney = new LatLng(-7.01629, 110.4631213);
            map.addMarker(new MarkerOptions().position(sydney).title("Kurnia Stempel").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker_biru)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
             */

            //detail customer
            LatLng custLocation = new LatLng(latCus, lngCus);
            map.addMarker(new MarkerOptions().position(custLocation).title("Customer").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(custLocation, 17));
            Log.d("latE", String.valueOf(latCus));
            Log.d("lngE", String.valueOf(lngCus));

            //set coordinate in variable from customer
            latCustMap = latCus;
            lngCustMap = lngCus;
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                //map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                latMitra = latti;
                lngMitra = longi;

                sLatTrackMitra = String.valueOf(latMitra);
                sLngTrackMitra = String.valueOf(lngMitra);
                //LatLng myPos = new LatLng(latti, longi);

                //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                //Marker marker = map.addMarker(markerOptions);
                //markerOptions.position(myPos);
                //marker.setPosition(myPos);

                /*
                MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng());
                Marker marker = map.addMarker(markerOptions);
                marker.setPosition(place.getLatLng());
                 */


                /*
                mPositionMarker = map.addMarker(new MarkerOptions()
                        .flat(true)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.ic_motor_teknisi))
                        .anchor(0.5f, 0.5f)
                        .position(
                                new LatLng(location.getLatitude(), location
                                        .getLongitude())));
                 */



                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.5f, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        latMitra = currentLocation.latitude;
                        lngMitra = currentLocation.longitude;

                        sLatTrackMitra = String.valueOf(latMitra);
                        sLngTrackMitra = String.valueOf(lngMitra);

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
            } else {

                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                double lattiNet = locationNet.getLatitude();
                double longiNet = locationNet.getLongitude();

                sLatTrackMitra = String.valueOf(lattiNet);
                sLngTrackMitra = String.valueOf(longiNet);

                //LatLng myPos = new LatLng(latti, longi);

            }
        }
    }

    private void telpUserConfirmation(){

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.telp_confirm_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                iCancel.setEnabled(true);
                                bTerima.setEnabled(true);
                                bTerima.setBackgroundResource(R.drawable.background_button_kotak);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
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
    public void onClick(View v) {
        if (v == iCancel){
            askDeclined();
        }

        if (v == bMerahTidak){
            selectNextActionStempel();
            /*
            if (bHijau.getText().equals("Ada Produsen")){
                selectNextActionStempel();
            } else { // equals "antar"
                selectNextActionStempel();
            }
             */
        }
        if (v == tvDetail){
            dialogDetailStemp();
        }

        if (v == tvDP){
            dialogDp();
        }
        if (v == iTelpon){
            Uri callCust = Uri.parse("tel:"+ sNoHpUser);
            Intent call = new Intent(Intent.ACTION_DIAL, callCust);
            startActivity(call);
        }
        if (v == iSms){
            Uri smsCust = Uri.parse("sms:" + sNoHpUser);
            Intent sms = new Intent(Intent.ACTION_VIEW, smsCust);
            startActivity(sms);
        }
        if (v == bTerima){
            if (bTerima.getText().equals("Terima")){
                updateStatusTransaksiKunci2();
            } else if (bTerima.getText().equals("Menuju Lokasi Customer")){
                updateStatusTransaksiKunci3();
            } else if (bTerima.getText().equals("Menuju Produsen")){
                //Toast.makeText(this, "List daftar produsen", Toast.LENGTH_SHORT).show();
                Intent toPilihProdusen = new Intent(OrderStempelActivity.this, PilihProdusenStempelActivity.class);
                startActivityForResult(toPilihProdusen, 123);

                /*
                Intent jumpJenis = new Intent(IsiDetailKunciActivity.this, PilihKunciActivity.class);
                startActivityForResult(jumpJenis, 111);
                 */
            }
        }
        if (v == bHijau){
            if (bHijau.getText().equals("Ada Produsen")){
                updateStatusTransaksiKunci5();
            } else if (bHijau.getText().equals("Antar")){
                updateStatusTransaksiKunci6();
            } else if (bHijau.getText().equals("Sampai")){
                updateStatusTransaksiKunci7();
            } else if (bHijau.getText().equals("Selesai")){
                //finishTransactionStempel();
                getDetailForRincian();
            }
        }
        if (v == iNavStempel){
            Intent jumpMaps = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps?saddr=My+Location&daddr="+latCustMap+", "+lngCustMap));
            startActivity(jumpMaps);
        }
        if (v == iNavProdusenStempel) {
            Intent jumpMaps = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps?saddr=My+Location&daddr="+sLatitudeProdusenStempel+", "+sLongitudeProdusenStempel));
            startActivity(jumpMaps);
        }
    }

    private void getDetailForRincian(){

        //method get detail
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.detail_trx_stempel+sIdTrxStemp, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("getdetailforrincian", response.toString());
                        try {
                            //Process os success response
                            JSONObject joData = response.getJSONObject("data");
                            String biayalain = joData.getString("biaya_lain");
                            String tips = joData.getString("tips");
                            String dp = joData.getString("dp");
                            String totalawal = joData.getString("total_awal");
                            String totalakhir = joData.getString("total_akhir");
                            //String biaya = joData.getString("harga_stempel");
                            //String jasa = joData.getString("biaya_layanan");
                            String biayadasar = joData.getString("biaya_dasar");
                            String tambahanbiayadasar = joData.getString("tambahan_biaya_antar");
                            int bdasar = Integer.parseInt(biayadasar);
                            int btambahan = Integer.parseInt(tambahanbiayadasar);
                            int totJasa = bdasar + btambahan;
                            String fixJasa = String.valueOf(totJasa);

                            //string for param to rincian trx stempel
                            sRincianTotalAwal = totalawal;
                            sRincianGrandTotal = totalakhir;
                            sRincianBiayaLain = biayalain;
                            sRincianTips = tips;
                            sRincianDp = dp;

                            Intent toRincianStemp = new Intent(OrderStempelActivity.this, RincianTrxStempelActivity.class);
                            toRincianStemp.putExtra("idtransaksistempel", sIdTrxStemp);
                            toRincianStemp.putExtra("tipestempel", sTypeStempel);
                            toRincianStemp.putExtra("ukuranstempel", sUkuranStemp);
                            toRincianStemp.putExtra("qtystempel", sQtyStemp );
                            //toRincianStemp.putExtra("rincianbiaya", biaya);
                            //toRincianStemp.putExtra("rincianjasa", jasa);
                            toRincianStemp.putExtra("rincianjasa", fixJasa);
                            toRincianStemp.putExtra("rinciantotalawal", sRincianTotalAwal);
                            toRincianStemp.putExtra("rinciangrandtotal", sRincianGrandTotal);
                            toRincianStemp.putExtra("rincianbiayalain", sRincianBiayaLain);
                            toRincianStemp.putExtra("rinciantips", sRincianTips);
                            toRincianStemp.putExtra("rinciandp", sRincianDp);
                            startActivity(toRincianStemp);

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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);

    }

    private void selectNextActionStempel() {
        final CharSequence[] items = { "Cari Produsen Lainnya", "Batal Order"};
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderStempelActivity.this);
        //builder.setTitle("Pilih Demo Order");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Cari Produsen Lainnya")) {
                    userChoosenTask="Cari Produsen Lainnya";
                    Intent toPilihProdusen = new Intent(OrderStempelActivity.this, PilihProdusenStempelActivity.class);
                    startActivityForResult(toPilihProdusen, 123);
                } else if (items[item].equals("Batal Order")) {
                    userChoosenTask="Batal Order";
                    if (bHijau.getText().equals("Ada Produsen")){
                        askDeclinedBtnMerahTidakAdaProdusen();
                    } else {
                        askDeclinedBtnMerahTidakAdaItem();
                    }
                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123){
            if (resultCode == 110){
                iNavProdusenStempel.setEnabled(true);
                sIdProdusenStempel = data.getStringExtra("idprodusenstempel");
                sLatitudeProdusenStempel = data.getStringExtra("latitudeprodusenstempel");
                sLongitudeProdusenStempel = data.getStringExtra("longitudeprodusenstempel");
                bHijau.setText("Ada Produsen");
                //Toast.makeText(orderStempelActivity, sIdProdusenStempel, Toast.LENGTH_SHORT).show();
                updateSelectedProdusenStempel(sIdProdusenStempel);
                updateStatusTransaksiKunci4();
            } else {
                Toast.makeText(orderStempelActivity, "Anda belum memilih produsen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void askDeclined() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan order?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                mitraDeclinedStempel();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void updateProdusenClosed(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.produsen_stempel_closed, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                //String konStatus = response.getString("status");
                                String msge = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msge, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateRequestItemNotAvailable(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.request_stempel_not_available, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                //String konStatus = response.getString("status");
                                String msge = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msge, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void askDeclinedBtnMerahTidakAdaProdusen() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan order?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                //Toast.makeText(OrderStempelActivity.this, "API Produsen Closed", Toast.LENGTH_SHORT).show();
                                updateProdusenClosed();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void askDeclinedBtnMerahTidakAdaItem() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan order?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                //Toast.makeText(OrderStempelActivity.this, "API Request not available", Toast.LENGTH_SHORT).show();
                                updateRequestItemNotAvailable();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void mitraDeclinedStempel(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_cancel_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        //Log.d("declinedjal", response.toString());
                        try {
                            //Process os success response
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                MainActivity.isBatal = true;
                                finish();

                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //finish();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateSelectedProdusenStempel(String idprodusenStempel){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("id_produsen_stempel", idprodusenStempel);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_selected_produsen_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
                                linearLayoutStatus.setVisibility(View.VISIBLE);
                                bTerima.setVisibility(View.INVISIBLE);
                            } else {
                                //String konStatus = response.getString("status");
                                String msge = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msge, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateStatusTransaksiKunci2(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("status_code", "2");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_trx_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                                bTerima.setText("Menuju Lokasi Customer");
                                tvDP.setEnabled(true);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateStatusTransaksiKunci3(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("status_code", "3");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_trx_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                                bTerima.setText("Menuju Produsen");
                                //statusTrxKunciPadaMitra(sIdTrx);
                                //updateDistanceMitra(sJarakKm);
                                //Log.d("sjarakTerima", sJarakKm);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateStatusTransaksiKunci4(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("status_code", "4");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_trx_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                                //bTerima.setText("Menuju Produsen");
                                //statusTrxKunciPadaMitra(sIdTrx);
                                //updateDistanceMitra(sJarakKm);
                                //Log.d("sjarakTerima", sJarakKm);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateStatusTransaksiKunci5(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("status_code", "5");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_trx_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                                //bTerima.setText("Menuju Produsen");
                                //statusTrxKunciPadaMitra(sIdTrx);
                                //updateDistanceMitra(sJarakKm);
                                //Log.d("sjarakTerima", sJarakKm);
                                bHijau.setText("Antar");
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateStatusTransaksiKunci6(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("status_code", "6");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_trx_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                                //bTerima.setText("Menuju Produsen");
                                //statusTrxKunciPadaMitra(sIdTrx);
                                //updateDistanceMitra(sJarakKm);
                                //Log.d("sjarakTerima", sJarakKm);
                                bHijau.setText("Sampai");
                                bMerahTidak.setVisibility(View.GONE);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateStatusTransaksiKunci7(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("status_code", "7");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_trx_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                                //bTerima.setText("Menuju Produsen");
                                //statusTrxKunciPadaMitra(sIdTrx);
                                //updateDistanceMitra(sJarakKm);
                                //Log.d("sjarakTerima", sJarakKm);
                                bHijau.setText("Selesai");
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void finishTransactionStempel(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.finish_transaction_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                                //bTerima.setText("Menuju Produsen");
                                //statusTrxKunciPadaMitra(sIdTrx);
                                //updateDistanceMitra(sJarakKm);
                                //Log.d("sjarakTerima", sJarakKm);
                                finish();
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateBiayaLainTipsStemp(String biayalainnya, String tipsnya){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("biaya_lain", biayalainnya);
        params.put("tips", tipsnya);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_biayalain_tips_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("jalbiayaStemp", response.toString());

                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                //Log.d("cobamsge", msge);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
                                //Log.d("elscobamsge", msg);
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void dialogDetailStemp(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_detail_stempel);
        dialog.setCancelable(true);

        dialog.show();

        TextView tLayananStempel = dialog.findViewById(R.id.tvValueLayananStempel); //1
        tLayananStempel.setText(sTypeStempel);

        TextView tUkuranStempel = dialog.findViewById(R.id.tvValueUkuranStempel); //2
        tUkuranStempel.setText(sUkuranStemp);

        TextView tKeteranganStempel = dialog.findViewById(R.id.tvValueKeterangan);
        tKeteranganStempel.setText(sKetStemp);

        final TextView tBiayaStemp = dialog.findViewById(R.id.tvValueBiaya);

        final TextView tJasaStemp = dialog.findViewById(R.id.tvValueJasaStemp);

        final TextView tHargaAwalStemp = dialog.findViewById(R.id.tvValueHargaAwal);

        final TextView tDPStemp = dialog.findViewById(R.id.tvValueDpStemp);

        final EditText etBiayaLainStemp = dialog.findViewById(R.id.etBiayaLainStempel);
        etBiayaLainStemp.addTextChangedListener(new NumberTextWatcherForThousand(etBiayaLainStemp));

        final EditText etTipsStemp = dialog.findViewById(R.id.etTipsStempel);
        etTipsStemp.addTextChangedListener(new NumberTextWatcherForThousand(etTipsStemp));

        final TextView tGrandTotStemp = dialog.findViewById(R.id.tvValueGrandTotalStemp);

        TextView tQtyStemp = dialog.findViewById(R.id.tvValueQtyStempel); //3
        tQtyStemp.setText(sQtyStemp);

        Button bUpdateTrxStemp = dialog.findViewById(R.id.btnUbahDetailStemp);
        bUpdateTrxStemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String biayalain = NumberTextWatcherForThousand.trimDotOfString(etBiayaLainStemp.getText().toString());
                String tips = NumberTextWatcherForThousand.trimDotOfString(etTipsStemp.getText().toString());

                if (biayalain.isEmpty() && tips.isEmpty()){
                    Toast.makeText(OrderStempelActivity.this, "Silahkan input biaya lain atau tips", Toast.LENGTH_SHORT).show();
                } else if (biayalain.isEmpty()){
                    biayalain = "0";
                    updateBiayaLainTipsStemp(biayalain, tips);
                } else if (tips.isEmpty()){
                    tips = "0";
                    updateBiayaLainTipsStemp(biayalain, tips);
                } else {
                    updateBiayaLainTipsStemp(biayalain, tips);
                }
                dialog.dismiss();
            }
        });


        //method get detail
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.detail_trx_stempel+sIdTrxStemp, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("detailStempel", response.toString());
                        try {
                            //Process os success response
                            JSONObject joData = response.getJSONObject("data");
                            String biayalain = joData.getString("biaya_lain"); //4
                            String tips = joData.getString("tips"); //5
                            String totalawal = joData.getString("total_awal"); //6
                            String totalakhir = joData.getString("total_akhir"); //7
                            //String biaya = joData.getString("harga_stempel");
                            //String jasa = joData.getString("biaya_layanan");
                            String biayadasar = joData.getString("biaya_dasar");
                            String tambahanbiayadasar = joData.getString("tambahan_biaya_antar");
                            String dpnya = joData.getString("dp");
                            int bdasar = Integer.parseInt(biayadasar);
                            int btambahan = Integer.parseInt(tambahanbiayadasar);
                            int totjasa = bdasar + btambahan;
                            String fixjasa = String.valueOf(totjasa);

                            //set DP
                            tDPStemp.setText(dpnya+" %");


                            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                            String rupiahBiaya = rupiahFormat.format(Double.parseDouble(totalawal));
                            String resultBiaya = Rupiah + " " + rupiahBiaya;
                            tBiayaStemp.setText(resultBiaya);

                            String rupiahJasa = rupiahFormat.format(Double.parseDouble(fixjasa));
                            String resultJasa = Rupiah + " " + rupiahJasa;
                            tJasaStemp.setText(resultJasa); // jasa


                            String rupiah = rupiahFormat.format(Double.parseDouble(totalawal));
                            String result = Rupiah + "" + rupiah;
                            tHargaAwalStemp.setText(result);

                            String rupiahtotal = rupiahFormat.format(Double.parseDouble(totalakhir));
                            String resulttotal = Rupiah + "" + rupiahtotal;
                            tGrandTotStemp.setText(resulttotal);

                            etBiayaLainStemp.setText(biayalain);
                            etTipsStemp.setText(tips);

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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void dialogDp(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_dp);
        dialog.setCancelable(true);

        dialog.show();

        final EditText eDP = dialog.findViewById(R.id.etDPStempel);

        Button bDP = dialog.findViewById(R.id.btnSimpanDP);
        bDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dpnya = eDP.getText().toString();

                //Toast.makeText(OrderStempelActivity.this, dpnya, Toast.LENGTH_SHORT).show();
                if (dpnya.equals("")){
                    Toast.makeText(OrderStempelActivity.this, "Silahkan masukkan biaya DP", Toast.LENGTH_SHORT).show();
                } else {
                    updateBiayaDP(dpnya);
                    dialog.dismiss();
                }
            }
        });



        ImageView imageView = dialog.findViewById(R.id.ivClose);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });



        //method get detail
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("id_transaksi_user", sIdTrxStemp);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.detail_trx_stempel+sIdTrxStemp, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("getDetailDP", response.toString());
                        try {
                            //Process os success response
                            JSONObject joData = response.getJSONObject("data");
                            String dpstemp = joData.getString("dp");
                            eDP.setText(dpstemp);
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        // add the request object to the queue to be executed
        //RequestQueue requestQueue = Volley.newRequestQueue(this);
        //requestQueue.add(request_json);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateBiayaDP(String nilaiDp){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxStemp);
        params.put("dp", nilaiDp);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.update_dp, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        //Log.d("responDP", response.toString());
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msg = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {
                                //String konStatus = response.getString("status");
                                String msge = response.getString("message");
                                Toast.makeText(OrderStempelActivity.this, msge, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
                return params;
            }
        };

        int socketTimeout = 20000; //20 detik
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
