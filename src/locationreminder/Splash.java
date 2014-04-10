package com.avengers.locationreminder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(1000);
					finish();
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			finally{
				Intent openWelcome = new Intent(getApplicationContext(), LReminder.class);
				startActivity(openWelcome);
				
			}
		}
		};
		timer.start();
	
	}
	
	

}
