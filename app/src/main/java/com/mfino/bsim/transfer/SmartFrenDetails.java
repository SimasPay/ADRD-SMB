package com.mfino.bsim.transfer;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ImageButton;
import android.widget.TextView;

import com.mfino.bsim.activities.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.containers.EncryptedResponseDataContainer;
import com.mfino.bsim.containers.ValueContainer;
import com.mfino.bsim.services.ConfigurationUtil;
import com.mfino.bsim.services.Constants;
import com.mfino.bsim.services.WebServiceHttp;
import com.mfino.bsim.services.XMLParser;
public class SmartFrenDetails extends Activity{
	
	
	private Button btn_ok;
	private EditText mdn,amount,pin;
	private AlertDialog.Builder alertbox;
	
	private String  responseXml ;
	private Bundle bundle;
	ValueContainer valueContainer ;
	private String transferID, parentTransferID, debitAmount, creditAmount, receiverNum;
	//private String screenValue;
	public String otpValue,sctl;
	boolean optCheck=true;
	EncryptedResponseDataContainer responseContainer = null;
	SharedPreferences languageSettings;
	String selectedLanguage;
	ProgressDialog dialog;
	Context context;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_to_smartfren);
		context=this;
		
		//Header code...
		 View headerContainer = findViewById(R.id.header); 
	     TextView screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
	     ImageButton back=(ImageButton)headerContainer.findViewById(R.id.back);
	     ImageButton home=(ImageButton)headerContainer.findViewById(R.id.home_button);
	     back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	     home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(SmartFrenDetails.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		
		bundle = getIntent().getExtras();
		mdn = (EditText) findViewById(R.id.ed_mdnValue);
		amount = (EditText) findViewById(R.id.ed_amountValue);
		pin = (EditText) findViewById(R.id.ed_pinValue);
		btn_ok = (Button) findViewById(R.id.btn_EnterPin_Ok);
		TextView destMDNTxt=(TextView)findViewById(R.id.fundTrasfer_smartfren_mdn);
		TextView amountTxt=(TextView)findViewById(R.id.fundTrasfer_smartFren_amount);
		
		
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",Context.MODE_WORLD_READABLE);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			screeTitle.setText(getResources().getString(R.string.eng_tosmartfren));
			destMDNTxt.setText(getResources().getString(R.string.eng_destnatin_mdn));
			amountTxt.setText(getResources().getString(R.string.eng_amount));
			btn_ok.setText(getResources().getString(R.string.eng_submit));
		} else {
			
			screeTitle.setText(getResources().getString(R.string.bahasa_tosmartfren));
			destMDNTxt.setText(getResources().getString(R.string.bahasa_destnation_mdn));
			amountTxt.setText(getResources().getString(R.string.bahasa_amount));
			btn_ok.setText(getResources().getString(R.string.bahasa_submit));

		}

		alertbox = new AlertDialog.Builder(this);
		
		btn_ok.setOnClickListener(new View.OnClickListener() {
			
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				
			
				boolean networkCheck=ConfigurationUtil.isConnectingToInternet(context);
				if(!networkCheck){
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
					ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_serverNotRespond), context);
					}else{
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_serverNotRespond), context);
					}
									
				}else if(isRequiredFieldEmpty()){
					
					 if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_fieldsNotEmpty));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_fieldsNotEmpty));
						}
						
	                 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
	                  public void onClick(DialogInterface arg0, int arg1) {
	                    
	       		      }
	                 });
	                 alertbox.show();
	                 
					}else if(Integer.parseInt(amount.getText().toString().trim()) < 1){
						
						
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_enterValidMobile));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_enterValidMobile));
						}
						
		                 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
		                  public void onClick(DialogInterface arg0, int arg1) {
		                	  amount.setText("");
		                  }
		                 });
		                 alertbox.show();
					}else if(pin.getText().toString().trim().length()<4){
						
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							alertbox.setMessage(getResources().getString(R.string.eng_pinLength));
						} else {
							alertbox.setMessage(getResources().getString(R.string.bahasa_pinLength));
						}
		                 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
		                  public void onClick(DialogInterface arg0, int arg1) {
		                	  amount.setText("");
		                  }
		                 });
		                 alertbox.show();
					}else{
						
						
						 int currentapiVersion = android.os.Build.VERSION.SDK_INT;
					     if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
					         if ((checkCallingOrSelfPermission(android.Manifest.permission.READ_SMS)
					                 != PackageManager.PERMISSION_GRANTED) && checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS)
					                 != PackageManager.PERMISSION_GRANTED) {

					             requestPermissions(new String[]{Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission.SEND_SMS},
					                     109);
					         } 
					     }
						 /** Set Parameters for Service Calling. */
						
						
						valueContainer = new ValueContainer();
						valueContainer.setTransactionName(Constants.TRANSACTION_TRANSFER_INQUIRY);
						valueContainer.setSourceMdn( Constants.SOURCE_MDN_NAME);
						valueContainer.setSourcePin(pin.getText().toString());
						valueContainer.setDestinationMdn(mdn.getText().toString().trim());
						valueContainer.setAmount(amount.getText().toString().trim());
						valueContainer.setSourcePocketCode(getResources().getString(R.string.source_packet_code));
						valueContainer.setDestinationPocketCode("2");
						valueContainer.setServiceName(Constants.SERVICE_BANK);
						valueContainer.setTransferType("toSmartFren");
								
							
						final WebServiceHttp webServiceHttp = new WebServiceHttp(valueContainer, SmartFrenDetails.this);
						 							
						if (selectedLanguage.equalsIgnoreCase("ENG")) {
							dialog = ProgressDialog.show(SmartFrenDetails.this, "  Banksinarmas               ",getResources().getString(R.string.eng_loading), true);

						} else {
							dialog = ProgressDialog.show(SmartFrenDetails.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);
						}
                       final Handler handler = new Handler() {

                          public void handleMessage(Message msg) {
                       	   
                       	  try {
							// pin = pinValue.getText().toString();
								System.out.println("Handler.....");
							   if(responseXml!=null){   
								   XMLParser obj = new XMLParser();  /** Parsing the response xml . */
								   try {
									   responseContainer =	obj.parse(responseXml);
								   } catch (Exception e) {
									
									e.printStackTrace();
								}
									                        		   
								   dialog.dismiss();	   
								transferID = responseContainer.getEncryptedTransferId();
								parentTransferID = responseContainer.getEncryptedParentTxnId();
								debitAmount = responseContainer.getEncryptedDebitAmount();
								creditAmount = responseContainer.getEncryptedCreditAmount();
									         			
								int msgCode = 0 ;
							try{
								msgCode= Integer.parseInt(responseContainer.getMsgCode());
							}catch(Exception e){
								msgCode=0;
							}
								 
							if(!( (msgCode==72) || (msgCode==684) || (msgCode==676) ) ){
									
									if(responseContainer.getMsg()==null){
										if (selectedLanguage.equalsIgnoreCase("ENG")) {
											alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
										} else {
											alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
										}
									}
									else {        						
										alertbox.setMessage(responseContainer.getMsg()+msgCode);
									}
								alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							      public void onClick(DialogInterface arg0, int arg1) {

										Intent intent = new Intent(getBaseContext(),HomeScreen.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
										pin.setText("");

													}
												});
							     alertbox.show();
							     
								}else{
									
									dialog.dismiss();
									
									try {
										
										valueContainer.setMfaMode(responseContainer.getMfaMode());
										} catch (Exception e1) {
											valueContainer.setMfaMode("NONE");
										}
									System.out.println("MFA MODE.."+ responseContainer.getMfaMode());
										if (valueContainer.getMfaMode().toString().equalsIgnoreCase("OTP")) {
											try {
										
										// final ProgressDialog dialog1 = ProgressDialog.show(SmartFrenDetails.this, "  Banksinarmas               ", "Please Wait for SMS....   ", true);
								 		Long startTimeInMillis = new java.util.Date().getTime();
								 		
										while(optCheck){
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
										System.out.println("Testing>>OTP>>"+ otpValue);
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
								        	  
								          	}
											 });
											 alertbox.show();
										}else{
											//dialog1.dismiss();
											System.out.println("Testing>>OTP>>not null");
											Intent intent = new Intent(SmartFrenDetails.this, ConfirmAddReceiver.class);
											intent.putExtra("MSGCODE",responseContainer.getMsgCode());
											intent.putExtra("RCVNUM",mdn.getText().toString().trim());
											intent.putExtra("MSG",responseContainer.getMsg());
											intent.putExtra("CUST_NAME", responseContainer.getCustName());
			       							intent.putExtra("DEST_NUMBER", responseContainer.getDestMDN());
			       							intent.putExtra("DEST_BANK", responseContainer.getDestBank());
			       							intent.putExtra("AMOUNT", responseContainer.getAmount());
											intent.putExtra("PIN", pin.getText().toString());
											intent.putExtra("TFNID", transferID);
											intent.putExtra("PTFNID", parentTransferID);
											intent.putExtra("MDN", mdn.getText().toString().trim());
											intent.putExtra("AMT", amount.getText().toString().trim());
											intent.putExtra("OTP", otpValue);
											intent.putExtra("MFA_MODE",responseContainer.getMfaMode());
											intent.putExtra("CHARGE", responseContainer.getEncryptedTransactionCharges());
											intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
											intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent);
										}
										
									} catch (Exception e) {
										//dialog1.dismiss();
										System.out.println("Testing>>exception>>");
									}
								}else{
									Intent intent = new Intent(SmartFrenDetails.this, ConfirmAddReceiver.class);
									intent.putExtra("MSGCODE",responseContainer.getMsgCode());
									intent.putExtra("RCVNUM",mdn.getText().toString().trim());
									intent.putExtra("MSG",responseContainer.getMsg());
									intent.putExtra("CUST_NAME", responseContainer.getCustName());
	       							intent.putExtra("DEST_NUMBER", responseContainer.getDestMDN());
	       							intent.putExtra("DEST_BANK", responseContainer.getDestBank());
	       							intent.putExtra("AMOUNT", responseContainer.getAmount());
									intent.putExtra("PIN", pin.getText().toString());
									intent.putExtra("TFNID", transferID);
									intent.putExtra("PTFNID", parentTransferID);
									intent.putExtra("MDN", mdn.getText().toString().trim());
									intent.putExtra("AMT", amount.getText().toString().trim());
									intent.putExtra("CHARGE", responseContainer.getEncryptedTransactionCharges());
									intent.putExtra("TRANSFER_TYPE", valueContainer.getTransferType());
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									
								}
									
								}     						
							        
							          pin.setText("");
							          
							   }else{
							  		dialog.dismiss();
							  		if (selectedLanguage.equalsIgnoreCase("ENG")) {
										alertbox.setMessage(getResources().getString(R.string.eng_serverNotRespond));
									} else {
										alertbox.setMessage(getResources().getString(R.string.bahasa_serverNotRespond));
									}
										 alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
							          public void onClick(DialogInterface arg0, int arg1) {
							            
							        	 Intent intent  = new Intent(getBaseContext(), HomeScreen.class);
							           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        
							           startActivity(intent);
							        	  
							          	}
										 });
										 alertbox.show();
							  	}
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                                 
                             }
                          };
                         
                          final Thread checkUpdate = new Thread() {

                       	   public void run() {
                       		   
                       		   try{
                       			   responseXml= webServiceHttp.getResponseSSLCertificatation();  /** Service call and getting response in String. */
                         		   }catch (Exception e) {
                   				responseXml=null;				
                   			}
                       		   
                       		  System.out.println("Testing Handler"+handler.sendEmptyMessage(0));
                       	   }
                          };
                          checkUpdate.start();	
					
			}
			
		}
		});
		
	}
	
	 private boolean isRequiredFieldEmpty(){
	    	
		 	mdn = (EditText)findViewById(R.id.ed_mdnValue);
			amount = (EditText)findViewById(R.id.ed_amountValue);
			pin= (EditText)findViewById(R.id.ed_pinValue);
	     
			if(!(mdn.getText().toString().equals(""))&&!(amount.getText().toString().equals(""))&&!(pin.getText().toString().equals("")))
			{
				return false;
			}
			else{
				return true;
			}
	 }
	 @SuppressLint("NewApi")
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
