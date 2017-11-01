package com.mfino.bsim.transfer;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mfino.bsim.activities.HomeScreen;
import com.mfino.bsim.R;

public class TransferSelection extends Activity {
	/** Called when the activity is first created. */
	ListView listView;
	private ImageView mimage1, mimage2, mimage3, mimage4;
	ArrayList<HashMap<String, Object>> recentItems = new ArrayList<HashMap<String, Object>>();
	SharedPreferences languageSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fundtransfer_home);

		// Header code...
		View headerContainer = findViewById(R.id.header);
		TextView screeTitle = (TextView) headerContainer.findViewById(R.id.screenTitle);
		ImageButton back = (ImageButton) headerContainer.findViewById(R.id.back);
		ImageButton home = (ImageButton) headerContainer.findViewById(R.id.home_button);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TransferSelection.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		// mimage1 = (ImageView)findViewById(R.id.imageView1);
		mimage2 = (ImageView) findViewById(R.id.imageView2);
		mimage2.setImageBitmap(
				roundCornerImage(BitmapFactory.decodeResource(getResources(), R.drawable.bank_sinarmas), 52));
		mimage3 = (ImageView) findViewById(R.id.imageView3);
		mimage3.setImageBitmap(
				roundCornerImage(BitmapFactory.decodeResource(getResources(), R.drawable.to_other_banks), 50));
		mimage4 = (ImageView) findViewById(R.id.imageView4);
		mimage4.setImageBitmap(
				roundCornerImage(BitmapFactory.decodeResource(getResources(), R.drawable.uangku_menu), 30));

		TextView bankSinarmas = (TextView) findViewById(R.id.textView1);
		TextView otherBanks = (TextView) findViewById(R.id.textView2);
		// TextView uangku=(TextView)findViewById(R.id.textView3);
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");

		if (selectedLanguage.equalsIgnoreCase("ENG")) {
			screeTitle.setText(getResources().getString(R.string.eng_fundTransfer));
			// mimage1.setImageResource(R.drawable.smartfren);
			bankSinarmas.setText(getResources().getString(R.string.eng_menuBankSinarmas));
			otherBanks.setText(getResources().getString(R.string.eng_toOtherBank));
			// uangku.setText(getResources().getString(R.string.eng_toUangku));
		} else {
			screeTitle.setText(getResources().getString(R.string.bahasa_fundTransfer));
			bankSinarmas.setText(getResources().getString(R.string.bahasa_menuBankSinarmas));
			otherBanks.setText(getResources().getString(R.string.bahasa_toOtherBank));
			// uangku.setText(getResources().getString(R.string.bahasa_toUangku));
		}

		/*
		 * mimage1.setOnClickListener(new View.OnClickListener() { public void
		 * onClick(View arg0) { Intent intent = new
		 * Intent(TransferSelection.this, SmartFrenDetails.class);
		 * startActivity(intent); } });
		 */

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

	public Bitmap roundCornerImage(Bitmap raw, float round) {
		int width = raw.getWidth();
		int height = raw.getHeight();
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawARGB(0, 0, 0, 0);

		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.parseColor("#000000"));

		final Rect rect = new Rect(0, 0, width, height);
		final RectF rectF = new RectF(rect);

		canvas.drawRoundRect(rectF, round, round, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(raw, rect, rect, paint);

		return result;
	}
}
