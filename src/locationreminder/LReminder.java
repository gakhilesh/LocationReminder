package com.avengers.locationreminder;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class LReminder extends Activity implements OnClickListener{

	public static String longitudeString="",latitudeString="",addressString="",accuracyString="1",unitsString="KM",flagString="0",tasksString="",nextTimeString="false",currentlySetString="true",uri;
	public static DBController longdbcon=null;
	TabHost tabHost;
	Button setAlarm;
    Button setTask;
    
    Intent settingsIntent,mapIntent;

    MapDBHandler dbHandler;
    
    int alarmNumber;
    
    ListView lvalarmList,lvtaskList;
    ArrayList<String> alarmList,taskList;
    ArrayAdapter<String> listAlarmAdapter,listTaskAdapter;
    
    public static boolean isReminder;
    public static Context appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lreminder);
        //Universal application Context
        appContext = this.getApplicationContext();
        mapIntent = new Intent(appContext,MapsClass.class);
		
        
        tabHost = (TabHost)findViewById(R.id.tabhost);
        setAlarm = (Button) findViewById(R.id.setalarm);
        setTask = (Button) findViewById(R.id.settask);
	    lvalarmList = (ListView)findViewById(R.id.alarmlist);
	    lvtaskList = (ListView)findViewById(R.id.tasklist);
        Drawable drawable;
       tabHost.setup();
       TabSpec specs = tabHost.newTabSpec("tag1");
       specs.setContent(R.id.alarm_tab);
       drawable = getResources().getDrawable(R.drawable.ic_lock_idle_anlarm);
       specs.setIndicator("ALARM",drawable);
       tabHost.addTab(specs);
       specs = tabHost.newTabSpec("tag2");
       specs.setContent(R.id.task_tab);
       drawable = getResources().getDrawable(R.drawable.ic_menu_agenda);
       specs.setIndicator("TASKS",drawable);
       tabHost.addTab(specs);
       

       alarmList = new ArrayList<String>();
       taskList = new ArrayList<String>();
       dbHandler = new MapDBHandler(this.getApplicationContext());
		ArrayList<DBController> dbcon=dbHandler.getAlarms();
		for(alarmNumber=0;alarmNumber<dbcon.size();alarmNumber++)
		{
			addressString = dbcon.get(alarmNumber).getAddress();
			flagString = dbcon.get(alarmNumber).getFlag();
			//Show each alarm under alarms tab and reminder under reminders tab
			if(flagString.equals("0"))
				alarmList.add(addressString);
			else
				taskList.add(addressString);
		}
		
	    listAlarmAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,alarmList);  
	    lvalarmList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    lvalarmList.setAdapter( listAlarmAdapter );
	    for ( int i=0; i< lvalarmList.getCount(); i++ ) {
	    	if((dbHandler.getAlarmByLocation((lvalarmList.getItemAtPosition(i)).toString()).getCurrentlySet().equals("true")))
	            lvalarmList.setItemChecked(i, true);
	    	else
	    		lvalarmList.setItemChecked(i, false);
	    }
	    lvalarmList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int i,
					long arg3) {
				// TODO Auto-generated method stub
		    		dbHandler.toggleCurrentlySet((lvalarmList.getItemAtPosition(i)).toString());
			}
		});
	    
	    
	    
	    lvalarmList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int i, long arg3) {
				// TODO Auto-generated method stub
				/*lvalarmList.getItemAtPosition(arg2);
				//startActivity("com.aven");
				if(lvalarmList.isItemChecked(arg2))
				lvalarmList.setItemChecked(arg2, false);*/
				//Toast.makeText(getApplicationContext(), "LONG CLICK", Toast.LENGTH_SHORT).show();
				longdbcon = dbHandler.getAlarmByLocation((lvalarmList.getItemAtPosition(i)).toString());
				startActivity(mapIntent);
				finish();
				return true;
			}
		});
	    
	    
	    
	    
	    listTaskAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,taskList);  
	    lvtaskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    lvtaskList.setAdapter( listTaskAdapter );
	    
	    for ( int i=0; i< lvtaskList.getCount(); i++ ) {
	    	if((dbHandler.getAlarmByLocation((lvtaskList.getItemAtPosition(i)).toString()).getCurrentlySet().equals("true")))
	    		lvtaskList.setItemChecked(i, true);
	    	else
	    		lvtaskList.setItemChecked(i, false);
	    }
	    lvtaskList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int i,
					long arg3) {
				// TODO Auto-generated method stub
				dbHandler.toggleCurrentlySet((lvtaskList.getItemAtPosition(i)).toString());
			}
		});
	    
	    
	    
	    lvtaskList.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int i, long arg3) {
				// TODO Auto-generated method stub
				longdbcon = dbHandler.getAlarmByLocation((lvalarmList.getItemAtPosition(i)).toString());
				startActivity(mapIntent);
			//	MapsClass.etNote.setVisibility(View.VISIBLE);
				//MapsClass.isReminder=true;
				finish();
				return true;
			}
		});
	    
	    
	    
	    
	    
	    
	    
	    
       //OnClickListeners
       setAlarm.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			isReminder = false;
			startActivity(mapIntent);
			finish();
		}
	});
       setTask.setOnClickListener(new View.OnClickListener() {
   		
   		@Override
   		public void onClick(View arg0) {
   			// TODO Auto-generated method stub
   			startActivity(mapIntent);
   			isReminder = true;
   			MapsClass.isReminder = true;
   			//MapsClass.etNote.setVisibility(View.VISIBLE);
			finish();
   		}
   	});
       
       
    }
    
    

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_MENU))
	    {
	    	if(settingsIntent==null)
	    		settingsIntent = new Intent("com.avengers.locationreminder.SettingsList");
	    	startActivity(settingsIntent);
	    }
	    return super.onKeyDown(keyCode, event);
	}
    
    
    
    //option menu method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_lreminder, menu);
        return true;
    }
    //onclicklistener method
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(DialogInterface arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
    
}
