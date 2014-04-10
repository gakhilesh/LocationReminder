package com.avengers.locationreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ServiceManager extends BroadcastReceiver{

	Context mContext;
	public static String towers;
	private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mContext = context;
		try{
		Thread.sleep(10000);
		}
		catch(Exception e){}
		Toast.makeText(context,"ServiceManager working!",Toast.LENGTH_LONG).show();
		String action = intent.getAction();
		if (action.equalsIgnoreCase(BOOT_ACTION)) {
                        //check for boot complete event & start your service
			startService();
		} 
	}
	
	private void startService() {
        //here, you will start your service

		  Intent serviceIntent = new Intent(mContext,ServiceClass.class);
		  mContext.startService(serviceIntent);
		/*Intent mServiceIntent = new Intent();
		mServiceIntent.setAction("com.avengers.locationreminder.ServiceClass");
		mContext.startService(mServiceIntent);*/
}

}
