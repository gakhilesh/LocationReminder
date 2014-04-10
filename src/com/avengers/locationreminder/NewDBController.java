package com.avengers.locationreminder;

public class NewDBController 
{
	String key;
	String value;
	public NewDBController(String key,String value)
	{
		this.key=key;
		this.value=value;
	}
	public String getKey()
	{
		return this.key;
	}
	public String getValue()
	{
		return this.value;
	}
}
