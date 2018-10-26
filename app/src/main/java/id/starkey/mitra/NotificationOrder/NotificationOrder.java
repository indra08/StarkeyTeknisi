package id.starkey.mitra.NotificationOrder;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import id.starkey.mitra.Kunci.OrderKunciActivity;

import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationOrder extends AppCompatActivity implements View.OnClickListener {

    private String inboxPesan, jenisTrx, namaUser, noHpUser, photoProfilUser, latUser, lngUser, photoItemUser;
    private Button bTerimaOrder, bTolakOrder;
    public CountDownTimer myCountDownTimer;
    private TextView tJenisLayanan, tNamaKuncidanLayanan, tQtyKunci, tKetKunci, tEstimasiTransaksi, tAlamatCust;
    private NumberFormat rupiahFormat;
    private String Rupiah = "Rp.";
    LocationManager locationManager;
    static final int REQUEST_LOCATION = 1;
    private String latMitra, lngMitra;
    private String idpostmitra, tokennyaUser, sFirebaseToken;
    private String sLat, sLng;

    static NotificationOrder notificationOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_order);

        notificationOrder = this;

        //init tv
        tJenisLayanan = findViewById(R.id.tvJenisLayanan);
        tNamaKuncidanLayanan = findViewById(R.id.tvNamaKuncidanLayanan);
        tQtyKunci = findViewById(R.id.tvQtyKunci);
        tKetKunci = findViewById(R.id.tvKetKunci);
        tEstimasiTransaksi = findViewById(R.id.tvEstimasiTransaksi);
        tAlamatCust = findViewById(R.id.tvAlamatCust);

        //get param order from cust
        Bundle bundle = getIntent().getExtras();
        inboxPesan = bundle.getString("message");
        jenisTrx = bundle.getString("jenisTrx");

        tJenisLayanan.setText(jenisTrx);

        //json string to json object
        try {
            JSONObject joOrderMasuk = new JSONObject(inboxPesan);
            idpostmitra = joOrderMasuk.getString("id");
            String typekunci = joOrderMasuk.getString("type_kunci");
            String namalayanankunci = joOrderMasuk.getString("nama_layanan_kunci");

            //harga format rupiah//
            String estimasiharga = joOrderMasuk.getString("total_awal");
            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
            String rupiah = rupiahFormat.format(Double.parseDouble(estimasiharga));
            String resultRp = Rupiah + " " + rupiah;
            //harga format rupiah//


            String ketkunci = joOrderMasuk.getString("keterangan");
            String qtykunci = joOrderMasuk.getString("qty");
            String imageURL = joOrderMasuk.getString("photo");
            String almtCust = joOrderMasuk.getString("user_location");

            tNamaKuncidanLayanan.setText(namalayanankunci+" "+typekunci);
            tQtyKunci.setText(qtykunci);
            tKetKunci.setText(ketkunci);
            tEstimasiTransaksi.setText(resultRp);
            tAlamatCust.setText(almtCust);

            //params to orderKunciAct
            namaUser = joOrderMasuk.getString("nama_user");
            noHpUser = joOrderMasuk.getString("nohp_user");
            photoProfilUser = joOrderMasuk.getString("profile_user");
            latUser = joOrderMasuk.getString("user_lat");
            lngUser = joOrderMasuk.getString("user_long");
            photoItemUser = joOrderMasuk.getString("photo");

            //Log.d("imageurlbro", imageURL);
            Log.d("cobajsonstring", joOrderMasuk.toString());
        } catch (JSONException ex){

        }

        //init btn
        bTerimaOrder = findViewById(R.id.btnTerimaOrder);
        bTerimaOrder.setOnClickListener(this);
        bTolakOrder = findViewById(R.id.btnTolakKunci);
        bTolakOrder.setOnClickListener(this);

        myCountDownTimer = new CountDownTimer(20000, 100){
            public void onTick(long millisUntilFinished) {
                String jdl = "Terima Order"+" "+" "+" "+" "+" "+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished));
                //bTerima.setText("Terima Order"+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished)));
                bTerimaOrder.setText(jdl);
            }

            public void onFinish() {
                //bTerima.setText("done!");
                //Toast.makeText(NotificationOrder.this, "onFinish toast", Toast.LENGTH_SHORT).show();
                //dialog.dismiss();
                mitraDeclinedOrder();
                finish();
                //Intent mainBack = new Intent(NotificationOrder.this, MainActivity.class);
                //startActivity(mainBack);
            }
        }.start();

        //Log.d("loginbox", inboxPesan);

        //MainActivity.getInstance().finish();

        //get id from pref
        getPref();
        getFirebaseToken();

        getLocation();

        updatePosisiMitra("2", sLat, sLng, sFirebaseToken);
        //Toast.makeText(this, "test oncreate", Toast.LENGTH_SHORT).show();
    }

    public static NotificationOrder getInstance(){
        return notificationOrder;
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaUser = custDetails.getString("tokenIdUser", "");
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");Log.d("jalfbtoken", sFirebaseToken);
    }

    private void mitraDeclinedOrder(){ //when countdown has finish
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", idpostmitra);
        params.put("declined", "false");


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_declined, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String resp = response.toString();
                        Log.d("mitDec", resp);
                        /*
                        try {
                            //Process os success response
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){

                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                         */

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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
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

    private void mitraDeclinedByButton(){ //when button 'tolak' is clicked >< params declined true
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", idpostmitra);
        params.put("declined", "true");


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_declined, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String resp = response.toString();
                        Log.d("mitDecByBtn", resp);
                        finish();
                        /*
                        try {
                            //Process os success response
                            String konStatus = response.getString("status");
                            if (konStatus.equals("success")){

                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                //Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                         */

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
                    message = "Authentification Failure";
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
                params.put("Authorization", "Bearer "+tokennyaUser);
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

    private void updatePosisiMitra(String avail, String latnya, String lngnya, String fbaseToken){

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
                        Log.d("updatenotifkunci", response.toString());
                        try {
                            //Process os success response
                            //loading.dismiss();
                            String hasil = response.getString("message");
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
                Toast.makeText(NotificationOrder.this,message, Toast.LENGTH_LONG).show();
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

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            //getfromprefgps
            SharedPreferences locprefgps = getSharedPreferences(ConfigLink.locationbyGps, MODE_PRIVATE);
            sLat = locprefgps.getString("latGps", "");
            sLng = locprefgps.getString("lngGps","");
            /*
            SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String userEmail = custDetails.getString("emailUser", "");
        etUsername.setText(userEmail);
             */

            /*
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                //map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                sLat = String.valueOf(latti);
                sLng = String.valueOf(longi);
                //LatLng myPos = new LatLng(latti, longi);
            } else {
                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                double lattiNet = locationNet.getLatitude();
                double longiNet = locationNet.getLongitude();
                sLat = String.valueOf(lattiNet);
                sLng = String.valueOf(longiNet);
                //LatLng myPos = new LatLng(latti, longi);
            }
             */

        }
    }

    @Override
    public void onClick(View v) {
        if (v == bTerimaOrder){
            Intent main = new Intent(NotificationOrder.this, OrderKunciActivity.class);
            main.putExtra("datatrxkunci", inboxPesan);
            Log.d("pencetKunci", inboxPesan);
            main.putExtra("jenisTrxKunci", jenisTrx);

            /*
            main.putExtra("idtrx", idpostmitra);
            main.putExtra("alamatUsernya", tAlamatCust.getText().toString());
            main.putExtra("latUsernya", latUser);
            main.putExtra("lngUsernya", lngUser);
            main.putExtra("namaUsernya", namaUser);
            main.putExtra("noHpUsernya", noHpUser);
            main.putExtra("photoProfilUsernya", photoProfilUser);
            main.putExtra("latitudeMitra", latMitra);
            main.putExtra("longitudeMitra", lngMitra);
            //params for dialog main layout
            main.putExtra("jenisTrxUsernya", jenisTrx);
            main.putExtra("namaKunciDanLayananUsernya", tNamaKuncidanLayanan.getText().toString());
            main.putExtra("qtyUsernya", tQtyKunci.getText().toString());
            main.putExtra("ketUsernya", tKetKunci.getText().toString());
            main.putExtra("estimasiHargaUsernya", tEstimasiTransaksi.getText().toString());
            main.putExtra("photoItemUser", photoItemUser);
             */
            //params for main layout

            startActivity(main);
            //finishAffinity();
            myCountDownTimer.cancel();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            /*
            NotificationManager notify_manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notify_manager.cancelAll();
             */
            mitraAccepted();
            finish();
        }

        if (v == bTolakOrder){
            myCountDownTimer.cancel();
            askTolakOrder();
        }
    }

    private void askTolakOrder() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan order?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                mitraDeclinedByButton();
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

    private void mitraAccepted(){
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", idpostmitra);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.mitra_accepted, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Process os success response
                            //loading.dismiss();
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
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
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
}
