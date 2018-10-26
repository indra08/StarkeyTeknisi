package id.starkey.mitra.History.Selesai;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dani on 2/26/2018.
 */

public class SelesaiFragment extends Fragment {
    public SelesaiFragment(){}

    View vmenu;
    private JSONObject jsonBody;
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private List<ListItemSelesai> listItemSelesais;
    private String tokennyaMitra;
    private RelativeLayout relativeLayoutKosong, relativeLayoutAda;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //vmenu = inflater.inflate(R.layout.fragment_dalam_proses, container, false);
        vmenu = inflater.inflate(R.layout.fragment_selesai, container, false);

        //init rv selesai
        recyclerView = vmenu.findViewById(R.id.recyclerViewHistorySelesai);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        listItemSelesais = new ArrayList<>();

        //init rlayout
        relativeLayoutAda = vmenu.findViewById(R.id.layoutAdaSelesai);
        relativeLayoutKosong = vmenu.findViewById(R.id.layoutKosongSelesai);


        getPref();
        statusTrxSelesai();
        return vmenu;
    }

    private void getPref() {
        SharedPreferences custDetails = getActivity().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    private void statusTrxSelesai(){
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.history_selesai, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        listItemSelesais.clear();
                        loading.dismiss();
                        Log.d("historyjal", response.toString());
                        try{
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray.length() == 0){
                                relativeLayoutKosong.setVisibility(View.VISIBLE);
                                relativeLayoutAda.setVisibility(View.INVISIBLE);
                                //Toast.makeText(getActivity(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                            } else {
                                relativeLayoutKosong.setVisibility(View.INVISIBLE);
                                relativeLayoutAda.setVisibility(View.VISIBLE);
                                //Log.d("messproses", jsonArray.toString());
                                for (int x=0; x<jsonArray.length(); x++){

                                    JSONObject obj = jsonArray.getJSONObject(x);
                                    //String nama = obj.getString("type_kunci");
                                    ListItemSelesai itemSelesai = new ListItemSelesai(
                                            obj.getString("id"),
                                            obj.getString("id_layanan"),
                                            obj.getString("nama_layanan"),
                                            obj.getString("id_biaya_layanan"),
                                            obj.getString("jenis_item"),
                                            obj.getString("total_biaya"),
                                            obj.getString("saldo_terakhir"),
                                            obj.getString("tanggal"),
                                            obj.getString("status_code"),
                                            obj.getString("status"),
                                            obj.getString("on_process"),
                                            obj.getString("is_rated")
                                    );
                                    listItemSelesais.add(itemSelesai);
                                }
                                adapter = new RVSelesai(listItemSelesais, getContext());
                                recyclerView.setAdapter(adapter);
                            }

                        }catch(JSONException ex){
                            ex.printStackTrace();
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
                Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
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
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request_json);
    }

}
