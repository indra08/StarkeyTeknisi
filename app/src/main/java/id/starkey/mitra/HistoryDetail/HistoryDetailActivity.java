package id.starkey.mitra.HistoryDetail;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class HistoryDetailActivity extends AppCompatActivity {

    private String tokennyaMitra, sIdTrx;
    private TextView tHargaItem, tJasa, tBiayaLain, tTips, tGrandTotal, tAlamatOrder, tTgl, tId, tTypeItemJenis;
    private TextView tNamaPelanggan, tKomenPelanggan, tShareProfit, tSaldo;
    private MaterialRatingBar ratingBar;
    //private String sNamaPelanggan, sKomenPelanggan, sShareProfit, sSaldo, sRatingBar;
    NumberFormat rupiahFormat;
    String Rupiah = "Rp.";
    private CircularImageView circularImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_detail);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetailHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Detail History");

        //get params from bundle
        Bundle bundle = getIntent().getExtras();
        sIdTrx = bundle.getString("idtransaksi");

        //init tv
        tTypeItemJenis = findViewById(R.id.tvTypeItemJenis);
        tHargaItem = findViewById(R.id.tvHargaItem);
        tJasa = findViewById(R.id.tvJasa);
        tBiayaLain = findViewById(R.id.tvBiayaLainDetailHistory);
        tTips = findViewById(R.id.tvTipsDetailHistory);
        tGrandTotal = findViewById(R.id.tvGrandTotalDetailHistory);
        tAlamatOrder = findViewById(R.id.tvAlamatDetailHistory);
        tTgl = findViewById(R.id.tvTglHistoryDetail);
        tId = findViewById(R.id.tvIdHistoryDetail);
        tId.setText("Order ID: "+sIdTrx);

        tNamaPelanggan = findViewById(R.id.tvNamaPelanggan);
        tKomenPelanggan = findViewById(R.id.tvKomenPelanggan);
        tShareProfit = findViewById(R.id.tvShareProfitHistoryDetail);
        tSaldo = findViewById(R.id.tvSaldoHistoryDetail);

        ratingBar = findViewById(R.id.ratingbarHistoryDetail);

        //init cv
        circularImageView = findViewById(R.id.circularIVHistoryDetail);


        getPref();
        getDetailTrx();
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void getDetailTrx() {
        final ProgressDialog loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Mohon tunggu...");
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        HashMap<String, String> params = new HashMap<String, String>();

        //String URL_DETAIL = "https://api.starkey.id/api/transaction/user/"+sIdTrx;

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.detail_history+sIdTrx, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadingDialog.dismiss();
                        Log.d("detailHistory", response.toString());

                        try {
                            //Process os success response
                            JSONObject joData = response.getJSONObject("data");
                            Log.d("joDetailHistory", joData.toString());
                            String type = joData.getString("type_item");
                            String jenis = joData.getString("jenis");

                            tTypeItemJenis.setText(type+" - "+jenis);

                            String tanggalOri = joData.getString("waktu_selesai");
                            String formatTgl = tanggalOri.substring(0, Math.min(tanggalOri.length(), 10));
                            String [] formatbaruTgl = formatTgl.split("-");
                            String fixtanggal = formatbaruTgl[2]+"-"+formatbaruTgl[1]+"-"+formatbaruTgl[0];
                            tTgl.setText(fixtanggal);


                            //String hargaitem = joData.getString("harga_item");
                            String totalawal = joData.getString("total_awal");
                            //String jasa = joData.getString("biaya_layanan");
                            String biayalain = joData.getString("biaya_lain");
                            String tips = joData.getString("tips");
                            String grandtotal = joData.getString("total_akhir");
                            String alamatnya = joData.getString("nama_lokasi");
                            String namapelanggan = joData.getString("nama_user");
                            String komenpelanggan = joData.getString("review");
                            String shareprofit = joData.getString("share_profit");
                            String saldo = joData.getString("saldo_terakhir");
                            String diratingpelanggan = joData.getString("d_rating");
                            String fotopelanggan = joData.getString("user_photo_profile");

                            //fot jasa
                            String biayadasar= joData.getString("biaya_dasar");
                            String tambahanbiayadasar = joData.getString("tambahan_biaya_antar");
                            int biayadasarint = Integer.parseInt(biayadasar);
                            int tambahanbiayadasarint = Integer.parseInt(tambahanbiayadasar);
                            int biayajasa = biayadasarint + tambahanbiayadasarint;
                            String fixbiayajasa = String.valueOf(biayajasa);

                            tNamaPelanggan.setText(namapelanggan);
                            tKomenPelanggan.setText(komenpelanggan);

                            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);

                            String rupiahSaldo = rupiahFormat.format(Double.parseDouble(saldo));
                            String resultSaldo = Rupiah + " " + rupiahSaldo;
                            tSaldo.setText(resultSaldo);

                            String rupiahShareProfit = rupiahFormat.format(Double.parseDouble(shareprofit));
                            String resultShareProfit = Rupiah + " " + rupiahShareProfit;
                            tShareProfit.setText(resultShareProfit);

                            String rupiahHargaitem = rupiahFormat.format(Double.parseDouble(totalawal));
                            String resultHargaItem = Rupiah + " " + rupiahHargaitem;
                            tHargaItem.setText(resultHargaItem);

                            String rupiahJasa = rupiahFormat.format(Double.parseDouble(fixbiayajasa));
                            String resultJasa = Rupiah + " " + rupiahJasa;
                            tJasa.setText(resultJasa);

                            String rupiahBiayaLain = rupiahFormat.format(Double.parseDouble(biayalain));
                            String resultBiayaLain = Rupiah + " " + rupiahBiayaLain;
                            tBiayaLain.setText(resultBiayaLain);

                            String rupiahTips = rupiahFormat.format(Double.parseDouble(tips));
                            String resultTips = Rupiah + " " + rupiahTips;
                            tTips.setText(resultTips);

                            String rupiahGrandTotal = rupiahFormat.format(Double.parseDouble(grandtotal));
                            String resultGrandTotal = Rupiah + " " + rupiahGrandTotal;
                            tGrandTotal.setText(resultGrandTotal);

                            tAlamatOrder.setText(alamatnya);



                            Float ratingnya =Float.parseFloat(diratingpelanggan);

                            ratingBar.setRating(ratingnya);

                            Picasso.with(getApplicationContext())
                                    .load(fotopelanggan)
                                    .placeholder(R.drawable.progress_animation)
                                    .into(circularImageView);

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
}
