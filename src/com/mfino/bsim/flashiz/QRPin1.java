package com.mfino.bsim.flashiz;

import java.util.HashMap;
import java.util.Map;

import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.account.AccountSelection;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.purchase.PurchaseDetails;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.handset.security.CryptoService;

public class QRPin1 extends Activity {

	private Button btn_ok, btn_cancel;
	// ValueContainer valueContainer;
	SharedPreferences languageSettings, encrptionKeys;
	String selectedLanguage;
	SharedPreferences settings;
	String xmlText, responseXml;
	Context context;
	ProgressDialog dialog;
	Bundle bundle;
	private EditText pinValue;
	private AlertDialog.Builder alertbox;
	// FlashiZ
	public String userApiKey, otp, parentTxnId, txnId;
	public String mInVoiceId, mAmount, mMarchantName, mfa;
	ProgressDialog dialogCon;
	String otpValue, sctl;
	String mfaMode;
	int msgCode = 0;
	String pin;
	String loyalityName;
	String discountAmount;
	String discountType;
	String numberOfCoupuns;
	layout layout;

	String QRBillerCode = "QRFLASHIZ";
	ValueContainer valueContainer;

	/** Called when the activity is first created. */

	// private final String SWIFT_VALUE = "BKKBIDJA";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_pin);
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",
				Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		settings = getSharedPreferences("LOGIN_PREFERECES",
				Context.MODE_PRIVATE);
		context = getBaseContext();
		// Language Option..
		encrptionKeys = getSharedPreferences("PUBLIC_KEY_PREFERECES",
				Context.MODE_WORLD_READABLE);
		/*
		 * languageSettings =
		 * getSharedPreferences("LANGUAGE_PREFERECES",Context.
		 * MODE_WORLD_READABLE); selectedLanguage =
		 * languageSettings.getString("LANGUAGE", "BAHASA");
		 */

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer
				.findViewById(R.id.screenTitle);
		Button back=(Button)findViewById(R.id.back);
		Button home=(Button)findViewById(R.id.home_button);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent=new Intent(QRPin1.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent=new Intent(QRPin1.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		/*back.setVisibility(View.GONE);
		home.setVisibility(View.GONE);*/
		screeTitle.setText("QR Payment");
		
		userApiKey = settings.getString("userApiKey", "NONE");
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		btn_cancel = (Button) findViewById(R.id.btn_EnterPin_cancel1);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

		} else {

			btn_ok.setText(getResources().getString(R.string.bahasa_submit));
			btn_cancel
					.setText(getResources().getString(R.string.bahasa_cancel));

		}

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				//finish();
				Intent i=new Intent(QRPin1.this,HomeScreen.class);
				startActivity(i);
				finish();
			}
		});

		alertbox = new AlertDialog.Builder(this);
		btn_ok.setOnClickListener(new View.OnClickListener() {
			

			@Override
			public void onClick(View arg0) {
/*				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/
				InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(pinValue.getWindowToken(), 0);
				//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

				if (pinValue.getText().length() < 1) {
					pinValue.setError(" Masukkan PIN Anda  ");
				} else {
					Intent returnIntent = new Intent();
					//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
					returnIntent.putExtra("PIN", pinValue.getText().toString());
					setResult(2, returnIntent);
					finish();
				}

			}
		});

	}

	public void displayDialog(String msg) {
		alertbox = new AlertDialog.Builder(QRPin1.this);
		alertbox.setMessage(msg);
		alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {

			}
		});
		alertbox.show();
	}

	@Override
	public void onBackPressed() {
		/*
		 * getSupportActionBar().hide();
		 * SDKLinkFragmentActivity.resetActionBar(); openMainMenu();
		 */
		/*InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);*/
		finish();
	}

}
