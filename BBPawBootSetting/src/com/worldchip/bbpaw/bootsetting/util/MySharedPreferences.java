package com.worldchip.bbpaw.bootsetting.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MySharedPreferences {

	private SharedPreferences sp;
	private Editor editor;

	@SuppressLint("CommitPrefEdits")
	@SuppressWarnings("static-access")
	public MySharedPreferences(Context context) {
		sp = context.getSharedPreferences(MyConstants.USER_INFO,
				context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public void setName(String name) {
		editor.putString("name", name);
		editor.commit();
	}

	public void setBirthday(String birthday) {
		editor.putString("birthday", birthday);
		editor.commit();
	}

	public void setGender(String gender) {
		editor.putString("gender", gender);
		editor.commit();
	}

	public void setEmail(String email) {
		editor.putString("email", email);
		editor.commit();
	}

	public void setPassword(String password) {
		editor.putString("password", password);
		editor.commit();
	}

	public String getName() {
		return sp.getString("name", "");
	}

	public String getBirthday() {
		return sp.getString("birthday", "");

	}

	public String getGender() {
		return sp.getString("gender", "��");
	}

	public String getEmail() {
		return sp.getString("email", "");
	}

	public String getPassword() {
		return sp.getString("password", "123456");
	}
}
