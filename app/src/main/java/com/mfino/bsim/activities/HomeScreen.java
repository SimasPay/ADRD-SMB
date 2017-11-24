package com.mfino.bsim.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dimo.PayByQR.PayByQRSDK;
import com.mfino.bsim.R;
import com.mfino.bsim.account.AccountSelection;
import com.mfino.bsim.billpayment.PaymentHome;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.db.DBHelper;
import com.mfino.bsim.flashiz.QRPayment2;
import com.mfino.bsim.purchase.PurchaseHome;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.bsim.transfer.TransferSelection;
import com.mfino.bsim.utils.AndroidPermissions;


/** @author pramod */
public class HomeScreen extends AppCompatActivity {
	SharedPreferences languageSettings, settings2;
	SharedPreferences settings;
	private String responseXml;
	private AlertDialog.Builder alertbox;
	DBHelper mydb;
	ValueContainer valueContainer;
	String mobileNumber, selectedLanguage;
	final private int PERMISSION_REQUEST_CODE = 123;
	public static final String LOG_TAG = "SIMOBI";
	private String token="";
	private  AlertDialog dialog;

	public HomeScreen() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_screen1);
		if (Build.VERSION.SDK_INT >= 23) {// self check permissions for Read SMS
			requestContactPermission();
			if (!AndroidPermissions.getInstance().checkReadSmsPermission(HomeScreen.this)) {
				AndroidPermissions.getInstance().displaySmsPermissionAlert(HomeScreen.this);
			}
		}


		mydb = new DBHelper(HomeScreen.this);
		settings2 = getSharedPreferences(LOG_TAG, 0);
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		settings2.edit().putString("ActivityName", "HomeScreen").apply();

		settings = getSharedPreferences("LOGIN_PREFERECES", 0);
		mobileNumber = settings.getString("mobile", "");


		final RelativeLayout bannerUpgradeLayout = findViewById(R.id.banner_upgrade);
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			String data = extras.getString("upgradeNow","no");
			if(data.equals("yes")){
				bannerUpgradeLayout.setVisibility(View.GONE);
				getToken();
				checkIfSimPlusExist();
			}else{
				bannerUpgradeLayout.setVisibility(View.VISIBLE);
			}
		}
		Button upgradeNow = findViewById(R.id.upgrade_now);
		upgradeNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(token.equals("")){
					getToken();
				}
				checkIfSimPlusExist();
			}
		});
		ImageButton closeBtn = findViewById(R.id.close_btn);
		closeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				bannerUpgradeLayout.setVisibility(View.GONE);
			}
		});


		/* Called when the activity is first created. */
		ImageButton logoutButton = findViewById(R.id.logoutButton);
		ImageView image1 = findViewById(R.id.imageView1);
		ImageView image2 = findViewById(R.id.imageView2);
		ImageView image3 = findViewById(R.id.imageView3);
		ImageView image4 = findViewById(R.id.imageView4);
		ImageView qrPayment = findViewById(R.id.qrPayment);
		ImageView promo = findViewById(R.id.imageViewPromo);
		TextView transfer = findViewById(R.id.textView1);
		TextView purchase = findViewById(R.id.textView2);
		TextView payment = findViewById(R.id.textView3);
		TextView account = findViewById(R.id.textView4);
		TextView qrText = findViewById(R.id.qrText);
		TextView promoText = findViewById(R.id.textViewPromo);

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
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				settings.edit().putString("userApiKey", "NONE").apply();
				startActivity(intent);

			}
		});
	}

	private void requestContactPermission() {
		int hasContactPermission = ActivityCompat.checkSelfPermission(HomeScreen.this, Manifest.permission.RECEIVE_SMS);
		if (hasContactPermission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECEIVE_SMS },
					PERMISSION_REQUEST_CODE);
		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {

		case AndroidPermissions.REQUEST_READ_SMS:
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Log.d(LOG_TAG, "permission_granted");
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

	public void getToken(){
		valueContainer = new ValueContainer();
		valueContainer.setSourceMdn(mobileNumber);
		valueContainer.setTransactionName(Constants.SERVICE_MIGRATE_TOKENSIMPLUS);
		valueContainer.setServiceName(Constants.SERVICE_ACCOUNT);
		valueContainer.setChannelId(Constants.CONSTANT_CHANNEL_ID);

		final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, HomeScreen.this);

		final ProgressDialog dialog = new ProgressDialog(HomeScreen.this, R.style.MyAlertDialogStyle);
		dialog.setCancelable(false);
		dialog.setTitle("Bank Sinarmas");
		dialog.setMessage("Loading....   ");
		dialog.show();

		@SuppressLint("HandlerLeak")
		final Handler handler = new Handler() {

			public void handleMessage(Message msg) {

				if (responseXml != null) {
					XMLParser obj = new XMLParser();
					EncryptedResponseDataContainer responseContainer = null;
					try {
						responseContainer = obj.parse(responseXml);
					} catch (Exception e) {
						e.printStackTrace();
					}

					dialog.dismiss();

					assert responseContainer != null;
					if (responseContainer.getMsg() == null) {

						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_transactionFail));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_transactionFail));
						}
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
							}
						});
						alertbox.show();
					} else if (responseContainer.getMsgCode().equals("2183")) {
						//sukses
						token = responseContainer.getMigrateToken();
						Log.d(LOG_TAG, "token value: "+token);
					} else if (responseContainer.getMsgCode().equals("631")) {
						alertbox.setMessage(responseContainer.getMsg());
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								Intent intent = new Intent(getBaseContext(), LoginScreen.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
						});
						alertbox.show();
					} else {
						alertbox.setMessage(responseContainer.getMsg());
						alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
								finish();
							}
						});
						alertbox.show();
					}

				} else {
					dialog.dismiss();
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
					}
					alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.dismiss();
							finish();
						}
					});
					alertbox.show();
				}
			}
		};

		final Thread checkUpdate = new Thread() {

			public void run() {
				/* Service calling in thread. */
				try {
					responseXml = webServiceHttp.getResponseSSLCertificatation();
				} catch (Exception e) {
					responseXml = null;
				}
				handler.sendEmptyMessage(0);
			}
		};
		checkUpdate.start();
	}

	private boolean isPackageInstalled() {
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo("com.simas.mobile.SimobiPlus", 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			Log.d(LOG_TAG, "e: " + e.toString());
		}
		return false;
	}

	private void checkIfSimPlusExist(){
		final boolean isInstalled = isPackageInstalled();
		final RelativeLayout bannerUpgradeLayout = findViewById(R.id.banner_upgrade);
		Log.d(LOG_TAG, "simobiplus installed? " + isInstalled);
		if(isInstalled){
			//if installed: true
			final AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
			builder.setCancelable(true);
			LayoutInflater inflater = this.getLayoutInflater();
			final View dialogView = View.inflate(this, R.layout.layout_upgrade_dialog, null);

			builder.setView(dialogView);

			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				TextView text_label = dialogView.findViewById(R.id.text_label);
				text_label.setText(getResources().getString(R.string.eng_simobiplus_exist));
				Button text_button = dialogView.findViewById(R.id.text_button);
				text_button.setText(getResources().getString(R.string.eng_register_now));
				text_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						bannerUpgradeLayout.setVisibility(View.GONE);
						Log.d(LOG_TAG, "isInstalled: " + isInstalled);
						Intent intent = new Intent (Intent.ACTION_VIEW);
						intent.setData (Uri.parse("smbplus://migrate/#"+token));
						Log.d(LOG_TAG, "smbplus://migrate/#" + token);
						startService(intent);
						//startActivity(intent);
					}
				});
			}else{
				TextView text_label = dialogView.findViewById(R.id.text_label);
				text_label.setText(getResources().getString(R.string.bahasa_simobiplus_exist));
				Button text_button = dialogView.findViewById(R.id.text_button);
				text_button.setText(getResources().getString(R.string.bahasa_register_now));
				text_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						bannerUpgradeLayout.setVisibility(View.GONE);
						Log.d(LOG_TAG, "isInstalled: " + isInstalled);
						Intent intent = new Intent (Intent.ACTION_VIEW);
						intent.setData (Uri.parse("smbplus://migrate/#"+token));
						Log.d(LOG_TAG, "smbplus://migrate/#" + token);
						startService(intent);
						//startActivity(intent);
					}
				});
			}

			dialog = builder.create();
			dialog.show();

		}else{
			//if installed: false
			final AlertDialog.Builder builder = new AlertDialog.Builder(HomeScreen.this);
			builder.setCancelable(true);
			LayoutInflater inflater = this.getLayoutInflater();
			final View dialogView = View.inflate(this, R.layout.layout_upgrade_dialog, null);
			builder.setView(dialogView);

			if (selectedLanguage.equalsIgnoreCase("ENG")) {
				TextView text_label = dialogView.findViewById(R.id.text_label);
				text_label.setText(getResources().getString(R.string.eng_simobiplus_doesntexist));
				Button text_button = dialogView.findViewById(R.id.text_button);
				text_button.setText(getResources().getString(R.string.eng_install_now));
				text_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Log.d(LOG_TAG, "isInstalled: " + isInstalled);
						final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
						}
					}
				});
			}else{
				TextView text_label = dialogView.findViewById(R.id.text_label);
				text_label.setText(getResources().getString(R.string.bahasa_simobiplus_doesntexist));
				Button text_button = dialogView.findViewById(R.id.text_button);
				text_button.setText(getResources().getString(R.string.bahasa_install_now));
				text_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						Log.d(LOG_TAG, "isInstalled: " + isInstalled);
						final String appPackageName = "com.simas.mobile.SimobiPlus"; // getPackageName() from Context or Activity object
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
						}
					}
				});
			}

			dialog = builder.create();
			dialog.show();
		}
	}

	public void setAlertbox(AlertDialog.Builder alertbox) {
		this.alertbox = alertbox;
	}
}