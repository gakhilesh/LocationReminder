package com.avengers.locationreminder;


import android.app.Activity;
import android.media.Ringtone;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AlarmPopup extends Activity{
Ringtone ringtone;
Button dismiss,snooze,close;
TextView tasks,location;
MapDBHandler dbHandler;
String latitude,longitude,address,accuracy,units,flag,task,currentlyset,nextTime,uri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_popup);
		ringtone = ServiceClass.ringtone;
		dismiss = (Button)findViewById(R.id.dismiss);
		snooze = (Button)findViewById(R.id.snooze);
		close = (Button)findViewById(R.id.close);
		tasks = (TextView)findViewById(R.id.etAddNote);
		location = (TextView)findViewById(R.id.etLocation);

		dbHandler = new MapDBHandler(this.getApplicationContext());
		latitude = ServiceClass.latitudeString;
		longitude = ServiceClass.longitudeString;
		address = ServiceClass.addressString;
		accuracy = ServiceClass.accuracyString;
		units = ServiceClass.unitsString;
		flag = ServiceClass.flagString;
		task = ServiceClass.tasksString;
		currentlyset = ServiceClass.currentlySetString;
		nextTime = ServiceClass.nextTimeString;
		uri = ServiceClass.uri;
		if(flag.equals("0"))
		{
			tasks.setText("Location Arrived!");
			tasks.setTextSize(25);
		}
		else if(flag.equals("1"))
		{
			tasks.setText(task);
			tasks.setTextSize(25);
		}
		
		
		
		location.setText(address);
		
		dismiss.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dbHandler.deleteTuple(longitude, latitude);
				ServiceClass.alarmIntent=null;
				ringtone.stop();
			}
		});
		
		close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dbHandler.deleteTuple(longitude, latitude);
				ServiceClass.alarmIntent=null;
				ringtone.stop();
				finish();
			}
		});
		
		snooze.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dbHandler.deleteTuple(longitude, latitude);
				ringtone.stop();ringtone.stop();
				dbHandler.addAlarms(longitude, latitude,address, accuracy, units, flag, task, "true", currentlyset,uri);
				ServiceClass.alarmIntent=null;
				finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
			dbHandler.deleteTuple(longitude, latitude);
			ServiceClass.alarmIntent=null;
			ringtone.stop();
			finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}

}
