package id.starkey.mitra.Stempel.RincianTrxStempel;

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
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;
import id.starkey.mitra.Stempel.OrderStempelActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RincianTrxStempelActivity extends AppCompatActivity implements View.OnClickListener {

    private String sTipeStempel, sUkuranStempel, sQtyStempel, sTotalAwalStempel, sGrandTotalStempel, sBiayaLainStempel,
            sTipsStempel, sDp, sJasa;
    private String sIdTransaksiStempel, tokennyaMitra;
    private TextView tTipeStempel, tUkuranStempel, tQtyStempel, tTotalAwalStemp, tGrandTotalStemp, tBiayaLainStemp, tTipsStemp, tDp,
            tBiayaLainNew;
    private Button bSimpan;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rincian_trx_stempel);

        //init btn
        bSimpan = findViewById(R.id.bSimpanTrxStemp);
        bSimpan.setOnClickListener(this);

        //init tv
        tTipeStempel = findViewById(R.id.tvTipeStempRincian);
        tUkuranStempel = findViewById(R.id.tvUkuranStempRincian);
        tQtyStempel = findViewById(R.id.tvQtyStempRincian);
        tTotalAwalStemp = findViewById(R.id.tvEstimasiRincianKunci);
        tGrandTotalStemp = findViewById(R.id.tvGrandTotalRincianKunci);
        tBiayaLainStemp = findViewById(R.id.tvBiayaLainStempelRincian);
        tTipsStemp = findViewById(R.id.tvTipsStempelRincian);
        tDp = findViewById(R.id.tvDpStempelRincian);
        tBiayaLainNew = findViewById(R.id.tvBiayaLainNewRinKunci);

        //get params
        Bundle bundle = getIntent().getExtras();
        sIdTransaksiStempel = bundle.getString("idtransaksistempel");
        sTipeStempel = bundle.getString("tipestempel");
        sUkuranStempel = bundle.getString("ukuranstempel");
        sQtyStempel = bundle.getString("qtystempel");
        sTotalAwalStempel = bundle.getString("rinciantotalawal");
        sGrandTotalStempel = bundle.getString("rinciangrandtotal");
        sBiayaLainStempel = bundle.getString("rincianbiayalain");
        sTipsStempel = bundle.getString("rinciantips");
        sDp = bundle.getString("rinciandp");
        sJasa = bundle.getString("rincianjasa");

        tDp.setText(sDp+" %");


        tTipeStempel.setText(sTipeStempel);
        tUkuranStempel.setText(sUkuranStempel);
        tQtyStempel.setText(sQtyStempel);

        rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
        String rupiahtotawal = rupiahFormat.format(Double.parseDouble(sTotalAwalStempel));
        String resulttotawal = Rupiah + " " + rupiahtotawal;
        tTotalAwalStemp.setText(resulttotawal);

        String biayalainnew = rupiahFormat.format(Double.parseDouble(sBiayaLainStempel));
        String resultbiayalainnew = Rupiah + " " + biayalainnew;
        tBiayaLainNew.setText(resultbiayalainnew);

        String rupiahGrandTot = rupiahFormat.format(Double.parseDouble(sGrandTotalStempel));
        String resultGrandTot = Rupiah + " " + rupiahGrandTot;
        tGrandTotalStemp.setText(resultGrandTot);

        String rupiahBiayaLain = rupiahFormat.format(Double.parseDouble(sJasa));
        String resultBiayalain = Rupiah + " " + rupiahBiayaLain;
        tBiayaLainStemp.setText(resultBiayalain);

        String rupiahTips = rupiahFormat.format(Double.parseDouble(sTipsStempel));
        String resultTips = Rupiah + " " + rupiahTips;
        tTipsStemp.setText(resultTips);

        //get id from pref
        getPref();
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    private void finishTransactionStempel(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id_transaksi_user", sIdTransaksiStempel);


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
                                Log.d("cobamsge", msge);
                                OrderStempelActivity.getInstance().finish();
                                finish();
                            } else {
                                //String konStatus = response.getString("status");
                                String msg = response.getString("message");
                                Toast.makeText(RincianTrxStempelActivity.this, msg, Toast.LENGTH_SHORT).show();
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
        if (v == bSimpan){
            finishTransactionStempel();
        }
    }
}
