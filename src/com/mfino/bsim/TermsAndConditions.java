package com.mfino.bsim;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TermsAndConditions extends AppCompatActivity {
	private WebView mWebView;
	private String url = "http://banksinarmas.com/tabunganonline/simobi";
	private ProgressBar progressbar;
	Handler handlerForJavascriptInterface = new Handler();
	private TextView myTextProgress;
	SharedPreferences languageSettings;
	String selectedLanguage;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_and_conditions);
		
		mWebView = (WebView) findViewById(R.id.isi_teks);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		myTextProgress = (TextView) findViewById(R.id.myTextProgress);
		TextView screenTitle = (TextView) findViewById(R.id.screenTitle);
		screenTitle.setText("Terms & Conditions");
		ImageButton back = (ImageButton) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				TermsAndConditions.this.finish();
			}
		});
		
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			myTextProgress.setText("Loading content..");
		}else{
			myTextProgress.setText("Memuat konten..");
		}
		progressbar.setVisibility(View.VISIBLE);
		myTextProgress.setVisibility(View.VISIBLE);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				//super.onPageFinished(view, url);
				progressbar.setVisibility(View.GONE);
				myTextProgress.setVisibility(View.GONE);
	            //String javaScript = "javascript:document.getElementsByTagName('body')[0].removeAttribute('background');";
	            //mWebView.loadUrl(javaScript);  
			}
		});
		mWebView.clearCache(true);
		mWebView.clearHistory();
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.loadUrl(url);
		
	}

}
