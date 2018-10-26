package id.starkey.mitra.Stempel.PilihProdusenStemp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
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
import com.android.volley.toolbox.Volley;
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PilihProdusenStempelActivity extends AppCompatActivity implements View.OnClickListener {

    private String tokennyaMitra;
    private JSONObject jsonBody;

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private List<ListItemPilihProdusenStempel> listItemPilihProdusenStempels;

    private RVProdusenStempel mAdapter;

    private Button bPilihProdusen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_produsen_stempel);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPilihProdusenStempel);
        setSupportActionBar(toolbar);
        setTitle("Produsen Stempel");

        //init btn
        bPilihProdusen = findViewById(R.id.btnPilihProdusenStempel);
        bPilihProdusen.setOnClickListener(this);

        //init rv produsen stempel
        recyclerView = findViewById(R.id.recyclerViewProdusenStempel);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItemPilihProdusenStempels = new ArrayList<>();


        //get id from pref
        getPref();
        getProdusenStempel();
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    private void getProdusenStempel(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        //Log.d("urlCombo", URLMerkLain);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, ConfigLink.produsen_stempel, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listItemPilihProdusenStempels.clear();
                        loading.dismiss();
                        try{
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() == 0){
                                //relativeLayoutKosong.setVisibility(View.VISIBLE);
                                //relativeLayoutAda.setVisibility(View.INVISIBLE);
                            } else {
                                //relativeLayoutKosong.setVisibility(View.INVISIBLE);
                                //relativeLayoutAda.setVisibility(View.VISIBLE);
                                for (int x=0; x<jsonArray.length(); x++){

                                    JSONObject obj = jsonArray.getJSONObject(x);
                                    //String nama = obj.getString("type_kunci");
                                    ListItemPilihProdusenStempel itemKunciLain = new ListItemPilihProdusenStempel(
                                            obj.getString("id"),
                                            obj.getString("nama"),
                                            obj.getString("alamat"),
                                            obj.getString("lat"),
                                            obj.getString("long"),
                                            obj.getString("email"),
                                            obj.getString("keterangan")
                                    );
                                    listItemPilihProdusenStempels.add(itemKunciLain);
                                }
                                //adapter = new RVKunciMotor(listItemKunciMotors, getContext());
                                //recyclerView.setAdapter(adapter);

                                mAdapter = new RVProdusenStempel(listItemPilihProdusenStempels, getApplicationContext());
                                recyclerView.setAdapter(mAdapter);

                                //mAdapter = new RVKunciLain(listItemKunciLains, getContext());
                                //recyclerView.setAdapter(mAdapter);
                            }

                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                error.printStackTrace();
                //Log.e("TAG", error.getMessage(), error);
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
                Toast.makeText(PilihProdusenStempelActivity.this,message, Toast.LENGTH_LONG).show();
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
        jsonObjectRequest.setRetryPolicy(policy);
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onClick(View v) {
        if (v == bPilihProdusen){
            int posisi = mAdapter.getPosisi();
            if (posisi != -1){
                Intent resulIntent = new Intent();
                String idprodusen = listItemPilihProdusenStempels.get(posisi).getId(); //send id
                resulIntent.putExtra("idprodusenstempel", idprodusen);
                String latprod = listItemPilihProdusenStempels.get(posisi).getLat();
                resulIntent.putExtra("latitudeprodusenstempel", latprod);
                String lngprod = listItemPilihProdusenStempels.get(posisi).getLng();
                resulIntent.putExtra("longitudeprodusenstempel", lngprod);

                setResult(110, resulIntent);
                finish();
            } else {
                Toast.makeText(this, "Silahkan pilih produsen stempel", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
