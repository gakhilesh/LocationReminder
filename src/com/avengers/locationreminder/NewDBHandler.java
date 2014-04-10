package com.avengers.locationreminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NewDBHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="DB8";
	private static final String TABLE_NAME="TB4";
	private static final String KEY_KEY="key";
	private static final String KEY_VALUE="value";
	
	
	
	String TAG=NewDBHandler.class.getSimpleName();

	
	public NewDBHandler(Context context) {
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		String createtable="CREATE TABLE "+TABLE_NAME+"("+KEY_KEY+" TEXT,"+KEY_VALUE+" TEXT"+")";
		Log.d(TAG,"Hulala"+createtable);
		db.execSQL(createtable);
		ContentValues cv=new ContentValues();
		cv.put(KEY_KEY,"vibration");
		cv.put(KEY_VALUE,"false");
		db.insert(TABLE_NAME, null, cv);
		cv.put(KEY_KEY,"range");
		cv.put(KEY_VALUE,"300");
		db.insert(TABLE_NAME, null, cv);
		
	}
	public void initialise()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues cv=new ContentValues();
		cv.put(KEY_KEY,"vibration");
		cv.put(KEY_VALUE,"false");
		db.insert(TABLE_NAME, null, cv);
		cv.put(KEY_KEY,"range");
		cv.put(KEY_VALUE,"300");
		db.insert(TABLE_NAME, null, cv);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		//db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		//onCreate(db);
	}
	
	
	public void update(String key,String value)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE key ='"+key+"'");
		ContentValues cv=new ContentValues();
		cv.put(KEY_KEY,key);
		cv.put(KEY_VALUE,value);
		db.insert(TABLE_NAME, null, cv);
		db.close();
	}
	
	public String getPreference(String key)
	{Cursor cursor;

	SQLiteDatabase db=this.getWritableDatabase();
		try
		{
		String selectQuery="SELECT * FROM "+ TABLE_NAME +" WHERE key ='"+key+"'";
		cursor=db.rawQuery(selectQuery,null);
		cursor.moveToFirst();
		}
		catch(Exception e){
			onCreate(db);
			initialise();
		String selectQuery="SELECT * FROM "+ TABLE_NAME +" WHERE key ='"+key+"'";
		cursor=db.rawQuery(selectQuery,null);
		cursor.moveToFirst();}
		return cursor.getString(1);
		
	}
}
