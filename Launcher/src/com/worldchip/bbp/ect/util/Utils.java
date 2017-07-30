package com.worldchip.bbp.ect.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.backup.BackupManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.MyApplication;
import com.worldchip.bbp.ect.db.DataManager;
import com.worldchip.bbp.ect.entity.IconInfo;
import com.worldchip.bbp.ect.entity.PatriarchControl;

public class Utils {

	public static final String MESSAGE_PUSH_SWITCH_ACTION = "com.worldchip.bbpaw.messageSwitch.Action";

	public static final String CLOSE_REST_WINDOW = "colse_rest_window";

	public static final int LOAD_SYSTEM_IMAGE_REQUEST_CODE = 800;
	public static final int LOAD_CUSTOM_IMAGE_REQUEST_CODE = 100;
	public static final int CROP_IMAGE_REQUEST_CODE = 200;

	public static final String USER_SHARD_USERNAME_KEY = "user_name";
	public static final String USER_SHARD_BIRTHDAY_KEY = "user_birthday";
	public static final String USER_SHARD_SEX_KEY = "user_sex";
	public static final String USER_SHARD_PHOTO_KEY = "user_photo";
	public static final String USER_SHARD_AGEINDEX_KEY = "age_index";

	public static final int BEAR_ANIM_GROUP_COUNT = 4;

	public static final int BEAR_ANIM_DEFAULT_INDEX = 0;
	public static final int BEAR_ANIM_SAYHELLO_INDEX = 1;
	public static final int BEAR_ANIM_HANDSTAND_INDEX = 2;
	public static final int BEAR_ANIM_ROTATE_INDEX = 3;

	public static final int BEAR_ANIM_DEFAULT_RES_BEGIN = 26;
	public static final int BEAR_ANIM_SAYHELLO_RES_BEGIN = 1;
	public static final int BEAR_ANIM_HANDSTAND_RES_BEGIN = 51;
	public static final int BEAR_ANIM_ROTATE_RES_BEGIN = 76;

	public static final int BEAR_ANIM_SAYHELLO_LENGHT = 25;
	public static final int BEAR_ANIM_DEFAULT_LENGHT = 25;
	public static final int BEAR_ANIM_HANDSTAND_LENGHT = 25;
	public static final int BEAR_ANIM_ROTATE_LENGHT = 25;

	public static final int[] BEAR_ANIM_DEFAULT_RES = new int[BEAR_ANIM_DEFAULT_LENGHT];
	public static final int[] BEAR_ANIM_SAYHELLO_RES = new int[BEAR_ANIM_SAYHELLO_LENGHT];
	public static final int[] BEAR_ANIM_HANDSTAND_RES = new int[BEAR_ANIM_HANDSTAND_LENGHT];
	public static final int[] BEAR_ANIM_ROTATE_RES = new int[BEAR_ANIM_ROTATE_LENGHT];

	public static final int AGE_3_4_OLD_INDEX = 0;
	public static final int AGE_4_5_OLD_INDEX = 1;
	public static final int AGE_5_6_OLD_INDEX = 2;

	public static final int ENG_LANGUAGE_INDEX = 0;
	public static final int CH_LANGUAGE_INDEX = 1;

	public static final String USER_BIRTHDAY_TIME_FORMAT = "yyyy-MM-dd";

	public static final int[][] BEAR_ANIM_RES = { BEAR_ANIM_DEFAULT_RES,
			BEAR_ANIM_SAYHELLO_RES, BEAR_ANIM_HANDSTAND_RES,
			BEAR_ANIM_ROTATE_RES };

	public static final String LOGIN_SECCUSS_ACTION = "com.worldchip.login.Action";
	public static final String LOGIN_OUT_ACTICON = "com.worldchip.loginout.Action";

	public static final int GALLERY_VIEWS_COUNT = 6;

	public static final String[] GALLERY_IMAGE_NAME_LIST = {
			"gallery_winsdom_selector", "gallery_education_selector",
			"gallery_yuleqihuan_selector", "gallery_jingying_selector",
			"gallery_dancemusic_selector", "gallery_kexujiaoyu_selector" };

	public static final int WHEELVIEW_TEXT_COLOR_NORMAL = Color
			.parseColor("#262425");
	public static final int WHEELVIEW_TEXT_COLOR_SELECTED = Color
			.parseColor("#061DFC");

	public static final String[] WHEEL_YEAR_ARR = new String[] { "2001",
			"2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
			"2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017",
			"2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025",
			"2026", "2027", "2028", "2029", "2030" };

	public static final String[] WHEEL_MONTH_ARR = new String[] { "01", "02",
			"03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };

	public static final String[] WHEEL_DAY_ARR = new String[] { "01", "02",
			"03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13",
			"14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
			"25", "26", "27", "28", "29", "30", "31" };

	public static List<PatriarchControl> controls = new ArrayList<PatriarchControl>();

	public static final int[] APP_NAME_SOUNDS_RES_MAP_CN = { R.raw.gnb17_cn,
			R.raw.gnb01_cn, R.raw.gnb16_cn, R.raw.gnb07_cn, R.raw.gnb13_cn,
			R.raw.gnb12_cn, R.raw.gnb18_cn, R.raw.gnb19_cn };
	public static final int[] APP_NAME_SOUNDS_RES_MAP_ENG = {
			R.raw.gn_b_18_eng, R.raw.gn_b_1_eng, R.raw.gn_b_17_eng,
			R.raw.gn_b_8_eng, R.raw.gn_b_14_eng, R.raw.gn_b_13_eng,
			R.raw.gn_b_19_eng, R.raw.gn_b_20_eng };

	static {

		PatriarchControl patriarchControl = new PatriarchControl();
		patriarchControl.setName("密码管理");
		patriarchControl.setDrawableTopImg(R.drawable.icon_a);
		patriarchControl.setIndex(1);

		PatriarchControl patriarchControl1 = new PatriarchControl();
		patriarchControl1.setName("行为统计");
		patriarchControl1.setDrawableTopImg(R.drawable.icon_b);
		patriarchControl1.setIndex(2);

		PatriarchControl patriarchControl3 = new PatriarchControl();
		patriarchControl3.setName("时间控制");
		patriarchControl3.setDrawableTopImg(R.drawable.icon_c);
		patriarchControl3.setIndex(3);

		PatriarchControl patriarchControl4 = new PatriarchControl();
		patriarchControl4.setName("媒体共享");
		patriarchControl4.setDrawableTopImg(R.drawable.icon_d);
		patriarchControl4.setIndex(4);

		PatriarchControl patriarchControl5 = new PatriarchControl();
		patriarchControl5.setName("家长模式");
		patriarchControl5.setDrawableTopImg(R.drawable.icon_e);
		patriarchControl5.setIndex(5);

		PatriarchControl patriarchControl6 = new PatriarchControl();
		patriarchControl6.setName("应用管理");
		patriarchControl6.setDrawableTopImg(R.drawable.icon_f);
		patriarchControl6.setIndex(6);

		PatriarchControl patriarchControl7 = new PatriarchControl();
		patriarchControl7.setName("网页管理");
		patriarchControl7.setDrawableTopImg(R.drawable.icon_g);
		patriarchControl7.setIndex(7);

		PatriarchControl patriarchControl8 = new PatriarchControl();
		patriarchControl8.setName("在线商店");
		patriarchControl8.setDrawableTopImg(R.drawable.icon_h);
		patriarchControl8.setIndex(8);

		PatriarchControl patriarchControl9 = new PatriarchControl();
		patriarchControl9.setName("消息推送");
		patriarchControl9.setDrawableTopImg(R.drawable.icon_i);
		patriarchControl9.setIndex(9);

		controls.add(patriarchControl);
		controls.add(patriarchControl1);
		controls.add(patriarchControl3);
		controls.add(patriarchControl4);
		controls.add(patriarchControl5);
		controls.add(patriarchControl6);
		controls.add(patriarchControl7);
		controls.add(patriarchControl8);
		controls.add(patriarchControl9);

		Resources resources = MyApplication.getAppContext().getResources();
		String pattern = "00000";
		java.text.DecimalFormat df = new java.text.DecimalFormat(pattern);
		for (int i = 0; i < BEAR_ANIM_GROUP_COUNT; i++) {
			int begin = 0;
			int lenght = 0;
			if (i == BEAR_ANIM_DEFAULT_INDEX) {
				begin = BEAR_ANIM_DEFAULT_RES_BEGIN;
				lenght = BEAR_ANIM_DEFAULT_RES_BEGIN + BEAR_ANIM_DEFAULT_LENGHT;

			} else if (i == BEAR_ANIM_SAYHELLO_INDEX) {
				begin = BEAR_ANIM_SAYHELLO_RES_BEGIN;
				lenght = BEAR_ANIM_SAYHELLO_RES_BEGIN
						+ BEAR_ANIM_SAYHELLO_LENGHT;

			} else if (i == BEAR_ANIM_HANDSTAND_INDEX) {
				begin = BEAR_ANIM_HANDSTAND_RES_BEGIN;
				lenght = BEAR_ANIM_HANDSTAND_RES_BEGIN
						+ BEAR_ANIM_HANDSTAND_LENGHT;

			} else if (i == BEAR_ANIM_ROTATE_INDEX) {
				begin = BEAR_ANIM_ROTATE_RES_BEGIN;
				lenght = BEAR_ANIM_ROTATE_RES_BEGIN + BEAR_ANIM_ROTATE_LENGHT;
			}

			for (int j = begin; j < lenght; j++) {
				String idName = "bg_" + df.format(j);
				try {
					int id = resources.getIdentifier(idName, "drawable",
							MyApplication.getAppContext().getPackageName());
					if (i == BEAR_ANIM_DEFAULT_INDEX) {
						BEAR_ANIM_DEFAULT_RES[j - begin] = id;
					} else if (i == BEAR_ANIM_SAYHELLO_INDEX) {
						BEAR_ANIM_SAYHELLO_RES[j - begin] = id;
					} else if (i == BEAR_ANIM_HANDSTAND_INDEX) {
						BEAR_ANIM_HANDSTAND_RES[j - begin] = id;
					} else if (i == BEAR_ANIM_ROTATE_INDEX) {
						BEAR_ANIM_ROTATE_RES[j - begin] = id;
					}
				} catch (Exception e) {
					Log.e("lee", e.toString());
				}
			}
		}

	}

	public static void showToastMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static void setTextViewTTF(Context context, TextView textview) {
		AssetManager mgr = context.getAssets();
		Typeface tf = Typeface.createFromAsset(mgr, "Droidhuakangbaoti.TTF");
		textview.setTypeface(tf);
	}

	public static void setFlickerAnimation(ImageView iv_chat_head) {
		final Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(500);
		animation.setInterpolator(new LinearInterpolator());
		animation.setRepeatCount(Animation.INFINITE);
		animation.setRepeatMode(Animation.REVERSE);
		iv_chat_head.setAnimation(animation);
	}

	public static void CloseKeyBoard(Context context, EditText text) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
	}

	public static void CloseKeyBoard(Context context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		System.out.println("isActive:" + imm.isActive());
		if (imm.isActive()) {

			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);

		}

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPixels) {
		if (bitmap == null) {
			return null;
		}

		Bitmap roundConcerImage = Bitmap.createBitmap(98, 98, Config.ARGB_8888);

		Canvas canvas = new Canvas(roundConcerImage);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return roundConcerImage;
	}

	public static void startApp(Context context, String packageName,
			String activityName) {

		try {
			Intent intent = new Intent();
			// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ComponentName comp = new ComponentName(packageName, activityName);
			intent.setComponent(comp);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(context, R.string.not_found_app_error_toast,
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(context, R.string.start_app_error_toast,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public static int getDrawableId(Context context, String resName) {
		String language = Locale.getDefault().getLanguage();
		String resString = resName + "_eng";
		if (language.contains("zh")) {
			resString = resName + "_cn";
		} else {
			resString = resName + "_eng";
		}
		return context.getResources().getIdentifier(resString, "drawable",
				context.getPackageName());
	}

	public static int getLanguageInfo(Context context) {
		String language = Locale.getDefault().getLanguage();
		if (language.contains("zh")) {
			return CH_LANGUAGE_INDEX;
		} else {
			return ENG_LANGUAGE_INDEX;
		}
	}

	@SuppressLint("NewApi")
	public static boolean getSetupWizardPreference(Context context) {
		try {
			Context bbpawmanagerContext = context.createPackageContext(
					"com.worldchip.bbpaw.bootsetting",
					Context.CONTEXT_IGNORE_SECURITY);
			SharedPreferences pref = bbpawmanagerContext.getSharedPreferences(
					"user", Context.MODE_MULTI_PROCESS);
			String value = pref.getString("first_start", "true");
			return Boolean.parseBoolean(value);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static void startApplication(Context context, String packageName,
			String activityName) {

		if (TextUtils.isEmpty(packageName))
			return;

		try {
			Intent intent = null;
			if (TextUtils.isEmpty(activityName)) {
				intent = context.getPackageManager().getLaunchIntentForPackage(
						packageName);
			} else {
				intent = new Intent();
				intent.setComponent(new ComponentName(packageName, activityName));
			}
			if (intent == null) {
				intent = getLaunchIntentForNoCategory(context, packageName);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Intent getLaunchIntentForNoCategory(Context context,
			String packageName) {
		Intent intent = null;

		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageinfo = null;
		try {
			packageinfo = packageManager.getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (packageinfo == null) {
			return null;
		}
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.setPackage(packageinfo.packageName);
		List<ResolveInfo> resolveinfoList = packageManager
				.queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			String className = resolveinfo.activityInfo.name;
			intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			ComponentName cn = new ComponentName(packageName, className);
			intent.setComponent(cn);
		}
		return intent;
	}

	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static String getDeviceId(Context context) {
		String deviceID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return deviceID;
	}

	public static String getCpuSerial(Context context) {
		FileReader fr = null;
		BufferedReader br = null;
		String cpuSerial = null;
		try {
			fr = new FileReader("/proc/cpuinfo");
			br = new BufferedReader(fr);
			String temp = null;
			while ((temp = br.readLine()) != null) {
				cpuSerial = temp;
			}
			cpuSerial = cpuSerial.replaceAll("\\s*", "");
			String[] array = cpuSerial.split(":");
			for (int i = 0; i < array.length; i++) {
				cpuSerial = array[i];
			}
			if (cpuSerial != null) {
				return cpuSerial;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static String date2TimeStamp(String date_str, String format) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return String.valueOf(sdf.parse(date_str).getTime() / 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
	public static void updateLanguage(final Locale locale) {
		new Thread() {
			public void run() {
				try {
					Class amnClass = Class
							.forName("android.app.ActivityManagerNative");
					Object amn = null;
					Configuration config = null;

					// amn = ActivityManagerNative.getDefault();
					Method methodGetDefault = amnClass.getMethod("getDefault");
					methodGetDefault.setAccessible(true);
					amn = methodGetDefault.invoke(amnClass);

					// config = amn.getConfiguration();
					Method methodGetConfiguration = amnClass
							.getMethod("getConfiguration");
					methodGetConfiguration.setAccessible(true);
					config = (Configuration) methodGetConfiguration.invoke(amn);

					// config.setLocale(locale);
					Class configClass = config.getClass();

					Method methodSetLocale = configClass.getMethod("setLocale",
							Locale.class);
					methodSetLocale.invoke(config, locale);

					// amn.updateConfiguration(config);
					Method methodUpdateConfiguration = amnClass.getMethod(
							"updateConfiguration", Configuration.class);
					methodUpdateConfiguration.setAccessible(true);

					methodUpdateConfiguration.invoke(amn, config);
					BackupManager.dataChanged("com.android.providers.settings");
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	public static int getResourcesId(Context context, String resName,
			String resTpye) {
		String language = Locale.getDefault().getLanguage();
		String resString = resName + "_eng";
		if (language.contains("zh")) {
			resString = resName + "_cn";
		} else {
			resString = resName + "_eng";
		}
		return context.getResources().getIdentifier(resString, resTpye,
				context.getPackageName());
	}

	public static List<IconInfo> getGalleryImageList() {
		List<IconInfo> imageViewList = new ArrayList<IconInfo>();
		for (int i = 0; i < GALLERY_VIEWS_COUNT; i++) {
			IconInfo icon = new IconInfo();
			icon.setIndex(i);
			imageViewList.add(icon);
		}
		return imageViewList;
	}

	public static int getGalleryImageDrawble(Context context, String resName) {
		String language = Locale.getDefault().getLanguage();
		String resString = resName + "_eng";
		if (language.contains("zh")) {
			resString = "cn_" + resName;
		} else {
			resString = "eng_" + resName;
		}
		return context.getResources().getIdentifier(resString, "drawable",
				context.getPackageName());
	}

	public static int calculateAge(String timeStamp) {
		if (timeStamp == null || TextUtils.isEmpty(timeStamp)) {
			return -1;
		}
		String birthDay = DateTimePickDialogUtil.getDateToString(timeStamp,
				"yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		int currYear = calendar.get(Calendar.YEAR);
		int currMonth = calendar.get(Calendar.MONTH) + 1;
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

	@SuppressLint("SimpleDateFormat")
	public static String timeStamp2Date(String timeStamp, String format) {
		if (TextUtils.isEmpty(timeStamp)) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(Long.valueOf(timeStamp)));
	}

	public static void initBBpawDataBases(final Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AudioManager audio = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
				if (volume == 0) {
					if (DataManager.getMainIsPlayEffectEnable(MyApplication.getAppContext())) {
						DataManager.setMainIsPlayEffectEnable(
								MyApplication.getAppContext(), false);
					}
					
				} else {
					if (!(DataManager.getMainIsPlayEffectEnable(MyApplication.getAppContext()))) {
						DataManager.setMainIsPlayEffectEnable(
								MyApplication.getAppContext(), true);
					}
				}
			}
		}).start();

	}
	
    private static long lastClickTime = 0;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();  
        long timeSpan = time - lastClickTime;
        if (  timeSpan > 0 && timeSpan< 2000) {   
            return true;   
        }   
        lastClickTime = time;   
        return false;   
    }
	
}