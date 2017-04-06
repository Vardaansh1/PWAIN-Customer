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


public class MainActivity extends AppCompatActivity{

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(getIntent().getExtras()!=null && getIntent().getExtras().containsKey("error")){
            textView = (TextView) findViewById(R.id.result);
            textView.setText(getIntent().getExtras().getString("error"));
            Log.e("main","here");
            textView.setVisibility(View.VISIBLE);
        }
    }

    public void QrScanner(View view){
        if(textView!=null) {
            Log.e("main","here now");

            textView.setVisibility(View.INVISIBLE);
        }
        startActivity(new Intent(this,QRScannerActivity.class));
    }


}
