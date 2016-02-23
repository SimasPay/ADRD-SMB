package com.mfino.bsim;
import com.mfino.bsim.account.AccountSelection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
/** @author pramod */

public class ActivationConfirmation extends Activity {
    /** Called when the activity is first created. */
	private Button loginButton;
	private Bundle bundle;
	private TextView tvMsg;
	String msg;
	SharedPreferences languageSettings;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation_confirm);
        
      //Header code...
	     /*TextView screeTitle=(TextView)findViewById(R.id.screenTitle);
	     Button back=(Button)findViewById(R.id.back);
	     
	     
	     back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(ActivationConfirmation.this, ActivationHome.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		});
	     */
        bundle = getIntent().getExtras();
        loginButton = (Button)findViewById(R.id.loginButton);
        tvMsg =  (TextView)findViewById(R.id.textView1);
        tvMsg.setText(bundle.getString("MSG"));
        
      //Language Option..
  		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
  		String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
  		loginButton.setText("Login");
  		/*if (selectedLanguage.equalsIgnoreCase("ENG")) {
  			
  			//screeTitle.setText(getResources().getString(R.string.eng_result));
  			//loginButton.setBackgroundResource(R.drawable.login_button);
			//back.setBackgroundResource(R.drawable.back_button);

  		} else {
  			
  			//screeTitle.setText(getResources().getString(R.string.bahasa_result));
  			//loginButton.setBackgroundResource(R.drawable.login_button);
			//back.setBackgroundResource(R.drawable.bahasa_back_button);

  		}*/
        
        loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
			
					Intent intent = new Intent(ActivationConfirmation.this, LoginScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				
				
			}
		});
    }
}

