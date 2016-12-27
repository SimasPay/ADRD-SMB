package com.mfino.bsim.flashiz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dimo.PayByQR.EULAFragmentListener;
import com.dimo.PayByQR.PayByQRSDK;
import com.mfino.bsim.R;
import com.mfino.bsim.db.DBHelper;

public class MyCustomEULA extends Fragment implements View.OnClickListener{
    private EULAFragmentListener mListener;
    DBHelper mydb ;
    Context context;
 	private  boolean mUserAccept = false;
 	PayByQRSDK payByQRSDK;
    String session="false";
    public static MyCustomEULA newInstance() {
    	MyCustomEULA fragment = new MyCustomEULA();
        return fragment;
    }

    public MyCustomEULA() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.qr_terms_conditions, container, false);

        Button btnAccept = (Button) rootView.findViewById(R.id.agreeButton);
        Button btnDecline = (Button) rootView.findViewById(R.id.decline);

        btnAccept.setOnClickListener(this);
        btnDecline.setOnClickListener(this);

		TextView disclosure=(TextView) rootView.findViewById(R.id.terms_conditions);
		disclosure.setText(Html.fromHtml(getResources().getString(R.string.flashiz_tc)));
        
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (EULAFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement EULAFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.agreeButton) {
        	Log.e("agreee_click", "agreeeee");
        	mUserAccept = true;
			if(mUserAccept=true){
            mListener.setEULAState(true);
            mydb = new DBHelper(getContext());			
		
 	        mydb.updatedatabase("true"); 
			} else{					
				payByQRSDK.setEULAState(false);
			}
        }else if (v.getId() == R.id.decline){
        	Log.e("agreee_click", "elseeeeeee");
            mListener.setEULAState(false);
        }
    }
}
