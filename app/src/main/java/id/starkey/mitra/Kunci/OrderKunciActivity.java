package id.starkey.mitra.Kunci;

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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.Kunci.EnlargeImgKunci.EnlargeImgKunci;
import id.starkey.mitra.Kunci.RincianTrxKunci.RincianTrxKunciActivity;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.MyHandler;
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;
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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderKunciActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    GoogleMap map;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    static final int REQUEST_LOCATION = 1;
    private Button bOrder;
    private TextView tvAlamatCust, tvNamaCust, tvIdTrx, tvNamaCustTop, tvDetail, tvStatusTrxMitra;
    private String alamatCustomer, namaCustomer, sLatUser, sLngUser, sIdTrx, sNoHpUser, sEstimasiHarga, sFirebaseToken;
    double latCust, lngCust;
    private ImageView btnNavDest, iTelp, iSms, iCancel;
    private String tokennyaMitra, sDataAllTrxKunci, sJenisTransaksi, sStatusCode;
    private String sLayanan, sKunci, sFotoKunci, sKeterangan;
    private Context mContext;
    private double latMitra, lngMitra;
    private String sLatTrackMitra = "-7.0160395", sLngTrackMitra = "110.4630368";
    private String sJarakKm;
    private String sRincianHargaAwal, sRincianBiayaLain, sRincianTips, sRincianGrandTotal;

    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    //interval
    //Handler h = new Handler();
    //int delay = 10*1000; //1 second=1000 milisecond, 5*1000=5seconds
    //Runnable runnable;
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
    static OrderKunciActivity orderKunciActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_kunci);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mContext = this;
        orderKunciActivity = this;

        MyHandler.getHandler();

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarOrderKunci);
        setSupportActionBar(toolbar);
        setTitle("Order Kunci");

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        //get param from notif act
        Bundle bundle = getIntent().getExtras();
        sDataAllTrxKunci = bundle.getString("datatrxkunci"); //jsonobject
        Log.d("alltrxKunci", sDataAllTrxKunci);
        sJenisTransaksi = bundle.getString("jenisTrxKunci");

        try {
            JSONObject joDataKunci = new JSONObject(sDataAllTrxKunci);
            Log.d("jodatakunci", joDataKunci.toString());

            alamatCustomer = joDataKunci.getString("user_location");
            namaCustomer = joDataKunci.getString("nama_user");
            sIdTrx = joDataKunci.getString("id");
            sNoHpUser = joDataKunci.getString("nohp_user");
            sEstimasiHarga = joDataKunci.getString("total_awal");
            sStatusCode = joDataKunci.getString("status_code");

            //param for dialog
            sLayanan = joDataKunci.getString("nama_layanan_kunci");
            sKunci = joDataKunci.getString("type_kunci");
            sFotoKunci = joDataKunci.getString("photo");
            sKeterangan = joDataKunci.getString("keterangan");
            Log.d("sfotokunci", sFotoKunci);


            sLatUser = joDataKunci.getString("user_lat");
            sLngUser = joDataKunci.getString("user_long");
            latCust = Double.parseDouble(sLatUser);
            lngCust = Double.parseDouble(sLngUser);


        } catch (JSONException ex){

        }
        /*
        alamatCustomer = bundle.getString("alamatUsernya");
        namaCustomer = bundle.getString("namaUsernya");
        sIdTrx = bundle.getString("idtrx");
        sNoHpUser = bundle.getString("noHpUsernya");
        sEstimasiHarga = bundle.getString("estimasiHargaUsernya");

        //param for dialog
        sLayanan = bundle.getString("jenisTrxUsernya");
        sKunci = bundle.getString("namaKunciDanLayananUsernya");
        sFotoKunci = bundle.getString("photoItemUser");
        sKeterangan = bundle.getString("ketUsernya");
        Log.d("sfotokunci", sFotoKunci);


        sLatUser = bundle.getString("latUsernya");
        sLngUser = bundle.getString("lngUsernya");
        latCust = Double.parseDouble(sLatUser);
        lngCust = Double.parseDouble(sLngUser);
         */

        //init btn
        bOrder = findViewById(R.id.btnOrder);
        bOrder.setOnClickListener(this);
        bOrder.setEnabled(false); // first is disable

        btnNavDest = findViewById(R.id.imageViewNavDest);
        btnNavDest.setOnClickListener(this);
        iTelp = findViewById(R.id.imgTelpon);
        iTelp.setOnClickListener(this);
        iSms = findViewById(R.id.imgSms);
        iSms.setOnClickListener(this);
        tvDetail = findViewById(R.id.tvDetailTrxKunci);
        tvDetail.setOnClickListener(this);
        iCancel = findViewById(R.id.ivCancelMitra);
        iCancel.setOnClickListener(this);
        iCancel.setEnabled(false);

        //cek status code for outstanding trx
        if (sStatusCode.equals("1")){
            bOrder.setEnabled(true);
            bOrder.setBackgroundResource(R.drawable.background_button_kotak);
        } else if (sStatusCode.equals("2")){
            bOrder.setEnabled(true);
            bOrder.setBackgroundResource(R.drawable.background_button_kotak);
            bOrder.setText("Menuju Lokasi");
        } else if (sStatusCode.equals("3")){
            bOrder.setEnabled(true);
            bOrder.setBackgroundResource(R.drawable.background_button_kotak);
            bOrder.setText("Sampai Lokasi");
        } else if (sStatusCode.equals("4")){
            bOrder.setEnabled(true);
            bOrder.setBackgroundResource(R.drawable.background_button_kotak);
            bOrder.setText("Proses Pengerjaan");
        } else if (sStatusCode.equals("5")){
            bOrder.setEnabled(true);
            bOrder.setBackgroundResource(R.drawable.background_button_kotak);
            bOrder.setText("Selesai");
        }

        googleApiClient = new GoogleApiClient.Builder(OrderKunciActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMap();
        //Toast.makeText(this, "Klik detail untuk melihat detail\nAtau Klik telepon untuk masuk ke halaman Ratting", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Klik detail untuk melihat detail pesanan", Toast.LENGTH_SHORT).show();

        //init tv and settext
        tvAlamatCust = findViewById(R.id.txtAlamatCust);
        tvAlamatCust.setText(alamatCustomer);
        tvNamaCust = findViewById(R.id.txtNamaCust);
        tvNamaCust.setText(namaCustomer);
        tvNamaCustTop = findViewById(R.id.txtnamaCustTop);
        tvNamaCustTop.setText(namaCustomer);
        tvIdTrx = findViewById(R.id.txtIdTrx);
        tvIdTrx.setText("Order ID: KC"+sIdTrx);
        tvStatusTrxMitra = findViewById(R.id.textViewStatusTrxMitra);

        //getlocationmitra
        getLocation();

        //start interval
        /*
        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                runnable=this;
                h.postDelayed(runnable, delay);
                //statusTransaksiKunci(sId);
                updatePosisiMitra("2", sLatTrackMitra, sLngTrackMitra);
            }
        }, delay);
         */



        //get id from pref
        getPref();
        getFirebaseToken();
        telpUserConfirmation();

        //status default trx kunci at mitra
        statusTrxKunciPadaMitra(sIdTrx);

        //get km
        double latMitra = Double.parseDouble(sLatTrackMitra);
        double lngMitra = Double.parseDouble(sLngTrackMitra);
        getJarakKm(latCust, lngCust, latMitra, lngMitra);
    }

    public static OrderKunciActivity getInstance(){
        return orderKunciActivity;
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
        //h.removeCallbacks(runnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyHandler.pauseMyHandler(runnablestatus2);
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");Log.d("jalfbtoken", sFirebaseToken);
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

                if(locationNet != null){

                    double lattiNet = locationNet.getLatitude();
                    double longiNet = locationNet.getLongitude();

                    sLatTrackMitra = String.valueOf(lattiNet);
                    sLngTrackMitra = String.valueOf(longiNet);

                    //LatLng myPos = new LatLng(latti, longi);
                }
            }
        }
    }

    private void dialogDetail(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        dialog.setContentView(R.layout.detail_trx_kunci);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Detail Transaksi");

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.FILL;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);

        dialog.show();

        TextView tLayanan = dialog.findViewById(R.id.tvValueLayanan);
        TextView tKunci = dialog.findViewById(R.id.tvValueKunci);
        final ImageView iFoto = dialog.findViewById(R.id.imageViewTrxKunci);
        TextView tKet = dialog.findViewById(R.id.tvValueKeterangan);
        final TextView tBiaya = dialog.findViewById(R.id.tvValueBiaya);
        final TextView tJasa = dialog.findViewById(R.id.tvValueJasa);
        final TextView tBiayaDasar = dialog.findViewById(R.id.tvBiayaDasarDialogKunci);
        final TextView tEstHarga = dialog.findViewById(R.id.tvValueEstimasiHargaAwal);
        final TextView tGrandTotal = dialog.findViewById(R.id.tvValueGrandTotal);
        final EditText etBiayaLain = dialog.findViewById(R.id.editTextBiayaLain);
        etBiayaLain.addTextChangedListener(new NumberTextWatcherForThousand(etBiayaLain));

        final EditText etTips = dialog.findViewById(R.id.editTextTips);
        etTips.addTextChangedListener(new NumberTextWatcherForThousand(etTips));

        tLayanan.setText(sLayanan);
        tKunci.setText(sKunci);
        //tEstHarga.setText(sEstimasiHarga);

        if (sFotoKunci.equals("0")){
            iFoto.setBackgroundResource(R.drawable.ic_no_image);
        } else {
            Picasso.with(mContext)
                    .load(sFotoKunci)
                    .placeholder(R.drawable.progress_animation)
                    .into(iFoto);
        }


        tKet.setText(sKeterangan);

        iFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEnlarge = new Intent(OrderKunciActivity.this, EnlargeImgKunci.class);
                toEnlarge.putExtra("ulrfotoorderan", sFotoKunci);
                startActivity(toEnlarge);
            }
        });


        Button bUbahDetail = dialog.findViewById(R.id.btnUbahDetail);
        bUbahDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String biayalain = NumberTextWatcherForThousand.trimDotOfString(etBiayaLain.getText().toString());
                String tips = NumberTextWatcherForThousand.trimDotOfString(etTips.getText().toString());

                if (biayalain.isEmpty() && tips.isEmpty()){
                    Toast.makeText(OrderKunciActivity.this, "Silahkan input biaya lain atau tips", Toast.LENGTH_SHORT).show();
                } else if (biayalain.isEmpty()){
                    biayalain = "0";
                    updateBiayaLainTips(biayalain, tips);
                } else if (tips.isEmpty()){
                    tips = "0";
                    updateBiayaLainTips(biayalain, tips);
                } else {
                    updateBiayaLainTips(biayalain, tips);
                }
                dialog.dismiss();
            }
        });

        //method get detail
        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Mohon tunggu...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();

        String URL_DETAIL = "https://api.starkey.id/api/transaction/user/"+sIdTrx;

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, URL_DETAIL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        String hasilDetail = response.toString();
                        Log.d("hasilDetailDialog", hasilDetail);

                        try {
                            //Process os success response

                            JSONObject joData = response.getJSONObject("data");
                            Log.d("jodataOrderKunci", joData.toString());
                            String biayalain = joData.getString("biaya_lain");
                            String biayatips = joData.getString("tips");
                            String totalawal = joData.getString("total_awal");
                            String totalakhir = joData.getString("total_akhir");
                            //String biaya = joData.getString("harga_item");
                            //String jasa = joData.getString("biaya_layanan");
                            String biayadasar = joData.getString("biaya_dasar");
                            String tambahanbiayadasar = joData.getString("tambahan_biaya_antar");

                            //convert jasa (biaya_dasar + tambahan_biaya) to int
                            int biayadasarint = Integer.parseInt(biayadasar);
                            int tambahanbiayaint = Integer.parseInt(tambahanbiayadasar);
                            int biayajasa = biayadasarint + tambahanbiayaint;
                            String jasanya = String.valueOf(biayajasa);


                            //string for send param to rincian trx kunci
                            sRincianHargaAwal = totalawal;
                            sRincianBiayaLain = biayalain;
                            sRincianTips = biayatips;
                            sRincianGrandTotal = totalakhir;

                            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);

                            String rupiahJasa = rupiahFormat.format(Double.parseDouble(jasanya));
                            String resultJasa = Rupiah + " " + rupiahJasa;
                            tJasa.setText(resultJasa);

                            /*
                            String rupiahBiayaDasar = rupiahFormat.format(Double.parseDouble(biayadasar));
                            String resultBiayaDasar = Rupiah + " " + rupiahBiayaDasar;
                            tBiayaDasar.setText(resultBiayaDasar);
                             */



                            String rupiah = rupiahFormat.format(Double.parseDouble(totalawal));
                            String result = Rupiah + " " + rupiah;
                            //tEstHarga.setText(result);
                            tBiaya.setText(result); //tbiaya set ke total awal

                            String rupiahtotal = rupiahFormat.format(Double.parseDouble(totalakhir));
                            String resulttotal = Rupiah + " " + rupiahtotal;
                            tGrandTotal.setText(resulttotal);

                            //setText edittext biaya lain & tips
                            etBiayaLain.setText(biayalain);
                            etTips.setText(biayatips);

                        } catch (JSONException e1) {
                            e1.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
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

    private void updateBiayaLainTips(String biayalainnya, String tipsnya){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrx);
        params.put("biaya_lain", biayalainnya);
        params.put("tips", tipsnya);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_biayalain_tips, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("jalbiaya", response.toString());

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
                                Toast.makeText(OrderKunciActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void finishTransaction(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrx);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.finish_transaction_new, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        try {
                            //Process os success response
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                Log.d("cobamsge", msge);
                                finish();

                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                Log.d("elscobamsge", msg);
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

    private void telpUserConfirmation(){

        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrx);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.telp_confirmation, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                //String cobatoken = response.getString("token");

                                /*
                                JSONObject dataJO = new JSONObject();
                                dataJO = response.getJSONObject("data");
                                String id = dataJO.getString("id");
                                String nama = dataJO.getString("first_name");
                                String namablkg = dataJO.getString("last_name");
                                String phone = dataJO.getString("phone");
                                String email = dataJO.getString("email");
                                String token = dataJO.getString("token");
                                 */


                                //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                //startActivity(intent);
                                //finish();
                                iCancel.setEnabled(true);
                                bOrder.setEnabled(true);
                                bOrder.setBackgroundResource(R.drawable.background_button_kotak);

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

    private void initializeMap() {
        if (map == null) {
            MapFragment mapFragment = (MapFragment)getFragmentManager()
                    .findFragmentById(R.id.mapOrderKunci);
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
    public void onBackPressed() {
        //super.onBackPressed();
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
        //bOrder.setText("SUDAH TIBA");
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


            //LatLng sydney = new LatLng(-7.01629, 110.4631213);
            //map.addMarker(new MarkerOptions().position(sydney).title("Your Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker_biru)));
            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));

            //detail customer
            LatLng custLocation = new LatLng(latCust, lngCust);
            map.addMarker(new MarkerOptions().position(custLocation).title("Customer").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(custLocation, 17));
            Log.d("latE", String.valueOf(latCust));
            Log.d("lngE", String.valueOf(lngCust));
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
                Toast.makeText(OrderKunciActivity.this,message, Toast.LENGTH_LONG).show();
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

    private void statusTrxKunciPadaMitra(String idTrxKunci){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        String URL_STATUS_TRX_MITRA = "https://api.starkey.id/api/status_transaksi_kunci/mitra/"+idTrxKunci;

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        //params.put("id_transaksi_user", sIdTrx);
        //params.put("status_code", "2");


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, URL_STATUS_TRX_MITRA, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                //String msge = response.getString("message");

                                //String cobatoken = response.getString("token");
                                JSONObject jostatusname = response.getJSONObject("data");
                                String statusTrxMitra = jostatusname.getString("status_name");
                                Log.d("onresponseTrxKunci", jostatusname.toString());
                                tvStatusTrxMitra.setText(statusTrxMitra);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderKunciActivity.this, msg, Toast.LENGTH_SHORT).show();
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
    public void onClick(View v) {
        if (v == bOrder){
            //Toast.makeText(this, "next api", Toast.LENGTH_SHORT).show();
            if (bOrder.getText().equals("Terima")){
                updateStatusTransaksiKunci();
            } else if (bOrder.getText().equals("Menuju Lokasi")){
                updateStatusTransaksiKunci3();
            } else if (bOrder.getText().equals("Sampai Lokasi")){
                updateStatusTransaksiKunci4();
            } else if (bOrder.getText().equals("Proses Pengerjaan")){
                updateStatusTransaksiKunci5();
            } else if (bOrder.getText().equals("Selesai")){
                //Toast.makeText(this, "API LAINNYA", Toast.LENGTH_SHORT).show();
                //finishTransaction();

                getDetailForRincianKunci();
            }
        }
        if (v == btnNavDest){
            Intent jumpMaps = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps?saddr=My+Location&daddr="+latCust+", "+lngCust));
            startActivity(jumpMaps);
        }
        if (v == iTelp){
            Uri callCust = Uri.parse("tel:" + sNoHpUser);
            Intent surf = new Intent(Intent.ACTION_DIAL, callCust);
            startActivity(surf);
            //telpUserConfirmation();
        }
        if (v == iSms){
            Uri smsCust = Uri.parse("sms:" + sNoHpUser);
            Intent surfSms = new Intent(Intent.ACTION_VIEW, smsCust);
            startActivity(surfSms);
        }
        if (v == tvDetail){
            dialogDetail();
        }
        if (v == iCancel){
            askDeclined();
        }
    }

    private void getDetailForRincianKunci(){
        //method get detail
        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Mohon tunggu...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();

        String URL_DETAIL = "https://api.starkey.id/api/transaction/user/"+sIdTrx;

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, URL_DETAIL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        Log.d("getDetailKunci", response.toString());

                        try {
                            //Process os success response

                            JSONObject joData = response.getJSONObject("data");
                            Log.d("jodataOrderKunci", joData.toString());
                            //String biayadasar = joData.getString("biaya_dasar");
                            String biayalain = joData.getString("biaya_lain");
                            String biayatips = joData.getString("tips");
                            String totalawal = joData.getString("total_awal"); // inc harga_item+biaya_layanan
                            //String hargaitem = joData.getString("harga_item");
                            //String biayalayanan = joData.getString("biaya_layanan");
                            String totalakhir = joData.getString("total_akhir");


                            String biayadasar = joData.getString("biaya_dasar");
                            String tambahanbiaya = joData.getString("tambahan_biaya_antar");
                            int biayadasarint = Integer.parseInt(biayadasar);
                            int tambahanbiayaint = Integer.parseInt(tambahanbiaya);
                            int jasa = biayadasarint + tambahanbiayaint;
                            String sJasa = String.valueOf(jasa);

                            //string for send param to rincian trx kunci
                            sRincianHargaAwal = totalawal;
                            sRincianBiayaLain = biayalain;
                            sRincianTips = biayatips;
                            sRincianGrandTotal = totalakhir;

                            Intent toRincian = new Intent(OrderKunciActivity.this, RincianTrxKunciActivity.class);
                            toRincian.putExtra("rincianId", sIdTrx); //id
                            //toRincian.putExtra("rincianBiayaDasar", biayadasar);
                            //toRincian.putExtra("rincianHargaItem", hargaitem);
                            //toRincian.putExtra("rincianBiayaLayanan", biayalayanan);
                            toRincian.putExtra("rincianLayanan", sLayanan); //layanan
                            toRincian.putExtra("rincianKunci", sKunci); //kunci item
                            toRincian.putExtra("rincianHargaAwal", sRincianHargaAwal); //est harga awal
                            toRincian.putExtra("rincianJasa", sJasa);
                            toRincian.putExtra("rincianBiayaLain", sRincianBiayaLain); //biaya lain
                            toRincian.putExtra("rincianTips", sRincianTips); //tips
                            toRincian.putExtra("rincianGrandTotal", sRincianGrandTotal); //grand total
                            startActivity(toRincian);

                        } catch (JSONException e1) {
                            e1.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
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

    private void askDeclined() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan order?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                mitraDeclined();
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

    private void mitraDeclined(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrx);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_cancel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("declinedjal", response.toString());
                        try {
                            //Process os success response
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){

                                //String msge = response.getString("message");
                                //Toast.makeText(OrderKunciActivity.this, msge, Toast.LENGTH_SHORT).show();
                                MainActivity.isBatal = true;
                                finish();
                            } else {

                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderKunciActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void updateStatusTransaksiKunci(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrx);
        params.put("status_code", "2");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_transaksi_kunci, new JSONObject(params),
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
                                //String cobatoken = response.getString("token");

                                //bOrder.setEnabled(true);
                                //bOrder.setBackgroundResource(R.drawable.background_button_kotak);
                                bOrder.setText("Menuju Lokasi");
                                statusTrxKunciPadaMitra(sIdTrx);
                                updateDistanceMitra(sJarakKm);
                                tvDetail.setEnabled(true);
                                Log.d("sjarakTerima", sJarakKm);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderKunciActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        params.put("id_transaksi_user", sIdTrx);
        params.put("status_code", "3");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_transaksi_kunci, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                Log.d("cobamsge", msge);
                                //String cobatoken = response.getString("token");

                                //bOrder.setEnabled(true);
                                //bOrder.setBackgroundResource(R.drawable.background_button_kotak);
                                bOrder.setText("Sampai Lokasi");
                                statusTrxKunciPadaMitra(sIdTrx);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderKunciActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        params.put("id_transaksi_user", sIdTrx);
        params.put("status_code", "4");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_transaksi_kunci, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                Log.d("cobamsge", msge);
                                //String cobatoken = response.getString("token");

                                //bOrder.setEnabled(true);
                                //bOrder.setBackgroundResource(R.drawable.background_button_kotak);
                                bOrder.setText("Proses Pengerjaan");
                                statusTrxKunciPadaMitra(sIdTrx);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderKunciActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        params.put("id_transaksi_user", sIdTrx);
        params.put("status_code", "5");


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_status_transaksi_kunci, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            loading.dismiss();
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){
                                String msge = response.getString("message");
                                Log.d("cobamsge", msge);
                                //String cobatoken = response.getString("token");

                                //bOrder.setEnabled(true);
                                //bOrder.setBackgroundResource(R.drawable.background_button_kotak);
                                bOrder.setText("Selesai");
                                statusTrxKunciPadaMitra(sIdTrx);
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(OrderKunciActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void updateDistanceMitra(String kilonya){

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrx);
        params.put("distance", kilonya);

        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.update_distance, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("distanceRespon", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

    private void getJarakKm(double latiUser, double longiUser, double latiMitra, double longiMitra){

        String urlMatrix = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins="+latiMitra+","+longiMitra+"&destinations="+latiUser+","+longiUser+"&key=AIzaSyD-N1rB8rGBmP_f2koq8bjdMTjYRLqBjwk";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlMatrix,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonRespRouteDistance = null;
                        try {
                            jsonRespRouteDistance = new JSONObject(response)
                                    .getJSONArray("rows")
                                    .getJSONObject(0)
                                    .getJSONArray ("elements")
                                    .getJSONObject(0)
                                    .getJSONObject("distance");

                            String kilometer = jsonRespRouteDistance.get("text").toString();
                            String fixkm = kilometer.substring(0, kilometer.length()-2);
                            sJarakKm = fixkm;
                            Log.d("aslikilomitra", kilometer);
                            Log.d("kilomitra", sJarakKm);
                            //Toast.makeText(TrxKunciActivity.this, distance, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //String destination_addr = new JSONObject(httpResponse).get("destination_addresses").toString();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(TrxKunciActivity.this, "Error Listener", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
