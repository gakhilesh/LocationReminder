package com.avengers.locationreminder;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsList extends ListActivity {
	
	String settingsClass[] = {"Preferences","Help","About Us","Feedback"};

	boolean vibrationSet=false;
	String range;
	EditText etrange;
	NewDBHandler dbHandler;
	
	CheckBox checkBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(SettingsList.this,android.R.layout.simple_list_item_1,settingsClass));
		getListView().setBackgroundColor(Color.WHITE);
		
		dbHandler = new NewDBHandler(this.getApplicationContext());
	}
	
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

        
		switch(position)
		{
		case 0:
			View checkBoxView = View.inflate(this, R.layout.checkbox, null);
			checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
			etrange=(EditText)checkBoxView.findViewById(R.id.etRange);
			
			vibrationSet=Boolean.valueOf(dbHandler.getPreference("vibration"));
	    	range=dbHandler.getPreference("range");
			
	    	Toast.makeText(getApplicationContext(), range, Toast.LENGTH_LONG).show();
	    	etrange.setText(range);
	    	if(vibrationSet==true)
			{
				checkBox.setChecked(true);
			}
			else
			{
				checkBox.setChecked(false);
			}
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			    @Override
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			    	
			        // Save to shared preferences
			    	if(vibrationSet==true)
			    		vibrationSet=false;
			    	else
			    		vibrationSet=true;
			    	dbHandler.update("vibration", String.valueOf(vibrationSet));
			    }
			});
			
			checkBox.setText("Vibrate on alarm");
			checkBox.setTextColor(Color.WHITE);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			    builder.setTitle("Vibration Settings")
			           .setView(checkBoxView)
			           .setCancelable(false)
			           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog, int id) {
                                  range=etrange.getText().toString();
                                  
                                  dbHandler.update("range", range);
			                                             }
			           }).show();
			   // Toast.makeText(getApplicationContext(), ""+vibrationSet, Toast.LENGTH_LONG).show();
			    
			break;
		case 1:
			Intent intentHelp = new Intent("com.avengers.locationreminder.HelpClass");
			startActivity(intentHelp);
			break;
		case 2:
			Intent intentAboutUs = new Intent("com.avengers.locationreminder.AboutUs");
			startActivity(intentAboutUs);
			break;
		case 3:
			final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		    intent.setType("text/plain");
		    final PackageManager pm = getPackageManager();
		    final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		    ResolveInfo best = null;
		    for (final ResolveInfo info : matches)
		      if (info.activityInfo.packageName.endsWith(".gm") ||
		          info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
		    if (best != null)
		      intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

	        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"developers.locationreminder@gmail.com"});
	        intent.putExtra(Intent.EXTRA_SUBJECT, "FeedBack");
		    startActivity(intent);
			break;
		
		}
		
	}
	
}
