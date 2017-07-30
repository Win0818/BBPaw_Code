package com.egreat.adlauncher.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.egreat.adlauncher.activity.MainActivity;
import com.mgle.launcher.R;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class WeatherUtil {

	public static final String LOGTAG = "WeatherUtil";

	public String date = null;
	public String weather = null;
	public String wind = null;
	public String temperature = null;

	Handler mHandler = null;
	Context mContext = null;

	public WeatherUtil(Context context, Handler handler) {
		mHandler = handler;
		mContext = context;
		getContentThread.start();
	}

	Thread getContentThread = new Thread() {
		@Override
		public void run() {
			Log.d(LOGTAG, "getContentThread");
			String ip = getIpaddress();
			String city = getCityCode(ip);
			getWeatherInfo(city);
		}
	};

	public String getIpaddress() {
		Log.d(LOGTAG, "getIpaddress");
		String url = "http://live.orangelive.tv:10086/IPAddress";
		String ipaddr = AppTool.getXmlStr(url);
		return ipaddr;
	}

	public String getCityCode(String ip) {
		Log.d(LOGTAG, "getCityCode");
		if (AppTool.isEmpty(ip))
			return null;
		String url;

		url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=" + ip;

		String result = AppTool.getXmlStr(url);
		if (AppTool.isEmpty(result))
			return null;

		try {
			JSONTokener jsTokener = new JSONTokener(result);
			JSONObject obj = (JSONObject) jsTokener.nextValue();

			String city = obj.getString("city");
			return city;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void getWeatherInfo(String city) {
		Log.d(LOGTAG, "getWeatherInfo");
		if (AppTool.isEmpty(city))
			return;
		String url;
		try {
			url = "http://weather.orangelive.tv:10086/weather/weather.php?city=" + URLEncoder.encode(city, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return;
		}
		String result = AppTool.getXmlStr(url);
		if (AppTool.isEmpty(result))
			return;

		try {
			JSONTokener jsTokener = new JSONTokener(result);
			JSONObject obj = (JSONObject) jsTokener.nextValue();

			date = obj.getString("date");
			weather = obj.getString("weather");
			wind = obj.getString("wind");
			temperature = obj.getString("temperature");
			mHandler.sendEmptyMessage(MainActivity.UPDATE_WEATHER);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static int setWeatherDetailDrawable(Context context, ImageView m_imageview, String title) {
		
		int id = R.drawable.cloudy;
		try{
			Log.i("info", "----title = " + title);
			int num = 0;
			if (context.getResources().getString(R.string.weather_sun).equals(title)) {
				num = 0;
			} else if (context.getResources().getString(R.string.weather_sun_cloud).equals(title)) {
				num = 1;
			} else if (context.getResources().getString(R.string.weather_yin).equals(title)) {
				num = 2;
			} else if (context.getResources().getString(R.string.weather_cloud).equals(title)) {
				num = 2;
			} else if (context.getResources().getString(R.string.weather_rain).equals(title)
					|| (context.getResources().getString(R.string.weather_rain_big).equals(title))) {
				num = 3;
			} else if (context.getResources().getString(R.string.weather_rain_lei).equals(title)) {
				num = 4;
			} else if (context.getResources().getString(R.string.weather_rain_bigbig).equals(title)) {
				num = 10;
			} else {
				try{
					if (title.indexOf(context.getResources().getString(R.string.weather_snow)) != -1) {
						num = 10;
					} else if (title.indexOf(context.getResources().getString(R.string.weather_raining)) != -1) {
						num = 8;
					} else {
						num = 2;
					}
				}catch(NullPointerException err){
					err.printStackTrace();
				}
			}
			switch (num) {
			case 0:
				m_imageview.setImageResource(R.drawable.sunny);
				id = R.drawable.sunny;
				break;
			case 1:
				m_imageview.setImageResource(R.drawable.cloudy);
				id = R.drawable.cloudy;
				break;
			case 2:
				m_imageview.setImageResource(R.drawable.cloudy_day);
				id = R.drawable.cloudy_day;
				break;
			case 3:
				m_imageview.setImageResource(R.drawable.rain);
				id = R.drawable.rain;
				break;
			case 4:
				m_imageview.setImageResource(R.drawable.heavy_rain);
				id = R.drawable.heavy_rain;
				break;
			case 5:
				m_imageview.setImageResource(R.drawable.heavy_rain);
				id = R.drawable.heavy_rain;
				break;
			case 6:
				m_imageview.setImageResource(R.drawable.heavy_rain);
				id = R.drawable.heavy_rain;
				break;
			case 7:
				m_imageview.setImageResource(R.drawable.heavy_rain);
				id = R.drawable.heavy_rain;
				break;
			case 8:
				m_imageview.setImageResource(R.drawable.rain);
				id = R.drawable.rain;
				break;
			case 9:
				m_imageview.setImageResource(R.drawable.cloudy);
				id = R.drawable.cloudy;
				break;
			case 10:
				m_imageview.setImageResource(R.drawable.rain);
				id = R.drawable.rain;
				break;
			default:
				m_imageview.setImageResource(R.drawable.cloudy);
				id = R.drawable.cloudy;
				break;
			}
		}catch(Exception err){
			err.printStackTrace();
		}
		return id;
	}
}
