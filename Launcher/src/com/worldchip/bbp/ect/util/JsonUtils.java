package com.worldchip.bbp.ect.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.worldchip.bbp.ect.entity.FindPasswordInfo;
import com.worldchip.bbp.ect.entity.LoginState;
import com.worldchip.bbp.ect.entity.UpdateBabyInfo;
import com.worldchip.bbp.ect.entity.UpdatePassword;

public class JsonUtils {

	public static LoginState doLoginJson2Bean(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) return null;
		LoginState loginState = LoginState.getInstance();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			int resultCode = jsonObject.getInt("resultCode");
			loginState.setResultCode(resultCode);
			loginState.setUserName(jsonObject.getString("user_name"));
			loginState.setPhotoUrl(jsonObject.getString("user_photo"));
			String agePeriod = jsonObject.getString("age_period");
			if (agePeriod!= null && !TextUtils.isEmpty(agePeriod)) {
				loginState.setAgeIndex(Integer.valueOf(agePeriod));
			}
			loginState.setBirthday(jsonObject.getString("dob"));
			String gender = jsonObject.getString("gender");
			if (gender!= null && !TextUtils.isEmpty(gender)) {
				loginState.setSex(Integer.valueOf(gender));
			}
			if (resultCode == HttpUtils.HTTP_RESULT_CODE_SUCCESS) {
				loginState.setClientKey(jsonObject.getString("clientKey"));
			} else {
				loginState.setClientKey("");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return loginState;
	}
	
	public static UpdatePassword doupdateJson2bean(String jsonstring){
		if (TextUtils.isEmpty(jsonstring)) return null;
		UpdatePassword updatepwd=UpdatePassword.getInstance();
		try {
		JSONObject jsonObject = new JSONObject(jsonstring);
		updatepwd.setResultCode(jsonObject.getInt("resultCode"));
		updatepwd.setUserName(jsonObject.getString("user_name"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return updatepwd;
	}
	
	public static String doParseValueForKey(String jsonstring, String key) {
		String value = "";
		if (!TextUtils.isEmpty(jsonstring)) {
			try {
				JSONObject jsonObject = new JSONObject(jsonstring);
					value = jsonObject.getString(key);
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
		}
		return value;
	}
	
	public static FindPasswordInfo doOriginalPSWJson2Bean(String jsonstring) {
		if (TextUtils.isEmpty(jsonstring)) return null;
		FindPasswordInfo info = new FindPasswordInfo();
		try {
			JSONObject jsonObject = new JSONObject(jsonstring);
			int resultCode = jsonObject.getInt("resultCode");
			info.setResultCode(resultCode);
			if (resultCode == HttpUtils.HTTP_RESULT_CODE_SUCCESS) {
				info.setAccount(jsonObject.getString("account"));
				info.setOriginalPassword(jsonObject.getString("password"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return info;
	}
	
	public static UpdateBabyInfo updateBabyInfoJson2bean(String jsonstring){
		if (TextUtils.isEmpty(jsonstring)) return null;
		UpdateBabyInfo updateBabyInfo=UpdateBabyInfo.getInstance();
		try {
		JSONObject jsonObject = new JSONObject(jsonstring);
		updateBabyInfo.setRusltCode(jsonObject.getInt("resultCode"));
		updateBabyInfo.setUsername(jsonObject.getString("user_name"));
		updateBabyInfo.setBrithday(jsonObject.getString("dob"));
		updateBabyInfo.setImagePath(jsonObject.getString("user_photo"));
		updateBabyInfo.setSex(jsonObject.getInt("gender"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return updateBabyInfo;
	}
	
	public static List<Integer> parseApkMenuData(String jsonstring) {
		List<Integer> menuData = new ArrayList<Integer>();
		if (TextUtils.isEmpty(jsonstring))
			return null;
		JSONObject jsonObject ;
		try {
			jsonObject = new JSONObject(jsonstring);
			menuData.add(jsonObject.getInt("TotalGameCount"));
			menuData.add(jsonObject.getInt("Arts_Music"));
			menuData.add(jsonObject.getInt("Arts_Painting"));
			menuData.add(jsonObject.getInt("EliteEducation"));
			menuData.add(jsonObject.getInt("InteractiveGames"));
			menuData.add(jsonObject.getInt("LearningZone_Chinese"));
			menuData.add(jsonObject.getInt("LearningZone_English"));
			menuData.add(jsonObject.getInt("LearningZone_GuoXue"));
			menuData.add(jsonObject.getInt("LearningZone_LifeSkills"));
			menuData.add(jsonObject.getInt("LearningZone_Mathematics"));
			menuData.add(jsonObject.getInt("LearningZone_Stories"));
			menuData.add(jsonObject.getInt("Science"));
			menuData.add(jsonObject.getInt("ToolBox"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return menuData;
	}
	
}
