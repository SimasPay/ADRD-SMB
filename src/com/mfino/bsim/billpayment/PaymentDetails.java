package com.mfino.bsim.billpayment;

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
import android.text.TextUtils;
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
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.mfino.bsim.ActivationDetails;
import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.LoginScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.account.AccountSelection;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.purchase.PurchaseDetails;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
import com.mfino.bsim.transfer.ConfirmAddReceiver;
import com.mfino.bsim.transfer.SmartFrenDetails;

public class PaymentDetails extends Activity {

	private static final int SUCCESS_MSGCODE = 660;
	private Button btn_ok;
	private EditText pinValue, mdn, amount;
	private AlertDialog.Builder alertbox;
	private String responseXml;
	ValueContainer valueContainer;
	private Bundle bundle;
	int msgCode = 0;
	public String invoiceNumber,amountValue,paymentMode;
	String otpValue,sctl;
	String mfaMode;
	SharedPreferences languageSettings;
	Spinner denomSpinner;
	String selectedLanguage;
	ProgressDialog dialog;
	boolean fieldCheck;
	ArrayList<String> denoms = new ArrayList<String>();
	String denomArray[];
	ArrayList<String> packageCode=new ArrayList<String>();
	ArrayList<String> packageValue=new ArrayList<String>();
	Context context;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_details);
		context=this;

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		Button back = (Button) headerContainer.findViewById(R.id.back);
		Button home = (Button) headerContainer.findViewById(R.id.home_button);
		
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(PaymentDetails.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		bundle = getIntent().getExtras();
		mdn = (EditText) findViewById(R.id.ed_mdnValue);
		amount = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		//LinearLayout invoice = (LinearLayout) findViewById(R.id.invoiceNumber);
		LinearLayout amtLayout = (LinearLayout) findViewById(R.id.amountLayout);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);

		TextView textViewdestMdn=(TextView)findViewById(R.id.textView_paymentDestMDN);
		//TextView textViewinVoiceNumber=(TextView)findViewById(R.id.textView_paymentInvoiceNum);
		TextView textViewamount=(TextView)findViewById(R.id.textView_paymentAmount);
		
		LinearLayout amountLayout = (LinearLayout) findViewById(R.id.amountLayout);
		//RelativeLayout denomLayout = (RelativeLayout) findViewById(R.id.denomLayout);
		denomSpinner = (Spinner) findViewById(R.id.denom_spinner);
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		
		String inVoiceType=bundle.getString("SELECTED_INVOICETYPE");
		String part[]=inVoiceType.split("\\|");
		System.out.println(part[0]+"Test>>"+part[1]);
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			screeTitle.setText(getResources().getString(R.string.eng_payment));
			textViewdestMdn.setText(part[0]);
			//textViewinVoiceNumber.setText(getResources().getString(R.string.eng_invoiceNumber));
			textViewamount.setText(getResources().getString(R.string.eng_amount));
			//home.setBackgroundResource(R.drawable.home_icon1);
			btn_ok.setText(getResources().getString(R.string.eng_submit));
			//textViewDenom.setText(getResources().getString(R.string.eng_));

		} else {
			
			screeTitle.setText(getResources().getString(R.string.bahasa_payment));
			textViewdestMdn.setText(part[1]);
			//textViewinVoiceNumber.setText(getResources().getString(R.string.bahasa_invoiceNumber));
			textViewamount.setText(getResources().getString(R.string.bahasa_amount));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));
			//home.setBackgroundResource(R.drawable.bahasa_home_icon1);
			//back.setBackgroundResource(R.drawable.bahasa_back_button);

		}
		paymentMode=bundle.getString("SELECTED_PAYMENT_MODE");
		
		if(paymentMode.equalsIgnoreCase("FullAmount")){
				amtLayout.setVisibility(View.VISIBLE);
			
		}else if(paymentMode.equalsIgnoreCase("ZeroAmount")){
			
				amtLayout.setVisibility(View.GONE);
			
		}else {
			amountLayout.setVisibility(View.GONE);
			//denomLayout.setVisibility(View.GONE);
			
		}
		System.out.println("Testing>>>Is CCPayment2>>"+bundle.getBoolean("IS_CCPAYMENT"));
		if(bundle.getBoolean("IS_CCPAYMENT")){
			
			amtLayout.setVisibility(View.GONE);
		
		}

		alertbox = new AlertDialog.Builder(this);
		
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				
		/*		Intent intent = new Intent(PaymentDetails.this,BillPaymentCCBillInquiry.class);
				intent.putExtra("PIN", pinValue.getText().toString());
				intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
				intent.putExtra("MSG","sfdgdsf");
				//intent.putExtra("SELECTED_OFFLINE", bundle.getString("SELECTED_OFFLINE"));
				System.out.println("Testing>>>Admitional INfo>>>>"+ "sk|DKL|DLKD|");
				try {
					intent.putExtra("IS_CCPAYMENT",true);
					intent.putExtra("ADITIONAL_INFO","sk|DKL|DLKD|");
					
				} catch (Exception e) {
	
					intent.putExtra("ADITIONAL_INFO","null");
				}
				intent.putExtra("PRODUCT_CODE",	"35986");
				intent.putExtra("AMOUNT",	"2345");
				intent.putExtra("BILLERNUM", "4295803425");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);*/
				
				

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
					 int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				     if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
				         if ((checkCallingOrSelfPermission(android.Manifest.permission.READ_SMS)
				                 != PackageManager.PERMISSION_GRANTED) && checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS)
				                 != PackageManager.PERMISSION_GRANTED) {

				             requestPermissions(new String[]{Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS},
				                     109);
				         } 
				     }

					
					invoiceNumber = mdn.getText().toString();
					if(bundle.getString("SELECTED_PAYMENT_MODE").equalsIgnoreCase("FullAmount")){
						amountValue=amount.getText().toString().trim();
					
					}else if(bundle.getString("SELECTED_PAYMENT_MODE").equalsIgnoreCase("ZeroAmount")){
						amountValue="0";
					}
					
					if(bundle.getBoolean("IS_CCPAYMENT")){
						
						valueContainer = new ValueContainer();
						valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
						valueContainer.setTransactionName(Constants.TRANSACTION_BILL_INQUIRY);
						valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
						valueContainer.setSourcePin(pinValue.getText().toString());
						valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
						//valueContainer.setAmount(amountValue);
						valueContainer.setPaymentMode(paymentMode);
						valueContainer.setBillerCode(bundle.getString("PRODUCT_CODE"));
						valueContainer.setBillNo(invoiceNumber);
					
					}else{
						valueContainer = new ValueContainer();
						valueContainer.setServiceName(Constants.SERVICE_BILLPAYMENT);
						valueContainer.setTransactionName(Constants.TRANSACTION_BILLPAYMENT_INQUIRY);
						valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
						valueContainer.setSourcePin(pinValue.getText().toString());
						valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
						valueContainer.setAmount(amountValue);
						valueContainer.setPaymentMode(paymentMode);
						valueContainer.setBillerCode(bundle.getString("PRODUCT_CODE"));
						valueContainer.setBillNo(invoiceNumber);
					}
					

					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, PaymentDetails.this);

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = ProgressDialog.show(PaymentDetails.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

					} else {
						dialog = ProgressDialog.show(PaymentDetails.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
					}

					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {
							if (responseXml != null) {
								/** Parse the response. */
								XMLParser obj = new XMLParser();
								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
									System.out.println("messsssage :"+ responseContainer.getMsgCode());
								} catch (Exception e) {

									// e.printStackTrace();
								}

								//dialog.dismiss();

								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
								} catch (Exception e) {
									msgCode = 0;
								}
								System.out.println("messsssage :"+ responseContainer.getMsgCode());
								System.out.println("messsssage :"+ responseContainer.getMfaMode());
								if (!((msgCode == SUCCESS_MSGCODE)|| (msgCode == 72) || (msgCode == 713)||(msgCode == 2021))) {
									if (responseContainer.getMsg() == null) {
										
										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											displayDialog(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											displayDialog(getResources().getString(R.string.bahasa_serverNotRespond));
										}
									} else {
										displayDialog(responseContainer.getMsg());
										System.out.println("Testing>>Buyenter>>rsponse"+ responseContainer.getMsg());
									}


								} else {

									//dialog.dismiss();
									try {
										if(TextUtils.isEmpty(responseContainer.getMfaMode())||TextUtils.equals(responseContainer.getMfaMode(),null)){
											valueContainer.setMfaMode("NONE");
										}else{
											valueContainer.setMfaMode(responseContainer.getMfaMode());
										}
										
									} catch (Exception e1) {
										valueContainer.setMfaMode("NONE");
									}
									if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
										try {

										 //final ProgressDialog dialog1 = ProgressDialog.show(PaymentDetails.this, "  Banksinarmas               ", "Please Wait for SMS....   ", true);
								 		Long startTimeInMillis = new java.util.Date().getTime();
								 		
										while(true){
											
											Thread.sleep(2000);
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
												
												if(new java.util.Date().getTime() - startTimeInMillis>=Constants.MFA_CONNECTION_TIMEOUT){
													break;
												}
													
												
											}
											
										}
										System.out.println("Testing>>OTP>>"+ otpValue);
										if(otpValue==null){
											//dialog1.dismiss();
											System.out.println("Testing>>OTP>>null");
											
											if (selectedLanguage.equalsIgnoreCase("ENG")) {
												
												displayDialog(getResources().getString(R.string.eng_transactionFail));
												
											} else {
												displayDialog(getResources().getString(R.string.bahasa_transactionFail));
											}
											
										}else{
											dialog.dismiss();
											
											if(bundle.getBoolean("IS_CCPAYMENT")){

												Intent intent = new Intent(PaymentDetails.this,BillPaymentCCBillInquiry.class);
												intent.putExtra("PIN", pinValue.getText().toString());
												intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
												intent.putExtra("MSG",responseContainer.getMsg());
												//intent.putExtra("SELECTED_OFFLINE", bundle.getString("SELECTED_OFFLINE"));
												System.out.println("Testing>>>Admitional INfo>>>>"+ responseContainer.getAditionalInfo());
												try {
													
													intent.putExtra("ADITIONAL_INFO",responseContainer.getAditionalInfo());
													intent.putExtra("IS_CCPAYMENT",bundle.getBoolean("IS_CCPAYMENT"));
												} catch (Exception e) {

													intent.putExtra("ADITIONAL_INFO","null");
												}
												intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
												intent.putExtra("PRODUCT_CODE",	bundle.getString("PRODUCT_CODE"));
												intent.putExtra("AMOUNT",	responseContainer.getEncryptedAmount());
												intent.putExtra("BILLERNUM", invoiceNumber);
												intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
												
											}else{
												//dialog1.dismiss();
												Intent intent = new Intent(PaymentDetails.this,BillPaymentConfirm.class);
												intent.putExtra("PIN", pinValue.getText().toString());
												intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
												intent.putExtra("MSG",responseContainer.getMsg());
												//intent.putExtra("SELECTED_OFFLINE", bundle.getString("SELECTED_OFFLINE"));
												System.out.println("Testing>>>Admitional INfo>>>>"+ responseContainer.getAditionalInfo());
												try {
													
													intent.putExtra("ADITIONAL_INFO",responseContainer.getAditionalInfo());
													intent.putExtra("IS_CCPAYMENT",bundle.getBoolean("IS_CCPAYMENT"));
												} catch (Exception e) {

													intent.putExtra("ADITIONAL_INFO","null");
												}
												intent.putExtra("PRODUCT_CODE",	bundle.getString("PRODUCT_CODE"));
												intent.putExtra("BILLERNUM", invoiceNumber);
												intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
												intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
												intent.putExtra("OTP", otpValue);
												intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
												intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
												intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
												startActivity(intent);
											}
											
										}
										
										} catch (Exception e) {
											System.out.println("Testing>>exception>>");
										}
									} else {
										
										//dialog.dismiss();
										if(bundle.getBoolean("IS_CCPAYMENT")){

											Intent intent = new Intent(PaymentDetails.this,BillPaymentCCBillInquiry.class);
											intent.putExtra("PIN", pinValue.getText().toString());
											intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
											intent.putExtra("MSG",responseContainer.getMsg());
											//intent.putExtra("SELECTED_OFFLINE", bundle.getString("SELECTED_OFFLINE"));
											System.out.println("Testing>>>Admitional INfo>>>>"+ responseContainer.getAditionalInfo());
											try {
												
												intent.putExtra("ADITIONAL_INFO",responseContainer.getAditionalInfo());
												intent.putExtra("IS_CCPAYMENT",bundle.getBoolean("IS_CCPAYMENT"));
											} catch (Exception e) {

												intent.putExtra("ADITIONAL_INFO","null");
											}
											intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
											intent.putExtra("PRODUCT_CODE",	bundle.getString("PRODUCT_CODE"));
											intent.putExtra("AMOUNT",	responseContainer.getEncryptedAmount());
											intent.putExtra("BILLERNUM", invoiceNumber);
											intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
											
										}else{
											Intent intent = new Intent(PaymentDetails.this,BillPaymentConfirm.class);
											intent.putExtra("PIN", pinValue.getText().toString());
											intent.putExtra("SELECTED_CATEGORY", bundle.getString("SELECTED_CATEGORY"));
											intent.putExtra("MSG",responseContainer.getMsg());
											//intent.putExtra("SELECTED_OFFLINE", bundle.getString("SELECTED_OFFLINE"));
											System.out.println("Testing>>>Admitional INfo>>>>"+ responseContainer.getAditionalInfo());
											try {
												intent.putExtra("ADITIONAL_INFO",responseContainer.getAditionalInfo());
												intent.putExtra("IS_CCPAYMENT",bundle.getBoolean("IS_CCPAYMENT"));
											} catch (Exception e) {

												intent.putExtra("ADITIONAL_INFO","null");
											}
											intent.putExtra("PRODUCT_CODE",	bundle.getString("PRODUCT_CODE"));
											intent.putExtra("BILLERNUM", invoiceNumber);
											intent.putExtra("SELECTED_PAYMENT_MODE", paymentMode);
											intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
											intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
										}
										
										
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
								responseXml = webServiceHttp.getResponseSSLCertificatation();
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
		
		if (paymentMode.equalsIgnoreCase("FullAmount")) {
			if (bundle.getBoolean("IS_CCPAYMENT")) {
				if (!(pinValue.getText().toString().equals(""))
						&& !(mdn.getText().toString().equals(""))) {
					fieldCheck = false;
				} else {
					fieldCheck = true;
				}

			} else {
				if (!(pinValue.getText().toString().equals(""))
						&& !(mdn.getText().toString().equals(""))
						&& !(amount.getText().toString().equals(""))) {
					fieldCheck = false;
				} else {
					fieldCheck = true;
				}

			}

		} else if (paymentMode.equalsIgnoreCase("ZeroAmount")) {

			if (!(pinValue.getText().toString().equals(""))
					&& !(mdn.getText().toString().equals(""))) {
				fieldCheck = false;
			} else {
				fieldCheck = true;
			}

		}
		return fieldCheck;

	}
	
	// Dialog Displaying

		public  void displayDialog(String msg) {
			try{
				dialog.dismiss();
				}catch(Exception e){
					e.printStackTrace();
				}
			alertbox = new AlertDialog.Builder(PaymentDetails.this);
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
