package com.avengers.locationreminder;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;

public class ServiceClass extends Service{

	public static Ringtone ringtone;
	//public static boolean isCalledByMapsClass;
	int flag;
	LocationManager locationManager;
	GeoPoint setLocation;
	Location alarmLocation,currentLocation;
	float accuracy;
	int alarmNumber;
	boolean vibrationSet;
	String towers;
	public static String longitudeString,latitudeString,addressString="",accuracyString="1",unitsString="KM",flagString="0",tasksString="",nextTimeString="false",currentlySetString="true",uri="";
	public static Intent alarmIntent=null;
	public static boolean isCalledByMapsClass=false;
	MapDBHandler dbHandler;
	NewDBHandler newDbHandler;
	int distanceForGpsEnable=300;
	String TAG = ServiceClass.class.getSimpleName();
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() 
	{
		// TODO Auto-generated method stub
		super.onCreate();
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
	}

	@Override
	public void onDestroy() 
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		dbHandler.deleteAll();
		Toast.makeText(getBaseContext(), "Service Destroyed!", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startId) 
	{
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		dbHandler = new MapDBHandler(this.getApplicationContext());
		newDbHandler = new NewDBHandler(this.getApplicationContext());
		distanceForGpsEnable=Integer.valueOf(newDbHandler.getPreference("range"));
		Toast.makeText(getApplicationContext(), distanceForGpsEnable+" ",Toast.LENGTH_LONG).show();
		//if(isCalledByMapsClass){
		accuracyString = MapsClass.accuracyString;
		setLocation = MapsClass.touchedPoint;
		latitudeString = String.valueOf(setLocation.getLatitudeE6()/1E6);
		longitudeString = String.valueOf(setLocation.getLongitudeE6()/1E6);
		addressString = MapsClass.addressString;
		flagString = MapsClass.flagString;
		tasksString= MapsClass.tasksString;
		unitsString = MapsClass.unitsString;
		uri=(MapsClass.ringtoneUri).toString();
		dbHandler.addAlarms(longitudeString,latitudeString,addressString,accuracyString,unitsString,flagString,tasksString,nextTimeString,currentlySetString,uri);
	//}
		/*else 
		{
			try{
				Thread.sleep(60000);
				Toast.makeText(getApplicationContext(),"Service finally started!",Toast.LENGTH_LONG).show();
			}
			catch(Exception e){}
		}*/

		Criteria criteria = new Criteria();
		 towers = locationManager.getBestProvider(criteria, false);
		alarmLocation = new Location("");
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {
			
			@Override
			public void run() {

				//flag=1;
				ArrayList<DBController> dbcon=dbHandler.getAlarms();
				if(dbcon.size()==0)
					stopSelf();
				for(alarmNumber=0;alarmNumber<dbcon.size();alarmNumber++)
				{
					longitudeString =dbcon.get(alarmNumber).getLongitude();
					latitudeString = dbcon.get(alarmNumber).getLatitude();
					addressString = dbcon.get(alarmNumber).getAddress();
					accuracyString = dbcon.get(alarmNumber).getAccuracy();
					unitsString = dbcon.get(alarmNumber).getUnits();
					flagString = dbcon.get(alarmNumber).getFlag();
					tasksString = dbcon.get(alarmNumber).getTasks();
					nextTimeString = dbcon.get(alarmNumber).getNextTime();
					currentlySetString = dbcon.get(alarmNumber).getCurrentlySet();
					uri = dbcon.get(alarmNumber).uri();
				
					if(unitsString.equals("KM"))
					{
						accuracy=Float.valueOf(accuracyString)*1000;
					}
					else if(unitsString.equals("Metre"))
					{
						accuracy=Float.valueOf(accuracyString);
					}
					//Toast.makeText(getApplication(), accuracy, Toast.LENGTH_LONG).show();
					alarmLocation.setLatitude(Double.valueOf(latitudeString));
					alarmLocation.setLongitude(Double.valueOf(longitudeString));
					if(flag==1){
					currentLocation = (Location)locationManager.getLastKnownLocation(towers);
					while(currentLocation==null)
					{
						Criteria criteria = new Criteria();
						towers=locationManager.getBestProvider(criteria, false);
						currentLocation = (Location)locationManager.getLastKnownLocation(towers);
						if(currentLocation==null)
						{
							towers=LocationManager.GPS_PROVIDER;
							currentLocation = (Location)locationManager.getLastKnownLocation(towers);
						}
						if(currentLocation==null)
						{
							towers=LocationManager.NETWORK_PROVIDER;
							currentLocation = (Location)locationManager.getLastKnownLocation(towers);
						}
					}}
					else
					{
						if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
						{
							Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
							intent.putExtra("enabled", true);
							sendBroadcast(intent);
						}
						currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						while(currentLocation==null)
						{
							Criteria criteria = new Criteria();
							towers=locationManager.getBestProvider(criteria, false);
							currentLocation = (Location)locationManager.getLastKnownLocation(towers);
							if(currentLocation==null)
							{
								towers=LocationManager.GPS_PROVIDER;
								currentLocation = (Location)locationManager.getLastKnownLocation(towers);
							}
						}
					}
					//GPS Enable if distance within set GPS range
					if(currentLocation.distanceTo(alarmLocation)<=distanceForGpsEnable)flag=0;
					/*if(currentLocation.distanceTo(alarmLocation)<=distanceForGpsEnable&&!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
					{
						Intent intent = new Intent("android.location.GPS_ENABLE");
						intent.putExtra("enabled", true);
						sendBroadcast(intent);
					}*/
					Toast.makeText(getApplicationContext(), String.valueOf(currentLocation.distanceTo(alarmLocation)),Toast.LENGTH_SHORT).show();
					Log.d(TAG,"c"+currentLocation.getLatitude()+"--"+currentLocation.getLongitude());
					Log.d(TAG,"a"+latitudeString+"--"+longitudeString);
					if(currentLocation.distanceTo(alarmLocation)<=accuracy&&Boolean.valueOf(currentlySetString)&&(!(Boolean.valueOf(nextTimeString))))
					{
						//Toast.makeText(getApplicationContext(),newDbHandler.getPreference() , Toast.LENGTH_LONG).show();
						vibrationSet=Boolean.valueOf(newDbHandler.getPreference("vibration"));
				    	if(alarmIntent==null){
							alarmIntent = new Intent("com.avengers.locationreminder.AlarmPopup");
							alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    		if(uri==null)
					    ringtone = RingtoneManager.getRingtone(getBaseContext(),RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM));
				    		else
							    ringtone = RingtoneManager.getRingtone(getBaseContext(),Uri.parse(uri));
				    	//	flag=1;
				    		ringtone.play();
				    		if(vibrationSet)
				    		{
				    			Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
				    			vibrator.vibrate(2000);
				    		}
							Toast.makeText(getApplicationContext(), "Location Arrived!", Toast.LENGTH_LONG).show();
						startActivity(alarmIntent);
				    	}
						//dbHandler.deleteTuple(longitudeString, latitudeString);
					}
					else if(currentLocation.distanceTo(alarmLocation)>accuracy&&Boolean.valueOf(nextTimeString))
					{
						dbHandler.deleteTuple(longitudeString, latitudeString);
						dbHandler.addAlarms(longitudeString, latitudeString,addressString, accuracyString, unitsString, flagString, tasksString, "false", currentlySetString,uri);
						Toast.makeText(getApplicationContext(), "Snoozed off!", Toast.LENGTH_SHORT).show();
					}
				}
				if(flag==1&&locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
				{
					Intent intent = new Intent("android.location.GPS_ENABLE");
					intent.putExtra("enabled", false);
					sendBroadcast(intent);
				}
				handler.postDelayed(this,5000);
			}
		};
		handler.postDelayed(runnable,5*1000);
	}
	

}
