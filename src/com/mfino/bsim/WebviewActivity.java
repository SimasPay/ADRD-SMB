package com.mfino.bsim;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class WebviewActivity extends Activity {
	private WebView webView;
	private EditText urlEditText;
	// private ProgressBar progress;
	ProgressDialog mProgress;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_webview);

		webView = (WebView) findViewById(R.id.webView1);

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		mProgress = ProgressDialog.show(WebviewActivity.this, "  Banksinarmas               ",getResources().getString(R.string.bahasa_loading) , true);

		webView.setBackgroundColor(0);
		webView.setBackgroundResource(R.drawable.blue_bg2);
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


