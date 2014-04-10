package com.avengers.locationreminder;

import java.util.ArrayList;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.util.Log;
import android.view.View;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class CustomPinPoint extends ItemizedOverlay<OverlayItem>{

	private ArrayList<OverlayItem> pinpoints = new ArrayList<OverlayItem>();
	private Context context;
	String longitudeString="",latitudeString="",currentAddressString="",addressString="",accuracyString="1",unitsString="KM",flagString="0",tasksString="",nextTimeString="false",currentlySetString="true",uri;
	LocationManager locationManager;
	int alarmNumber;
	public static int editFromPinpointClick;
	boolean isReminder;
	Context applicationContext;
	MapDBHandler dbHandler;
	SlidingDrawer slidingDrawer;
	GeoPoint p;
	String TAG = CustomPinPoint.class.getSimpleName();
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, false);
    }
	public CustomPinPoint(Drawable arg0) {
		super(boundCenter(arg0));
		slidingDrawer=MapsClass.slidingDrawer;
		editFromPinpointClick=0;
		// TODO Auto-generated constructor stub
	}
	
	
	
	@Override
	public boolean onTap(int index) {
		// TODO Auto-generated method stub
		Log.d(TAG,"emma!");
		OverlayItem item = pinpoints.get(index);
		p=item.getPoint();
		editFromPinpointClick=0;
		applicationContext=context;
		slidingDrawer.setVisibility(View.VISIBLE);
		dbHandler = new MapDBHandler(applicationContext);
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
			Log.d(TAG,"ontap"+latitudeString+"\n"+p.getLatitudeE6()/1E6+"\n"+longitudeString+"\n"+p.getLongitudeE6()/1E6);
			if((p.getLatitudeE6()==(int)(Double.valueOf(latitudeString)*1E6))&&(p.getLongitudeE6()==(int)(Double.valueOf(longitudeString)*1E6)))
			//if(((int)((Double.valueOf(latitudeString)*1E6)/1E6)==(int)p.getLatitudeE6()/1E6)&&((int)((Double.valueOf(longitudeString)*1E6)/1E6)==(int)p.getLongitudeE6()/1E6))
			{
				Toast.makeText(context,tasksString+"\n"+addressString, Toast.LENGTH_LONG).show();
		        MapsClass.longitudeString=longitudeString;
		        MapsClass.latitudeString=latitudeString;
		        MapsClass.addressString=addressString;
		        MapsClass.accuracyString=accuracyString;
		        MapsClass.unitsString=unitsString;
		        MapsClass.flagString=flagString;
		        MapsClass.tasksString=tasksString;
		        MapsClass.nextTimeString=nextTimeString;
		        MapsClass.currentlySetString=currentlySetString;
		        MapsClass.uri=uri;
		    	editFromPinpointClick=1;
		    	MapsClass.longflag=0;
				slidingDrawer.animateClose();
		        slidingDrawer.animateOpen();
		        if(flagString.equalsIgnoreCase("0"))
		        	MapsClass.isReminder=false;
		        else 
		        	MapsClass.isReminder=true;
				break;
			}
		}
        
		return super.onTap(index);
	}
	
	
	public CustomPinPoint(Drawable m,Context context) {
		// TODO Auto-generated constructor stub
		this(m);
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub]
		return pinpoints.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return pinpoints.size();
		
	
	}
	
	public void insertPinpoint(OverlayItem item)
	{
		pinpoints.add(item);
		this.populate();
	}

}
