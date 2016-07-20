package com.mfino.bsim;

import com.mfino.bsim.account.AccountSelection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class Confirmation_History extends Activity{
	
	
	private Bundle bundle;
	int count=0;
	SharedPreferences languageSettings;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.last_transaction);
		
		//Header code...
		/* View headerContainer = findViewById(R.id.header); 
	     TextView screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
	     //screeTitle.setText("LAST 3 TRANSACTIONS");
	     Button back=(Button)headerContainer.findViewById(R.id.back);
	     Button home=(Button)headerContainer.findViewById(R.id.home_button);*/
		
		Button ok=(Button)findViewById(R.id.ok);
	 	//Language Option..
			languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
			String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
			
		/*	if (selectedLanguage.equalsIgnoreCase("ENG")) {
				screeTitle.setText(getResources().getString(R.string.eng_history));
				back.setBackgroundResource(R.drawable.back_button);
				home.setBackgroundResource(R.drawable.home_icon1);

			} else {
				screeTitle.setText(getResources().getString(R.string.bahasa_history));
				//home.setBackgroundResource(R.drawable.bahasa_home_icon1);
				//back.setBackgroundResource(R.drawable.bahasa_back_button);

			}*/
	     
	  /*   back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});*/
	     ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Confirmation_History.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		
		bundle = getIntent().getExtras();
		
		
		String msg = bundle.getString("MSG");
		System.out.println("Testing>History>>"+msg);
		
		TextView history=(TextView)findViewById(R.id.history);
		history.setText( msg);
		
	}
	 /** This method for handling the back pressing event of android device navigate to Home Screen */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		   if (keyCode == KeyEvent.KEYCODE_BACK) {
		     
		           Intent intent  = new Intent(getBaseContext(), HomeScreen.class);
		           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        
		           startActivity(intent);
		          return true;
		   }
		   return super.onKeyDown(keyCode, event);
		}

}

