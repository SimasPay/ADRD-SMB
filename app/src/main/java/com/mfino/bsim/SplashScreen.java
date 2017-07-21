package com.mfino.bsim;

import java.util.List;
import com.mfino.bsim.flashiz.QRPayment2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/** 
 * @author himanshu.kumar
 * */
public class SplashScreen extends AppCompatActivity {
    /** Called when the activity is first created. */
	private final int SPLASH_DISPLAY_LENGHT = 3000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
           
        new Handler().postDelayed(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
					if ((checkCallingOrSelfPermission(
							android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
							&& checkCallingOrSelfPermission(
									Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {

						requestPermissions(new String[] { Manifest.permission.READ_SMS,
								android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS },
								109);
					}
				}
				Intent intent=new Intent(SplashScreen.this, LandingScreen.class);
				if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
					Uri data = getIntent().getData();
		            List<String> params = data.getPathSegments();
		            String payinappDatas = params.get(0);
		            String invoiceID = payinappDatas.substring(0, payinappDatas.indexOf("&"));
		            String urlCallback = payinappDatas.substring((payinappDatas.indexOf("&")+"&backUrl=".length()), payinappDatas.length());
		            
		            intent.putExtra(QRPayment2.INTENT_EXTRA_INVOICE_ID, invoiceID);
		            intent.putExtra(QRPayment2.INTENT_EXTRA_URL_CALLBACK, urlCallback);
		            
		            Log.d("Simobi", "PayInApp invoiceID: "+invoiceID);
		            Log.d("Simobi", "PayInApp urlCallback: "+urlCallback);
				}
				
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SplashScreen.this.finish();

			}
		}, SPLASH_DISPLAY_LENGHT);
    }
    
    @SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 109) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.e("if_permission", "*********");

			} else {
				Log.e("elseeeee_permission", "*********");

			}
		}
	}
}