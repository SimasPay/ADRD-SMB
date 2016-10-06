package com.mfino.bsim;

import java.util.List;
import com.mfino.bsim.flashiz.QRPayment2;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/** 
 * @author himanshu.kumar
 * */
public class SplashScreen extends Activity {
    /** Called when the activity is first created. */
	private final int SPLASH_DISPLAY_LENGHT = 3000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
           
        new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
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
}