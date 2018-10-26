package id.starkey.mitra.Stempel;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import id.starkey.mitra.MainActivity;
import id.starkey.mitra.R;

public class BatalOrderStempelActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bBatal, bCari, bSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batal_order_stempel);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarBatalOrderStempel);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Konfirmasi Batal Order");

        //init btn
        bBatal = findViewById(R.id.btnKonfirmBatal);
        bBatal.setOnClickListener(this);
        bCari = findViewById(R.id.btnKonfirmCari);
        bCari.setOnClickListener(this);
        bSimpan = findViewById(R.id.btnKonfirmSimpan);
        bSimpan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == bBatal){
            Intent intent = new Intent(BatalOrderStempelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (v == bCari){
            Intent intent = new Intent(BatalOrderStempelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(BatalOrderStempelActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
