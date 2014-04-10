package com.avengers.locationreminder;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;

import android.widget.TextView;

public class HelpClass extends Activity{

	TextView textview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		textview = (TextView)findViewById(R.id.textviewhelp);
		textview.setVerticalScrollBarEnabled(true);
		textview.setMovementMethod(new ScrollingMovementMethod());
		textview.setTextSize(18);
		textview.setBackgroundColor(Color.BLACK);
		textview.setTextColor(Color.WHITE);
		textview.setText("INSTRUCTIONS:\n\n1. For alarms of long range set an accuracy of atleast 2-5 km.\n2. For reminders in short areas set an accuracy of atleast 40 metres for accurate results.\n3. Avoid using long duration reminders for locations within a range of 500 metres as within this range GPS would be used and will unnecessarily drain the battery.\n4. Make sure you have a data plan activated on your mobile.\n5. If GPS doesnot work well in your local area use GPS Logger app available on Play Store.");
	}

}
