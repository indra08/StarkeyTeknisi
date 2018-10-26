package id.starkey.mitra;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.starkey.mitra.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    GoogleMap map;
    LocationManager locationManager;
    private GoogleApiClient googleApiClient;
    static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMap();
    }

    private void initializeMap() {
        if (map == null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(MapsActivity.this);
            }

            // check if map is created successfully or not
            if (null == map) {
                //Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //Toast.makeText(this, "map ready", Toast.LENGTH_SHORT).show();
        //setUpMapIfNeeded();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
            return;
        }

        map.setMyLocationEnabled(true);
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null) {
                map.clear();
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                LatLng myPos = new LatLng(latti, longi);

                MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                Marker marker = map.addMarker(markerOptions);
                markerOptions.position(myPos);
                marker.setPosition(myPos);

                /*
                MarkerOptions markerOptions = new MarkerOptions().position(place.getLatLng());
                Marker marker = map.addMarker(markerOptions);
                marker.setPosition(place.getLatLng());
                 */

                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                //getDescLoc(latti, longi);

                //((EditText)findViewById(R.id.etLocationLat)).setText("Latitude: " + latti);
                //((EditText)findViewById(R.id.etLocationLong)).setText("Longitude: " + longi);
            } else {
                map.clear();
                //((EditText)findViewById(R.id.etLocationLat)).setText("Unable to find correct location.");
                //((EditText)findViewById(R.id.etLocationLong)).setText("Unable to find correct location. ");
                //double lattiElse = fromMainLat;
                //double longiElse = fromMainLng;
                Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                double latti = locationNet.getLatitude();
                double longi = locationNet.getLongitude();

                LatLng myPos = new LatLng(latti, longi);

                MarkerOptions markerOptions = new MarkerOptions().position(myPos);
                Marker marker = map.addMarker(markerOptions);
                markerOptions.position(myPos);
                marker.setPosition(myPos);

                //map.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17.0f));
                //getDescLoc(lattiElse, longiElse);
            }
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
}
