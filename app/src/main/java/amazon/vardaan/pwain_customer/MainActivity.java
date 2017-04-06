package amazon.vardaan.pwain_customer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1. Asks the user permission to use his camera.

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //18. If some data is being passed to this activity, it means an error string is passed here via some other
        // activity
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("error")) {
            textView = (TextView) findViewById(R.id.result);
            //19. setting the passed error message
            textView.setText(getIntent().getExtras().getString("error"));
            Log.e("main", "here");
            //20. Setting the error message as visible (its invisible by default)
            textView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * On click listener for SCAN button
     *
     * @param view
     */
    public void QrScanner(View view) {
        if (textView != null) {
            Log.e("main", "here now");
            //2. this text view is the ERROR message. Hidden for now.
            textView.setVisibility(View.INVISIBLE);
        }
        //3. starts the QR scanner
        startActivity(new Intent(this, QRScannerActivity.class));
    }


}
