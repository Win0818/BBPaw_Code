package com.worldchip.bbp.bbpawmanager.cn.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;

import com.worldchip.bbp.bbpawmanager.cn.model.Information;

public class InformationJsonParse {
	
	@SuppressLint("SimpleDateFormat")
	public static List<Information> doParseJsonToBean(String json) {
		List<Information> list = new ArrayList<Information>();  
		try {
			JSONObject person = new JSONObject(json);
			int count = person.getInt("count");
			if (count <= 0) {
				return null;
			}
			JSONArray groupArray = person.getJSONArray("group");
			for(int i = 0; i < groupArray.length(); i++) {  
				Information info = new Information();
				info.setMainType(Integer.valueOf((person.getString("maintype"))));
			    JSONObject jsonObject = groupArray.getJSONObject(i); 
			    info.setMsgId(Integer.valueOf(jsonObject.getString("id")));
			    info.setType(Integer.valueOf(jsonObject.getString("type")));
			    info.setDate(jsonObject.getString("update_time"));
			    info.setMsgType(jsonObject.getInt("order_type"));
			    info.setIcon(jsonObject.getString("mini_image"));
			    info.setMsgImage(jsonObject.getString("large_image"));
			    info.setActivitiesUrl(jsonObject.getString("activities_url"));
			    info.setDownloadUrl(jsonObject.getString("url"));
			    info.setTitle(jsonObject.getString("title"));
			    info.setDescription(jsonObject.getString("description"));
			    info.setContent(jsonObject.getString("content"));
			    info.setRead(false);
			    list.add(info);
			}  
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static String getDefaultJson(Context context, String fileName) {

		StringBuilder stringBuilder = new StringBuilder();
		try {
			AssetManager assetManager = context.getAssets();
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					assetManager.open(fileName),"UTF-8"));
			String line;
			while ((line = bf.readLine()) != null) {
				stringBuilder.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}
