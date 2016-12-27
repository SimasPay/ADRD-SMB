package com.mfino.bsim;

import com.mfino.bsim.services.ConfigurationUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
/** @author himanshu.kumar */


public class ReActivationDetails extends Activity {
    /** Called when the activity is first created. */
	private Button okButton;
	private EditText mobileNumber,bankPin,pin,confirmPin; 
	private AlertDialog.Builder alertbox;
	private Bundle bundle;
	SharedPreferences languageSettings;
	String selectedLanguage;
	Context context;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.reactivation_details);
        context=this;
        
      //Header code...
	     TextView screeTitle=(TextView)findViewById(R.id.screenTitle);
	     ImageButton back=(ImageButton)findViewById(R.id.back);
	     ImageButton home=(ImageButton)findViewById(R.id.home_button);
	     back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
	     
	     home.setVisibility(View.GONE);
        
        bundle = getIntent().getExtras();
        mobileNumber = (EditText)findViewById(R.id.cardPanEditText);
        bankPin = (EditText)findViewById(R.id.bankPinEditText);    
        pin = (EditText)findViewById(R.id.pinEditText);
        confirmPin = (EditText)findViewById(R.id.rePinEditText);
        okButton = (Button)findViewById(R.id.okButton);
		alertbox = new AlertDialog.Builder(this);
		
		TextView textViewinNewPin=(TextView)findViewById(R.id.textView_newPin);
		TextView textVieConfirmPin=(TextView)findViewById(R.id.textView_confirmNewPin);
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		//Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			screeTitle.setText(getResources().getString(R.string.eng_reactivation));
			textViewinNewPin.setText(getResources().getString(R.string.eng_newPin));
			textVieConfirmPin.setText(getResources().getString(R.string.eng_confimPin));
			back.setBackgroundResource(R.drawable.back_button);
			okButton.setText(getResources().getString(R.string.eng_submit));

		} else {
			
			screeTitle.setText(getResources().getString(R.string.bahasa_reactivation));
			textViewinNewPin.setText(getResources().getString(R.string.bahasa_newPin));
			textVieConfirmPin.setText(getResources().getString(R.string.bahasa_confimPin));
			back.setBackgroundResource(R.drawable.back_button);
			okButton.setText(getResources().getString(R.string.bahasa_submit));
		}

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				boolean networkCheck=ConfigurationUtil.isConnectingToInternet(context);
				if(!networkCheck){
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
					ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.eng_noInterent), context);
					}else{
						ConfigurationUtil.networkDisplayDialog(getResources().getString(R.string.bahasa_noInternet), context);
					}
									
				}else if (isRequiredFieldEmpty()) {

					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_fieldsNotEmpty));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_fieldsNotEmpty));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0,int arg1) {

						}
					});
					alertbox.show();

				} else if (!isValidMobileNumber(mobileNumber.getText().toString().trim())) {
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_enterValidMobile));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_enterValidMobile));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0,int arg1) {

						}
					});
					alertbox.show();
				} else if (pin.getText().toString().trim().length() < 6) {
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_pinActivation));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_pinActivation));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0,int arg1) {

						}
					});
					alertbox.show();
				} else if (confirmPin.getText().toString().trim().length() < 6) {
					
					if (selectedLanguage.equalsIgnoreCase("ENG")) {
						alertbox.setMessage(getResources().getString(R.string.eng_confirmPinLenth));
					} else {
						alertbox.setMessage(getResources().getString(R.string.bahasa_confirmPinLenth));
					}
					alertbox.setNeutralButton("OK",	new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0,int arg1) {

						}
					});
					alertbox.show();
				} else if (!(pin.getText().toString().equals(confirmPin
						.getText().toString()))) {
					if (selectedLanguage.equalsIgnoreCase("ENG")) {

						alertbox.setMessage(getResources().getString(
								R.string.eng_mPinNotMatch));

					} else {

						alertbox.setMessage(getResources().getString(
								R.string.bahasa_mPinNotMatch));
					}

					alertbox.setNeutralButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
										int arg1) {

								}
							});
					alertbox.show();
				} else {

					/**
					 * Call Activation Disclosure here and pass value of Mobile
					 * Number, Activation Key and PIN
					 */

					Intent intent = new Intent(ReActivationDetails.this,ActivationDisclosure.class);
					intent.putExtra("CARD_PAN", mobileNumber.getText().toString());
					intent.putExtra("MDN", bundle.getString("MDN"));
					intent.putExtra("SOURCE_PIN", bankPin.getText().toString());
					intent.putExtra("PIN", pin.getText().toString());
					intent.putExtra("CONFIRM_PIN", confirmPin.getText().toString());
					intent.putExtra("ACTIVATION_TYPE", "ReActivation");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					//mobileNumber.setText("");

				}
			}
		});

	}
    
    private boolean isRequiredFieldEmpty(){
    	
    	 mobileNumber = (EditText)findViewById(R.id.cardPanEditText);
         bankPin = (EditText)findViewById(R.id.bankPinEditText);    
         pin = (EditText)findViewById(R.id.pinEditText);
         confirmPin = (EditText)findViewById(R.id.rePinEditText);
			if(!(mobileNumber.getText().toString().equals(""))&&!(bankPin.getText().toString().equals(""))&&!(pin.getText().toString().equals(""))&&!(confirmPin.getText().toString().equals("")))
			{
				return false;
			}
			else{
				
				return true;
			}
	}
    
	private boolean isValidMobileNumber(String number) {

		try {
			if (Long.parseLong(number) < 1000000) {
				return false;
			}
		} catch (NumberFormatException e) {

			return true;
		}
		return true;
	}
  
}

