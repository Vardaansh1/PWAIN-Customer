package amazon.vardaan.pwain_customer;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

public class PaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getData() != null) {
            if (getIntent().getData().getHost().equalsIgnoreCase("tinyurl.com")) {
                try {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                            .setShowTitle(true)
                            .setToolbarColor(Color.parseColor("#FF9900"))
                            .setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .build();
                    customTabsIntent.intent.setPackage("com.android.chrome");
                    customTabsIntent.launchUrl(this, Uri.parse(new LengthenUrl().execute(getIntent().getData().toString()).get()));
                } catch (Exception e) {
                    Log.wtf("errohr", "", e);

                }
            } else if (!getIntent().getData().getQueryParameter("reasonCode").equalsIgnoreCase("001")) {
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("PAYMENT FAILED :(");
            }
        } else {
            try {
                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .setToolbarColor(Color.parseColor("#FF9900"))
                        .setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .build();
                customTabsIntent.launchUrl(this, Uri.parse(new LengthenUrl().execute(getIntent().getExtras()
                        .getString("url")).get()));
            } catch (Exception e) {
                Log.wtf("error", "", e);
            }
        }
    }

    private class LengthenUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                // open connection
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);

                // stop following browser redirect
                httpURLConnection.setInstanceFollowRedirects(false);

                // extract location header containing the actual destination URL
                String expandedURL = httpURLConnection.getHeaderField("Location");
                Log.wtf("expanded url=", expandedURL);

                httpURLConnection.disconnect();
                return expandedURL;
            } catch (Exception e) {
                Log.wtf("Error", "", e);
//
            }
            return null;
        }
    }
}
