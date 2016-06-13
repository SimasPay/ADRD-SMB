package com.dimo.PayByQR.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dimo.PayByQR.EULAFragmentListener;
import com.dimo.PayByQR.R;
import com.dimo.PayByQR.data.Constant;
import com.dimo.PayByQR.view.DIMOButton;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EULAFragmentListener} interface
 * to handle interaction events.
 * Use the {@link DefaultEULAFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DefaultEULAFragment extends Fragment implements View.OnClickListener{
    private EULAFragmentListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DefaultEULAFragment.
     */
    public static DefaultEULAFragment newInstance(String url) {
        DefaultEULAFragment fragment = new DefaultEULAFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.INTENT_EXTRA_EULA_URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    public DefaultEULAFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_default_eula, container, false);

        DIMOButton btnAccept = (DIMOButton) rootView.findViewById(R.id.fragment_eula_btn_accept);
        DIMOButton btnDecline = (DIMOButton) rootView.findViewById(R.id.fragment_eula_btn_decline);
        WebView webView = (WebView) rootView.findViewById(R.id.fragment_eula_webview);


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.loadUrl(getArguments().getString(Constant.INTENT_EXTRA_EULA_URL));


        btnAccept.setOnClickListener(this);
        btnDecline.setOnClickListener(this);

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
        if(v.getId() == R.id.fragment_eula_btn_accept) {
            mListener.setEULAState(true);
        }else if (v.getId() == R.id.fragment_eula_btn_decline){
            mListener.setEULAState(false);
        }
    }
}
