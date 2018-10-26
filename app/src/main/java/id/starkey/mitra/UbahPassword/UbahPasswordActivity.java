package id.starkey.mitra.UbahPassword;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UbahPasswordActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText ePassLama, ePassBaru, eCPassBaru;
    private Button bSimpanPass;
    private String tokennyaMitra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubah_password);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarChangepass);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Ubah Password");
        /*
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleTextColor(Color.parseColor("#000000"));
         */

        //init et
        ePassLama = findViewById(R.id.etPassLama);
        ePassBaru = findViewById(R.id.edtPassBaru);
        eCPassBaru = findViewById(R.id.edtRePassBaru);

        bSimpanPass = findViewById(R.id.btnChangepass);
        bSimpanPass.setOnClickListener(this);

        getPref();
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    @Override
    public void onClick(View v) {
        if (v == bSimpanPass){
            String oldpas = ePassLama.getText().toString();
            String nyupas = ePassBaru.getText().toString();
            String cnyupas = eCPassBaru.getText().toString();

            if (oldpas.isEmpty() || nyupas.isEmpty() || cnyupas.isEmpty()){
                Toast.makeText(this, "Pastikan semua kolom terisi", Toast.LENGTH_SHORT).show();
            } else {
                updatePassword(oldpas, nyupas, cnyupas);
            }
        }
    }

    private void updatePassword(String oldPass, String nyuPass, String cNyupass){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("old_password", oldPass);
        params.put("new_password", nyuPass);
        params.put("c_new_password", cNyupass);


        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.PATCH, ConfigLink.ubah_password, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        try {
                            //Process os success response
                            String msg = response.getString("message");
                            Toast.makeText(UbahPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                VolleyLog.e("Err Volley: ", error.getMessage());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
