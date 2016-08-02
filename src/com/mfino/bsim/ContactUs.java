package com.mfino.bsim;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ContactUs extends AppCompatActivity {
	SharedPreferences languageSettings;
	String selectedLanguage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_us);
		TextView screenTitle = (TextView) findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				ContactUs.this.finish();
			}
		});
		TextView customerCare = (TextView) findViewById(R.id.bank_sinarmas_care);
		TextView phoneNum = (TextView) findViewById(R.id.phone_num);
		TextView companyWebSite = (TextView) findViewById(R.id.company_website);
		TextView webSite = (TextView) findViewById(R.id.website);
		TextView mailUsAt = (TextView) findViewById(R.id.mail_us_at);
		TextView mail = (TextView) findViewById(R.id.mail);

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screenTitle.setText("Contact us");
			customerCare.setText(getResources().getString(R.string.eng_bankSinarmasCare));
			phoneNum.setText(" : 500 153" + "\n" + "(021)501 88888");
			companyWebSite.setText(getResources().getString(R.string.eng_companyWebsite));
			webSite.setText(" : www.banksinarmas.com");
			mailUsAt.setText(getResources().getString(R.string.eng_mainUsat));
			mail.setText(" : care@banksinarmas.com");

		} else {

			screenTitle.setText("Kontak kami");
			customerCare.setText(getResources().getString(R.string.bahasa_bankSinarmasCare));
			phoneNum.setText(" : 500 153" + "\n" + "  (021)501 88888");
			companyWebSite.setText(getResources().getString(R.string.bahasa_companyWebsite));
			webSite.setText(" : www.banksinarmas.com");
			mailUsAt.setText(getResources().getString(R.string.bahasa_mainUsat));
			mail.setText(" : care@banksinarmas.com");

		}
	}

}
