package com.avengers.locationreminder;


import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MapDBHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="DB8";
	private static final String TABLE_NAME="TB5";
	private static final String KEY_LATITUDE="latitude";
	private static final String KEY_LONGITUDE="longitude";
	private static final String KEY_ADDRESS="address";
	private static final String KEY_ACCURACY="accuracy";
	private static final String KEY_UNITS="units";
	private static final String KEY_FLAG="flag";
	private static final String KEY_TASKS="tasks";
	private static final String KEY_NEXTTIME="nexttime";
	private static final String KEY_CURRENTLYSET="currentyset";
	private static final String KEY_URI="uri";
	
	String TAG=MapDBHandler.class.getSimpleName();

	
	public MapDBHandler(Context context) {
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onCreate ca;;ed hula hulala");
		String createtable="CREATE TABLE "+TABLE_NAME+"("+KEY_LONGITUDE+" TEXT,"+KEY_LATITUDE+" TEXT,"+KEY_ADDRESS+" TEXT,"+KEY_ACCURACY+" TEXT,"+KEY_UNITS+" TEXT,"+KEY_FLAG+" TEXT,"+KEY_TASKS+" TEXT,"+KEY_NEXTTIME+" TEXT,"+KEY_CURRENTLYSET+" TEXT,"+KEY_URI+" TEXT"+")";
		db.execSQL(createtable);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
		
	}
	
	
	public void addAlarms(String longitude,String latitude,String address,String accuracy,String units,String flag,String tasks,String nexttime,String currentlyset,String uri)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put(KEY_LONGITUDE,longitude);
		cv.put(KEY_LATITUDE, latitude);
		cv.put(KEY_ADDRESS, address);
		cv.put(KEY_ACCURACY,accuracy);
		cv.put(KEY_UNITS, units);
		cv.put(KEY_FLAG,flag);
		cv.put(KEY_TASKS, tasks);
		cv.put(KEY_NEXTTIME,nexttime);
		cv.put(KEY_CURRENTLYSET, currentlyset);
		cv.put(KEY_URI, uri);
		db.insert(TABLE_NAME, null, cv);
		//Log.d(TAG,"Katrina Kaif addalarm boom boom"+longitude+"\n"+latitude+"\n"+accuracy+"\n"+units+"\n"+flag+"\n"+tasks+"\n"+nexttime+"\n"+currentlyset);
		db.close();
		
	}
	
	public void deleteTuple(String longitude,String latitude)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		//db.execSQL("DELETE * FROM "+ TABLE_NAME);// +" WHERE latitude = "+ latitude +" AND longitude = "+ longitude );
		db.delete(TABLE_NAME, "longitude='"+longitude+"' AND latitude = '"+latitude+"'", null);
	}
	
	public void deleteAll()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
		onCreate(db);
		
	}
	
	public ArrayList<DBController> getAlarms()
	{
		ArrayList<DBController> dbcon=new ArrayList<DBController>();
		SQLiteDatabase db=this.getWritableDatabase();
		String selectQuery=" SELECT * FROM "+TABLE_NAME;
		Cursor cursor=db.rawQuery(selectQuery,null);
		if(cursor.moveToFirst())
		{
			
			do
			{
				DBController dbc=new DBController(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9));
			
				dbcon.add(dbc);
				//Log.d(TAG,"Anushka Sharma boom boom"+cursor.getString(0)+"\n"+cursor.getString(1)+"\n"+cursor.getString(2)+"\n"+cursor.getString(3)+"\n"+cursor.getString(4)+"\n"+cursor.getString(5)+"\n"+cursor.getString(6)+"\n"+cursor.getString(7));

			}while(cursor.moveToNext());
			
		}
		return dbcon;
		
		
		
	}
	
	public DBController getAlarmByLocation(String address)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String selectQuery="SELECT * FROM "+ TABLE_NAME +" WHERE address ='"+ address+"'";
		Cursor cursor=db.rawQuery(selectQuery,null);
		cursor.moveToFirst();
		DBController dbc=new DBController(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9));
		return dbc;
	}
		
	
	public void toggleCurrentlySet(String address)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		String selectQuery="SELECT * FROM "+ TABLE_NAME +" WHERE address ='"+ address+"'";
		Cursor cursor=db.rawQuery(selectQuery,null);
		cursor.moveToFirst();
		String longi=cursor.getString(0),lati= cursor.getString(1),add= cursor.getString(2),accuracy= cursor.getString(3),units= cursor.getString(4),flag= cursor.getString(5),tasks= cursor.getString(6),nextTime= cursor.getString(7),uri= cursor.getString(9);
		if(cursor.getString(8).equals("true"))
		{
			deleteTuple(longi,lati);
			addAlarms(longi, lati, add, accuracy, units, flag, tasks, nextTime, "false", uri);
		}
		else
		{
			deleteTuple(longi,lati);
			addAlarms(longi, lati, add, accuracy, units, flag, tasks, nextTime, "true", uri);	
		}
	}
	
	
	
	
}

