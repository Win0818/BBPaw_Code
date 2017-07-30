package com.worldchip.bbp.bbpawmanager.cn.utils;

import com.worldchip.bbp.bbpawmanager.cn.model.NotifyMessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

public class NotifyUtils {

	public static final long VIBRA_TIME = 1000;
	public static final int ID_LED = 19871103;
	public static final String DEFAULT_RINGTONE = "default";
	
	public static void notify(Context context, String notifyType, String notifyValue) {
		if (!TextUtils.isEmpty(notifyType)) {
			String[] split = notifyType.split(",");
			for (int i =0;i<split.length;i++) {
				String type = split[i].trim();
				if (type.equals(String.valueOf(NotifyMessage.NOTIFY_TYPE_MUSIC))) {
					playSound(context, notifyValue);
				} else if (type.equals(String.valueOf(NotifyMessage.NOTIFY_TYPE_VIBRA))) {
					vibrator(context, VIBRA_TIME);
				} else if(type.equals(String.valueOf(NotifyMessage.NOTIFY_TYPE_LED))) {
					LEDLight(context);
				}
			}
		}
	}
	
	public static void playSound(Context context, String url) {
		 Log.e("lee", "playSound ------------------------>>>>>>>");
		 if (TextUtils.isEmpty(url) || url.trim().equals(DEFAULT_RINGTONE)) {
			 Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			 Log.e("lee", " defaultUri == "+defaultUri);
			 NotifyPlayer.getInstance().playByUri(context, defaultUri,false);
		 } else {
			 Log.e("lee", " url == "+url);
			 NotifyPlayer.getInstance().playByUrl(url);
		 }
		 
	}
	
	
	public static void vibrator(Context cotext, long milliseconds) {
		Vibrator vib = (Vibrator) cotext.getSystemService(Service.VIBRATOR_SERVICE);   
        vib.vibrate(milliseconds); 
        Log.e("lee", "vibrator ------------------------>>>>>>>");
	}
	
	public static void LEDLight(Context cotext) {
		NotificationManager notify =(NotificationManager)cotext.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
        notification.ledARGB = 0xFFFFFF;
        notification.ledOnMS = 100;
        notification.ledOffMS = 100;
        notification.flags = Notification.FLAG_SHOW_LIGHTS;
        notify.notify(ID_LED, notification);
        notify.cancel(ID_LED);
		Log.e("lee", "LEDLight ------------------------>>>>>>>");
	}
}
