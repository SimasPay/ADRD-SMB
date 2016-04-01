package com.mfino.bsim.flashiz;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dimo.PayByQR.EULAFragmentListener;
import com.mfino.bsim.R;

public class MyCustomEULA extends Fragment implements View.OnClickListener{
    private EULAFragmentListener mListener;

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
            mListener.setEULAState(true);
        }else if (v.getId() == R.id.decline){
            mListener.setEULAState(false);
        }
    }
}
