package id.starkey.mitra.JasaLain;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.starkey.mitra.BuildConfig;
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.History.HistoryActivity;
import id.starkey.mitra.JasaLain.Adapter.ListOrderJLAdapter;
import id.starkey.mitra.Login.LoginActivity;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.PedomanMitra.PedomanMitraActivity;
import id.starkey.mitra.R;
import id.starkey.mitra.Statistik.StatistikActivity;
import id.starkey.mitra.TransaksiBahan.TransaksiBahanActivity;
import id.starkey.mitra.UbahPassword.UbahPasswordActivity;
import id.starkey.mitra.Utilities.CustomItem;

public class HomeJasaLain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Context context;
    private NavigationView navigationView;
    private Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
    Toolbar toolbar;
    private TextView headerName, headerEmail, headerPhone;
    private String sFirebaseToken = "";
    private ListView lvOrder;
    private List<CustomItem> masterList = new ArrayList<>();
    private ListOrderJLAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_jasa_lain);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pesanan anda");
        context = this;

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //tv versioning
        String versionName = BuildConfig.VERSION_NAME;
        TextView tvVersion = navigationView.findViewById(R.id.lblVersionMain);
        tvVersion.setText("Versi "+versionName);

        //backlight selected item nav bar no color
        navigationView.setItemBackground(transparentDrawable);
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };
        int[] colors = new int[] {
                Color.BLACK,
                Color.RED,
                Color.GREEN,
                Color.BLUE
        };
        ColorStateList myList = new ColorStateList(states, colors);
        navigationView.setItemTextColor(myList);
        navigationView.setItemIconTintList(myList);

        //click navdraw header
        View headerview = navigationView.getHeaderView(0);
        headerview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //Toast.makeText(MainActivity.this, "header clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //init tv nav header
        headerName = headerview.findViewById(R.id.namaHeader);
        headerEmail = headerview.findViewById(R.id.emailHeader);
        headerPhone = headerview.findViewById(R.id.phoneHeader);

        //get detail from preference
        getPref();
        getFirebaseToken();
        initUI();
    }

    private void initUI() {

        lvOrder = (ListView) findViewById(R.id.lv_order);
        masterList = new ArrayList<>();
        adapter = new ListOrderJLAdapter((Activity) context, masterList);
        lvOrder.setAdapter(adapter);

        //dummy
        masterList.clear();
        masterList.add(new CustomItem("1", "2019-02-28 20:10:11", "Indra", "Potong Rambut, Smoothing", "50000", "12 KM, Jl. Majapahit No. 53"));
        masterList.add(new CustomItem("2", "2019-03-01 20:10:11", "Maul", "Potong Rambut", "50000", "11 KM, Jl. Majapahit No. 53"));
        masterList.add(new CustomItem("3", "2019-03-02 20:10:11", "Lana", "Smoothing", "50000", "13 KM, Jl. Majapahit No. 53"));
        masterList.add(new CustomItem("4", "2019-03-03 20:10:11", "Husni", "Creambath", "50000", "10 KM, Jl. Majapahit No. 53"));
        masterList.add(new CustomItem("5", "2019-03-04 20:10:11", "Muba", "Potong Rambut", "50000", "12 KM, Jl. Majapahit No. 53"));

        adapter.notifyDataSetChanged();
    }

    private void getFirebaseToken(){
        //SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        SharedPreferences tokenfirebaseuser = getSharedPreferences(ConfigLink.firebasePref, MODE_PRIVATE);
        sFirebaseToken = tokenfirebaseuser.getString("firebaseUser", "");
    }

    private void getPref(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String nama = custDetails.getString("namaUser", "");
        String email = custDetails.getString("emailUser", "");
        String hp = custDetails.getString("phoneUser", "");
        String tokenId = custDetails.getString("tokenIdUser", "");
        String role = custDetails.getString("roleUser", "");
        headerName.setText(nama);
        headerEmail.setText(email);
        headerPhone.setText(hp);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(id);

        return true;
    }

    private void displaySelectedScreen(int id){

        //initializing the fragment object which is selected
        switch (id){
            case R.id.nav_aturan_toko:
                Intent intenSyarat = new Intent(context, TransaksiBahanActivity.class);
                startActivity(intenSyarat);
                break;
            case R.id.nav_history:
                Intent intent = new Intent(context, HistoryJasaLain.class);
                startActivity(intent);
                break;
            case R.id.nav_statistik:
                Intent intenBan = new Intent(context, StatistikActivity.class);
                startActivity(intenBan);
                break;
            case R.id.nav_ubahpass:
                Intent intentUbahpass = new Intent(context, UbahPasswordActivity.class);
                startActivity(intentUbahpass);
                break;
            case R.id.nav_pedoman:
                Intent intenPenga = new Intent(context, PedomanMitraActivity.class);
                startActivity(intenPenga);
                break;
            case R.id.nav_logout:
                askLogout();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void askLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan Logout?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                clearAttributeUser();
                                Intent aw = new Intent(context, LoginActivity.class);
                                startActivity(aw);
                                finish();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void askExitApps() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Anda yakin akan keluar aplikasi?")
                .setCancelable(false)
                .setPositiveButton("Ya",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){

                                finish();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Batal",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void clearAttributeUser(){
        SharedPreferences sharedPreferences = getSharedPreferences(ConfigLink.loginPref, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            askExitApps();
        }

    }
}
