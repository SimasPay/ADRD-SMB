package com.mfino.bsim;
import com.mfino.bsim.account.AccountSelection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SplashScreen.this.finish();

			}
		}, SPLASH_DISPLAY_LENGHT);

    }
}