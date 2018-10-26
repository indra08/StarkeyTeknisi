package id.starkey.mitra.Service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import id.starkey.mitra.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dani on 4/16/2018.
 */

public class IntervalService extends Service {

    private Handler h = new Handler();
    private Runnable runnable;
    private int delay = 5*1000; //1 second=1000 milisecond, 5*1000=5seconds
    private Context mContext;
    LocationManager locationManager;
    static final int REQUEST_LOCATION = 1;
    private Activity activity;
    private String tokennyaMitra, sLat, sLng, sFirebaseToken;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
    @Override
    public void onCreate() {
        runnable = new Runnable() {
            @Override
            public void run() {
                f();
                h.postDelayed(this, delay);
            }
        };
        h.postDelayed(runnable, delay);
    }
     */


    public void f(){
        Toast t = Toast.makeText(this, "Service is still running", Toast.LENGTH_SHORT);
        t.show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        //get detail from preference
        getPref();
        getFirebaseToken();

        runnable = new Runnable() {
            @Override
            public void run() {
                runnable = this;
                h.postDelayed(runnable, delay);
                //f();
                getLocation();
            }
        };

        h.postDelayed(runnable, delay);

        /*
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                runnable = this;
                h.postDelayed(runnable, delay);
                Toast.makeText(IntervalService.this, "from service", Toast.LENGTH_SHORT).show();
            }
        }, delay);
         */


        return START_STICKY;
    }

    private void getPref() {
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        tokennyaMitra = custDetails.getString("tokenIdUser", "");
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");
    }

    private void getLocation() {

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mContext = activity;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                //map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                sLat = String.valueOf(latti);
                sLng = String.valueOf(longi);

                //LatLng myPos = new LatLng(latti, longi);
                //saveLocationGps(sLat, sLng);
                //Toast.makeText(IntervalService.this, "Your current location \n"+ latti + " "+longi, Toast.LENGTH_SHORT).show();
                updatePosisiMitra("1", sLat, sLng, sFirebaseToken);
            }
            /*
            else {
                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (locationNet != null){
                    double lattiNet = locationNet.getLatitude();
                    double longiNet = locationNet.getLongitude();
                    //sLat = String.valueOf(lattiNet);
                    //sLng = String.valueOf(longiNet);
                    //LatLng myPos = new LatLng(latti, longi);
                    //saveLocationNetwork(sLat, sLng);
                }
            }
             */

        }
    }

    private void updatePosisiMitra(String avail, String latnya, String lngnya, String fbaseToken){
        //final ProgressDialog loading = new ProgressDialog(this);
        //loading.setMessage("Mohon tunggu...");
        //loading.setCancelable(false);
        //loading.show();

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
                        Log.d("bgserv", response.toString());
                        try {
                            //Process os success response
                            //loading.dismiss();
                            String hasil = response.getString("message");
                            //Log.d("hasilupdate", hasil);
                            //Toast.makeText(IntervalService.this, hasil, Toast.LENGTH_SHORT).show();

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
                //Toast.makeText(IntervalService.this,message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaMitra);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        h.removeCallbacksAndMessages(null);
    }
}
