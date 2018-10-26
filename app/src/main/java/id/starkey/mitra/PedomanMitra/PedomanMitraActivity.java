package id.starkey.mitra.PedomanMitra;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import id.starkey.mitra.R;

public class PedomanMitraActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedoman_mitra);

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPedomanMitra);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_silang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setTitle("Pedoman Mitra");

        //init webview
        webView = findViewById(R.id.webViewPedomanMitra);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("http://www.manual-book.starkey.id/mitra/");
        webView.getSettings().setJavaScriptEnabled(true);

        //init progressbar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl("http://www.manual-book.starkey.id/mitra/");
            //return super.shouldOverrideUrlLoading(view, url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}
