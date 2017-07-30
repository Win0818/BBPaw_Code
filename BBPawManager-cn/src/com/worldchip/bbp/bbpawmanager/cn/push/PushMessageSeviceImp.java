package com.worldchip.bbp.bbpawmanager.cn.push;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.worldchip.bbp.bbpawmanager.cn.db.DataManager;
import com.worldchip.bbp.bbpawmanager.cn.json.HomePageJsonParse;
import com.worldchip.bbp.bbpawmanager.cn.json.InformationJsonParse;
import com.worldchip.bbp.bbpawmanager.cn.json.NotifyMessageJsonParse;
import com.worldchip.bbp.bbpawmanager.cn.model.HomePageInfo;
import com.worldchip.bbp.bbpawmanager.cn.model.Information;
import com.worldchip.bbp.bbpawmanager.cn.model.NotifyMessage;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;

public class PushMessageSeviceImp {

	
	public static void parsePushMessage(Context context, String jsonStr) {
		String messageType = getMessageType(jsonStr);
		if (TextUtils.isEmpty(messageType))
			return;
		if (messageType.equals(PushMessageContents.NOTICE_MESSAGE_MAIN_TYPE)) {
			HomePageInfo info = HomePageJsonParse.doParseJsonToBean(jsonStr);
        	DataManager.updateNoticeBoardToDB(context, info);
		} else if (messageType.equals(PushMessageContents.GROWTH_MESSAGE_MAIN_TYPE)){
			
		} else if (messageType.equals(PushMessageContents.INFORMATION_MAIN_TYPE)){
			List<Information> infos = InformationJsonParse.doParseJsonToBean(jsonStr);
			DataManager.updateInformMessageToDB(context, infos);
		} else if (messageType.equals(PushMessageContents.NOTIFY_MSG_MAIN_TYPE)){
				List<NotifyMessage> infos = NotifyMessageJsonParse.doParseJsonToBean(jsonStr);
				DataManager.updateNotifyMessageToDB(context, infos);
		}
		Common.sendReceivePushMessageBroadcast(context, messageType);
		//PushMessageManager.getInstance().receivePushMessage(messageType);
	}
	
	
	public static String getMessageType(String jsonStr) {
		try {
			JSONObject person = new JSONObject(jsonStr);
			return person.getString(PushMessageContents.JSON_TYPE_KEY);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
}
