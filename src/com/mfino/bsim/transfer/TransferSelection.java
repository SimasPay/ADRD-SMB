package com.mfino.bsim.transfer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;

public class TransferSelection extends Activity {
    /** Called when the activity is first created. */
	ListView listView;
	private ImageView mimage1,mimage2,mimage3,mimage4;
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String,Object>>();
	SharedPreferences languageSettings;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fundtransfer_home);
        
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
				Intent intent=new Intent(TransferSelection.this,HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
       
        //mimage1 = (ImageView)findViewById(R.id.imageView1);
        mimage2 = (ImageView)findViewById(R.id.imageView2);
        mimage3 = (ImageView)findViewById(R.id.imageView3);
        mimage4 = (ImageView)findViewById(R.id.imageView4);

        TextView bankSinarmas=(TextView)findViewById(R.id.textView1);
        TextView otherBanks=(TextView)findViewById(R.id.textView2);
        
    	languageSettings = getSharedPreferences("LANGUAGE_PREFERECES",0);
		String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			
			screeTitle.setText(getResources().getString(R.string.eng_fundTransfer));
			//mimage1.setImageResource(R.drawable.smartfren);
			bankSinarmas.setText(getResources().getString(R.string.eng_menuBankSinarmas));
			otherBanks.setText(getResources().getString(R.string.eng_toOtherBank));

		} else {
			
			screeTitle.setText(getResources().getString(R.string.bahasa_fundTransfer));
			bankSinarmas.setText(getResources().getString(R.string.bahasa_menuBankSinarmas));
			otherBanks.setText(getResources().getString(R.string.bahasa_toOtherBank));

		}
        
       /* mimage1.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View arg0) {
        	Intent intent = new Intent(TransferSelection.this, SmartFrenDetails.class);
			startActivity(intent);
        	}
        });*/
        
        mimage2.setOnClickListener(new View.OnClickListener() {
       	public void onClick(View arg0) {
       	Intent intent = new Intent(TransferSelection.this, ToBankSinarmas.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
       	}
       });
        
        
        mimage3.setOnClickListener(new View.OnClickListener() {
           	public void onClick(View arg0) {
           	Intent intent = new Intent(TransferSelection.this, OtherBankList.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
           	}
           });
        
        mimage4.setOnClickListener(new View.OnClickListener() {
           	public void onClick(View arg0) {
           	Intent intent = new Intent(TransferSelection.this, TransferToUangku.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
           	}
           });
    }
}
