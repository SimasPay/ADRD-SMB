package com.mfino.bsim;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.audiofx.BassBoost.Settings;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mfino.bsim.account.AccountSelection;
import com.mfino.bsim.billpayment.PaymentHome;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.db.DBHelper;
import com.mfino.bsim.flashiz.MyCustomEULA;
import com.mfino.bsim.flashiz.QRPayment;
import com.mfino.bsim.purchase.PurchaseHome;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.bsim.transfer.TransferSelection;
import com.mobey.fragment.abs.SDKLinkFragmentActivity;

/** @author pramod */
public class HomeScreen extends Activity {
	/** Called when the activity is first created. */
	private Button logoutButton;
	private ImageView image1, image2, image3, image4;
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	SharedPreferences languageSettings;
	private TextView transfer, purchase, payment, account, qrText;
	SharedPreferences settings;
	Context context;
	DBHelper mydb;
	ValueContainer valueContainer;
	private String responseXml;
	ProgressDialog dialog;
	public static String module;
	String userApiKey;
	int msgcode;
	private AlertDialog.Builder alertbox;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.home_screen1);
		alertbox = new AlertDialog.Builder(this);

		mydb = new DBHelper(this);

		settings = getSharedPreferences("LOGIN_PREFERECES",
				Context.MODE_WORLD_READABLE);
		String mobileNumber = settings.getString("mobile", "");

		logoutButton = (Button) findViewById(R.id.logoutButton);
		image1 = (ImageView) findViewById(R.id.imageView1);
		image2 = (ImageView) findViewById(R.id.imageView2);
		image3 = (ImageView) findViewById(R.id.imageView3);
		image4 = (ImageView) findViewById(R.id.imageView4);
		ImageView qrPayment = (ImageView) findViewById(R.id.qrPayment);
		transfer = (TextView) findViewById(R.id.textView1);
		purchase = (TextView) findViewById(R.id.textView2);
		payment = (TextView) findViewById(R.id.textView3);
		account = (TextView) findViewById(R.id.textView4);
		qrText = (TextView) findViewById(R.id.qrText);

		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",
				Context.MODE_WORLD_READABLE);
		String selectedLanguage = languageSettings.getString("LANGUAGE",
				"BAHASA");
		System.out.println("Testing>>language>>" + selectedLanguage);

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			logoutButton.setBackgroundResource(R.drawable.logout_button);
			transfer.setText(getResources()
					.getString(R.string.eng_fundTransfer));
			purchase.setText(getResources().getString(R.string.eng_purchase));
			payment.setText(getResources().getString(R.string.eng_payment));
			account.setText(getResources().getString(R.string.eng_myaccont));
			qrText.setText(getResources().getString(R.string.eng_Flashiz));

		} else {
			System.out.println("Testing>>Bahasa");
			logoutButton.setBackgroundResource(R.drawable.logout_button);
			transfer.setText(getResources()
					.getString(R.string.eng_fundTransfer));
			purchase.setText(getResources().getString(R.string.bahasa_purchase));
			payment.setText(getResources().getString(R.string.bahasa_payment));
			account.setText(getResources().getString(R.string.bahasa_myaccont));
			qrText.setText(getResources().getString(R.string.bahasa_Flashiz));
		}

		qrPayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Intent intent = new Intent(HomeScreen.this, QRPayment.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);

				Log.e("agree_clicked", "");

				Cursor rs = mydb.getFlashizData();
				Log.e("countttt", rs.getCount() + "");
				if (rs.getCount() != 0) {

					while (rs.moveToNext()) {
						// array.clear();
						String session_value = rs.getString(rs
								.getColumnIndex("session_value"));
						Log.e("session_value", session_value + "--------------");
						if (session_value.equalsIgnoreCase("false")) {

							SDKLinkFragmentActivity.setUserEulaState(false);
							Intent intent = new Intent(HomeScreen.this,
									QRPayment.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							// getUserAPIKey();

						} else {
							SDKLinkFragmentActivity.setUserEulaState(true);
							Intent intent = new Intent(HomeScreen.this,
									QRPayment.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);

						}

					}
				} else {
					Log.e("Nodata_founddd", "*******************");

					// Log.e("cursor-----count_****************",
					// rs2.getCount()+"");

				}

			}
		});

		image1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeScreen.this,
						TransferSelection.class);
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
				Intent intent = new Intent(HomeScreen.this,
						AccountSelection.class);
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
