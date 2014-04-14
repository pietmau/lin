package com.lisnx.event;

public class EventNotifier {

	public static ILisner lisner;
	
	public EventNotifier(ILisner lisner){
		EventNotifier.lisner = lisner;
	}
	
	public EventNotifier(){}
	
	public void notifyEvent(){
		if(lisner != null){
			lisner.updateLocation();
		}
	}
}
