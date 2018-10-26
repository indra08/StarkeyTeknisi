package id.starkey.mitra.Kunci.EnlargeImgKunci;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import id.starkey.mitra.R;
import com.squareup.picasso.Picasso;

public class EnlargeImgKunci extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivOrderKunci;
    private String urlFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_img_kunci);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarEnlargeImgKunci);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Kunci");

        //get param url img
        Bundle bundle = getIntent().getExtras();
        urlFoto = bundle.getString("ulrfotoorderan");

        //init imageview
        ivOrderKunci = findViewById(R.id.imgOrderKunci);
        Picasso.with(this)
                .load(urlFoto)
                .placeholder(R.drawable.progress_animation)
                .into(ivOrderKunci);
    }

    @Override
    public void onClick(View v) {

    }
}
