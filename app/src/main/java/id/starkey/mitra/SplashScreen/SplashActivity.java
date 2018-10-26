package id.starkey.mitra.SplashScreen;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
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
import id.starkey.mitra.BuildConfig;
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.Login.LoginActivity;
import id.starkey.mitra.R;

import id.starkey.mitra.RequestHandler;
import id.starkey.mitra.Utilities.WebFitWidth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private String flagChangelog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        maintenanceApp();
    }

    private void setFlagChangelog(String flage){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(ConfigLink.countPrefChangeLog, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("flag", flage);
        editor.commit();
    }

    private void getFlagChangelog(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.countPrefChangeLog, MODE_PRIVATE);
        flagChangelog = custDetails.getString("flag", "0");

    }

    private void setClearFlagChangelog(){
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigLink.countPrefChangeLog, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    private void maintenanceApp(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        HashMap<String, String> params = new HashMap<String, String>();

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.maintenance_status, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("cekceklagi", response.toString());

                        try {
//                            String statuse = response.getString("maintenance_status");
//                            Log.d("statusebro", statuse);
                            boolean isMaintenance = response.getBoolean("maintenance_status");
                            if (isMaintenance){
                                //true

                                //set pref changelog 0
                                setFlagChangelog("0");

                                JSONObject jsonData = response.getJSONObject("data");
                                String dari = jsonData.getString("dari");
                                String sampai = jsonData.getString("sampai");
                                String ket = jsonData.getString("keterangan");

                                dialogMaintenance(dari, sampai, ket);



                            } else {
                                //false
                                //eksekusi changelog

                                getFlagChangelog();
                                if (flagChangelog.equals("0")){
                                    getChangelogInfo();
                                } else {
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        } catch (JSONException ex){
                            ex.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Authentification Failed";
                } else if (error instanceof ParseError) {
                    message = "Parsing data Error";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut";
                }
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        });

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

    private void dialogMaintenance(String infodari, String infosampai, String infoket){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Maintenance Aplikasi");
        alertDialogBuilder.setMessage("Dari "+infodari+"\n"+"Sampai "+infosampai+"\n"+infoket)
                .setCancelable(false)
                .setPositiveButton("Oke",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                finish();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void getChangelogInfo(){
        final ProgressDialog loading = new ProgressDialog(this);
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        HashMap<String, String> params = new HashMap<String, String>();

        String versionName = BuildConfig.VERSION_NAME;
        Log.d("stringVerName", versionName);
        String jenisApp = "mitra";

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET,
                ConfigLink.changeLogInfo+"v_app="+versionName+"&jenis_app="+jenisApp
                , new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading.dismiss();
                        Log.d("hasilChangeLog", response.toString());
                        setClearFlagChangelog();
                        setFlagChangelog("1");
                        try {
                            String status = response.getString("status");
                            if (status.equals("success")){
                                //there is changelog
                                JSONObject joData = response.getJSONObject("data");
                                String vapp = joData.getString("v_app");
                                String jenis_app = joData.getString("jenis_app");
                                String judul = joData.getString("judul");
                                String keterangan_awal = joData.getString("keterangan_awal");
                                String point_detail = joData.getString("point_detail");
                                String keterangan_akhir = joData.getString("keterangan_akhir");

                                popupChangeLog(point_detail, vapp, judul, keterangan_awal, keterangan_akhir);
                            } else {
                                //there is no changelog
                                //status : not_found
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }catch (JSONException ex){
                            ex.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Tidak ada koneksi Internet";
                } else if (error instanceof ServerError) {
                    message = "Server tidak ditemukan";
                } else if (error instanceof AuthFailureError) {
                    message = "Authentification Failed";
                } else if (error instanceof ParseError) {
                    message = "Parsing data Error";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut";
                }
                Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
            }
        });

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void popupChangeLog(String pointHtml, String svApps, String sJudul, String sKetAwal, String sKetAkhir){
        final Dialog dialog = new Dialog(this);
        //dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_changelog);
        dialog.setCancelable(false);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER;
        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);

        dialog.show();

        TextView tvapps = dialog.findViewById(R.id.lblvAppChangeLog);
        TextView judule = dialog.findViewById(R.id.lblJudulChangeLog);
        TextView ketawal = dialog.findViewById(R.id.lblKetAwalChangeLog);
        TextView ketakhir = dialog.findViewById(R.id.lblKetAkhirChangeLog);
        WebView webView = dialog.findViewById(R.id.webViewChangeLog);

        tvapps.setText(svApps);
        judule.setText(sJudul);
        ketawal.setText(sKetAwal);
        ketakhir.setText(sKetAkhir);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setBackgroundColor(Color.argb(1, 0, 0, 0));
        WebFitWidth WFW = new WebFitWidth();
        webView.loadData(WFW.changedHeaderHtml(pointHtml), "text/html; charset=utf-8", null);

        Button buttonCl = dialog.findViewById(R.id.btnOkeChangeLog);
        buttonCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
