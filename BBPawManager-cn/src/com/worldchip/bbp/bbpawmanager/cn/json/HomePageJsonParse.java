package com.worldchip.bbp.bbpawmanager.cn.json;

import org.json.JSONException;
import org.json.JSONObject;

import com.worldchip.bbp.bbpawmanager.cn.model.HomePageInfo;

public class HomePageJsonParse {
	
	public static HomePageInfo doParseJsonToBean(String json) {
		HomePageInfo homePageInfo = new HomePageInfo();
		try {
			JSONObject person = new JSONObject(json);
			homePageInfo.setId(Integer.valueOf(person.getString("id")));
			homePageInfo.setTitle(person.getString("theme"));
			homePageInfo.setContent(person.getString("content"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return homePageInfo;
	}
	
	
}
