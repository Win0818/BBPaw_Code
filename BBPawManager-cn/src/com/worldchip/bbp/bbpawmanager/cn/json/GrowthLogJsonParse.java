package com.worldchip.bbp.bbpawmanager.cn.json;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.bbpawmanager.cn.model.GrowthMessage;
import com.worldchip.bbp.bbpawmanager.cn.model.HomePageInfo;
import com.worldchip.bbp.bbpawmanager.cn.model.HomePageSubInfo;

public class GrowthLogJsonParse {
	
	@SuppressLint("SimpleDateFormat")
	public static List<GrowthMessage> doParseJsonToBean(String json) {
		List<GrowthMessage> list = new ArrayList<GrowthMessage>();  
		try {
			JSONObject person = new JSONObject(json);
			JSONArray groupArray = person.getJSONArray("group");
			for(int i = 0; i < groupArray.length(); i++) {  
				GrowthMessage info = new GrowthMessage();
			    JSONObject jsonObject = groupArray.getJSONObject(i); 
			    info.setId(jsonObject.getString("id"));
			    info.setStudyTime(jsonObject.getString("study_time"));
			    info.setStudyConetnt(jsonObject.getString("content"));
			    info.setIconPath(jsonObject.getString("icon"));
			    String dateStr = jsonObject.getString("date");
			    if (dateStr != null && !TextUtils.isEmpty(dateStr)) {
			    	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy.MM.dd");
					Date date = new Date(Long.parseLong(dateStr));
				    info.setDate(dateSF.format(date));
			    }
			    list.add(info);
			}  
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return list;
	}
	
	
}
