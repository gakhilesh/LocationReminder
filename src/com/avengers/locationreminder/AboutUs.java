package com.avengers.locationreminder;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AboutUs extends Activity{

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
		
	}

}
