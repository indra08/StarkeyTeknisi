package id.starkey.mitra.Kunci.RincianTrxKunci;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RincianTrxKunciActivity extends AppCompatActivity implements View.OnClickListener{

    private Button bSimpan;
    private String tokennyaMitra;
    private String sIdTrx, sLayanan, sKunci, sHargaAwal, sBiayaLain, sTips, sGrandTotal, sJasa;
    private TextView tLayanan, tKunci, tHargaItem, tBiayaDasar, tJasaBiayaLayanan, tBiayalain, tGrandTotal, tTips;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_trx_kunci);

        //get param from notif act
        Bundle bundle = getIntent().getExtras();
        sIdTrx = bundle.getString("rincianId");
        //sHargaItem = bundle.getString("rincianHargaItem");
        //sJasaBiayaLayanan = bundle.getString("rincianBiayaLayanan");
        //sBiayaDasar = bundle.getString("rincianBiayaDasar");
        sLayanan = bundle.getString("rincianLayanan");
        sKunci = bundle.getString("rincianKunci");
        sHargaAwal = bundle.getString("rincianHargaAwal");
        sJasa = bundle.getString("rincianJasa");
        sBiayaLain = bundle.getString("rincianBiayaLain");
        sTips = bundle.getString("rincianTips");
        sGrandTotal = bundle.getString("rincianGrandTotal");

        //init tv
        tLayanan = findViewById(R.id.tvLayananRincianKunci);
        tKunci = findViewById(R.id.tvKunciItemRincianKunci);

        tHargaItem = findViewById(R.id.tvHargaItemRincianKunci);

        //tBiayaDasar = findViewById(R.id.tvBiayaDasarRinKunci);
        tJasaBiayaLayanan = findViewById(R.id.tvJasaBiayaLayanan);
        tBiayalain = findViewById(R.id.tvBiayaLainRinKunci);
        tGrandTotal = findViewById(R.id.tvGrandTotalRinKunci);
        tTips = findViewById(R.id.tvTipsRinKunci);

        //settext tv
        tLayanan.setText(sLayanan);
        tKunci.setText(sKunci);

        //settext tv harga awal
        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);

        String rupiahHargaItem = rupiahFormat.format(Double.parseDouble(sHargaAwal));
        String resultHargaItem = Rupiah + " " + rupiahHargaItem;
        tHargaItem.setText(resultHargaItem);


        /*
        String rupiahBiayaDasar = rupiahFormat.format(Double.parseDouble(sBiayaDasar));
        String resultBiayaDasar = Rupiah + " " + rupiahBiayaDasar;
        tBiayaDasar.setText(resultBiayaDasar);
         */




        String rupiahJasaBiayaLayanan = rupiahFormat.format(Double.parseDouble(sJasa));
        String resultJasaBiayaLayanan = Rupiah + " " + rupiahJasaBiayaLayanan;
        tJasaBiayaLayanan.setText(resultJasaBiayaLayanan);




        //settext tv biayalain
        String rupiahbiayalain = rupiahFormat.format(Double.parseDouble(sBiayaLain));
        String resultbiayalain = Rupiah + " " + rupiahbiayalain;
        tBiayalain.setText(resultbiayalain);

        //settext tv grandtotal
        String rupiahgrandtotal = rupiahFormat.format(Double.parseDouble(sGrandTotal));
        String resultgrandtotal = Rupiah + " " + rupiahgrandtotal;
        tGrandTotal.setText(resultgrandtotal);

        //settext tv tips
        String rupiahtips = rupiahFormat.format(Double.parseDouble(sTips));
        String resulttips = Rupiah + " " + rupiahtips;
        tTips.setText(resulttips);

        //init btn
        bSimpan = findViewById(R.id.bSimpanTrxKunci);
        bSimpan.setOnClickListener(this);

        //get id from pref
        getPref();
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    @Override
    public void onClick(View v) {
        if (v == bSimpan){
            //fungsi finish
            finishTransaction();
        }
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
                                OrderKunciActivity.getInstance().finish();
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
}
