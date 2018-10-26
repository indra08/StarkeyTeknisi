package id.starkey.mitra.Home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
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
import id.starkey.mitra.Utilities.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Dani on 2/23/2018.
 */

public class FragmentHome extends Fragment implements
        View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        LocationListener{
    public FragmentHome(){}

    View vmenu;
    FragmentActivity fragmentActivity;
    GoogleMap map;
    Marker mPositionMarker;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    static final int REQUEST_LOCATION = 1;
    private Context mContext;
    private GPSTracker gps;
    private double latFromClass, lngFromClass;
    private String tokennyaUser;
    private TextView tSaldo, tPendapatan, tPenerimaan, tRating, tPembatalan;
    private NumberFormat rupiahFormat;
    private String Rupiah = "Rp.";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vmenu = inflater.inflate(R.layout.fragment_home, container, false);

        mContext = fragmentActivity;

        //init tv
        tSaldo = vmenu.findViewById(R.id.tvSaldo);
        tPendapatan = vmenu.findViewById(R.id.tvPendapatanMain);
        tPenerimaan = vmenu.findViewById(R.id.tvValuePenerimaan);
        tRating = vmenu.findViewById(R.id.tvValuePenilaian);
        tPembatalan = vmenu.findViewById(R.id.tvValuePembatalan);

        locationManager = (LocationManager)fragmentActivity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(fragmentActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        //from gpstracker class
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {
            //Toast.makeText(mContext,"You need have granted permission",Toast.LENGTH_SHORT).show();
            gps = new GPSTracker(mContext, getActivity());

            // Check if GPS enabled
            if (gps.canGetLocation()) {

                latFromClass = gps.getLatitude();
                lngFromClass = gps.getLongitude();

                // \n is for new line
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                gps.showSettingsAlert();
            }
        }

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMap();

        getPref();
        getPerformaMitra();

        return vmenu;
    }

    @Override
    public void onAttach(Context context) {
        fragmentActivity = (FragmentActivity)context;
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPerformaMitra();
    }

    private void getPref(){
        SharedPreferences custDetails = getActivity().getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String tokenId = custDetails.getString("tokenIdUser", "");
        tokennyaUser = tokenId;
    }

    private void getPerformaMitra(){
        final ProgressDialog loading = new ProgressDialog(getActivity());
        loading.setMessage("Mohon tunggu...");
        loading.setCancelable(false);
        loading.show();

        // Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();

        //JSONObject jalparam = new JSONObject(params);
        //Log.d("jalparamfb", jalparam.toString());

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.GET, ConfigLink.performa_mitra, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("performaMitra", response.toString());
                        loading.dismiss();
                        try {
                            JSONObject dataJO = response.getJSONObject("data");
                            String saldo = dataJO.getString("saldo");
                            String pendapatan = dataJO.getString("pendapatan");

                            String penerimaan = dataJO.getString("count_penerimaan");
                            String ratting = dataJO.getString("rating");
                            String pembatalan = dataJO.getString("pembatalan");

                            rupiahFormat = NumberFormat.getInstance(Locale.GERMANY);
                            String rupiah = rupiahFormat.format(Double.parseDouble(pendapatan));
                            String result = Rupiah + " " + rupiah;
                            tPendapatan.setText(result);

                            String rupiahsaldo = rupiahFormat.format(Double.parseDouble(saldo));
                            String resultsaldo = Rupiah + " " + rupiahsaldo;
                            tSaldo.setText(resultsaldo);

                            tPenerimaan.setText(penerimaan);
                            tRating.setText(ratting);
                            tPembatalan.setText(pembatalan);


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
                Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+tokennyaUser);
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
        RequestHandler.getInstance(getActivity()).addToRequestQueue(request_json);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
            case 2:
                getLocation();
                break;
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(fragmentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(fragmentActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(fragmentActivity, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                LatLng myPos = new LatLng(latti, longi);

                //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                //Marker marker = map.addMarker(markerOptions);
                //markerOptions.position(myPos);
                //marker.setPosition(myPos);

                /*
                MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng());
                Marker marker = map.addMarker(markerOptions);
                marker.setPosition(place.getLatLng());
                 */


                mPositionMarker = map.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_motor_teknisi))
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(location.getLatitude(), location
                                    .getLongitude())));


                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0.5f, new android.location.LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (mPositionMarker != null){
                            mPositionMarker.setPosition(currentLocation);
                        } else {
                            mPositionMarker = map.addMarker(new MarkerOptions()
                                    .flat(true)
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.ic_motor_teknisi))
                                    .anchor(0.5f, 0.5f)
                                    .position(currentLocation));
                        }

                        animateMarker(mPositionMarker, location); // Helper method for smooth
                        // animation

                        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location
                                .getLatitude(), location.getLongitude())));
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                animateMarker(mPositionMarker, location); // Helper method for smooth
                //getDescLoc(latti, longi);

                //((EditText)findViewById(R.id.etLocationLat)).setText("Latitude: " + latti);
                //((EditText)findViewById(R.id.etLocationLong)).setText("Longitude: " + longi);
                //Toast.makeText(fragmentActivity, "KONDISI GPS PROVIDER", Toast.LENGTH_SHORT).show();
            } else {
                map.clear();
                //((EditText)findViewById(R.id.etLocationLat)).setText("Unable to find correct location.");
                //((EditText)findViewById(R.id.etLocationLong)).setText("Unable to find correct location. ");
                //double lattiElse = fromMainLat;
                //double longiElse = fromMainLng;
                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (locationNet != null){
                    double latti = locationNet.getLatitude();
                    double longi = locationNet.getLongitude();

                    LatLng myPos = new LatLng(latti, longi);

                    mPositionMarker = map.addMarker(new MarkerOptions()
                            .flat(true)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_motor_teknisi))
                            .anchor(0.5f, 0.5f)
                            .position(
                                    new LatLng(locationNet.getLatitude(), locationNet
                                            .getLongitude())));

                    //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                    //Marker marker = map.addMarker(markerOptions);
                    //markerOptions.position(myPos);
                    //marker.setPosition(myPos);

                    //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                    animateMarker(mPositionMarker, locationNet); // Helper method for smooth
                    //getDescLoc(lattiElse, longiElse);
                    //Toast.makeText(fragmentActivity, "KONDISI NETWORK PROVIDER", Toast.LENGTH_SHORT).show();
                } else {
                    Location locationNetElse = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    LatLng myPos = new LatLng(latFromClass, lngFromClass);

                    mPositionMarker = map.addMarker(new MarkerOptions()
                            .flat(true)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_motor_teknisi))
                            .anchor(0.5f, 0.5f)
                            .position(
                                    new LatLng(latFromClass, lngFromClass)));

                    //MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                    //Marker marker = map.addMarker(markerOptions);
                    //markerOptions.position(myPos);
                    //marker.setPosition(myPos);

                    //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                    animateMarker(mPositionMarker, locationNetElse); // Helper method for smooth
                }

            }
        }
    }

    private void initializeMap() {
        if (map == null) {
            MapFragment mapFragment = (MapFragment)fragmentActivity.getFragmentManager()
                    .findFragmentById(R.id.mapMainMenu);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            // check if map is created successfully or not
            if (null == map) {
                //Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(fragmentActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(fragmentActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fragmentActivity.checkSelfPermission
                    (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && fragmentActivity.checkSelfPermission
                    (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
            return;
        }

        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        getLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void saveLocationGps(String lat, String lng){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConfigLink.locationbyGps, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latGps", lat);
        editor.putString("lngGps", lng);
        editor.commit();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (mPositionMarker != null){
            mPositionMarker.setPosition(currentLocation);
            String lat = String.valueOf(location.getLatitude());
            String lng = String.valueOf(location.getLongitude());
            saveLocationGps(lat, lng);
        } else {
            mPositionMarker = map.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_motor_teknisi))
                    .anchor(0.5f, 0.5f)
                    .position(currentLocation));
        }

        /*
        if (location == null)
            return;

        if (mPositionMarker == null) {

            mPositionMarker = map.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_q_med))
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(location.getLatitude(), location
                                    .getLongitude())));
        }
         */


        animateMarker(mPositionMarker, location); // Helper method for smooth
        // animation

        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location
                .getLatitude(), location.getLongitude())));

        //stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

    }

    public void animateMarker(final Marker marker, final Location location) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final LatLng startLatLng = marker.getPosition();
        final double startRotation = marker.getRotation();
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (location != null){
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);

                    double lng = t * location.getLongitude() + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * location.getLatitude() + (1 - t)
                            * startLatLng.latitude;

                    float rotation = (float) (t * location.getBearing() + (1 - t)
                            * startRotation);

                    marker.setPosition(new LatLng(lat, lng));
                    marker.setRotation(rotation);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
                /*
                else {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed
                            / duration);

                    double lng = t * lngFromClass + (1 - t)
                            * startLatLng.longitude;
                    double lat = t * latFromClass + (1 - t)
                            * startLatLng.latitude;

                    float rotation = (float) (t * location.getBearing() + (1 - t)
                            * startRotation);

                    marker.setPosition(new LatLng(lat, lng));
                    marker.setRotation(rotation);

                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
                 */

            }
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return true;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){
            map.clear();
//            double lat = location.getLatitude();
//            double lng = location.getLongitude();

            mPositionMarker = map.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_motor_teknisi))
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(location.getLatitude(), location
                                    .getLongitude())));
        } else {
            map.clear();
            //Toast.makeText(this, "Jaringan atau GPS anda lemah", Toast.LENGTH_SHORT).show();
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            double latNet = locationNet.getLatitude();
//            double lngNet = locationNet.getLongitude();
            mPositionMarker = map.addMarker(new MarkerOptions()
                    .flat(true)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.ic_motor_teknisi))
                    .anchor(0.5f, 0.5f)
                    .position(
                            new LatLng(locationNet.getLatitude(), locationNet
                                    .getLongitude())));

        }

        //double lat = fromMainLat;
        //double lng = fromMainLng;
        //getDescLoc(lat, lng);
        return false;
    }
}
