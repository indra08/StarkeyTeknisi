package id.starkey.mitra.TransaksiBahan;

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

public class TransaksiBahanActivity extends AppCompatActivity {

    private AsyncHttpClient client;
    private LinearLayout framePulldown, layoutKosong;
    private TextView lblPullInfo;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RVTransaksiBahan rvTransaksiBahan;
    private List<ListItemTransaksiBahan> listItemTransaksiBahans;
    private String tokennyaMitra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_bahan);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarTransaksiBahan);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Transaksi Bahan");

        getPref();

        listItemTransaksiBahans = new ArrayList<>();

        framePulldown = findViewById(R.id.framePulldown);
        layoutKosong = findViewById(R.id.layoutKosongTrxBahan);
        lblPullInfo = findViewById(R.id.lblPullInfo);
        refreshLayout = findViewById(R.id.refreshLayoutTrxBahan);
        recyclerView = findViewById(R.id.recyclerViewTrxBahan);

        refreshLayout.setColorSchemeResources(R.color.red, R.color.orange, R.color.green_bright, R.color.blue);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTransaksiBahan(tokennyaMitra);
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //newsAdapter = new NewsAdapter(getActivity(), newsList);
        //recyclerView.setAdapter();



        getTransaksiBahan(tokennyaMitra);
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

    private void getTransaksiBahan(String token){
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
        client.get(this, ConfigLink.transaksi_bahan, null, new TextHttpResponseHandler() {
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
                listItemTransaksiBahans.clear();
                Log.d("suksesBahan", responseString);

                try {
                    JSONObject jsonObject = new JSONObject(responseString);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (jsonArray.length() == 0){
                        layoutKosong.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        layoutKosong.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);

                        for (int x=0; x<jsonArray.length(); x++){
                            JSONObject obj = jsonArray.getJSONObject(x);
                            ListItemTransaksiBahan itemTransaksiBahan = new ListItemTransaksiBahan(
                                    obj.getString("id"),
                                    obj.getString("tanggal"),
                                    obj.getString("total"),
                                    obj.getString("status")
                            );
                            listItemTransaksiBahans.add(itemTransaksiBahan);

                        }
                        adapter = new RVTransaksiBahan(listItemTransaksiBahans, getApplicationContext());
                        recyclerView.setAdapter(adapter);

                    }
                } catch (JSONException ex){
                    ex.printStackTrace();
                }
            }
        });
    }
}
