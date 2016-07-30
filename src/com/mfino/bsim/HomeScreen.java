package com.mfino.bsim;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRSDK;
import com.mfino.bsim.account.AccountSelection;
import com.mfino.bsim.billpayment.PaymentHome;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.db.DBHelper;
import com.mfino.bsim.flashiz.QRPayment2;
import com.mfino.bsim.purchase.PurchaseHome;
import com.mfino.bsim.transfer.TransferSelection;

/** @author pramod */
public class HomeScreen extends Activity {
	/** Called when the activity is first created. */
	private Button logoutButton;
	private ImageView image1, image2, image3, image4, qrPayment, promo;
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	SharedPreferences languageSettings;
	private TextView transfer, purchase, payment, account, qrText, promoText;
	SharedPreferences settings;
	Context context;
	DBHelper mydb;
	ValueContainer valueContainer;
	//private String responseXml;
	ProgressDialog dialog;
	public static String module;
	String userApiKey;
	int msgcode;
	//private AlertDialog.Builder alertbox;
	PayByQRSDK payByQRSDK;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen1);
		//alertbox = new AlertDialog.Builder(HomeScreen.this);

		mydb = new DBHelper(HomeScreen.this);

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		//String mobileNumber = settings.getString("mobile", "");

		logoutButton = (Button) findViewById(R.id.logoutButton);
		image1 = (ImageView) findViewById(R.id.imageView1);
		image2 = (ImageView) findViewById(R.id.imageView2);
		image3 = (ImageView) findViewById(R.id.imageView3);
		image4 = (ImageView) findViewById(R.id.imageView4);
		qrPayment = (ImageView) findViewById(R.id.qrPayment);
		promo = (ImageView) findViewById(R.id.imageViewPromo);
		transfer = (TextView) findViewById(R.id.textView1);
		purchase = (TextView) findViewById(R.id.textView2);
		payment = (TextView) findViewById(R.id.textView3);
		account = (TextView) findViewById(R.id.textView4);
		qrText = (TextView) findViewById(R.id.qrText);
		promoText = (TextView) findViewById(R.id.textViewPromo);

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		System.out.println("Testing>>language>>" + selectedLanguage);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			logoutButton.setBackgroundResource(R.drawable.logout_button);
			transfer.setText(getResources().getString(R.string.eng_fundTransfer));
			purchase.setText(getResources().getString(R.string.eng_purchase));
			payment.setText(getResources().getString(R.string.eng_payment));
			account.setText(getResources().getString(R.string.eng_myaccont));
			qrText.setText(getResources().getString(R.string.eng_Flashiz));
			promoText.setText(getResources().getString(R.string.eng_promo));

		} else {
			System.out.println("Testing>>Bahasa");
			logoutButton.setBackgroundResource(R.drawable.logout_button);
			transfer.setText(getResources().getString(R.string.eng_fundTransfer));
			purchase.setText(getResources().getString(R.string.bahasa_purchase));
			payment.setText(getResources().getString(R.string.bahasa_payment));
			account.setText(getResources().getString(R.string.bahasa_myaccont));
			qrText.setText(getResources().getString(R.string.bahasa_Flashiz));
			promoText.setText(getResources().getString(R.string.bahasa_promo));
		}

		qrPayment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this, QRPayment2.class);
				intent.putExtra(QRPayment2.INTENT_EXTRA_MODULE, PayByQRSDK.MODULE_PAYMENT);
				startActivity(intent);

				Log.e("agree_clicked", "");

				/*
				 * Cursor rs = mydb.getFlashizData(); Log.e("countttt",
				 * rs.getCount() + ""); if (rs.getCount() != 0) {
				 * 
				 * while (rs.moveToNext()) { // array.clear(); String
				 * session_value = rs.getString(rs
				 * .getColumnIndex("session_value")); Log.e("session_value",
				 * session_value + "--------------"); if
				 * (session_value.equalsIgnoreCase("false")) {
				 * //SDKLinkFragmentActivity.setUserEulaState(false);
				 * payByQRSDK.setEULAState(false); Intent intent1 = new
				 * Intent(HomeScreen.this, QRPayment2.class);
				 * intent.putExtra(QRPayment2.INTENT_EXTRA_MODULE,
				 * PayByQRSDK.MODULE_PAYMENT); startActivity(intent1); //
				 * getUserAPIKey(); } else {
				 * //SDKLinkFragmentActivity.setUserEulaState(true);
				 * payByQRSDK.setEULAState(true); Intent intent2 = new
				 * Intent(HomeScreen.this, QRPayment2.class);
				 * intent.putExtra(QRPayment2.INTENT_EXTRA_MODULE,
				 * PayByQRSDK.MODULE_PAYMENT); startActivity(intent2); } } }
				 * else { Log.e("Nodata_founddd", "*******************");
				 * 
				 * // Log.e("cursor-----count_****************", //
				 * rs2.getCount()+""); }
				 */

			}
		});

		promo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this, QRPayment2.class);
				intent.putExtra(QRPayment2.INTENT_EXTRA_MODULE, PayByQRSDK.MODULE_LOYALTY);
				startActivity(intent);
			}
		});

		image1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this, TransferSelection.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		image2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this, PaymentHome.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		image3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this, PurchaseHome.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		image4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this, AccountSelection.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		logoutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this, LoginScreen.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				settings.edit().putString("userApiKey", "NONE").commit();
				// LoginScreen.loginId.setText("");

				startActivity(intent);

			}
		});
	}

}
