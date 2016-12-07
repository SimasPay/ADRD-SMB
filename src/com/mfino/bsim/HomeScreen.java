package com.mfino.bsim;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.mfino.bsim.utils.AndroidPermissions;

/** @author pramod */
public class HomeScreen extends AppCompatActivity {
	/** Called when the activity is first created. */
	private Button logoutButton;
	private ImageView image1, image2, image3, image4, qrPayment, promo;
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	SharedPreferences languageSettings;
	private TextView transfer, purchase, payment, account, qrText, promoText;
	SharedPreferences settings, settings2;
	Context context;
	DBHelper mydb;
	ValueContainer valueContainer;
	// private String responseXml;
	ProgressDialog dialog;
	public static String module;
	String userApiKey;
	int msgcode;
	// private AlertDialog.Builder alertbox;
	PayByQRSDK payByQRSDK;
	final private int PERMISSION_REQUEST_CODE = 123;
	public static final String LOG_TAG = "SIMOBI";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen1);
		
		settings2 = getSharedPreferences(LOG_TAG, 0);
		settings2.edit().putString("ActivityName", "HomeScreen").commit();
        
		if (Build.VERSION.SDK_INT >= 23) {// self check permissions for Read SMS
			requestContactPermission();
			if (!AndroidPermissions.getInstance().checkReadSmsPermission(HomeScreen.this)) {
				AndroidPermissions.getInstance().displaySmsPermissionAlert(HomeScreen.this);
			}
		}
		// alertbox = new AlertDialog.Builder(HomeScreen.this);

		mydb = new DBHelper(HomeScreen.this);

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		// String mobileNumber = settings.getString("mobile", "");

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
			logoutButton.setBackgroundResource(R.drawable.logout);
			transfer.setText(getResources().getString(R.string.eng_fundTransfer));
			purchase.setText(getResources().getString(R.string.eng_purchase));
			payment.setText(getResources().getString(R.string.eng_payment));
			account.setText(getResources().getString(R.string.eng_myaccont));
			qrText.setText(getResources().getString(R.string.eng_Flashiz));
			promoText.setText(getResources().getString(R.string.eng_promo));

		} else {
			System.out.println("Testing>>Bahasa");
			logoutButton.setBackgroundResource(R.drawable.logout);
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

	private void requestContactPermission() {
		int hasContactPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS);
		if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECEIVE_SMS },
					PERMISSION_REQUEST_CODE);
		} else {
			// Toast.makeText(AddContactsActivity.this, "Contact Permission is
			// already granted", Toast.LENGTH_LONG).show();
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {

		case AndroidPermissions.REQUEST_READ_SMS:
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

			} else {
				AndroidPermissions.getInstance().displayAlert(HomeScreen.this, AndroidPermissions.REQUEST_READ_SMS);
			}
			break;
		case PERMISSION_REQUEST_CODE:
			// Check if the only required permission has been granted
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.i("Permission", "Contact permission has now been granted. Showing result.");
			} else {
				Log.i("Permission", "Contact permission was NOT granted.");
			}
			break;
		default: {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
		}
	}

}
