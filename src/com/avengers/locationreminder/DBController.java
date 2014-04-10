package com.avengers.locationreminder;

public class DBController {
	String longitude;
	String latitude;
	String address;
	String accuracy;
	String units;
	String flag;
	String tasks;
	String nexttime;
	String currentlyset;
	String uri;
	public DBController()
	{}
	
	
	
	public DBController(String longitude,String latitude,String address,String accuracy,String units,	String flag, String tasks,	String nexttime,String currentlyset,String uri)
	{
		this.longitude=longitude;
		this.latitude=latitude;
		this.address=address;
		this.accuracy=accuracy;
		this.units=units;
		this.flag=flag;
		this.tasks=tasks;
		this.nexttime=nexttime;
		this.currentlyset=currentlyset;
		this.uri=uri;
		//Log.d(DBController.class.getSimpleName(),"Katrina Boom Boom"+longitude+"\n"+latitude+"\n"+accuracy+"\n"+units+"\n"+flag+"\n"+tasks+"\n"+nexttime+"\n"+currentlyset);
	}
	
	
	public String getLatitude()
	{
		return this.latitude;
		
	}
	
	
	public String getUnits()
	{
		
		return this.units;
		
	}
	
	public String getAccuracy()
	{
		
		return this.accuracy;
		
	}
	
	
	
	public String getLongitude()
	{
		
		return this.longitude;
		
	}
	public String getAddress()
	{
		return this.address;
		
	}
	
	
	public String getFlag()
	{
		
		return this.flag;
		
	}
	public String getTasks()
	{
		
		return this.tasks;
		
	}
	
	public String getCurrentlySet()
	{
		
		return this.currentlyset;
		
	}
	
	public String getNextTime()
	{
		
		return this.nexttime;
		
	}
	
	public String uri()
	{
		return this.uri;
	}
	
	
	
}
