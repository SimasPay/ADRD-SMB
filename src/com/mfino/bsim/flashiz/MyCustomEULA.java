package com.mfino.bsim.flashiz;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mfino.bsim.HomeScreen;
import com.mfino.bsim.R;
import com.mfino.bsim.billpayment.PaymentHome;
import com.mfino.bsim.db.DBHelper;
import com.mobey.fragment.abs.SDKLinkFragmentActivity;
import com.mobey.fragment.intern.listener.BankSDKCallBackListener;
import com.neopixl.fragment.NPFragment;
import com.neopixl.logger.NPLog;

/**
 * Example
 * @author Olivier Demolliens - @ odemolliens - olivier.demolliens@flashiz.com
 * Copyright 2014 FLASHiZ - All rights reserved.
 */
@SuppressLint("ValidFragment")
public class MyCustomEULA extends NPFragment{

	//View
	private Button mBtnCancel;
	private Button mBtnValidate;
	
	 DBHelper mydb ;
	   String session="false";
	   SharedPreferences settings;
	   String f_mdn;
	  	 ArrayList<String> array_session = new ArrayList<String>();
	  	String session_value,flash_mdn,mobileNumber;
	  	 ArrayList<String> array = new ArrayList<String>();

	


	//Model
	  	private  boolean mUserAccept = false;

	//Listener
	private BankSDKCallBackListener mListener;

	public MyCustomEULA(BankSDKCallBackListener listener) {


		setListener(listener);
	}


	/**
	 * Public Constructor
	 */

	public static MyCustomEULA newInstance(BankSDKCallBackListener listener) {
		MyCustomEULA f = new MyCustomEULA(listener);
		Bundle args = new Bundle();
		f.setArguments(args);
		return f;
	}

	/**
	 * Called before onCreateView
	 */
	@Override
	public void onCreateModel(Bundle arg0) {
		//Nothing to do
	}

	/**
	 * Return activity context
	 */
	@Override
	public Context getActivityContext() {
		return getActivity();
	}

	/**
	 * Return fragment layout
	 */
	@Override
	public int getViewID() {
		getActionBar().hide();
		return R.layout.qr_terms_conditions;
	}

	/**
	 * Link View with code (findByView)
	 */
	@Override
	public void linkView(View view) {
		setBtnCancel((Button) view.findViewById(R.id.decline));
		setBtnValidate((Button) view.findViewById(R.id.agreeButton));
		TextView disclosure=(TextView)view.findViewById(R.id.terms_conditions);
		disclosure.setText(Html.fromHtml(getResources().getString(R.string.flashiz_tc)));
		
		
		view.findViewById(R.id.agreeButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("haiiiiii", "agreee");
				
			}
		});
		/*// Header code...
				View headerContainer = view.findViewById(R.id.header);
				TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
				screeTitle.setText("PAYMENT");
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
						// TODO Auto-generated method stub
						Intent intent=new Intent(MyCustomEULA.this,HomeScreen.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				});*/
	}

	@Override
	public void onDestroy() {
		//Intercept physical back button
		if(mUserAccept==false){
			getListener().callBackUserHasCancelledEula();
		}

		super.onDestroy();
	}

	@Override
	public void onBackResume() {
		super.onBackResume();
	}
	
	@Override
	public void onStart() {
		
		super.onStart();
	}
	/**
	 * Set your onClick and others things...
	 */
	@Override
	public void linkViewAction() {
		

		getBtnCancel().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SDKLinkFragmentActivity activity = (SDKLinkFragmentActivity) getActivity();
				if(activity != null){
					SDKLinkFragmentActivity.setUserKey("");
					activity.closeSDK();
					getListener().callBackUserHasCancelledEula();
				}else{
					NPLog.e("can't start fragment (activity = null");
				}
			}
		});
		
		getBtnValidate().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("haiiiiii", "agreee");

				mUserAccept = true;
				if(mUserAccept=true){
					
					Log.e("haiiiiii", "iffffffffffffffffff");

					SDKLinkFragmentActivity activity = (SDKLinkFragmentActivity) getActivity();
					if(activity != null){
						getActionBar().show();
						SDKLinkFragmentActivity.setUserEulaState(true);
						getListener().callBackUserHasConfirmEula();
						activity.removeFragment(getMySelfFragment());
					}else{
						NPLog.e("can't start fragment (activity = null");
					}
					
					mydb = new DBHelper(getContext());
					
					Log.e("agree_clicked", session);
					
         	        mydb.updatedatabase("true"); 
					// Cursor rs = mydb.getFlashizData();						            	
					
	            	 
	            		
					
				}else{
					Log.e("haiiiiii", "else_start");

//       			 SDKLinkFragmentActivity.resetUserSession();
//       			 SDKLinkFragmentActivity.setUserEulaState(true);
					SDKLinkFragmentActivity.setUserKey(null);
					Log.e("haiiiiii", "elseeeeeeeee_out");


				}
				
            	 

				
					
					/*
					Log.e("haiiiiii", "agreee");
					settings = getContext().getSharedPreferences("LOGIN_PREFERECES",	Context.MODE_WORLD_READABLE);
					 mobileNumber = settings.getString("mobile", "");
					
						   mydb = new DBHelper(getContext());
		 			session="false";
					
					Log.e("agree_clicked", session);
					
					//mydb.insertfalshiz(session,f_mdn);
					 Cursor rs = mydb.getFlashizData();						            	
					 Log.e("countttt", rs.getCount()+"");
	            	 if(rs.getCount()!=0){
	            	 
	            	 while (rs.moveToNext()) {
	            		// array.clear();
	            		 session_value = rs.getString(rs.getColumnIndex("session_value"));
	            		 flash_mdn=rs.getString(rs.getColumnIndex("f_mdn"));
	            		 Log.e("session_value", session_value+"--------------");
	            		 if(session_value.equalsIgnoreCase("false")){
		            		 Log.e("session_value", session_value+"---2222222222222-----------");

	            			 SDKLinkFragmentActivity.resetUserSession();
		             	        mydb.updatedatabase(session_value); 
			            		 Log.e("session_value", session_value+"----3333333----------");

	            		 }else{
		            		 Log.e("session_value", session_value+"----4444444444----------");

	            		 }
	            		
	            		
	            		 
	         	        array.add(session_value);
	         	       if(array.size()!=0){
							 if(array.contains("false")){
					            	Log.e("check_mdn_name", mobileNumber+array.toString()+"iffffff");
					            	SDKLinkFragmentActivity.resetUserSession();
			             	        mydb.updatedatabase(session_value); 

					            }else{
					            	Log.e("check_mdn_name", mobileNumber+"elseeeeeee");            	
					            	

					            }

	            		 
	            	    }else{
		   				 Log.e("cursor-----count_****************", rs2.getCount()+"");

		   				
	     			
	               	    }
	            	 }
	            	 }else{
		   				 Log.e("Nodata_founddd","*******************");

		   				// Log.e("cursor-----count_****************", rs2.getCount()+"");

	            		
	                     Log.e("array_session", array_session+"nodataaaaaaaaaaaaaaa;"+session_value+"session_value");
	 
	            	 }      	 */
				
			}
		});
	}

	/**
	 * Nothing to do. But you can add an ActionBar by this way. (with Sherlock library)
	 */
	@Override
	public void linkViewActionBar() {

	}

	/**
	 * Set color, string, drawable values
	 */
	@Override
	public void linkViewRessources(View view) {
	}


	/**
	 * 
	 * After this line, there are few custom methods. But we don't need it
	 * 
	 */


	@Override
	public void onChildStart() {
		//Nothing to do
	}

	@Override
	public void onChildStop() {
		//Nothing to do
	}

	@Override
	public void onFragmentResume() {
		//Nothing to do
	}

	@Override
	public void onFrameViewChanged() {
		//Nothing to do
	}

	private BankSDKCallBackListener getListener() {
		return mListener;
	}

	private void setListener(BankSDKCallBackListener mListener) {
		this.mListener = mListener;
	}


	private Button getBtnCancel() {
		return mBtnCancel;
	}


	private void setBtnCancel(Button mBtnCancel) {
		this.mBtnCancel = mBtnCancel;
	}


	private Button getBtnValidate() {
		return mBtnValidate;
	}


	private void setBtnValidate(Button mBtnValidate) {
		this.mBtnValidate = mBtnValidate;
	}


	@Override
	public boolean isPinCodeIsInFront() {
		return false;
	}


	@Override
	public void enableTabBar() {
	}


	@Override
	public void onBackButtonAction() {
		//Inform Banking application
		//SampleAppManager.getManager().getListenerBankSDK().callBackUserHasCancelledEula();
	}

}
