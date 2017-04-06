package amazon.vardaan.pwain_customer;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaticQRProcessor extends AppCompatActivity {

    Button button;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_qrprocessor);
        //7. Getting a handle on the amount field and button in activity_static_qrprocessor layout
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        // 8. Fetching the passed params required to make a sign and encrypt call
        final HashMap<String, String> params = (HashMap) getIntent().getExtras().get("params");
        // Listener for submit payment button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // 9. puts the users specified amount and executes merchant backend async task to get initiate
                    // payment url
                    params.put("orderTotalAmount", editText.getText().toString());
                    Intent i = new Intent(getApplicationContext(), PaymentActivity.class);
                    // 10. sends the initiate payment url to payment activity
                    i.putExtra("url", new MerchantBackendTask().execute(params).get());
                    startActivity(i);
                } catch (Exception e) {
                    Log.wtf("error", "error", e);
                }
            }
        });


    }

    /**
     * Makes a sign and encrypt call, and returns the initiate payment url
     */
    private class MerchantBackendTask extends AsyncTask<Map<String, String>, Void, String> {

        private static final String LOG_TAG = "merchant server";

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            try {
                Log.d(LOG_TAG, "Fetching from merchant endpoint");
                HttpGet httpGet;
                Uri uri = createUri(new URL("http://ec2-35-162-20-220.us-west-2.compute.amazonaws.com"), maps[0],
                        "/prod/signAndEncrypt.jsp");
                Log.d("requestUri", uri.toString());
                httpGet = new HttpGet(uri.toString());

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpGet);

                int status = response.getStatusLine().getStatusCode();
                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity).trim();
                    Log.d("response", data);
                    Map<String, String> decodedParams = getDecodedQueryParameters(data);
                    for (String key : decodedParams.keySet()) {
                        Log.d("key", key);
                        Log.d("value", decodedParams.get(key));
                    }
                    decodedParams.put("requestId", UUID.randomUUID().toString());
                    decodedParams.put("redirectUrl", "amzn" + "://amazonpay.amazon.in/" + "customerApp");

                    String url = createURLString(new URL("https://amazonpay.amazon.in"),
                            decodedParams,
                            "/initiatePayment");
                    Log.wtf(LOG_TAG, "url=" + url);
                    return url;
                } else

                {
                    Log.d(LOG_TAG, String.format("Unable to sign payload. Received following status code: %d", status));
                }
            } catch (
                    Exception e)

            {
                Log.e(LOG_TAG, "ERROR IN MERCHANT SERVER", e);
            }
            return null;
        }

        String createURLString(URL endpoint, Map<String, String> parameters, String path) {
            return createUri(endpoint, parameters, path).toString();
        }

        /**
         * Returns the uri created from the supplied parameters
         *
         * @param endpoint   The endpoint of the uri
         * @param parameters The parameters to be added to the uri
         * @param path       The path of the uri
         * @return The created URI based on the supplied params
         */
        private Uri createUri(URL endpoint, Map<String, String> parameters, String path) {
            Uri uri = Uri.parse(endpoint.toString());

            if (path != null && !path.isEmpty()) {
                uri = uri.buildUpon().path(path).build();
            }

            if (parameters != null && parameters.size() > 0) {
                uri = addQueryParameters(uri, parameters);
            }

            return uri;
        }

        /**
         * Add the supplied query params to the supplied URI
         *
         * @param uri        the uri to which the params have to be added
         * @param parameters the params that are to be added
         * @return the uri with the params added
         */
        private Uri addQueryParameters(Uri uri, Map<String, String> parameters) {
            for (String key : parameters.keySet()) {
                uri = uri.buildUpon().appendQueryParameter(key, parameters.get(key)).build();
            }
            return uri;
        }

        /**
         * Get the decoded Query params
         *
         * @param query the encoded query params
         * @return the decoded query params
         * @throws UnsupportedEncodingException thrown if the encoding used on the query params is not supported
         */
        Map<String, String> getDecodedQueryParameters(String query) throws UnsupportedEncodingException {
            if (query == null || query.trim().length() < 1) {
                return null;
            }
            HashMap<String, String> parameters = new HashMap<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int index = pair.indexOf("=");
                parameters.put(URLDecoder.decode(pair.substring(0, index), "UTF-8"), URLDecoder.decode(pair.substring
                        (index + 1), "UTF-8"));
            }
            return parameters;

        }
    }
}
