package com.worldchip.bbpaw.bootsetting.util;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.text.TextUtils;

public class AccountRegisterUtil {

	public static final int PASSWORD_VALID = 0;
	public static final int PASSWORD_ENPTY = 1;
	public static final int PASSWORD_NOT_MATCH = 2;
	public static final int PASSWORD_MIN_LENGTH = 6;
	
	public static boolean validateAccountName(String name) {
		boolean isValid = false;
		if (name != null && !TextUtils.isEmpty(name)) {
			return true;
		}
		return isValid;
	}

	public static boolean validateBirthday(String birthday) {
		if (birthday != null && !TextUtils.isEmpty(birthday)) {
			/*int age = calculateAge(birthday, "yyyy-MM-dd");
			if (age >= 0 && age <= 12) {
				return true;
			}*/
			String[] birthDaySet = birthday.split("-");
			int birthMonth = Integer.parseInt(birthDaySet[1]);
			int birthdayDay = Integer.parseInt(birthDaySet[2]);
			if (birthMonth <= 0 || birthMonth > 12) {
				return false;
			} else if (birthdayDay <= 0 || birthdayDay > 31) {
				return false;
			} else {
				return true;
			}
			
		}
		return false;
	}

	public static boolean validateEmail(String email) {
		boolean isValid = false;
		if (null != email || !TextUtils.isEmpty(email)) {
			Pattern p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");// 复杂匹配
			Matcher m = p.matcher(email);
			return m.matches();
		}
		return isValid;
	}
	
	public static boolean validatePhoneNumber(String number) {
		boolean isValid = false;
		if (number != null || !TextUtils.isEmpty(number)) {
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(number);
			return m.matches();
		}
		return isValid;
	}

	public static int validatePassword(String password,String confirmPsw) {
		if (password == null || TextUtils.isEmpty(password)) {
			return PASSWORD_ENPTY;
		} else if (!password.equals(confirmPsw)) {
			return PASSWORD_NOT_MATCH;
		} else if (confirmPsw.length() < PASSWORD_MIN_LENGTH){
			return PASSWORD_MIN_LENGTH;
		}else {
			return PASSWORD_VALID;
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static int calculateAge(String birthDay, String formatPattern) {
			if (birthDay == null || TextUtils.isEmpty(birthDay)) {
				return -1;
			}
			
			Calendar calendar = Calendar.getInstance();
			int currYear = calendar.get(Calendar.YEAR);
			int currMonth = calendar.get(Calendar.MONTH) +1;
			int currDay = calendar.get(Calendar.DAY_OF_MONTH);
			String[] birthDaySet = birthDay.split("-");
			int birthYear = Integer.parseInt(birthDaySet[0]);
			int birthMonth = Integer.parseInt(birthDaySet[1]);
			int birthdayDay = Integer.parseInt(birthDaySet[2]);
			int age = currYear - birthYear;
			if (birthMonth <= 0 || birthMonth > 12) {
				return -1;
			}
			if (birthdayDay <= 0 || birthdayDay > 31) {
				return -1;
			}
			
			if (currMonth - birthMonth < 0) {
				age = age - 1;
			} else if (currDay - birthdayDay < 0) {
				age = age - 1;
			}
			return age;
	}
}
