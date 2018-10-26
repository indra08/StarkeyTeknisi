package id.starkey.mitra.TransaksiBahan.DetailTransaksiBahan;

import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DetailTransaksiBahanActivity extends AppCompatActivity {

    private String sIdtransaksi, sTgltransasi, sTotaltransaksi, sStatustransaksi;
    private AsyncHttpClient client;
    private LinearLayout framePulldown, layoutKosong;
    private TextView lblPullInfo, tTglTrans, tStatusTrans, tTotalTrans;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private String tokennyaMitra;
    private List<ListItemDetailTransaksiBahan> listItemDetailTransaksiBahans;
    /*
    private RVTransaksiBahan rvTransaksiBahan;
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi_bahan);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarDetailTransaksiBahan);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Detail");

        //init tv
        tTglTrans = findViewById(R.id.tvTglDetBahan);
        tStatusTrans = findViewById(R.id.tvStatusDetBahan);
        tTotalTrans = findViewById(R.id.tvTotalDetBahan);

        getPref();

        //get params
        Bundle bundle = getIntent().getExtras();
        sIdtransaksi = bundle.getString("idtrans");
        sTgltransasi = bundle.getString("tanggal");
        sTotaltransaksi = bundle.getString("total");
        sStatustransaksi = bundle.getString("status");

        //set string to tv
        tTglTrans.setText(sTgltransasi);
        tStatusTrans.setText(sStatustransaksi);
        tTotalTrans.setText(sTotaltransaksi);

        listItemDetailTransaksiBahans = new ArrayList<>();
        framePulldown = findViewById(R.id.framePulldownDetTrxBahan);
        layoutKosong = findViewById(R.id.layoutKosongDetTrxBahan);
        lblPullInfo = findViewById(R.id.lblPullInfoDetTrxBahan);
        refreshLayout = findViewById(R.id.refreshLayoutDetTrxBahan);
        recyclerView = findViewById(R.id.recyclerViewDetTrxBahan);

        refreshLayout.setColorSchemeResources(R.color.red, R.color.orange, R.color.green_bright, R.color.blue);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDetailTransaksiBahan(tokennyaMitra, sIdtransaksi);
            }
        });

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        getDetailTransaksiBahan(tokennyaMitra, sIdtransaksi);
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    private void getDetailTransaksiBahan(String token, String idtrans){
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });

        client = new AsyncHttpClient();
        client.setTimeout(60000);
        client.addHeader("Authorization", "Bearer " + token);
        //RequestParams params = new RequestParams();
        //params.put("Authorization", "Bearer " + token);
        client.get(this, ConfigLink.detail_transaksi_bahan + idtrans, null, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                refreshLayout.setRefreshing(false);
                framePulldown.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                lblPullInfo.setText("Error: " + String.valueOf(statusCode) + " " + getResources().getString(R.string.error_request));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                refreshLayout.setRefreshing(false);
                framePulldown.setVisibility(View.GONE);
                listItemDetailTransaksiBahans.clear();
                Log.d("suksesDetailBahan", responseString);

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONObject joData = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = joData.getJSONArray("detail");
                    Log.d("arraydetail", jsonArray.toString());
                    if (jsonArray.length() == 0){
                        layoutKosong.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        layoutKosong.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);

                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject obj = jsonArray.getJSONObject(i);
                            ListItemDetailTransaksiBahan item = new ListItemDetailTransaksiBahan(
                                    obj.getString("id"),
                                    obj.getString("id_barang"),
                                    obj.getString("nama_barang"),
                                    obj.getString("jumlah"),
                                    obj.getString("harga"),
                                    obj.getString("subtotal")
                            );
                            listItemDetailTransaksiBahans.add(item);
                        }
                        adapter = new RVDetailTransaksiBahan(listItemDetailTransaksiBahans, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }

                } catch (JSONException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
