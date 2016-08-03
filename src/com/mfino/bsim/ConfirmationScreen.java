package com.mfino.bsim;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmationScreen extends Activity {

	private Button btn_home;
	private Bundle bundle;
	// private String msg;
	private TextView tvDetails, aditionalInfo;
	SharedPreferences languageSettings;
	String selectedLanguage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmation);

		// Header code...
		/*
		 * View headerContainer = findViewById(R.id.header); TextView
		 * screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
		 * Button back=(Button)headerContainer.findViewById(R.id.back); Button
		 * home=(Button)headerContainer.findViewById(R.id.home_button);
		 * 
		 * 
		 * 
		 * back.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { finish(); } });
		 * back.setVisibility(View.GONE); home.setOnClickListener(new
		 * OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent=new
		 * Intent(ConfirmationScreen.this,HomeScreen.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent); } });
		 */

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		bundle = getIntent().getExtras();

		tvDetails = (TextView) findViewById(R.id.tv_transactionDetails);
		aditionalInfo = (TextView) findViewById(R.id.aditional_info);
		System.out.println("Testing>>AditionalINfo" + bundle.getString("ADITIONAL_INFO"));

		if (bundle.getString("MSG").equals("ERROR: Invalid Data")) {
			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				tvDetails.setText(getResources().getString(R.string.eng_incorrectotp));
			} else {
				tvDetails.setText(getResources().getString(R.string.bahasa_incorrectotp));
			}
		} else if(bundle.getString("MSG").contains("Your Bank Account Balance as on ")){
			String msg = "";
			Log.d("TES", "test");
			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
				Calendar cal = Calendar.getInstance();
				String date = dateFormat.format(cal.getTime());
				msg = "Date/Time : " + date + "\nAccount No. : " + bundle.getString("AccountNumber") + "\nBalance : " + bundle.getString("Amount") + " IDR";
			}else{
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
				Calendar cal = Calendar.getInstance();
				String date = dateFormat.format(cal.getTime());
				msg = "Tanggal/Waktu : " + date + "\nNo. Rekening : " + bundle.getString("AccountNumber") + "\nSaldo : Rp" + bundle.getString("Amount");
			}
			if(!bundle.getString("AccountNumber").equals("") && !bundle.getString("Amount").equals("")){
				tvDetails.setText(msg);
			}else{
				tvDetails.setText(bundle.getString("MSG"));
			}
		}else {
			tvDetails.setText(bundle.getString("MSG"));
		}

		Log.e("conf_screen_masg_uangku********", bundle.getString("MSG"));

		try {
			if (bundle.getString("ADITIONAL_INFO").length() <= 0
					|| bundle.getString("ADITIONAL_INFO").equalsIgnoreCase("null")) {

				aditionalInfo.setVisibility(View.GONE);
				Log.e("conf_screen_masg_uangku********", "iffffffffffffff");

			} else {
				Log.e("conf_screen_masg_uangku********", "elseeeeeeeeeeeeeee");

				aditionalInfo.setVisibility(View.VISIBLE);
				String adInfo = bundle.getString("ADITIONAL_INFO");
				StringBuilder sb = new StringBuilder();

				String delimiter = "\\|";
				String temp[] = adInfo.split(delimiter);
				for (int i = 0; i < temp.length; i++)
					sb.append(temp[i]).append("\n");

				aditionalInfo.setText(sb.toString());
			}
		} catch (Exception e) {

			aditionalInfo.setVisibility(View.GONE);
		}
		btn_home = (Button) findViewById(R.id.btn_DetailsHistoryHome);

		/*
		 * btn_home = (Button)findViewById(R.id.btn_DetailsHistoryHome);
		 * btn_logout = (Button)findViewById(R.id.btn_DetailsHistory_Logout);
		 */
		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			// screeTitle.setText(getResources().getString(R.string.eng_result));
			/*
			 * btn_home.setBackgroundResource(R.drawable.home_button);
			 * btn_logout.setBackgroundResource(R.drawable.logout_btn_hover);
			 * home.setBackgroundResource(R.drawable.home_icon1);
			 * back.setBackgroundResource(R.drawable.back_button);
			 */

		} else {

			// screeTitle.setText(getResources().getString(R.string.bahasa_result));
			/*
			 * btn_home.setBackgroundResource(R.drawable.bahasa_home_button);
			 * btn_logout.setBackgroundResource(R.drawable.logout_btn_hover);
			 * home.setBackgroundResource(R.drawable.bahasa_home_icon1);
			 */
			// back.setBackgroundResource(R.drawable.bahasa_back_button);

		}
		btn_home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(bundle.getString("MSG").trim().equals("Please login again") || bundle.getString("MSG").trim().equals("ERROR: Not Registered. Hi! You are currently not registered as a Simobi user")) {
					Intent intent = new Intent(ConfirmationScreen.this, LoginScreen.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
					Intent intent = new Intent(ConfirmationScreen.this, HomeScreen.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}
		});

		/*
		 * btn_home.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * Intent intent = new Intent(ConfirmationScreen.this,
		 * HomeScreen.class); intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent); } });
		 * 
		 * btn_logout.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * Intent intent = new Intent(ConfirmationScreen.this,
		 * LoginScreen.class); intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent); } });
		 */
	}

	/**
	 * This method for handling the back pressing event of android device
	 * navigate to Home Screen
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent = new Intent(getBaseContext(), HomeScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
