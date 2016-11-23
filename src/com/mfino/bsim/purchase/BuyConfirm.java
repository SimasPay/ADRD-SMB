package com.mfino.bsim.purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.bsim.ConfirmationScreen;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

public class BuyConfirm extends Activity {

	private Button btn_confirm, btn_cancel;
	private TextView tvConfirmMsg, aditionalInfo;
	private Bundle bundle;
	private String responseXml;
	ValueContainer valueContainer;
	private AlertDialog.Builder alertbox;
	int msgCode = 0;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	SharedPreferences settings;
	public static final String LOG_TAG = "SIMOBI";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);
		settings = getSharedPreferences(LOG_TAG, 0);
        settings.edit().putString("FragName", "BuyConfirm").commit();

		// Header code...
		/*
		 * View headerContainer = findViewById(R.id.header); TextView screeTitle
		 * = (TextView) headerContainer.findViewById(R.id.screenTitle); Button
		 * back = (Button) headerContainer.findViewById(R.id.back); Button home
		 * = (Button) headerContainer.findViewById(R.id.home_button);
		 * aditionalInfo = (TextView) findViewById(R.id.aditional_info);
		 * 
		 * back.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) {
		 * 
		 * finish(); } }); back.setVisibility(View.GONE);
		 * 
		 * home.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * Intent intent=new Intent(BuyConfirm.this,HomeScreen.class);
		 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 * startActivity(intent); } });
		 */

		aditionalInfo = (TextView) findViewById(R.id.aditional_info);
		bundle = getIntent().getExtras();

		btn_confirm = (Button) findViewById(R.id.confirmButton);
		btn_cancel = (Button) findViewById(R.id.cancelButton);

		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {

			btn_confirm.setText(getResources().getString(R.string.eng_confirm));
			btn_cancel.setText(getResources().getString(R.string.eng_cancel));

		} else {

			btn_confirm.setText(getResources().getString(R.string.bahasa_confirm));
			btn_cancel.setText(getResources().getString(R.string.bahasa_cancel));

		}

		tvConfirmMsg = (TextView) findViewById(R.id.tv_Confirm_info);
		alertbox = new AlertDialog.Builder(BuyConfirm.this, R.style.MyAlertDialogStyle);
		tvConfirmMsg.setText(bundle.getString("MSG"));

		try {
			if (bundle.getString("ADDITIONAL_INFO").length() <= 0
					|| bundle.getString("ADDITIONAL_INFO").equalsIgnoreCase("null")) {

				aditionalInfo.setVisibility(View.GONE);

			} else {

				aditionalInfo.setVisibility(View.VISIBLE);
				String adInfo = bundle.getString("ADDITIONAL_INFO");
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
		btn_confirm.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("HandlerLeak")
			@Override
			public void onClick(View arg0) {
				/** Set Parameters for Service call . */
				valueContainer = new ValueContainer();

				if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase("Mobile Phone")) {

					System.out.println("Testing>>>airtime>>BuyCon");
					valueContainer.setServiceName(Constants.SERVICE_BUY);
					valueContainer.setTransactionName(Constants.TRANSACTION_AIRTIME_PURCHASE);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setDestinationMdn(bundle.getString("DESTMDN"));
					valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
					valueContainer.setAmount(bundle.getString("AMT"));
					valueContainer.setPaymentMode(bundle.getString("SELECTED_PAYMENT_MODE"));
					valueContainer.setParentTxnId(bundle.getString("PTFNID"));
					valueContainer.setTransferId(bundle.getString("TFNID"));
					valueContainer.setConfirmed("true");
					valueContainer.setCompanyId(bundle.getString("COMPID"));

				} else {

					System.out.println("Testing>TransID>>" + bundle.getString("TFNID"));
					System.out.println("Testing>PTransID>>" + bundle.getString("PTFNID"));
					valueContainer = new ValueContainer();
					valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
					valueContainer.setTransactionName(Constants.TRANSACTION_BILLPAYMENT);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
					valueContainer.setConfirmed("true");
					valueContainer.setAmount(bundle.getString("AMT"));
					valueContainer.setPaymentMode(bundle.getString("SELECTED_PAYMENT_MODE"));
					valueContainer.setBillerCode(bundle.getString("PRODUCT_CODE"));
					valueContainer.setBillNo(bundle.getString("BILLERNUM"));
					valueContainer.setParentTxnId(bundle.getString("PTFNID"));
					valueContainer.setTransferId(bundle.getString("TFNID"));

				}

				try {

					if (bundle.getString("MFA_MODE").equalsIgnoreCase("OTP")) {
						valueContainer.setOTP(bundle.getString("OTP"));
						valueContainer.setMfaMode(bundle.getString("MFA_MODE"));
					}
				} catch (Exception e1) {

				}

				final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, BuyConfirm.this);

				if (selectedLanguage.equalsIgnoreCase("ENG")) {
					dialog = new ProgressDialog(BuyConfirm.this, R.style.MyAlertDialogStyle);
					dialog.setCancelable(false);
					dialog.setTitle("Bank Sinarmas");
					dialog.setMessage(getResources().getString(R.string.eng_loading));
					dialog.show();
				} else {
					dialog = new ProgressDialog(BuyConfirm.this, R.style.MyAlertDialogStyle);
					dialog.setCancelable(false);
					dialog.setTitle("Bank Sinarmas");
					dialog.setMessage(getResources().getString(R.string.bahasa_loading));
					dialog.show();
				}
				final Handler handler = new Handler() {

					public void handleMessage(Message msg) {

						if (responseXml != null) {
							/** Parse the Response. */
							XMLParser obj = new XMLParser();
							EncryptedResponseDataContainer responseContainer = null;
							try {
								System.out.println("Testing>>>>BuyCon>>xml" + responseXml);
								responseContainer = obj.parse(responseXml);
							} catch (Exception e) {

								// //e.printStackTrace();
							}
							dialog.dismiss();
							try {
								msgCode = Integer.parseInt(responseContainer.getMsgCode());
							} catch (Exception e) {
								msgCode = 0;
							}
							System.out.println("message1 :" + responseContainer.getMsgCode());

							if (responseContainer.getMsg() == null) {
								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
								}
							} else {

								Intent intent = new Intent(BuyConfirm.this, ConfirmationScreen.class);
								intent.putExtra("MSG", responseContainer.getMsg());
								intent.putExtra("ADITIONAL_INFO", responseContainer.getAditionalInfo());
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);

							}

						} else {
							dialog.dismiss();
							if (selectedLanguage.equalsIgnoreCase("ENG")) {
								alertbox.setMessage(getResources().getString(R.string.eng_appTimeout));
							} else {
								alertbox.setMessage(getResources().getString(R.string.bahasa_appTimeout));
							}
							alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0, int arg1) {

								}
							});
							alertbox.show();
						}

					}
				};

				final Thread checkUpdate = new Thread() {

					public void run() {
						/** Service calling in thread. */

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
		});

		btn_cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(BuyConfirm.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	}

}
