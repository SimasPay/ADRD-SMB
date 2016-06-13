<<<<<<< HEAD
package com.mfino.bsim.transfer;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransferToUangku  extends Activity{
	private Button btn_ok;
	private EditText pinValue,destAccountNo,amountValue;
	private AlertDialog.Builder alertbox;
	private Bundle bundle;
	private String responseXml;
	ValueContainer valueContainer ;
	String otpValue,sctl;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	
	/** Called when the activity is first created. */	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_to_other_bank_details);
		context=this;
		
		//Header code...
		 View headerContainer = findViewById(R.id.header); 
	     TextView screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
	     Button back=(Button)headerContainer.findViewById(R.id.back);
	     Button home=(Button)headerContainer.findViewById(R.id.home_button);
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
				
				Intent intent=new Intent(TransferToUangku.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		destAccountNo = (EditText) findViewById(R.id.ed_destAcNoValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		TextView destAcountTxt=(TextView)findViewById(R.id.fundTransfer_otherBank_destAc);
		TextView amountTxt=(TextView)findViewById(R.id.fundTransfer_otherBank_amount);
		alertbox = new AlertDialog.Builder(this);
		bundle = getIntent().getExtras();
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		//screeTitle.setText(bundle.getString("TAG_NAME"));
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText("My Money");

			//screeTitle.setText(getResources().getString(R.string.eng_toOtherBank));
			destAcountTxt.setText(getResources().getString(R.string.eng_mobileNumber));
			amountTxt.setText(getResources().getString(R.string.eng_amount));
			btn_ok.setText(getResources().getString(R.string.eng_submit));

		} else {
			screeTitle.setText("Uangku");

			//screeTitle.setText(getResources().getString(R.string.bahasa_toOtherBank));
			destAcountTxt.setText(getResources().getString(R.string.bahasa_mobileNumber));
			amountTxt.setText(getResources().getString(R.string.bahasa_amount));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));

		}
		
		

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				//System.out.println("Testing>>>"+bundle.getString("code"));
				boolean networkCheck=ConfigurationUtil.isConnectingToInternet(context);
				if(!networkCheck){
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
					ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_serverNotRespond), context);
					}else{
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_serverNotRespond), context);
					}
									
				}else if (isRequiredFieldEmpty()) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}
					alertbox.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							});
					alertbox.show();
				} else if (pinValue.getText().length() < 4) {
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinLength));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinLength));
					}
					alertbox.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							});
					alertbox.show();
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
					
					  /** Set Paramenters for Call Service.*/
					
					valueContainer = new ValueContainer();
					valueContainer.setTransactionName(Constants.TRANSACTION_Uangku_INQUIRY);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setDestinationBankAccount(destAccountNo.getText().toString().trim());
					valueContainer.setSourcePin(pinValue.getText().toString().trim());
					valueContainer.setAmount(amountValue.getText().toString().trim());
					//valueContainer.setBankCode(bundle.getString("code").trim());
					valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
					valueContainer.setDestinationPocketCode("2");
					valueContainer.setServiceName(Constants.SERVICE_BANK);
					valueContainer.setTransferType("toUnagku");
					
					
					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, TransferToUangku.this);
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = ProgressDialog.show(TransferToUangku.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

					} else {
						dialog = ProgressDialog.show(TransferToUangku.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
					}
					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {

							if (responseXml != null) {
								/** Parse response xml. */
								XMLParser obj = new XMLParser();
								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
								} catch (Exception e) {

									e.printStackTrace();
								}

								//dialog.dismiss();

								int msgCode = 0;
								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
								} catch (Exception e) {
									msgCode = 0;
								}

								if (!(msgCode == 72)) {
									if (responseContainer.getMsg() == null) {

										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
										}

									} else {
										alertbox.setMessage(responseContainer.getMsg());
									}

									alertbox.setNeutralButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface arg0,int arg1) {
													dialog.dismiss();


													Intent intent = new Intent(getBaseContext(),HomeScreen.class);
													intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													startActivity(intent);
													pinValue.setText("");

												}
											});
									alertbox.show();

								} else {
									
									//dialog.dismiss();
									try {
										System.out.println("MFA MODE.."+ responseContainer.getMfaMode());
										if(responseContainer.getMfaMode()==null){
											valueContainer.setMfaMode("NONE");
										}else{
											valueContainer.setMfaMode(responseContainer.getMfaMode());
										}
										
										
										} catch (Exception e1) {
											System.out.println("Testing>>MFAMODE>>exception");
											valueContainer.setMfaMode("NONE");
										}
										if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
									
									try {
										
										 //final ProgressDialog dialog1 = ProgressDialog.show(ToOtherBankDetails.this, "  Banksinarmas               ", "Please Wait for SMS....   ", true);
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
												
												System.out.println("Testing>>SCTL>>else");
												if(new java.util.Date().getTime() - startTimeInMillis>=Constants.MFA_CONNECTION_TIMEOUT){
													System.out.println("Testing>>TimeOut>>");
													break;
												}
													
												
											}
											
										}
										
										if(otpValue==null){
											//dialog1.dismiss();
											System.out.println("Testing>>OTP>>null");
											
											if (selectedLanguage.equalsIgnoreCase("ENG")) {
												
												alertbox.setMessage(getResources().getString(R.string.eng_transactionFail));
											} else {
												alertbox.setMessage(getResources().getString(R.string.bahasa_transactionFail));
											}
											
											 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								          public void onClick(DialogInterface arg0, int arg1) {
								        	  dialog.dismiss();
								        	  
								          	}
											 });
											 alertbox.show();
										}else{
											//dialog1.dismiss();
											dialog.dismiss();
											System.out.println("Testing>>OTP>>not null");
											Intent intent = new Intent(TransferToUangku.this,TransferToUnagkuConfirmation.class);
											intent.putExtra("SRCPOCKETCODE", "2");
											intent.putExtra("PIN", pinValue.getText().toString());
											intent.putExtra("CUST_NAME",responseContainer.getCustName());
											//intent.putExtra("DEST_NUMBER",responseContainer.getDestMDN());
											intent.putExtra("DEST_BANK", responseContainer.getDestBank());
											intent.putExtra("DEST_ACCOUNT_NUM",	responseContainer.getAccountNumber());
											intent.putExtra("AMOUNT",responseContainer.getAmount());
											intent.putExtra("AMT", amountValue.getText().toString());
											intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
											intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
											intent.putExtra("TRANSFER_TYPE",valueContainer.getTransferType());
											intent.putExtra("MSG",responseContainer.getMsg());
											intent.putExtra("OTP", otpValue);
											intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
											intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
											}

										} catch (Exception e) {
											System.out.println("Testing>>exception>>");
										}
									}else{

										Intent intent = new Intent(TransferToUangku.this,TransferToUnagkuConfirmation.class);
										intent.putExtra("SRCPOCKETCODE", "2");
										intent.putExtra("PIN", pinValue.getText().toString());
										intent.putExtra("CUST_NAME",responseContainer.getCustName());
										//intent.putExtra("DEST_NUMBER",responseContainer.getDestMDN());
										intent.putExtra("DEST_BANK", responseContainer.getDestBank());
										intent.putExtra("DEST_ACCOUNT_NUM",	responseContainer.getAccountNumber());
										intent.putExtra("AMOUNT",responseContainer.getAmount());
										intent.putExtra("AMT", amountValue.getText().toString());
										intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
										intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
										intent.putExtra("TRANSFER_TYPE",valueContainer.getTransferType());
										intent.putExtra("MSG",responseContainer.getMsg());
										intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
												dialog.dismiss();
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
						/** Service call inside the thread. */
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
	  /** Empty Field Validation. */
	private boolean isRequiredFieldEmpty() {

		destAccountNo = (EditText) findViewById(R.id.ed_destAcNoValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);

		if(!(destAccountNo.getText().toString().equals(""))&&!(amountValue.getText().toString().equals(""))&&!(pinValue.getText().toString().equals("")))
		{
			return false;
		}
		else{
			return true;
		}
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
=======
package com.mfino.bsim.transfer;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TransferToUangku  extends Activity{
	private Button btn_ok;
	private EditText pinValue,destAccountNo,amountValue;
	private AlertDialog.Builder alertbox;
	private Bundle bundle;
	private String responseXml;
	ValueContainer valueContainer ;
	String otpValue,sctl;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	
	/** Called when the activity is first created. */	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_to_other_bank_details);
		context=this;
		
		//Header code...
		 View headerContainer = findViewById(R.id.header); 
	     TextView screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
	     Button back=(Button)headerContainer.findViewById(R.id.back);
	     Button home=(Button)headerContainer.findViewById(R.id.home_button);
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
				
				Intent intent=new Intent(TransferToUangku.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		destAccountNo = (EditText) findViewById(R.id.ed_destAcNoValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		TextView destAcountTxt=(TextView)findViewById(R.id.fundTransfer_otherBank_destAc);
		TextView amountTxt=(TextView)findViewById(R.id.fundTransfer_otherBank_amount);
		alertbox = new AlertDialog.Builder(this);
		bundle = getIntent().getExtras();
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		//screeTitle.setText(bundle.getString("TAG_NAME"));
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText("My Money");

			//screeTitle.setText(getResources().getString(R.string.eng_toOtherBank));
			destAcountTxt.setText(getResources().getString(R.string.eng_mobileNumber));
			amountTxt.setText(getResources().getString(R.string.eng_amount));
			btn_ok.setText(getResources().getString(R.string.eng_submit));

		} else {
			screeTitle.setText("Uangku");

			//screeTitle.setText(getResources().getString(R.string.bahasa_toOtherBank));
			destAcountTxt.setText(getResources().getString(R.string.bahasa_mobileNumber));
			amountTxt.setText(getResources().getString(R.string.bahasa_amount));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));

		}
		
		

		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				//System.out.println("Testing>>>"+bundle.getString("code"));
				boolean networkCheck=ConfigurationUtil.isConnectingToInternet(context);
				if(!networkCheck){
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
					ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_serverNotRespond), context);
					}else{
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_serverNotRespond), context);
					}
									
				}else if (isRequiredFieldEmpty()) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}
					alertbox.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							});
					alertbox.show();
				} else if (pinValue.getText().length() < 4) {
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinLength));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinLength));
					}
					alertbox.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							});
					alertbox.show();
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
					
					  /** Set Paramenters for Call Service.*/
					
					valueContainer = new ValueContainer();
					valueContainer.setTransactionName(Constants.TRANSACTION_Uangku_INQUIRY);
					valueContainer.setSourceMdn(Constants.SOURCE_MDN_NAME);
					valueContainer.setDestinationBankAccount(destAccountNo.getText().toString().trim());
					valueContainer.setSourcePin(pinValue.getText().toString().trim());
					valueContainer.setAmount(amountValue.getText().toString().trim());
					//valueContainer.setBankCode(bundle.getString("code").trim());
					valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
					valueContainer.setDestinationPocketCode("2");
					valueContainer.setServiceName(Constants.SERVICE_BANK);
					valueContainer.setTransferType("toUnagku");
					
					
					final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, TransferToUangku.this);
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						dialog = ProgressDialog.show(TransferToUangku.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

					} else {
						dialog = ProgressDialog.show(TransferToUangku.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
					}
					final Handler handler = new Handler() {

						public void handleMessage(Message msg) {

							if (responseXml != null) {
								/** Parse response xml. */
								XMLParser obj = new XMLParser();
								EncryptedResponseDataContainer responseContainer = null;
								try {
									responseContainer = obj.parse(responseXml);
								} catch (Exception e) {

									e.printStackTrace();
								}

								//dialog.dismiss();

								int msgCode = 0;
								try {
									msgCode = Integer.parseInt(responseContainer.getMsgCode());
								} catch (Exception e) {
									msgCode = 0;
								}

								if (!(msgCode == 72)) {
									if (responseContainer.getMsg() == null) {

										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
										}

									} else {
										alertbox.setMessage(responseContainer.getMsg());
									}

									alertbox.setNeutralButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(DialogInterface arg0,int arg1) {
													dialog.dismiss();


													Intent intent = new Intent(getBaseContext(),HomeScreen.class);
													intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													startActivity(intent);
													pinValue.setText("");

												}
											});
									alertbox.show();

								} else {
									
									//dialog.dismiss();
									try {
										System.out.println("MFA MODE.."+ responseContainer.getMfaMode());
										if(responseContainer.getMfaMode()==null){
											valueContainer.setMfaMode("NONE");
										}else{
											valueContainer.setMfaMode(responseContainer.getMfaMode());
										}
										
										
										} catch (Exception e1) {
											System.out.println("Testing>>MFAMODE>>exception");
											valueContainer.setMfaMode("NONE");
										}
										if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
									
									try {
										
										 //final ProgressDialog dialog1 = ProgressDialog.show(ToOtherBankDetails.this, "  Banksinarmas               ", "Please Wait for SMS....   ", true);
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
												
												System.out.println("Testing>>SCTL>>else");
												if(new java.util.Date().getTime() - startTimeInMillis>=Constants.MFA_CONNECTION_TIMEOUT){
													System.out.println("Testing>>TimeOut>>");
													break;
												}
													
												
											}
											
										}
										
										if(otpValue==null){
											//dialog1.dismiss();
											System.out.println("Testing>>OTP>>null");
											
											if (selectedLanguage.equalsIgnoreCase("ENG")) {
												
												alertbox.setMessage(getResources().getString(R.string.eng_transactionFail));
											} else {
												alertbox.setMessage(getResources().getString(R.string.bahasa_transactionFail));
											}
											
											 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
								          public void onClick(DialogInterface arg0, int arg1) {
								        	  dialog.dismiss();
								        	  
								          	}
											 });
											 alertbox.show();
										}else{
											//dialog1.dismiss();
											dialog.dismiss();
											System.out.println("Testing>>OTP>>not null");
											Intent intent = new Intent(TransferToUangku.this,TransferToUnagkuConfirmation.class);
											intent.putExtra("SRCPOCKETCODE", "2");
											intent.putExtra("PIN", pinValue.getText().toString());
											intent.putExtra("CUST_NAME",responseContainer.getCustName());
											//intent.putExtra("DEST_NUMBER",responseContainer.getDestMDN());
											intent.putExtra("DEST_BANK", responseContainer.getDestBank());
											intent.putExtra("DEST_ACCOUNT_NUM",	responseContainer.getAccountNumber());
											intent.putExtra("AMOUNT",responseContainer.getAmount());
											intent.putExtra("AMT", amountValue.getText().toString());
											intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
											intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
											intent.putExtra("TRANSFER_TYPE",valueContainer.getTransferType());
											intent.putExtra("MSG",responseContainer.getMsg());
											intent.putExtra("OTP", otpValue);
											intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
											intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
											}

										} catch (Exception e) {
											System.out.println("Testing>>exception>>");
										}
									}else{

										Intent intent = new Intent(TransferToUangku.this,TransferToUnagkuConfirmation.class);
										intent.putExtra("SRCPOCKETCODE", "2");
										intent.putExtra("PIN", pinValue.getText().toString());
										intent.putExtra("CUST_NAME",responseContainer.getCustName());
										//intent.putExtra("DEST_NUMBER",responseContainer.getDestMDN());
										intent.putExtra("DEST_BANK", responseContainer.getDestBank());
										intent.putExtra("DEST_ACCOUNT_NUM",	responseContainer.getAccountNumber());
										intent.putExtra("AMOUNT",responseContainer.getAmount());
										intent.putExtra("AMT", amountValue.getText().toString());
										intent.putExtra("PTFNID", responseContainer.getEncryptedParentTxnId());
										intent.putExtra("TFNID", responseContainer.getEncryptedTransferId());
										intent.putExtra("TRANSFER_TYPE",valueContainer.getTransferType());
										intent.putExtra("MSG",responseContainer.getMsg());
										intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
												dialog.dismiss();
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
						/** Service call inside the thread. */
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
	  /** Empty Field Validation. */
	private boolean isRequiredFieldEmpty() {

		destAccountNo = (EditText) findViewById(R.id.ed_destAcNoValue);
		amountValue = (EditText) findViewById(R.id.ed_amountValue);
		pinValue = (EditText) findViewById(R.id.ed_pinValue);

		if(!(destAccountNo.getText().toString().equals(""))&&!(amountValue.getText().toString().equals(""))&&!(pinValue.getText().toString().equals("")))
		{
			return false;
		}
		else{
			return true;
		}
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
>>>>>>> c27489062df6371d180a3ff039d9dc73fb2d6b61
