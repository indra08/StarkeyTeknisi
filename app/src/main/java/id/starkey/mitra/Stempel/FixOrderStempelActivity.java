package id.starkey.mitra.Stempel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import id.starkey.mitra.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class FixOrderStempelActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap map;
    private GoogleApiClient googleApiClient;
    static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix_order_stempel);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarFixOrderStempel);
        setSupportActionBar(toolbar);
        setTitle("Order Stempel");

        googleApiClient = new GoogleApiClient.Builder(FixOrderStempelActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMap();
    }

    private void initializeMap() {
        if (map == null) {
            MapFragment mapFragment = (MapFragment)getFragmentManager()
                    .findFragmentById(R.id.mapFixOrderStempel);
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
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission
                    (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        2);
            }
            return;
        }

        getLocationStart();
    }

    private void getLocationStart() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            //LatLng from = new LatLng(-7.0049918, 110.4551927);
            //map.addMarker(new MarkerOptions().position(from).title("Pengguna").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker)));
            //map.moveCamera(CameraUpdateFactory.newLatLng(from));

            LatLng sydney = new LatLng(-7.01629, 110.4631213);
            map.addMarker(new MarkerOptions().position(sydney).title("Kurnia Stempel").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker_biru)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 17));
        }
    }

    @Override
    public void onClick(View v) {

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
}
