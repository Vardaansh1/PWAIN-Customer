package amazon.vardaan.pwain_customer;

import android.content.Intent;
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
        //14. This will be non null, if either the sms link redirects us here, or the custom tab redirects us here
        if (getIntent().getData() != null) {
            // if intent contains tinyurl in data it means sms redirected us here, so we need to initiate payment
            if (getIntent().getData().getHost().equalsIgnoreCase("tinyurl.com")) {
                try {
                    Intent i = new Intent(this, CustomerWebView.class);
                    Log.wtf("sending url", new LengthenUrl().execute(getIntent().getData()
                            .toString()).get());
                    i.putExtra("url", new LengthenUrl().execute(getIntent().getData()
                            .toString()).get());
                    startActivity(i);
                } catch (Exception e) {
                    Log.wtf("errohr", "", e);

                }
            } else if (!getIntent().getData().getQueryParameter("reasonCode").equalsIgnoreCase("001")) {
                //18. if we are here it means the the custom tab redirected us here with the response code.
                // If response code is not 001 its a failure
                // default text is set to success in the layout so we didnt have to write an if block for that
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("PAYMENT FAILED :(");
            }
        } else {
            // We are here means the we are still in app and were not directed here via SMS or browser/custom tab
            //15. if we got a tiny url, in prod, we should first expand it here and validate that it is an amazon pay
            // url. As of now, ANY tiny url is accepted. Currently, URL Lengthening is happening, validation is not.
            // Seemed overkill for hackathon, but can be done if time permits. Its a tiny ass change
            try {
                Intent i = new Intent(this, CustomerWebView.class);
                i.putExtra("url", new LengthenUrl().execute(getIntent().getExtras()
                        .getString("url")).get());
                startActivity(i);
//                CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
//                        .setShowTitle(true)
//                        .setToolbarColor(Color.parseColor("#FF9900"))
//                        .setStartAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                        .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                        .build();
//                //16. always opens custom tab and does not allow user to choose between other browser
//                customTabsIntent.intent.setPackage("com.android.chrome");
//                customTabsIntent.launchUrl(this, Uri.parse(new LengthenUrl().execute(getIntent().getExtras()
//                        .getString("url")).get()));
            } catch (Exception e) {
                Log.wtf("error", "", e);
            }
        }
    }

    /**
     * Lengthens the tiny url
     */
    private class LengthenUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.wtf("received url",strings[0]);
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
