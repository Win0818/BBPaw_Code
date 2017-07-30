package com.worldchip.bbp.bbpawmanager.cn.json;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;

import com.worldchip.bbp.bbpawmanager.cn.model.NotifyMessage;

public class NotifyMessageJsonParse {
	
	@SuppressLint("SimpleDateFormat")
	public static List<NotifyMessage> doParseJsonToBean(String json) {
		List<NotifyMessage> list = new ArrayList<NotifyMessage>();  
		try {
			JSONObject person = new JSONObject(json);
			JSONArray groupArray = person.getJSONArray("group");
			for(int i = 0; i < groupArray.length(); i++) {  
				NotifyMessage info = new NotifyMessage();
				info.setMainType(Integer.valueOf((person.getString("maintype"))));
			    JSONObject jsonObject = groupArray.getJSONObject(i); 
			    //info.setId(Integer.valueOf(jsonObject.getString("id")));
			    info.setType(Integer.valueOf(jsonObject.getString("type")));
			    info.setTitle(jsonObject.getString("title"));
			    info.setDescription(jsonObject.getString("content"));
			    info.setClickOperation(Integer.valueOf(jsonObject.getString("click_operation")));
			    info.setOperationValue(jsonObject.getString("operation_value"));
			    info.setNotifyType(jsonObject.getString("notify_type"));
			    info.setMusicUri(jsonObject.getString("music_uri"));
			    info.setDate(jsonObject.getString("update_time"));
			    info.setRead(false);
			    list.add(info);
			}  
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return list;
	}
	
	
}
