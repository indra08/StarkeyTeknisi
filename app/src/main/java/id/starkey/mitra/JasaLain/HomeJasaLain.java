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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.starkey.mitra.BuildConfig;
import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.History.HistoryActivity;
import id.starkey.mitra.JasaLain.Adapter.ListOrderJLAdapter;
import id.starkey.mitra.Login.LoginActivity;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.PedomanMitra.PedomanMitraActivity;
import id.starkey.mitra.R;
import id.starkey.mitra.RequestHandler;
import id.starkey.mitra.Statistik.StatistikActivity;
import id.starkey.mitra.TransaksiBahan.TransaksiBahanActivity;
import id.starkey.mitra.UbahPassword.UbahPasswordActivity;
import id.starkey.mitra.Utilities.CustomItem;
import id.starkey.mitra.Utilities.ItemValidation;
import id.starkey.mitra.Utilities.SessionManager;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class HomeJasaLain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Context context;
    private NavigationView navigationView;
    private Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
    private Toolbar toolbar;
    private TextView headerName, headerEmail, headerPhone;
    private String sFirebaseToken = "";
    private ListView lvOrder;
    private List<CustomItem> masterList = new ArrayList<>();
    private ListOrderJLAdapter adapter;
    private int start = 0, count = 10;
    private boolean isLoading = false;
    private View footerList;
    private SessionManager session;
    private ItemValidation iv = new ItemValidation();
    private SwipeRefreshLayout srlData;
    public static boolean isOrderLain = false;
    private TextView tvSaldo, tvRating;
    private MaterialRatingBar rbMitra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_jasa_lain);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Pesanan anda");
        context = this;
        session = new SessionManager(context);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if(!session.getRole().equals("3")){ // mitra jasa kunci dan stample

            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
            finish();
        }

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

    @Override
    protected void onResume() {
        super.onResume();

        if(isOrderLain){

            isOrderLain = false;
            Intent intent = new Intent(context, HistoryJasaLain.class);
            startActivity(intent);
        }else{

            initData();
        }
    }

    private void initUI() {

        lvOrder = (ListView) findViewById(R.id.lv_order);
        srlData = (SwipeRefreshLayout) findViewById(R.id.sfl_data);
        masterList = new ArrayList<>();
        isLoading = false;
        start = 0;
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footerList = li.inflate(R.layout.footer_list, null);
        tvSaldo = (TextView) findViewById(R.id.tv_saldo);
        rbMitra = (MaterialRatingBar) findViewById(R.id.rb_mitra);
        tvRating = (TextView) findViewById(R.id.tv_rating);

        lvOrder.addFooterView(footerList);
        adapter = new ListOrderJLAdapter((Activity) context, masterList);
        lvOrder.removeFooterView(footerList);
        lvOrder.setAdapter(adapter);

        lvOrder.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

                int threshold = 1;
                int total = lvOrder.getCount();

                if (i == SCROLL_STATE_IDLE) {
                    if (lvOrder.getLastVisiblePosition() >= total - threshold && !isLoading) {

                        isLoading = true;
                        start += count;
                        initData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

        lvOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CustomItem item = (CustomItem) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(context, DetailOrerJL.class);
                intent.putExtra("id", item.getItem1());
                startActivity(intent);
            }
        });


        srlData.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                start = 0;
                initData();
            }
        });
    }

    private void initData() {

        isLoading = true;
        lvOrder.addFooterView(footerList);

        JSONArray jStatus = new JSONArray();
        jStatus.put("1");

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("keyword","");
            jBody.put("start", String.valueOf(start));
            jBody.put("count", String.valueOf(count));
            jBody.put("id", "");
            jBody.put("id_toko", "");
            jBody.put("id_mitra", session.getID());
            jBody.put("id_user", "");
            jBody.put("datestart", "");
            jBody.put("dateend", "");
            jBody.put("status", jStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.getTransaksi
                , jBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        srlData.setRefreshing(false);
                        lvOrder.removeFooterView(footerList);
                        isLoading = false;
                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                        if(start == 0) masterList.clear();

                        try {
                            String status = response.getJSONObject("metadata").getString("status");
                            message = response.getJSONObject("metadata").getString("message");

                            if (status.equals("200")){

                                JSONArray ja = response.getJSONArray("response");
                                for(int i = 0; i < ja.length(); i++){

                                    JSONObject jo = ja.getJSONObject(i);
                                    masterList.add(new CustomItem(
                                            jo.getString("id")
                                            ,jo.getString("insert_at")
                                            ,jo.getString("first_name")
                                            ,jo.getString("produk")
                                            ,jo.getString("total")
                                            ,iv.parseNullString(jo.getString("state"))
                                            ,iv.parseNullString(jo.getString("keterangan"))
                                            ,jo.getString("status")
                                    ));
                                }
                            }else{
                                if(start == 0) Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }

                        getDataRating();
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                srlData.setRefreshing(false);
                adapter.notifyDataSetChanged();
                lvOrder.removeFooterView(footerList);
                isLoading = false;
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
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client-Service", "starkey");
                params.put("Auth-Key", "44b7eb3bbdccdfdaa202d5bfd3541458");
                return params;
            }
        };

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
    }

    private void getDataRating() {

        JSONObject jBody = new JSONObject();

        try {
            jBody.put("id_mitra",session.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request_json = new JsonObjectRequest(Request.Method.POST,
                ConfigLink.getRatingMitra
                , jBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        String message = "Terjadi kesalahan saat memuat data, harap ulangi";
                        try {
                            String status = response.getJSONObject("metadata").getString("status");
                            message = response.getJSONObject("metadata").getString("message");

                            if (status.equals("200")){

                                JSONObject jo = response.getJSONObject("response");
                                tvSaldo.setText(iv.ChangeToRupiahFormat(jo.getString("saldo_akhir")));
                                tvRating.setText(iv.doubleToString(iv.parseNullDouble(jo.getString("rating")),"1"));
                                rbMitra.setRating(iv.parseNullFloat(jo.getString("rating")));

                            }else{
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            }

                        }catch (JSONException ex){
                            ex.printStackTrace();
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

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
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Client-Service", "starkey");
                params.put("Auth-Key", "44b7eb3bbdccdfdaa202d5bfd3541458");
                return params;
            }
        };

        int socketTimeout = 30000; //30 detik
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request_json.setRetryPolicy(policy);
        RequestHandler.getInstance(this).addToRequestQueue(request_json);
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
                Intent intenSyarat = new Intent(context, PengaturanToko.class);
                startActivity(intenSyarat);
                break;
            case R.id.nav_history:
                Intent intent = new Intent(context, HistoryJasaLain.class);
                startActivity(intent);
                break;
            case R.id.nav_statistik:
                Intent intenBan = new Intent(context, StatisticJasaLainActivity.class);
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
