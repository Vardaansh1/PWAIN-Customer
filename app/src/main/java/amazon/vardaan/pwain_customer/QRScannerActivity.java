package amazon.vardaan.pwain_customer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.zxing.Result;

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    HashMap<String, String> params = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view<br />
        setContentView(mScannerView);

        mScannerView.startCamera();

    }

    @Override
    protected void onStart() {
        super.onStart();
//        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view<br />
//        setContentView(mScannerView);
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.<br />
        mScannerView.resumeCameraPreview(this);
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        // show the scanner result into dialog box.
        try {
            Log.e("result=", rawResult.getText());
            if ((!rawResult.getText().startsWith("http://tinyurl.com/")) && rawResult.getText().contains("isSandbox=true") && rawResult.getText().contains
                    ("transactionTimeout=1000") && rawResult.getText().contains("orderTotalCurrencyCode=INR")) {
                String result = rawResult.getText().replaceAll("\\{|\\}", " ").trim();
                String[] tokens = result.split(",");
                for (String s : tokens) {
                    Log.wtf("token=", s);
                    String[] subTokens = s.trim().split("=");
                    params.put(subTokens[0], subTokens[1]);
                }
                params.put("orderTotalAmount", "1");

                Log.wtf("map=", params.toString());
                Log.e("stat", "about to start intent");
                Intent i = new Intent(this, StaticQRProcessor.class);
                i.putExtra("params", params);
                startActivity(i);
                }
           else if (!rawResult.getText().startsWith("http://tinyurl.com/")) {
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("error", "Invalid Amazon Pay QR Code");
                startActivity(i);
            } else {
                Log.e("stat", "about to start intent");
                Intent i = new Intent(this, PaymentActivity.class);
                i.putExtra("url", rawResult.getText());
                startActivity(i);
            }
        } catch (Exception e) {
            Log.e("exception", "error", e);
        }
        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
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
                Log.wtf("expanded url=",expandedURL);

                httpURLConnection.disconnect();
                return expandedURL;
            }catch (Exception e){
                                Log.wtf("Error","",e);
//
            }
//                String shortenUrl = "http://www.daveltd.com/bin/tinylookup.cgi";
//                shortenUrl = shortenUrl + "?url=" + strings[0];
//                HttpGet httpGet = new HttpGet(shortenUrl);
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpResponse response = httpclient.execute(httpGet);
//                int status = response.getStatusLine().getStatusCode();
//                Log.wtf("async task","making call" );
//
//                if (status == 200) {
//                    Log.wtf("async task","status 200" );
//
//                    HttpEntity entity = response.getEntity();
//                    String data = EntityUtils.toString(entity).trim();
//                    Pattern pattern = Pattern.compile(".*<p>Refers to <a href=\"(.*?)\">.*");
//                    Matcher matcher = pattern.matcher(data);
//                    String result = matcher.group(1);
//                    Log.wtf("regex result=", result);
//
//                    if (matcher.find()) {
//                        Log.wtf("regex result=", result);
//                    }
//                    Log.d("response", data);
//                    return result;
//                }
//            } catch (Exception e) {
//            }
            return null;
        }
    }
}
