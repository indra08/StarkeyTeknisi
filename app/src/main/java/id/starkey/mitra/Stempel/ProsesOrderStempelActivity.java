package id.starkey.mitra.Stempel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

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

public class ProsesOrderStempelActivity extends AppCompatActivity implements
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback{

    private Button bPilihProd;
    GoogleMap map;
    private GoogleApiClient googleApiClient;
    static final int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proses_order_stempel);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarProsesOrderStempel);
        setSupportActionBar(toolbar);
        setTitle("Order Kunci");

        //init btn
        bPilihProd = findViewById(R.id.btnPilihProdusen);
        bPilihProd.setOnClickListener(this);

        googleApiClient = new GoogleApiClient.Builder(ProsesOrderStempelActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        initializeMap();
    }

    private void initializeMap() {
        if (map == null) {
            MapFragment mapFragment = (MapFragment)getFragmentManager()
                    .findFragmentById(R.id.mapProsesOrderStempel);
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
        getLocationDest();
    }

    private void getLocationDest() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            LatLng from = new LatLng(-7.0049918, 110.4551927);
            map.addMarker(new MarkerOptions().position(from).title("User").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_marker)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(from, 17));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocationDest();
                break;
            case 2:
                getLocationDest();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == bPilihProd){
            Intent tofix = new Intent(ProsesOrderStempelActivity.this, FixOrderStempelActivity.class);
            startActivity(tofix);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //googleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //googleApiClient.disconnect();
    }


}
