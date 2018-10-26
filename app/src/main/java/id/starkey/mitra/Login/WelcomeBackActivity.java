package id.starkey.mitra.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import id.starkey.mitra.ConfigLink;
import id.starkey.mitra.MainActivity;
import id.starkey.mitra.R;

public class WelcomeBackActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bLoginNext;
    private TextView tvNamaUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_back);

        //init btn
        bLoginNext = findViewById(R.id.btnLoginNext);
        bLoginNext.setOnClickListener(this);

        //init tv
        tvNamaUser = findViewById(R.id.txtNamaUser);

        getPref();
    }

    private void getPref(){
        SharedPreferences custDetails = getSharedPreferences(ConfigLink.loginPref, MODE_PRIVATE);
        String userEmail = custDetails.getString("namaUser", "");
        tvNamaUser.setText(userEmail);
    }

    @Override
    public void onClick(View v) {
        if (v == bLoginNext){
            Intent intent = new Intent(WelcomeBackActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
