package com.avengers.locationreminder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapsClass extends MapActivity implements LocationListener,OnDrawerCloseListener,OnDrawerOpenListener{


	
	public static String towers;
	public static GeoPoint touchedPoint;
	public static Uri ringtoneUri;
	public static String longitudeString="",latitudeString="",currentAddressString="",addressString="",accuracyString="1",unitsString="KM",flagString="0",tasksString="",nextTimeString="false",currentlySetString="true",uri;
	
	
	MapView mapView;
	GeoPoint ourLocation,alarmsGeoPoint;
	Geocoder geocoder;
	MapController mapController;
	LocationManager locationManager;
	Location location;
	boolean gpsEnabled,networkEnabled;

	DBController longdbcon;   //Long presses! 
	public static int longflag;
	
	
	CustomPinPoint lastCustomPinPoint,lastCustomLocation,custom;
	boolean isFirstPinPoint=true;
	List<Overlay> currentLocationOverlayList;//,pinpointOverlayList;
	
	float startTouchTime=0,stopTouchTime=0,stopTouchTimeLast=-600;
	int x,y;
	int alarmNumber;
	int longitude=0,latitude=0;
	int editFromPinpointClick;
	
	public static boolean isReminder;
	Drawable pinpointIcon,alarmPointIcon,reminderPointIcon,currentPointIcon;
	static Context applicationContext;
	MapDBHandler dbHandler;
	String[] unitsArray = {"KM","Metre"};
	public static EditText etNote,etAccuracy,etFindLocation,etLocationName;
	Button bSetRingtone,bFindLocation,bCurrentLocation;
	public static Spinner unitSpinner;
	Button bSaveAlarm,bDeleteAlarm;
	public static SlidingDrawer slidingDrawer;
	ImageButton imageButton;
	String TAG = MapsClass.class.getSimpleName();
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.map_layout);
		mapView = (MapView)findViewById(R.id.maps);
		mapView.setBuiltInZoomControls(true);
		@SuppressWarnings({ "unused", "deprecation" })
		View zoomButtons = mapView.getZoomControls();
		isReminder = LReminder.isReminder;
		
		etAccuracy = (EditText)findViewById(R.id.etAccuracy);
		etNote = (EditText)findViewById(R.id.etNote);
		etLocationName=(EditText)findViewById(R.id.etLocationName);
		bSetRingtone = (Button)findViewById(R.id.setRingtone);
		unitSpinner = (Spinner)findViewById(R.id.setUnits);
        bSaveAlarm=(Button)findViewById(R.id.saveAlarm);
        bDeleteAlarm=(Button)findViewById(R.id.deleteAlarm);
        etFindLocation = (EditText)findViewById(R.id.etFindLocation);
        bFindLocation = (Button)findViewById(R.id.bFindLocation);
        bCurrentLocation = (Button)findViewById(R.id.bCurrentLocation);
        ringtoneUri=RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM);
        imageButton = (ImageButton)findViewById(R.id.handleDrawer); 
        slidingDrawer = (SlidingDrawer)findViewById(R.id.slidingDrawer);
		slidingDrawer.setOnDrawerCloseListener(this);
		slidingDrawer.setOnDrawerOpenListener(this);

	     pinpointIcon = getResources().getDrawable(R.drawable.map_icon);
	     alarmPointIcon = getResources().getDrawable(R.drawable.ic_menu_myplajces); 
		reminderPointIcon = getResources().getDrawable(R.drawable.ic_menu_myplaces);
		currentPointIcon = getResources().getDrawable(R.drawable.ic_maps_indicator_current_position_anim2);
		editFromPinpointClick= CustomPinPoint.editFromPinpointClick;
		bDeleteAlarm.setVisibility(View.GONE);
		if(editFromPinpointClick==1) bDeleteAlarm.setVisibility(View.VISIBLE);
		//imageButton.setVisibility(View.GONE);
		
	//	 slidingDrawer.setVisibility(View.INVISIBLE);
        if (!(isReminder))
			{
        		etNote.setVisibility(View.GONE);
        		
			}
			else
			{
				etNote.setVisibility(View.VISIBLE);
			}
        
				//Data Settings
		boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
		if(!haveConnectedMobile&&!haveConnectedWifi)
		{
			AlertDialog alertDialog = new AlertDialog.Builder(MapsClass.this).create();
        	alertDialog.setTitle("Settings");
        	alertDialog.setMessage("Enable Internet Settings");
        	alertDialog.setButton("Settings", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent settingsIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
					startActivity(settingsIntent);
				}
			});
        	alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "No Data Connectivity!", Toast.LENGTH_LONG).show();
				}
			});
        	alertDialog.show();
		}
		Touchy t = new Touchy();
        currentLocationOverlayList = mapView.getOverlays();
        currentLocationOverlayList.add(t);

		 mapView.setSatellite(true);
        
		 mapController = mapView.getController();
                
        locationManager  = (LocationManager)getSystemService(LOCATION_SERVICE);
        

        //GPS,Network Enable if disabled
        gpsEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if((!gpsEnabled)||(!networkEnabled))
        {
        	AlertDialog alertDialog = new AlertDialog.Builder(MapsClass.this).create();
        	alertDialog.setTitle("Settings");
        	alertDialog.setMessage("Enable Location Settings");
        	alertDialog.setButton("Settings", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(settingsIntent);
				}
			});
        	alertDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if((!gpsEnabled)&&(!networkEnabled))
					{
						Toast.makeText(getApplicationContext(), "Location Settings Disabled!", Toast.LENGTH_LONG).show();
					}
				}
			});
        	alertDialog.show();
        }
        

        
        
		//For use in dbHandler::Fetching alarms/reminders through DB
		dbHandler = new MapDBHandler(this.getApplicationContext());
		ArrayList<DBController> dbcon=dbHandler.getAlarms();
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
			//Show each alarm under alarms tab and reminder under reminders tab
			latitude=(int)(Double.valueOf(latitudeString)*1E6);
			longitude=(int)(Double.valueOf(longitudeString)*1E6);
			alarmsGeoPoint = new GeoPoint(latitude,longitude);
	        OverlayItem overlay = new OverlayItem(alarmsGeoPoint,"Location:",addressString);
	        if (flagString.equalsIgnoreCase("1"))
			custom = new CustomPinPoint(reminderPointIcon, MapsClass.this);
	        else
	        custom = new CustomPinPoint(alarmPointIcon, MapsClass.this);
	        	
			custom.insertPinpoint(overlay);
			
			currentLocationOverlayList.add(custom);
		}
        
		longdbcon = LReminder.longdbcon;
		if(longdbcon!=null)
		{
			mapController.animateTo(new GeoPoint(((int)(Double.valueOf(longdbcon.getLatitude())*1E6)),(int)( Double.valueOf(longdbcon.getLongitude())*1E6)));
			slidingDrawer.setVisibility(View.VISIBLE);
			if(!(slidingDrawer.isOpened()))
			slidingDrawer.animateOpen();
			etAccuracy.setText(longdbcon.getAccuracy());
			etLocationName.setText(longdbcon.getAddress());
			etNote.setText(longdbcon.getTasks());
			longitudeString=longdbcon.getLongitude();
			latitudeString=longdbcon.getLatitude();
			addressString=longdbcon.getAddress();
			accuracyString=longdbcon.getAccuracy();
			flagString=longdbcon.getFlag();
			isReminder=false;
			if(flagString.equalsIgnoreCase("1"))
				isReminder=true;
	    	 nextTimeString=longdbcon.getNextTime();
	    	 currentlySetString=longdbcon.getCurrentlySet();
	    	 uri=longdbcon.uri();
	    	LReminder.longdbcon=null;
			longflag=1;
		}

		
		
	bDeleteAlarm.setVisibility(View.GONE);
     if(longflag==1)
     {   
    	 bDeleteAlarm.setVisibility(View.VISIBLE);
    					//dbHandler.deleteTuple(longdbcon.getLongitude(), longdbcon.getLatitude());
     }
     if(editFromPinpointClick==1)
    	 bDeleteAlarm.setVisibility(View.VISIBLE);

		bDeleteAlarm.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					slidingDrawer.animateClose();
					if(editFromPinpointClick==1)
					{
						dbHandler.deleteTuple(longitudeString, latitudeString);
					    longflag=0;
				    }
					if(longflag==1)
					dbHandler.deleteTuple(longdbcon.getLongitude(), longdbcon.getLatitude());
                    
			    	finish();
					  Intent intent = new Intent(getApplicationContext(), LReminder.class);
			          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			          startActivity(intent);
				}
			});
 

		
		
        //Current Location
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        towers = locationManager.getBestProvider(criteria, false);
        
        
        location = locationManager.getLastKnownLocation(towers);
        if(location==null)
        {
        	towers = LocationManager.GPS_PROVIDER;
            location = locationManager.getLastKnownLocation(towers);
        }
        if(location==null)
        {
        	towers = LocationManager.NETWORK_PROVIDER;
            location = locationManager.getLastKnownLocation(towers);
        }
        if(location==null)
        {
        	towers = LocationManager.PASSIVE_PROVIDER;
            location = locationManager.getLastKnownLocation(towers);
        }
        if(location!=null)
        {
        	Log.d(TAG,"Hello Hello!");
        	longitude = (int)(location.getLongitude()*1E6);
        	latitude = (int)(location.getLatitude()*1E6);
        	ourLocation = new GeoPoint(latitude,longitude);
        	geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
			try
			{
				List<Address> address = geocoder.getFromLocation(latitude/1E6,longitude/1E6, 1);
			    currentAddressString="";
				if(address.size()>0)
				{
					for(int i=0;i<address.get(0).getMaxAddressLineIndex();i++)
					{
						if(i>0)
							currentAddressString+="\n";
						currentAddressString+=address.get(0).getAddressLine(i);
					}
					
				}
			}
			catch(IOException ex){ex.printStackTrace();}
        	OverlayItem overlay = new OverlayItem(ourLocation,"YOU",currentAddressString);
       // 	Log.d(TAG,"Hello Hello Hello!");
			CustomPinPoint custom = new CustomPinPoint(currentPointIcon, MapsClass.this);
			custom.insertPinpoint(overlay);
		//	Log.d(TAG,"Hello Hello hell!");
			lastCustomLocation = custom;
			currentLocationOverlayList.add(custom);
			mapController.animateTo(ourLocation);
			mapController.setZoom(10);
			mapView.invalidate();
		//	Log.d(TAG,"Hello Hell!");
        }
        locationManager.requestLocationUpdates(towers, 100,5, this);
		
		final LocationListener l= this;
		
		
	//getting current location
		
	bCurrentLocation.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			   if(ourLocation!=null){
				locationManager.requestLocationUpdates(towers, 100,5,l);
				mapController.animateTo(ourLocation);
			   }
			   //else
				 //  locationManager.requestLocationUpdates(towers, 30000,5, this);

				   
		}
	});
		
		
	//Search Box
    bFindLocation.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			GeoPoint p;
			try{
			Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());
			List<Address> addresses = gc.getFromLocationName(etFindLocation.getText().toString(),5);
            if(addresses.size() > 0)
            {
                 p = new GeoPoint( (int) (addresses.get(0).getLatitude() * 1E6), 
                                  (int) (addresses.get(0).getLongitude() * 1E6));

                   mapController.animateTo(p);
                   mapController.setZoom(12);


                   mapView.invalidate();
                   etFindLocation.setText("");
            }
            else
            {
                    AlertDialog.Builder adb = new AlertDialog.Builder(MapsClass.this);
                    adb.setTitle("Google Map");
                    adb.setMessage("Please Provide the Correct Location");
                    adb.setPositiveButton("Close",null);
                    adb.show();
            }
			}
			catch(Exception e){e.printStackTrace();}
		}
    }
    );
		
		
	//spinner for selecting km and metre
	ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, unitsArray);
	unitSpinner.setAdapter(arrayAdapter);
	unitSpinner.setSelection(0);
	
	// for Selecting Ringtone
	bSetRingtone.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent ringtoneIntent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	    	ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"Select ringtone for notifications:");
	    	ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,true);
	    	ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,true);
	    	ringtoneIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
	    	startActivityForResult(ringtoneIntent, 999);
			onActivityResult(999, 999, ringtoneIntent);
			
		}
	});
	
	// sliding drwar icon change
		slidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				imageButton.setImageDrawable(getResources().getDrawable(R.drawable.expander_ic_maximized));		
				
			}
		});	
		slidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
		
		
		@Override
			public void onDrawerOpened() {
				// TODO Auto-generated method stub
			etLocationName.setText(addressString);
			etAccuracy.setText(accuracyString);
			if(unitsString.equalsIgnoreCase("KM"))
				unitSpinner.setSelection(0);
			else
				unitSpinner.setSelection(1);
			if (!(isReminder))
				{
				etNote.setVisibility(View.GONE);
				etNote.setText("");
				}
			else
			  { 
				etNote.setVisibility(View.VISIBLE);
				etNote.setText(tasksString);
				
			  }
		        
			
			
			imageButton.setImageDrawable(getResources().getDrawable(R.drawable.expander_ic_minimized));		
				}
		});
	
	//Save alarm/Reminder
	bSaveAlarm.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			if(longflag==1)
			{
				dbHandler.deleteTuple(longdbcon.getLongitude(), longdbcon.getLatitude());
				longflag=0;
			}
			if(editFromPinpointClick==1)
			{
				dbHandler.deleteTuple(longitudeString, latitudeString);
			    editFromPinpointClick=0;	
			}
			addressString="";
		     addressString = etLocationName.getText().toString();
			 accuracyString = etAccuracy.getText().toString();
			 if (accuracyString.equals(""))
				 accuracyString = "1";
			 if(isReminder)
			  {
				  flagString="1";
				  tasksString=etNote.getText().toString();
			  }                        
			  else
			  { flagString="0";
			    tasksString="";
			  }
			 unitsString = unitsArray[unitSpinner.getSelectedItemPosition()];
			 ServiceClass.isCalledByMapsClass=true;
			 finish();
		  Intent serviceIntent = new Intent(getBaseContext(),ServiceClass.class);
		  startService(serviceIntent);
		  Intent intent = new Intent(getApplicationContext(), LReminder.class);
          startActivity(intent);
		}
	});
	
	}
	//overridden method for choosing Ringtone
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
    		Intent intent) {

    		if (resultCode != Activity.RESULT_OK) {

    		}

    		else if (requestCode == 999) {

    		ringtoneUri = intent
    		.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
/*
    		if (ringtoneUri == null) {
    			
    		ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM);	
    		Toast.makeText(getApplicationContext(),
    		"No Ringtone Path Found!", Toast.LENGTH_LONG).show();
    		}*/
    		
    		super.onActivityResult(requestCode, resultCode, intent);

    		}
	}
/*Overriden method of MapActivity*/
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(towers, 10000,5, this);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	/*Till here*/
	
	class Touchy extends Overlay
	{
		public boolean onTouchEvent(MotionEvent e,MapView v)
		{
			int temp=0;  //For correction
			if(e.getAction() == MotionEvent.ACTION_DOWN)
			{
				startTouchTime = e.getEventTime();
				x = (int)e.getX();
				y = (int)e.getY();
				touchedPoint = mapView.getProjection().fromPixels(x, y);
			}
			if(e.getAction() == MotionEvent.ACTION_UP)
			{   stopTouchTimeLast=stopTouchTime;
				stopTouchTime = e.getEventTime();
				temp=(x+y)/30;
			}
			if(stopTouchTime-stopTouchTimeLast<500)
			{
				mapController.zoomIn();
				stopTouchTimeLast=-600;
			}
			
			if((temp == (int)((e.getX()+e.getY())/30))&&(stopTouchTime-startTouchTime>=1000))
			{
				AlertDialog alertDialog = new AlertDialog.Builder(MapsClass.this).create();
				alertDialog.setTitle("Choose");
				alertDialog.setMessage("Choose an option");
				alertDialog.setButton("PinPoint",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						bDeleteAlarm.setVisibility(View.GONE);
						 geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
							try
							{
								List<Address> address = geocoder.getFromLocation(touchedPoint.getLatitudeE6()/1E6, touchedPoint.getLongitudeE6()/1E6, 1);
							    addressString="";
								if(address.size()>0)
								{
									for(int i=0;i<address.get(0).getMaxAddressLineIndex();i++)
									{  
										if(i>0)
											addressString+="\n";	
										addressString+=address.get(0).getAddressLine(i);
									}
									Toast t = Toast.makeText(getBaseContext(),addressString, Toast.LENGTH_SHORT);
									t.show();
								}
							}
							catch(IOException ex){ex.printStackTrace();}					
					    etLocationName.setText(addressString);
						OverlayItem overlay = new OverlayItem(touchedPoint,"YOU",addressString);
						custom = new CustomPinPoint(pinpointIcon, MapsClass.this);
						custom.insertPinpoint(overlay);
						//slidingDrawer.unlock();
						slidingDrawer.setVisibility(View.VISIBLE);
						slidingDrawer.animateOpen();
						if(isFirstPinPoint)
							isFirstPinPoint=false;
						else
							currentLocationOverlayList.remove(lastCustomPinPoint);
						currentLocationOverlayList.add(custom);
						lastCustomPinPoint = custom;
						mapView.invalidate();
					
					}
				
				}
				);
				alertDialog.setButton2("Toggle View",new DialogInterface.OnClickListener()
				{

					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						if(mapView.isSatellite())
						{
							mapView.setSatellite(false);
							mapView.setStreetView(true);
						}
						else if(mapView.isStreetView())
						{
							mapView.setStreetView(false);
							mapView.setSatellite(true);
						}
						mapView.invalidate();
					}
				
				}
				);
				alertDialog.setButton3("View Address",new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						 geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
							try
							{
								List<Address> address = geocoder.getFromLocation(touchedPoint.getLatitudeE6()/1E6, touchedPoint.getLongitudeE6()/1E6, 1);
							    addressString="";
								if(address.size()>0)
								{
									for(int i=0;i<address.get(0).getMaxAddressLineIndex();i++)
									{

										if(i>0)
											addressString+="\n";
										addressString+=address.get(0).getAddressLine(i);
									}
									Toast t = Toast.makeText(getBaseContext(),addressString, Toast.LENGTH_SHORT);
									t.show();
								}
							}
							catch(IOException ex){ex.printStackTrace();}					
					    							
						Toast.makeText(getBaseContext(),addressString, Toast.LENGTH_LONG).show();
					}
				
				}
				);
				alertDialog.show();
				
			return true;
			}
			return false;
		}
	}

	
	
	/*LocationListener overriden methods*/
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		location = locationManager.getLastKnownLocation(towers);
		Log.d(TAG,"onLocationChanged!");
		latitude = (int)(location.getLatitude()*1E6);
		longitude = (int)(location.getLongitude()*1E6);
		ourLocation = new GeoPoint(latitude,longitude);
		geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
		try
		{
			List<Address> address = geocoder.getFromLocation(latitude/1E6,longitude/1E6, 1);
		    currentAddressString="";
			if(address.size()>0)
			{
				for(int i=0;i<address.get(0).getMaxAddressLineIndex();i++)
				{   
					if(i>0)
						currentAddressString+="\n";
					currentAddressString+=address.get(0).getAddressLine(i);
				}
				
			}
		}
		catch(IOException ex){ex.printStackTrace();}
    	OverlayItem overlay = new OverlayItem(ourLocation,"What's up!",currentAddressString);
		CustomPinPoint custom = new CustomPinPoint(currentPointIcon, MapsClass.this);
		custom.insertPinpoint(overlay);
		currentLocationOverlayList.remove(lastCustomLocation);
		currentLocationOverlayList.add(custom);
		lastCustomLocation = custom;
		//mapController.animateTo(ourLocation);
		//mapController.setZoom(6);
		//mapView.invalidate();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDrawerOpened() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onDrawerClosed() {
		// TODO Auto-generated method stub
		
	}
	
	/*LocationListener overriden methods Till here*/
	
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event)
		{
		    if ((keyCode == KeyEvent.KEYCODE_BACK))
		    {
		    	finish();
				  Intent intent = new Intent(getApplicationContext(), LReminder.class);
		          startActivity(intent);
		    }
		    return super.onKeyDown(keyCode, event);
		}
}
