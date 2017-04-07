package amazon.vardaan.pwain_customer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.net.URL;

public class CustomerWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.wtf("webview","reached here");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

    }

    @Override
    protected void onStart() {
        super.onStart();
        String url = getIntent().getExtras().getString("url");
        WebView wv = (WebView) findViewById(R.id.webview);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                pb.setVisibility(View.VISIBLE);
            }

            public synchronized void onPageFinished(WebView view, String url) {
                ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
                pb.setVisibility(View.INVISIBLE);
                try {
                    super.onPageFinished(view, url);
                    if (url.startsWith("amzn://amazonpay.amazon.in/customerApp")) {
                        Intent i = new Intent(getApplicationContext(),PaymentActivity.class);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        finish();
                    }
                } catch (Exception e) {

                }
            }
        });
        wv.loadUrl(url);
    }
}
