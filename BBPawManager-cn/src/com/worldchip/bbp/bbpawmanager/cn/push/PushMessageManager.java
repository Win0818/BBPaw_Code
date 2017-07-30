package com.worldchip.bbp.bbpawmanager.cn.push;

import java.util.ArrayList;
import java.util.List;

public class PushMessageManager implements Observable {

	private List<Observer> mObservers = new ArrayList<Observer>();
	private static PushMessageManager mInstance = null;
	public static final String RECEIVE_MESSAGE_EVENT = "RECEIVE_MESSAGE_EVENT";
	
	
	
	public synchronized static PushMessageManager getInstance() {
    	if (mInstance == null) {
    		mInstance = new PushMessageManager();
    	}
    	return mInstance;
    }
	
	
	public void receivePushMessage(String messageType) {
		notifyObservers(RECEIVE_MESSAGE_EVENT, messageType);
	}
	
	private void notifyObservers(String event, String messageType) {
        for (Observer obs : mObservers) {
            obs.update(event, messageType);
        }
	}
	
	
	public synchronized void addObserver(Observer obs)
	{
		if (mObservers.indexOf(obs) < 0) 
		{
			mObservers.add(obs);
		}
	}
	
	public synchronized void deleteObserver(Observer obs) {
		mObservers.remove(obs);
	}
	
}
