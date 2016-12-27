package com.mfino.bsim.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{
	
	public static final String DATABASE_NAME = "Bsim.db";
	   public static final String CONTACTS_COLUMN_NAME = "mdn";
	   public static final String SESSION_COLUMN_NAME = "session_value";
	   String session_value;
	      String fmdn;
	      String session="false";




	public DBHelper(Context context)
	   {
	      super(context, DATABASE_NAME , null, 1);
	   }

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(
			      "create table mdns " +
			      "(id integer primary key, mdn text)"
			      );
		db.execSQL(
			      "create table flashiz_session " +
			      "(id integer primary key, session_value text,f_mdn text)"
			      );
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 db.execSQL("DROP TABLE IF EXISTS mdns");
	      onCreate(db);
		
	}
	public boolean insertMdn  (String mdn)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();
	      contentValues.put("mdn", mdn);
	     
	      db.insert("mdns", null, contentValues);
	      return true;
	   }
	
	public boolean insertfalshiz  (String session,String fmdn)
	   {
	      SQLiteDatabase db = this.getWritableDatabase();
	      ContentValues contentValues = new ContentValues();
	      contentValues.put("session_value", session);
	      contentValues.put("f_mdn", fmdn);
	     
	      db.insert("flashiz_session", null, contentValues);
	      return true;
	   }
	   
	  /* public Cursor getData(String  mdn){
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from mdns where mdn="+mdn+"", null );
	      return res;
	   }*/
	
	 public Cursor getData(){
	      SQLiteDatabase db = this.getReadableDatabase();
	      Cursor res =  db.rawQuery( "select * from mdns" , null );
	      return res;
	   }
	 
	 public Cursor getFlashizData(){
	      SQLiteDatabase db = this.getReadableDatabase();
	      //db.query("sku_table", columns, "owner='"+owner+"'", null, null, null, null);
			// attdata=db.rawQuery("select * from dtdccaf2 where status='0'", null);

		//Cursor res =  db.rawQuery( "select * from flashiz_session where session_value='"+session+"'", null );
			Cursor res =  db.rawQuery( "select * from flashiz_session", null );


	      return res;
	   }
	 
	 
	 public void updatedatabase(String string) {
	      SQLiteDatabase db = this.getWritableDatabase();
	      Log.e("updateee", "---------------");

			ContentValues cv1 = new ContentValues();
			cv1.put("session_value", "true");
			db.update("flashiz_session", cv1, "session_value='"+session+"'", null);
		      Log.e("updateee", "---------------"+cv1);

			// db.update("Capture", cv1, "status=" ? , null);
		}

	

	
}
