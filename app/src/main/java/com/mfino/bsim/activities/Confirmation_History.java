package com.mfino.bsim.activities;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mfino.bsim.R;

import android.widget.TableRow.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Confirmation_History extends Activity {

	private Bundle bundle;
	int count = 0;
	SharedPreferences languageSettings;
	ArrayList<String> nodeNames = new ArrayList<String>();
	TableLayout table;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.last_transaction);

		// Header code...
		/*
		 * View headerContainer = findViewById(R.id.header); TextView
		 * screeTitle=(TextView)headerContainer.findViewById(R.id.screenTitle);
		 * //screeTitle.setText("LAST 3 TRANSACTIONS"); Button
		 * back=(Button)headerContainer.findViewById(R.id.back); Button
		 * home=(Button)headerContainer.findViewById(R.id.home_button);
		 */

		Button ok = (Button) findViewById(R.id.ok);
		// Language Option..
		languageSettings = getSharedPreferences("LANGUAGE_PREFERECES", 0);
		String selectedLanguage = languageSettings.getString("LANGUAGE", "BAHASA");
		table = (TableLayout) findViewById(R.id.TableLayout01);

		/*
		 * if (selectedLanguage.equalsIgnoreCase("ENG")) {
		 * screeTitle.setText(getResources().getString(R.string.eng_history));
		 * back.setBackgroundResource(R.drawable.back_button);
		 * home.setBackgroundResource(R.drawable.home_icon1);
		 * 
		 * } else {
		 * screeTitle.setText(getResources().getString(R.string.bahasa_history))
		 * ; //home.setBackgroundResource(R.drawable.bahasa_home_icon1);
		 * //back.setBackgroundResource(R.drawable.bahasa_back_button);
		 * 
		 * }
		 */

		/*
		 * back.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // TODO Auto-generated
		 * method stub finish(); } });
		 */
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Confirmation_History.this, HomeScreen.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		bundle = getIntent().getExtras();

		String msg = bundle.getString("MSG");
		String contentXML = bundle.getString("Content");
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			ByteArrayInputStream bis = new ByteArrayInputStream(contentXML.getBytes());
			Document doc = db.parse(bis);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("transactionDetail");
			TableRow rowheader = new TableRow(this);
	        TextView a = new TextView(this);
	        TextView b = new TextView(this);
	        TextView c = new TextView(this);  
	        TextView d = new TextView(this);
	        TextView e = new TextView(this);
	        a.setGravity(Gravity.LEFT);
	        b.setGravity(Gravity.LEFT);
	        c.setGravity(Gravity.CENTER_HORIZONTAL);
	        d.setGravity(Gravity.RIGHT);
	        e.setGravity(Gravity.RIGHT);
	        a.setTypeface(null, Typeface.BOLD);
	        b.setTypeface(null, Typeface.BOLD);
	        c.setTypeface(null, Typeface.BOLD);
	        d.setTypeface(null, Typeface.BOLD);
	        e.setTypeface(null, Typeface.BOLD);
	        
	        if (selectedLanguage.equalsIgnoreCase("ENG")) {
	        	a.setText("Date");
		        b.setText("Trx Type");
		        c.setText("D/C  ");
		        d.setText("");
		        e.setText("Amount");
	        }else{
	        	a.setText("Tanggal");
		        b.setText("Tipe Trx");
		        c.setText("D/C  ");
		        d.setText("");
		        e.setText("Jumlah");
	        }
	        
	        rowheader.addView(a, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
	        rowheader.addView(b, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
	        rowheader.addView(c);
	        rowheader.addView(d);
	        rowheader.addView(e, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
	        table.addView(rowheader,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        
	        /**
	        TableRow rowT = new TableRow(this);
	        TextView f = new TextView(this);
	        TextView g = new TextView(this);
	        TextView h = new TextView(this);  
	        TextView i = new TextView(this);
	        TextView j = new TextView(this);
	        f.setGravity(Gravity.LEFT);
	        g.setGravity(Gravity.LEFT);
	        h.setGravity(Gravity.CENTER_HORIZONTAL);
	        i.setGravity(Gravity.RIGHT);
	        j.setGravity(Gravity.RIGHT);
	        f.setText("03/08/06");
	        g.setText("Minjem Duit");
	        h.setText("(C)  ");
	        i.setText("IDR");
	        j.setText("2.999.999.999");
	        rowT.addView(f, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
	        rowT.addView(g, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
	        rowT.addView(h);
	        rowT.addView(i);
	        rowT.addView(j, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
	        table.addView(rowT,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	        **/
	        
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					SimpleDateFormat sm = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
					Date date1 = sm.parse(eElement.getElementsByTagName("transactionTime").item(0).getTextContent());
					DateFormat outputFormatter1 = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
					String strDate = outputFormatter1.format(date1); // 
					System.out.println("Transaction Time : " + strDate);
					System.out.println("Transaction Type : " + eElement.getElementsByTagName("transactionType").item(0).getTextContent());
					System.out.println("Amount : " + eElement.getElementsByTagName("amount").item(0).getTextContent());
					String CurrentString = eElement.getElementsByTagName("transactionType").item(0).getTextContent();
					String[] separated = CurrentString.split("\\(");
					String type = separated[0].trim();
					String initType = "(" + separated[1].trim();
					TableRow row = new TableRow(this);
			        TextView t = new TextView(this);
			        TextView u = new TextView(this);
			        TextView v = new TextView(this);  
			        TextView w = new TextView(this);
			        TextView x = new TextView(this);
			        t.setGravity(Gravity.LEFT);
			        u.setGravity(Gravity.LEFT);
			        v.setGravity(Gravity.CENTER_HORIZONTAL);
			        w.setGravity(Gravity.RIGHT);
			        x.setGravity(Gravity.RIGHT);
			        t.setText("" + strDate + "    ");
			        u.setText("" + type + "");
			        v.setText("" + initType + "  ");
			        
			        if (selectedLanguage.equalsIgnoreCase("ENG")) {
			        	w.setText("IDR");
			        }else{
			        	w.setText("Rp ");
			        }
			        
			        x.setText("" + eElement.getElementsByTagName("amount").item(0).getTextContent() + "");
			        row.addView(t, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
			        row.addView(u, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
			        row.addView(v);
			        row.addView(w);
			        row.addView(x, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
			        table.addView(row,new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Log.d("Simobi-Transaction", msg);
		System.out.println("Testing>History>>" + msg);

		if(Integer.parseInt(bundle.getString("MsgCode"))==38){
			TextView history = (TextView) findViewById(R.id.history);
			history.setText(msg);
		}
	}

	/**
	 * This method for handling the back pressing event of android device
	 * navigate to Home Screen
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			Intent intent = new Intent(getBaseContext(), HomeScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
