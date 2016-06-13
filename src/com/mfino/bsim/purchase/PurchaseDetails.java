package com.mfino.bsim.purchase;

import java.util.ArrayList;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.billpayment.PaymentDetails;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.bsim.transfer.ConfirmAddReceiver;
import com.mfino.bsim.transfer.SmartFrenDetails;

public class PurchaseDetails extends Activity {

	private static final int SUCCESS_MSGCODE = 660;
	private Button btn_ok;
	private EditText pinValue, mdn,  amount;
	private AlertDialog.Builder alertbox;
	private String responseXml;
	ValueContainer valueContainer;
	private Bundle bundle;
	int msgCode = 0, length;
	ArrayList<String> denoms = new ArrayList<String>();
	String denomArray[];
	Spinner denomSpinner;
	String denomValue;
	TextView amountTextView;
	TextView denom;
	String smsValue;
	String sctl,otpValue,paymentMode;
	int denomSize;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	ArrayList<String> packageCode=new ArrayList<String>();
	ArrayList<String> packageValue=new ArrayList<String>();
	Context context;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.purchase_details);
		context=this;
		
		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		Button back = (Button) headerContainer.findViewById(R.id.back);
		Button home = (Button) headerContainer.findViewById(R.id.home_button);
		
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(PurchaseDetails.this, HomeScreen.class));
			}
		});

		bundle = getIntent().getExtras();
		System.out.println("Test>>>" + bundle.getString("PRODUCT_DENOM") + ">>");
		System.out.println("******** invoice>>>*"+bundle.getString("SELECTED_INVOICETYPE"));
		System.out.println("******** paymentMode>>>*"+bundle.getString("SELECTED_PAYMENT_MODE"));
		//LinearLayout invoice = (LinearLayout) findViewById(R.id.invoiceNumber);
		LinearLayout mdnLayout = (LinearLayout) findViewById(R.id.mdnLayout);
		LinearLayout amountLayout = (LinearLayout) findViewById(R.id.amountLayout);
		RelativeLayout denomLayout = (RelativeLayout) findViewById(R.id.denomLayout);
		mdn = (EditText) findViewById(R.id.ed_mdnValue);

		amount = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		denomSpinner = (Spinner) findViewById(R.id.denom_spinner);
		amountTextView = (TextView) findViewById(R.id.amount_textView);

		denom = (TextView) findViewById(R.id.denom);
		TextView textViewdestMdn=(TextView)findViewById(R.id.textView_purchaseDestMDN);
		TextView textViewamount=(TextView)findViewById(R.id.amount_textView);
		
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		//InvoiceType parsing
		String inVoiceType=bundle.getString("SELECTED_INVOICETYPE");
		String part[] = null;
		if(inVoiceType!=null){
			 part=inVoiceType.split("\\|");
			//System.out.println(part[0]+"Test>>"+part[1]);
		}
		
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			screeTitle.setText(getResources().getString(R.string.eng_purchase));
			//textViewdestMdn.setText(getResources().getString(R.string.eng_destnatin_mdn));
			textViewdestMdn.setText(part[0]);
			//textViewinVoiceNumber.setText(getResources().getString(R.string.eng_invoiceNumber));
			amountTextView.setText(getResources().getString(R.string.eng_amount));
			denom.setText(getResources().getString(R.string.eng_availableDenoms));
			btn_ok.setText(getResources().getString(R.string.eng_submit));

		} else {
			
			screeTitle.setText(getResources().getString(R.string.bahasa_purchase));
			textViewdestMdn.setText(part[1]);
			//textViewinVoiceNumber.setText(getResources().getString(R.string.bahasa_invoiceNumber));
			denom.setText(getResources().getString(R.string.bahasa_availableDenoms));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));

		}
		
		//Amount or Denom decision
		paymentMode=bundle.getString("SELECTED_PAYMENT_MODE");
		
		if(paymentMode.equalsIgnoreCase("FullAmount")){
			
			amountLayout.setVisibility(View.VISIBLE);
			denomLayout.setVisibility(View.GONE);
			
		}else if(paymentMode.equalsIgnoreCase("ZeroAmount")){
			if(bundle.getBoolean("IS_PLN_PREPAID"))
				amountLayout.setVisibility(View.VISIBLE);
			else
				amountLayout.setVisibility(View.GONE);
			
				denomLayout.setVisibility(View.GONE);
				denomValue="0";
			
		}else if(paymentMode.equalsIgnoreCase("Denom")){
			
			String data=bundle.getString("PRODUCT_DENOM");
			denomArray=data.split("\\|");
			for (int i = 0; i < denomArray.length; i++) {
				denoms.add(denomArray[i]);
				System.out.println(denomArray[i]+"Test>>");
			}
			

			amountLayout.setVisibility(View.GONE);
			denomLayout.setVisibility(View.VISIBLE);
			amount.setVisibility(View.GONE);
			amountTextView.setVisibility(View.GONE);
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseDetails.this, R.layout.spinner_row, denomArray);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			denomSpinner.setAdapter(dataAdapter);
			
			denomSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
							denomValue = denomArray[arg2];
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
			
		}else if(paymentMode.equalsIgnoreCase("PackageType")){
			
			String data=bundle.getString("PRODUCT_DENOM");
			denomArray=data.split("\\|");
			/*
			for (int i = 0; i < denomArray.length; i++) {
				denoms.add(denomArray[i]);
				System.out.println(denomArray[i]+"Test>>");
			}*/
			
			for (int i = 0; i < denomArray.length; i++) {
				
				String code1=denomArray[i].substring(denomArray[i].indexOf("[")+1, denomArray[i].indexOf("]"));
				String value1=denomArray[i].substring(denomArray[i].indexOf("]")+1, denomArray[i].length());
				packageCode.add(code1);
				packageValue.add(value1);
				
				System.out.println(value1+"Test>>"+code1);
				
			}

			amountLayout.setVisibility(View.GONE);
			denomLayout.setVisibility(View.VISIBLE);
			amount.setVisibility(View.GONE);
			amountTextView.setVisibility(View.GONE);
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(PurchaseDetails.this, R.layout.spinner_row, packageValue);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			denomSpinner.setAdapter(dataAdapter);
			
			denomSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
						@Override
						public void onItemSelected(AdapterView<?> arg0,View arg1, int arg2, long arg3) {
							
							denomValue = packageCode.get(arg2);
							//denomValue=denomValue.substring(1, denomValue.indexOf("]"));
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {

						}
					});
			
		}else {
			amountLayout.setVisibility(View.GONE);
			denomLayout.setVisibility(View.GONE);
			
		}
				
	
		alertbox = new AlertDialog.Builder(this);
		
		
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				boolean networkCheck=ConfigurationUtil.isConnectingToInternet(context);
				if(!networkCheck){
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
					ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_serverNotRespond), context);
					}else{
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_serverNotRespond), context);
					}
									
				}else if (isRequiredFieldEmpty()) {

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						displayDialog(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						displayDialog(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}

				} else if (pinValue.getText().length() < 4) {
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						displayDialog(getResources().getString(R.string.eng_pinLength));
					} else {
						displayDialog(getResources().getString(R.string.bahasa_pinLength));
					}

				} else {
					
					if(paymentMode.equalsIgnoreCase("FullAmount")){
						
						denomValue=amount.getText().toString();
						
					}else if(paymentMode.equalsIgnoreCase("ZeroAmount")){
						if(bundle.getBoolean("IS_PLN_PREPAID"))
							denomValue=amount.getText().toString();
						else
							denomValue="0";
						
					}
					

					/** Set Parameters for Service Calling. */
					valueContainer = new ValueContainer();
					 int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				     if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
				         if ((checkCallingOrSelfPermission(android.Manifest.permission.READ_SMS)
				                 != PackageManager.PERMISSION_GRANTED) && checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS)
				                 != PackageManager.PERMISSION_GRANTED) {

				             requestPermissions(new String[]{Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS},
				                     109);
				         } 
				     }

					if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase("Mobile Phone")) {

						System.out.println("Testing>>>airtime");
						valueContainer.setServiceName(Constants.SERVICE_BUY);
						valueContainer.setTransactionName(Constants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY);
						valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
						valueContainer.setSourcePin(pinValue.getText().toString());
						valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
						valueContainer.setDestinationMdn(mdn.getText().toString().trim());
						valueContainer.setAmount(denomValue);
						valueContainer.setPaymentMode(paymentMode);
						valueContainer.setCompanyId(bundle.getString("PRODUCT_CODE"));

					} else {

						System.out.println("Testing>>>purchase");
						valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
						valueContainer.setTransactionName(Constants.TRANSACTION_BILLPAYMENT_INQUIRY);
						valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
						valueContainer.setSourcePin(pinValue.getText().toString());
						valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
						valueContainer.setAmount(denomValue);
						valueContainer.setPaymentMode(paymentMode);
						valueContainer.setBillerCode(bundle.getString("PRODUCT_CODE"));
						valueContainer.setBillNo(mdn.getText().toString());
						
					}

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, PurchaseDetails.this);

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = ProgressDialog.show(PurchaseDetails.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

					} else {
						dialog = ProgressDialog.show(PurchaseDetails.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
					}
					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {
							if (responseXml != null) {
								/** Parse the response. */
								XMLParser obj = new XMLParser();
								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
									System.out.print("Testing>>"+responseContainer.getMfaMode());
								} catch (Exception e) {

									// e.printStackTrace();
								}

								//dialog.dismiss();

								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
								} catch (Exception e) {
									msgCode = 0;
								}
								
								
								
								if (!((msgCode == SUCCESS_MSGCODE)
										|| (msgCode == 72) || (msgCode == 713) || (msgCode == 660))) {
									if (responseContainer.getMsg() == null) {
										
										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											displayDialog(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											displayDialog(getResources().getString(R.string.bahasa_serverNotRespond));
										}
									} else {
										displayDialog(responseContainer.getMsg());
									}


								} else {
									
									//dialog.dismiss();
									try {
										if(responseContainer.getMfaMode()==null){
											valueContainer.setMfaMode("NONE");
											
										}else{
											valueContainer.setMfaMode(responseContainer.getMfaMode());
										}
										
									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
									}
									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
									try {
										
										// final ProgressDialog dialog1 = ProgressDialog.show(PurchaseDetails.this, "  Banksinarmas               ", "Please Wait for SMS....   ", true);
								 		Long startTimeInMillis = new java.util.Date().getTime();
								 		
										while(true){
											
											Thread.sleep(3000);
											System.out.println("Testing>>inside Loop");
											final Uri SMS_INBOX = Uri.parse("content://sms/inbox");
											Cursor c = getContentResolver().query(SMS_INBOX, null,null, null,"DATE desc");

											c.moveToFirst();
											for (int i = 0; i < 10; i++) {
												String body = c.getString(c.getColumnIndexOrThrow("body")).toString().trim();
												String number = c.getString(c.getColumnIndexOrThrow("address")).toString();
												
												if(body.contains("Kode Simobi Anda")&&body.contains(responseContainer.getSctl())){
													
													otpValue=body.substring(new String("Kode Simobi Anda ").length(), body.indexOf("(no ref"));
													sctl=body.substring(body.indexOf(":")+1, body.indexOf(")"));
													break;
													
												}else if(body.contains("Your Simobi Code is")&&body.contains(responseContainer.getSctl())){
													
													otpValue=body.substring(new String("Your Simobi Code is ").length(), body.indexOf("(ref"));
													sctl=body.substring(body.indexOf("(ref no: ")+new String("(ref no: ").length(), body.indexOf(")"));
													break;
												}else{
													c.moveToNext();
												}
												
											}
											c.close();
											
											if(!(otpValue==null)){
												System.out.println("Testing>>SCTL");
												break;
											}else{
												
												System.out.println("Testing>>SCTL>>else");
												if(new java.util.Date().getTime() - startTimeInMillis>=Constants.MFA_CONNECTION_TIMEOUT){
													System.out.println("Testing>>TimeOut>>");
													break;
												}
												
											}
											
										}
										System.out.println("Testing>>OTP>>"+ otpValue);
										if(otpValue==null){
											
											if (selectedLanguage.equalsIgnoreCase("ENG")) {
												displayDialog(getResources().getString(R.string.eng_transactionFail));
											} else {
												displayDialog(getResources().getString(R.string.bahasa_transactionFail));
											}
											 
										}else{
											dialog.dismiss();
											
											if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase("Mobile Phone")) {
												System.out.println("Testing>>>airtime");
												Intent intent = new Intent(PurchaseDetails.this,BuyConfirm.class);
												intent.putExtra("SELECTED_CATEGORY",bundle.getString("SELECTED_CATEGORY"));
												intent.putExtra("PIN", pinValue.getText().toString());
												intent.putExtra("AMT", denomValue);
												intent.putExtra("DESTMDN", mdn.getText().toString().trim());
												intent.putExtra("COMPID", bundle.getString("PRODUCT_CODE"));
												intent.putExtra("MSG",responseContainer.getMsg());
												intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
												intent.putExtra("PTFNID",responseContainer.getEncryptedParentTxnId());
												intent.putExtra("TFNID",responseContainer.getEncryptedTransferId());
												try {
													intent.putExtra("ADDITIONAL_INFO",responseContainer.getAditionalInfo());
													
												} catch (Exception e) {

													intent.putExtra("ADDITIONAL_INFO","null");
												}
												intent.putExtra("OTP", otpValue);
												intent.putExtra("MFA_MODE",responseContainer.getMfaMode());

												startActivity(intent);
											} else {
												System.out.println("Testing>>>purchase");
												Intent intent = new Intent(PurchaseDetails.this,BuyConfirm.class);
												intent.putExtra("PIN", pinValue.getText().toString());
												intent.putExtra("SELECTED_CATEGORY",bundle.getString("SELECTED_CATEGORY"));
												intent.putExtra("MSG",responseContainer.getMsg());
												intent.putExtra("PRODUCT_CODE", bundle.getString("PRODUCT_CODE"));
												intent.putExtra("BILLERNUM",mdn.getText().toString());

												try {
													intent.putExtra("ADDITIONAL_INFO",responseContainer.getAditionalInfo());
												} catch (Exception e) {

													intent.putExtra("ADDITIONAL_INFO","null");
												}
												
												intent.putExtra("PTFNID",responseContainer.getEncryptedParentTxnId());
												intent.putExtra("TFNID",responseContainer.getEncryptedTransferId());
												intent.putExtra("OTP", otpValue);
												intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
												intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
												startActivity(intent);

											}
										}
										
									} catch (Exception e) {
										System.out.println("Testing>>exception>>");
									}
									}else {
										System.out.println("Testing>>>purchase");
										Intent intent = new Intent(PurchaseDetails.this,BuyConfirm.class);
										intent.putExtra("AMT", denomValue);
										intent.putExtra("DESTMDN", mdn.getText().toString().trim());
										intent.putExtra("COMPID", bundle.getString("PRODUCT_CODE"));
										intent.putExtra("PIN", pinValue.getText().toString());
										intent.putExtra("SELECTED_CATEGORY",bundle.getString("SELECTED_CATEGORY"));
										intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
										intent.putExtra("MSG",responseContainer.getMsg());
										intent.putExtra("PRODUCT_CODE", bundle.getString("PRODUCT_CODE"));
										intent.putExtra("BILLERNUM",mdn.getText().toString());

										try {
											intent.putExtra("ADDITIONAL_INFO",responseContainer.getAditionalInfo());
										} catch (Exception e) {

											intent.putExtra("ADDITIONAL_INFO","null");
										}
										
										intent.putExtra("PTFNID",responseContainer.getEncryptedParentTxnId());
										intent.putExtra("TFNID",responseContainer.getEncryptedTransferId());
										startActivity(intent);
										
									}
									
								}

								pinValue.setText("");

							} else {

								dialog.dismiss();
								if (selectedLanguage.equalsIgnoreCase("ENG")) {
									alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
								} else {
									alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
								}
								alertbox.setNeutralButton("OK",new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface arg0,int arg1) {

												Intent intent = new Intent(getBaseContext(),HomeScreen.class);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
											}
										});
								alertbox.show();
							}

						}
					};

					final Thread checkUpdate = new Thread() {
						/** Service call in this thread. */
						public void run() {

							try {
								responseXml = webServiceHttp
										.getResponseSSLCertificatation();
								System.out.println("Testing>>");
							} catch (Exception e) {
								responseXml = null;
							}
							handler.sendEmptyMessage(0);
						}
					};
					checkUpdate.start();

				}
			}
		});

	}

	/** Empty field validation. */
	private boolean isRequiredFieldEmpty() {

		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		amount = (EditText) findViewById(R.id.ed_amountValue);
		
		if (bundle.getString("SELECTED_CATEGORY").equalsIgnoreCase(	"Mobile Phone")) {
			
			mdn = (EditText) findViewById(R.id.ed_mdnValue);
			//if (!(pinValue.getText().toString().equals(""))&& !(mdn.getText().toString().equals("")))
			if (!(pinValue.getText().toString().equals("")))
			{
				System.out.println("Testing>>false");
				return false;
			} else {
				System.out.println("Testing>>true");
				return true;
			}

		} else {
			mdn = (EditText) findViewById(R.id.ed_mdnValue);
			
			//if (!(pinValue.getText().toString().equals(""))&& !(inVoiceNumber.getText().toString().equals(""))&& !(amount.getText().toString().equals(""))) {
			if (!(pinValue.getText().toString().equals(""))) {
				System.out.println("Testing>>false");
				return false;
			} else {
				System.out.println("Testing>>true");
				return true;
			}
		}

	}
	
	// Dialog Displaying

	public  void displayDialog(String msg) {
		//if(dialog.isShowing()){
			try{
				dialog.dismiss();
				Log.e("haiii", "-----------");

			}catch(Exception e){
				e.printStackTrace();
			}
		
		//}

		alertbox = new AlertDialog.Builder(PurchaseDetails.this);
		alertbox.setMessage(msg);
		alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				try{
				dialog.dismiss();
				}catch(Exception e){
					e.printStackTrace();
				}


				if (msgCode == 631) {
					
					Intent intent = new Intent(getBaseContext(),LoginScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					
				} else if (msgCode == 699) {
					amount.setText("");
				} else {
					
				/*	Intent intent = new Intent(getBaseContext(),HomeScreen.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);*/
				}

				pinValue.setText("");

			}
		});
		alertbox.show();
	}
	
	@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 109) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            	Log.e("if_permission","*********");
              
            } else {
            	Log.e("elseeeee_permission","*********");

            }
        }
    }

}
