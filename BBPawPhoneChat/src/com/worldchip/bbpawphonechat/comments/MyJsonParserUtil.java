package com.worldchip.bbpawphonechat.comments;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.GetChars;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity.ListQuestionEntity;
import com.worldchip.bbpawphonechat.entity.CommonProblemEntity.ListQuestionEntity.ResponseEntity;
import com.worldchip.bbpawphonechat.entity.LearnLog;

public class MyJsonParserUtil {

	private static final String TAG = "CHRIS";

	/**
	 * 获取Token值的Json解析
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static List<String> parserMyToken(String jsonStr) {
		List<String> mLoginDatas = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			String mUserName = jsonObject.getString("username");
			mLoginDatas.add(mUserName);
			String mPassword = jsonObject.getString("password");
			mLoginDatas.add(mPassword);
			Log.e(TAG, mUserName
					+ "&&&&&&&&&&MyJsonParserUtil保存登陆信息成功&&&&&&&&&&&"
					+ mPassword);
			return mLoginDatas;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 解析返回的json数据
	 */
	public static List<LearnLog> parserLearnLog(String jsonStr) {
		List<LearnLog> learnLogList = new ArrayList<LearnLog>();
		try {
			JSONObject person = new JSONObject(jsonStr);
			JSONArray groupArray = person.getJSONArray("group");
		    for (int i = 0; i < groupArray.length(); i++) {
		    	    LearnLog learnLog = new LearnLog();
					JSONObject jsonObject = groupArray.getJSONObject(i);
					learnLog.setId(jsonObject.getString("id"));
					learnLog.setLearnTime(jsonObject.getString("study_time"));
					learnLog.setLearnContent(jsonObject.getString("content"));
					learnLog.setApkIcon(jsonObject.getString("icon"));
				    String dateStr = jsonObject.getString("date");
				    if (dateStr != null && !TextUtils.isEmpty(dateStr)) {
				    	SimpleDateFormat dateSF = new SimpleDateFormat("yyyy.MM.dd");
						Date date = new Date(Long.parseLong(dateStr));
						learnLog.setLearnDate(dateSF.format(date));
				    }
				    learnLogList.add(learnLog);
				}
		    return learnLogList;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<CommonProblemEntity.ListQuestionEntity> parserCommonProblem(
			String jsonStr) {

		List<CommonProblemEntity.ListQuestionEntity> list = new ArrayList<CommonProblemEntity.ListQuestionEntity>();
		String resultCode, content, reposeContent, question_ID;
		JSONObject jsonObject, reposeObject, temp;
		JSONArray jsonArray, my_commit, BBpaw_feed;
		String commit_time, response_time;
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd  kk:mm");

		String whether_replied, whether_solved;
		boolean isReplied = false, isSolved = false;

		try {
			jsonObject = new JSONObject(jsonStr);
			resultCode = jsonObject.getString("resultCode");
			// Log.e("...resultCode.....", resultCode);
			if ("200".equals(resultCode)) {
				jsonArray = jsonObject.getJSONArray("list_question");

				for (int i = 0; i < jsonArray.length(); i++) {
					List<String> myUrl = new ArrayList<String>(), BBpawUrl = new ArrayList<String>();
					temp = jsonArray.getJSONObject(i);
					question_ID = temp.getString("question_ID");

					whether_replied = temp.getString("whether_replied");
					if ("0".equals(whether_replied)) {
						isReplied = false;
					} else if ("1".equals(whether_replied)) {
						isReplied = true;
					}

					whether_solved = temp.getString("whether_solved");
					if ("0".equals(whether_solved) || "".equals(whether_solved)) {
						isSolved = false;
					} else if ("1".equals(whether_solved)) {
						isSolved = true;
					}

					reposeObject = new JSONObject(temp.getString("response"));
					content = temp.getString("content");
					reposeContent = reposeObject.getString("content");
					if (reposeContent == null || "".equals(reposeContent)) {

						reposeContent = MyApplication.getInstance()
								.getApplicationContext().getResources()
								.getString(R.string.default_response);
					}

					commit_time = format.format(new Date(Long.parseLong(temp
							.getString("create_time"))));

					String repose_time = reposeObject.getString("create_time");
					if ("0".equals(repose_time)) {
						response_time = "";
					} else {
						response_time = format.format(new Date(Long
								.parseLong(repose_time)));

					}

					my_commit = temp.getJSONArray("user_image");

					int len = my_commit.length();
					if (len >= 4) {
						len = 3;
					}

					for (int j = 0; j < len; j++) {
						String str = my_commit.getJSONObject(j)
								.getString("url");
						if (str != null) {
							myUrl.add(str);
						} else {
							myUrl.add("");
						}

					}
					BBpaw_feed = new JSONArray(
							reposeObject.getString("feed_image"));
					for (int k = 0; k < BBpaw_feed.length(); k++) {
						String str = BBpaw_feed.getJSONObject(k).getString(
								"url");

						BBpawUrl.add(str);

					}

					list.add(new ListQuestionEntity(content, myUrl,
							commit_time, isReplied, isSolved, question_ID,
							new ResponseEntity(reposeContent, BBpawUrl,
									response_time)));

				}
				return list;

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static List<String> praserNewVersionInfos(String jsonstr) {
		List<String> newVersionInfos = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(jsonstr);
			newVersionInfos.add(jsonObject.getString("file_name"));
			newVersionInfos.add(jsonObject.getString("version"));
			newVersionInfos.add(jsonObject.getString("introduction"));
			newVersionInfos.add(jsonObject.getString("url"));
			Log.i(TAG,
					"------praserNewVersionInfos------"
							+ newVersionInfos.size());
			return newVersionInfos;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String parserMyheadUrl(String imageUrl) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(imageUrl);
			String imgUrl = jsonObject.getString("image");
			return imgUrl;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
