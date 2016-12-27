package com.mfino.bsim;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebviewActivity extends Activity {
	private WebView webView;
	//private EditText urlEditText;
	// private ProgressBar progress;
	ProgressDialog mProgress;
	String url;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_webview);

		webView = (WebView) findViewById(R.id.webView1);

		WebSettings settings = webView.getSettings();
		mProgress = new ProgressDialog(WebviewActivity.this, R.style.MyAlertDialogStyle);
		mProgress.setTitle("Bank Sinarmas");
		mProgress.setCancelable(false);
		mProgress.setMessage(getResources().getString(R.string.bahasa_loading));
		mProgress.show();
		webView.setBackgroundColor(0);
		settings.setJavaScriptEnabled(true);
		settings.setUserAgentString("Desktop");
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setBuiltInZoomControls(true);
		webView.setBackgroundResource(R.drawable.bg_simobi);
		webView.setWebViewClient(new WebViewClient() {
			// load url public
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			// when finish loading page
			public void onPageFinished(WebView view, String url) {
				if (mProgress.isShowing()) {
					mProgress.dismiss();

				}
			}
		}); // set url for webview to load
		webView.loadUrl("https://www.banksinarmas.com/EForm/lakupandai/");
		//webView.loadUrl("http://10.32.1.77:8080/EForm/lakupandai");

	}

}


