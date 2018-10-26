package id.starkey.mitra.NotificationOrder;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;
import id.starkey.mitra.Stempel.OrderStempelActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static id.starkey.mitra.NotificationOrder.NotificationOrder.REQUEST_LOCATION;

public class NotificationOrderStempel extends AppCompatActivity implements View.OnClickListener {

    private String inboxPesan, jenisTrx, tokennyaUser, sIdTrxCus, sFirebaseToken;
    private String sLatUser, sLngUser, sNamaUser, sNoHpUser, sPhotoProfilUser;
    private Button bTerima, bTolakOrder;
    public CountDownTimer myCountDownTimer;
    private TextView tJenisStemp, tUkuranStemp, tKetStemp, tQtyStemp, tAlamatCus, tEstTrxStemp, tJenisLayanan;
    private String sTypeStemp, sUkuranStemp, sQtyStemp, sKetStemp, sAlamatCust, sEstTrxStemp;
    private NumberFormat rupiahFormat;
    private String Rupiah = "Rp.";
    private String sLat, sLng;
    private String latMitra, lngMitra;

    static NotificationOrderStempel notificationOrderStempel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_order_stempel);

        notificationOrderStempel = this;

        //init tv
        tJenisStemp = findViewById(R.id.tvJenisStempel);
        tUkuranStemp = findViewById(R.id.tvUkuranStempel);
        tKetStemp = findViewById(R.id.tvKetStempel);
        tQtyStemp = findViewById(R.id.tvQtyStemp);
        tAlamatCus = findViewById(R.id.tvAlamatCustStempel);
        tEstTrxStemp = findViewById(R.id.tvEstimasiTransaksiStempel);
        tJenisLayanan = findViewById(R.id.tvJenisLayananStempel);


        //get param order stemp from cust
        Bundle bundle = getIntent().getExtras();
        inboxPesan = bundle.getString("messageStempel");
        jenisTrx = bundle.getString("jenisTrxStempel");
        //Log.d("payloadstemp", inboxPesan);

        //json string to json object
        try {
            JSONObject joOrderStempel = new JSONObject(inboxPesan);
            Log.d("jorderstempel", joOrderStempel.toString());
            sIdTrxCus = joOrderStempel.getString("id");
            sTypeStemp = joOrderStempel.getString("type_stempel");
            sUkuranStemp = joOrderStempel.getString("ukuran");
            sQtyStemp = joOrderStempel.getString("qty");
            sKetStemp = joOrderStempel.getString("keterangan");
            sAlamatCust = joOrderStempel.getString("user_location");
            sEstTrxStemp = joOrderStempel.getString("total_awal");
            sLatUser = joOrderStempel.getString("user_lat");
            sLngUser = joOrderStempel.getString("user_long");
            sNamaUser = joOrderStempel.getString("nama_user");
            sNoHpUser = joOrderStempel.getString("nohp_user");
            sPhotoProfilUser = joOrderStempel.getString("profile_user");

            /*
            String estimasiharga = joOrderMasuk.getString("total_awal");
            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
            String rupiah = rupiahFormat.format(Double.parseDouble(estimasiharga));
            String resultRp = Rupiah + " " + rupiah;
             */

            //set to tv
            tJenisStemp.setText(sTypeStemp);
            tUkuranStemp.setText(sUkuranStemp);
            tKetStemp.setText(sKetStemp);
            tQtyStemp.setText(sQtyStemp);
            tAlamatCus.setText(sAlamatCust);

            String totalharga = joOrderStempel.getString("total_awal");
            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
            String rupiah = rupiahFormat.format(Double.parseDouble(totalharga));
            String resultTotal = Rupiah + " " + rupiah;
            tEstTrxStemp.setText(resultTotal);

            tJenisLayanan.setText(jenisTrx);


        } catch (JSONException ex){

        }

        //init btn
        bTerima = findViewById(R.id.btnTerimaOrderStempel);
        bTerima.setOnClickListener(this);
        bTolakOrder = findViewById(R.id.btnTolakStempel);
        bTolakOrder.setOnClickListener(this);

        myCountDownTimer = new CountDownTimer(20000, 100){
            public void onTick(long millisUntilFinished) {
                String jdl = "Terima Order"+" "+" "+" "+" "+" "+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished));
                //bTerima.setText("Terima Order"+" "+" " +" " +" " +" " +new SimpleDateFormat("ss").format(new Date( millisUntilFinished)));
                bTerima.setText(jdl);
            }

            public void onFinish() {
                mitraDeclinedOrderStempel();
                finish();
            }
        }.start();

        //get id from pref
        getPref();
        getFirebaseToken();

        getLocation();

        updatePosisiMitra("2", sLat, sLng, sFirebaseToken);

    }

    public static NotificationOrderStempel getInstance(){
        return notificationOrderStempel;
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


        }
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
                        try {
                            //Process os success response
                            //loading.dismiss();
                            String hasil = response.getString("message");
                            //Log.d("hasilupdateposisi", hasil);
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
                Toast.makeText(NotificationOrderStempel.this,message, Toast.LENGTH_LONG).show();
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

    private void mitraAcceptedStempel(){
        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxCus);


        JsonObjectRequest request_json = new JsonObjectRequest(ConfigLink.mitra_accepted_stempel, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d("hasilacceptstempel", response.toString());
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

    @Override
    public void onClick(View v) {
        if (v == bTerima){
            Intent main = new Intent(NotificationOrderStempel.this, OrderStempelActivity.class);
            //params
            main.putExtra("datatrxstempel", inboxPesan);
            //Log.d("pencetStempel", inboxPesan);
            main.putExtra("jenisTrxStempel", jenisTrx);
            startActivity(main);
            /*
            main.putExtra("idtrx", sIdTrxCus);
            main.putExtra("alamatUser", sAlamatCust);
            main.putExtra("latUser", sLatUser);
            main.putExtra("lngUser", sLngUser);
            main.putExtra("namaUser", sNamaUser);
            main.putExtra("noHpUser", sNoHpUser);
            main.putExtra("photoProfilUser", sPhotoProfilUser);
            main.putExtra("latitudeMitra", latMitra);
            main.putExtra("longitudeMitra", lngMitra);
            //params for dialog main layout
            main.putExtra("jenisTrx", jenisTrx);
            main.putExtra("typeStempel", sTypeStemp);
            main.putExtra("ukuranStempel", sUkuranStemp);
            main.putExtra("qty", sQtyStemp);
            main.putExtra("ketStemp", sKetStemp);
            main.putExtra("estimasiHargaStemp", sEstTrxStemp);

             */



            myCountDownTimer.cancel();
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
            mitraAcceptedStempel();
            finish();
        }

        if (v == bTolakOrder){
            askTolakOrderStempel();
        }
    }

    private void askTolakOrderStempel() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan membatalkan order?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                //mitraDeclinedByButton();
                                mitraDeclinedStempelByButton();
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

    private void mitraDeclinedStempelByButton(){ //when countdown has finish
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxCus);
        params.put("declined", "true");


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_declined_stempel, new JSONObject(params),
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

    private void mitraDeclinedOrderStempel(){ //when countdown has finish
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTrxCus);
        params.put("declined", "false");


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST, ConfigLink.mitra_declined_stempel, new JSONObject(params),
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
