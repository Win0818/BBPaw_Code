package com.egreat.devicemanger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.model.MemberInfo;
import cn.trinea.android.common.util.HttpUtils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DeviceManager {

	private static final String LOGTAG = "DeviceManager";

	private static final String VERSION = "v1.1";

	private static final String mServerHost = "101.200.173.180";
	//private static final String mServerHost = "user.egreatworld.com";

	private static final int mServerPort = 8660;
	private static final int mMemberServerPort = 8290;
	
	static final int HEART_BEAT_TIME = 5 * 60;

	private static DeviceManager mDeviceManager = null;
	private static LoginStatus mLoginStatus = new LoginStatus();

	static String loginUrl = null;
	static String mMemoryStartUrl = null;
	static String memoryUrl = null;
	static String encryTokenUrl = null;
	static Long encrytoken = (long) -1;
	static String mac = null;
	static String userid = null;
	static String stbid = null;
	static String password = null;

	static String upgradedomain = null;
	static String usertoken = null;
	static int TokenExpireTime = -1;
	static String EPGHeartUrl = null;

	static String mCustomerId = null;
	
	static String EPGDomain = null;

	static String textUrl = null;

	static String rollText = null;

	static String mainBackgroundPoster = null;
	
	//index json data url
	static String programListUrl = null;

	static String indexJsonData = null;

	static String subCategoryUrl = null;
	static String subCategoryJsonData = null;
	
	static String shortcutJsonData = null;
	static String peopleLiveJsonData = null;
	
	//WGQ
	static String propertyUserUrl = null;
	static String updatePropertyInfoUrl = null;
	static String memberInfoUrl = null;
	static String updateMemberIntegralUrl = null;
	static String updateMemberInfoUrl = null;
	static String addExchangeRecordUrl = null;
	static String seleteExchangeRecordUrl = null;
	static String delExchangeRecordUrl = null;
	static String updateExchangeRecordStatusUrl =null;
	static String updateMerchantsInfoUrl = null;
	static String merchantsLoginUrl = null;
	
	private static Context mCtx;

	private static String mMemoryJsonData;
	
	private DeviceManager(Context context) {
		mDeviceManagerHandler = new DeviceManagerHandler(context);

		mCtx = context;
		mac = getLanMacAddress(":");
		password = getLanMacAddress(":");
		Log.d(LOGTAG, "password= " + password);
		userid = getLanMacAddress("");
		Log.d(LOGTAG, "userid=" + userid);
		stbid = getLanMacAddress("");
		Log.d(LOGTAG, "userid=" + stbid);
		encryTokenUrl = "http://" + mServerHost + ":" + mServerPort + "/aaa/ott/getLoginCode?userid=" + userid;
		Log.d(LOGTAG, "encryTokenUrl=" + encryTokenUrl);
		loginUrl = "http://" + mServerHost + ":" + mServerPort + "/aaa/ott/login";
		Log.d(LOGTAG, "mLoginUrl=" + loginUrl);
		mMemoryStartUrl = "http://" + mServerHost + ":" + mMemberServerPort + "/epgserver/propertyuser/getMemberInfoByCustomerId";
		Log.d(LOGTAG, "mMemoryStartUrl=" + mMemoryStartUrl);
	}

	public static DeviceManager getInstance(Context context) {
		if (mDeviceManager == null) {
			mDeviceManager = new DeviceManager(context);
			Log.d(LOGTAG, "getInstance() new DeviceManager");
		}
		return mDeviceManager;
	}

	public static String getShortcutJsonData(){
		return shortcutJsonData;
	}
	
	public static String getPeopleLiveJsonData(){
		return peopleLiveJsonData;
	}
	
	public static String getVersion() {
		return VERSION;
	}

	public static LoginStatus Login() {
		Log.d(LOGTAG, "Login()");
		if (encrytoken == -1)
			getEncryToken();

		if (encrytoken == -1) {
			Log.d(LOGTAG, "getEncryToken() error!");
			mLoginStatus.status = false;
			return mLoginStatus;
		}
		String ip = getLocalIpAddress();
		Log.d(LOGTAG, "ip=" + ip);
		String toBeEncry = getRandomStr(9) + "$" + encrytoken + "$" + userid + "$" + stbid + "$" + ip + "$" + mac + "$Reserved$SV";
		Log.d(LOGTAG, "toBeEncry=" + toBeEncry);
		EncryptUtil encryptUtil = EncryptUtil.getDesInstance();
		String dencryString = encryptUtil.encryptToHexString(toBeEncry, password);
		Log.d(LOGTAG, "dencryString=" + dencryString);

		String getUrl = loginUrl + "?userid=" + userid + "&Authenticator=" + dencryString + "&type=AndroidStb";

		/*Map<String, String> map = new HashMap<String, String>();
		map.put("userid", userid);
		map.put("Authenticator", dencryString);
		map.put("type", "AndroidStb");
		HttpRequest request = new HttpRequest(loginUrl, map);*/
		Log.d(LOGTAG, "getUrl=" + getUrl);
		HttpRequest request = new HttpRequest(getUrl);
		HttpResponse respone = HttpUtils.httpGet(request);
		Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200) {
			Log.d(LOGTAG, "respone=" + respone.getResponseBody());
			try {
				JSONTokener jsonParser = new JSONTokener(respone.getResponseBody());
				JSONObject obj = (JSONObject) jsonParser.nextValue();

				String returnCode = obj.getString("returnCode");
				String description = obj.getString("description");
				if (returnCode.equals("0") != true) {
					mLoginStatus.returnCode = returnCode;
					mLoginStatus.description = description;
					mLoginStatus.status = false;
					return mLoginStatus;
				}
				String updateurl = obj.getString("upgradedomain");
				String tokenexpire = obj.getString("TokenExpireTime");
				String hearturl = obj.getString("EPGHeartUrl");
				String token = obj.getString("usertoken");
				String epgdomain = obj.getString("epgdomain");
				String customerId = obj.getString("customerid");
				try {
					TokenExpireTime = Integer.valueOf(tokenexpire);
				} catch (NumberFormatException e) {
					TokenExpireTime = 1800;
				}
				upgradedomain = updateurl;
				EPGHeartUrl = hearturl;
				usertoken = token;
				EPGDomain = epgdomain;
				mCustomerId = customerId;

				mLoginStatus.returnCode = returnCode;
				mLoginStatus.description = description;
				mLoginStatus.status = true;

				mDeviceManagerHandler.removeMessages(UPDATE_USERTOKEN);
				mDeviceManagerHandler.sendEmptyMessageDelayed(UPDATE_USERTOKEN, TokenExpireTime * 1000);
				mDeviceManagerHandler.removeMessages(HEART_BEAT);
				mDeviceManagerHandler.sendEmptyMessageDelayed(HEART_BEAT, HEART_BEAT_TIME * 1000);
				return mLoginStatus;
			} catch (JSONException ex) {
				ex.printStackTrace();
				mLoginStatus.status = false;
				return mLoginStatus;
			}
		}
		mLoginStatus.status = false;
		return mLoginStatus;
	}

	public static String getToken(){
		return usertoken;
	}
	
	public static String getCustomerId(){
		return mCustomerId;
	}
	
	public static String getMainBackgroundPoster(){
		return mainBackgroundPoster;
	}
	
	public static String getSubCategoryjsonIndex(){
		return subCategoryJsonData;
	}
	
	public static void getMemoryInfoIndex() {
		Log.d(LOGTAG, "getMemoryInfoIndex...mCustomerId="+mCustomerId);
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) {
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return;
		}
		
		memoryUrl = mMemoryStartUrl + "?customerid="+mCustomerId+"&usertoken=" + usertoken;
		
		Log.d(LOGTAG, "memoryUrl=" + memoryUrl);
		HttpRequest request = new HttpRequest(memoryUrl);
		HttpResponse respone = HttpUtils.httpGet(request);
		if(respone==null) return;
		Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200) {
			Log.d(LOGTAG, "respone=" + respone.getResponseBody());

			mMemoryJsonData = respone.getResponseBody();
			Log.d(LOGTAG, "getMemoryInfoIndex mMemoryJsonData=" + mMemoryJsonData);
		}
	}
	
	public static void getEPGIndex() {
		Log.d(LOGTAG, "getEPGIndex");
		if (usertoken == null || EPGDomain == null || EPGDomain.equals("")) {
			Log.d(LOGTAG, "usertoken is empty");
			return;
		}

		try{
			String epgUrl =EPGDomain + "?type=AndroidStb&usertoken=" + usertoken;
			Log.d(LOGTAG, "epgUrl=" + epgUrl);
			HttpRequest request = new HttpRequest(epgUrl);
			HttpResponse respone = HttpUtils.httpGet(request);
			Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
			
			if (respone.getResponseCode() == 200) {
				Log.d(LOGTAG, "respone=" + respone.getResponseBody());

				try {
					JSONTokener jsonParser = new JSONTokener(respone.getResponseBody());
					JSONObject obj = (JSONObject) jsonParser.nextValue();

					String texturl = obj.getString("textUrl");
					JSONArray category = obj.getJSONArray("category");
					JSONObject category1 = category.getJSONObject(0);

					textUrl = texturl;
					propertyUserUrl = obj.getString("propertyUserUrl");
					updatePropertyInfoUrl = obj.getString("updatePropertyInfoUrl");
					memberInfoUrl = obj.getString("memberInfoUrl");
					updateMemberIntegralUrl = obj.getString("updateMemberIntegralUrl");
					updateMemberInfoUrl = obj.getString("updateMemberInfoUrl");
					/*addExchangeRecordUrl = obj.getString("addExchangeRecordUrl");
					seleteExchangeRecordUrl = obj.getString("seleteExchangeRecordUrl");
					delExchangeRecordUrl = obj.getString("delExchangeRecordUrl");
					updateExchangeRecordStatusUrl = obj.getString("updateExchangeRecordStatusUrl");
					updateMerchantsInfoUrl = obj.getString("updateMerchantsInfoUrl");
					merchantsLoginUrl = obj.getString("merchantsLoginUrl");*/
					
					getMemoryInfoIndex();
					
					//Log.e(LOGTAG, "category1.getString(poster)="+category1.getString("poster"));
					mainBackgroundPoster = category1.getString("poster");
					programListUrl = category1.getString("programListUrl");
					subCategoryUrl = category1.getString("subCategoryUrl"); 
					indexJsonData = getIndexJsonData(programListUrl);
					Log.e(LOGTAG, "mainBackgroundPoster = "+mainBackgroundPoster+"; programListUrl="+programListUrl+";\n\t indexJsonData="+indexJsonData);
					rollText = getRollText(textUrl);
					
					subCategoryJsonData =getSubCategoryJsonData(subCategoryUrl);
					getThirdSubCategoryJsonData(subCategoryJsonData);
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}

			}
		}catch(Exception err){
			return;
		}
		
		
	}

	private static String getThirdSubCategoryJsonData(String jsonData){
		try {
			if(jsonData==null) return null;
			JSONTokener jsonParser = new JSONTokener(jsonData);
			JSONObject obj = (JSONObject) jsonParser.nextValue();

			JSONArray category = obj.getJSONArray("category");
			JSONObject subCategory = null;
			for(int i=0; i<category.length(); i++){
				subCategory = category.getJSONObject(i);
				String name = subCategory.getString("name");
				String subCategoryUrl = subCategory.getString("subCategoryUrl");
				String subProgramUrl = subCategory.getString("programListUrl");
				
				if(name==null) continue;
				if(name.equals(mCtx.getResources().getString(R.string.short_cut))){
					shortcutJsonData = getThirdCategoryJsonData(subProgramUrl);
					Log.e(LOGTAG, "shortcutJsonData="+shortcutJsonData);
				}else if(name.equals(mCtx.getResources().getString(R.string.people_live))){
					peopleLiveJsonData = getThirdCategoryJsonData(subCategoryUrl);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			return "";
		}
		return null;
	}
	
	public static String getThirdCategoryJsonData(String url) {
		String subCategoryUrl = url +"&usertoken=" + usertoken;
		HttpRequest request = new HttpRequest(subCategoryUrl);
		HttpResponse respone = HttpUtils.httpGet(request);
		if (respone.getResponseCode() == 200) {
			return respone.getResponseBody();
		}
		return null;
	}
	
	
	private static String getSubCategoryJsonData(String url) {
		String subCategoryUrl = url +"&usertoken=" + usertoken;
		Log.e(LOGTAG, "getSubCategoryJsonData.subCategoryUrl = "+subCategoryUrl);
		HttpRequest request = new HttpRequest(subCategoryUrl);
		HttpResponse respone = HttpUtils.httpGet(request);
		Log.d(LOGTAG, "getSubCategoryJsonData..respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200) {
			Log.d(LOGTAG, "getSubCategoryJsonData..respone=" + respone.getResponseBody());
			return respone.getResponseBody();
		}
		return null;
	}
	
	private static String getIndexJsonData(String url) {
		HttpRequest request = new HttpRequest(url+"&usertoken="+usertoken);
		HttpResponse respone = HttpUtils.httpGet(request);
		Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200) {
			Log.d(LOGTAG, "respone=" + respone.getResponseBody());
			return respone.getResponseBody();
		}
		return null;
	}

	public static String getMemoryJsonData(){
		return mMemoryJsonData;
	}
	
	public static MemberInfo getMemberInfoData()
	{
		MemberInfo mMemberInfo = new MemberInfo();
		Log.d("MemberInfo", "MemberInfo_jsonData=" + mMemoryJsonData);
		try {
			if(mMemoryJsonData != null)
			{
				JSONTokener jsonParser = new JSONTokener(mMemoryJsonData);
				JSONObject epgObj = (JSONObject) jsonParser.nextValue();
				
				mMemberInfo.customername = epgObj.getString("customername");
				mMemberInfo.level = epgObj.getInt("level");
				mMemberInfo.mobile = epgObj.getString("mobile");
				mMemberInfo.onlineDuration = epgObj.getLong("onlineDuration");
				mMemberInfo.integral = epgObj.getInt("integral");
				mMemberInfo.paypassword = epgObj.getString("paypassword");
				mMemberInfo.villagename = epgObj.getString("villagename");
				mMemberInfo.corridorunit = epgObj.getString("corridorunit");
				
				mMemberInfo.addExchangeRecordUrl = epgObj.getString("addExchangeRecordUrl");
				mMemberInfo.seleteExchangeRecordUrl = epgObj.getString("seleteExchangeRecordUrl");
				mMemberInfo.delExchangeRecordUrl = epgObj.getString("delExchangeRecordUrl");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(Exception err){
			err.printStackTrace();
		}
		return mMemberInfo;
	}
	
	public static String getIndexJsonData() {
		return indexJsonData;
	}

	private static String getRollText(String url) {
		HttpRequest request = new HttpRequest(url+"&usertoken="+usertoken);
		HttpResponse respone = HttpUtils.httpGet(request);
		Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200) {
			Log.d(LOGTAG, "respone=" + respone.getResponseBody());
			try {
				JSONTokener jsonParser = new JSONTokener(respone.getResponseBody());
				JSONObject obj = (JSONObject) jsonParser.nextValue();
				JSONArray textArray = obj.getJSONArray("text");
				JSONObject textObj = textArray.getJSONObject(0);
				String content = textObj.getString("content");
				return content;

			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static String getRollText() {
		return rollText;
	}

	private static Long getEncryToken() {
		Log.d(LOGTAG, "getEncryToken()");

		if (encryTokenUrl == null) {
			encrytoken = (long) -1;
			return encrytoken;
		}
		HttpRequest request = new HttpRequest(encryTokenUrl);
		HttpResponse respone = HttpUtils.httpGet(request);

		if(respone==null) return encrytoken;
		Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200) {
			try {
				Log.d(LOGTAG, "respone=" + respone.getResponseBody());
				JSONTokener jsonParser = new JSONTokener(respone.getResponseBody());
				JSONObject obj = (JSONObject) jsonParser.nextValue();
				encrytoken = obj.getLong("loginCode");
				Log.d(LOGTAG, "encrytoken=" + encrytoken);
			} catch (JSONException ex) {
				ex.printStackTrace();
				encrytoken = (long) -1;
				return encrytoken;
			}
		}

		return encrytoken;
	}

	public static UpdateInfo getApkUpdateInfo() {
		Log.d(LOGTAG, "getApkUpdateInfo()");
		if (upgradedomain == null) {
			Log.d(LOGTAG, "Please login first!");
			return null;
		}
		
		Log.d(LOGTAG, "upgradedomain=" + upgradedomain);
		HttpRequest request = new HttpRequest(upgradedomain);
		HttpResponse respone = HttpUtils.httpGet(request);
		if(respone==null){
			return null;
		}
		
		Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200) {
			try {
				Log.d(LOGTAG, "update...respone=" + respone.getResponseBody());
				JSONTokener jsonParser = new JSONTokener(respone.getResponseBody());
				JSONObject obj = (JSONObject) jsonParser.nextValue();
				UpdateInfo updateInfo = new UpdateInfo();
				updateInfo.apkname = obj.getString("apkname");
				updateInfo.apkurl = obj.getString("apkurl");
				updateInfo.descripton = obj.getString("descripton");
				updateInfo.verName = obj.getString("verName");
				updateInfo.verCode = obj.getInt("verCode");
				return updateInfo;
			} catch (JSONException ex) {
				ex.printStackTrace();
				return null;
			}
		} else
			return null;
	}

	private static final int UPDATE_USERTOKEN = 0x12000001;
	private static final int HEART_BEAT = 0x12000002;

	private static DeviceManagerHandler mDeviceManagerHandler = null;

	static class DeviceManagerHandler extends Handler {

		public DeviceManagerHandler(Context context) {
			super(context.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			Log.d(LOGTAG, "DeviceManagerHandler.handleMessage() msg.what=" + msg.what);
			switch (msg.what) {
			case UPDATE_USERTOKEN:
				mUpdateUserTokenThread = new UpdateUserTokenThread();
				mUpdateUserTokenThread.start();
				break;
			case HEART_BEAT:
				mHeartBeatThread = new HeartBeatThread();
				mHeartBeatThread.start();
			default:
				break;
			}

			super.handleMessage(msg);
		}

	}

	private static UpdateUserTokenThread mUpdateUserTokenThread;

	static class UpdateUserTokenThread extends Thread {

		@Override
		public void run() {
			Log.d(LOGTAG, "UpdateUserTokenThread.run()");
			Login();
		}

	}

	private static HeartBeatThread mHeartBeatThread;

	static class HeartBeatThread extends Thread {
		@Override
		public void run() {
			Log.d(LOGTAG, "HeartBeatThread.run()");
			sendHeartBeat();
		}
	}

	private static void sendHeartBeat() {
		Log.d(LOGTAG, "sendHeartBeat()");
		if (EPGHeartUrl == null) {
			Log.d(LOGTAG, "Please login first!");
			return;
		}

		String url = EPGHeartUrl + "?userToken=" + usertoken + "&streamStatus=1";
		Log.d(LOGTAG, "heart beat url=" + url);
		HttpRequest request = new HttpRequest(url);
		HttpResponse respone = HttpUtils.httpGet(request);
		Log.d(LOGTAG, "respone.getResponseCode=" + respone.getResponseCode());
		if (respone.getResponseCode() == 200)
			Log.d(LOGTAG, "Send heart beat ok!");

		mDeviceManagerHandler.removeMessages(HEART_BEAT);
		mDeviceManagerHandler.sendEmptyMessageDelayed(HEART_BEAT, HEART_BEAT_TIME * 1000);
	}

	private static String getRandomStr(int strLength) {
		if (strLength > 0) {
			int a = 9;
			if (strLength < 9)
				a = strLength;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < a; i++) {
				sb.append("9");
			}
			int maxNum = Integer.valueOf(sb.toString());
			Random random = new Random();
			int value = random.nextInt(maxNum);
			String str = String.valueOf(value);
			if (str.length() < strLength) {
				sb = new StringBuffer();
				for (int i = 0; i < strLength - str.length(); i++) {
					sb.append("0");
				}
				sb.append(str);
				str = sb.toString();
			}
			return str;
		}
		return null;
	}

	@SuppressLint("DefaultLocale")
	private static String getLanMacAddress(String split) {
		String lanMac = null;
		NetworkInterface NIC;
		try {
			NIC = NetworkInterface.getByName("eth0");
		} catch (SocketException e) {
			Log.e(LOGTAG, "get ethernet NetworkInterface error!!");
			e.printStackTrace();
			return null;
		}
		byte[] buf;
		try {
			buf = NIC.getHardwareAddress();
		} catch (SocketException e) {
			Log.e(LOGTAG, "get eth0 mac address error!!");
			e.printStackTrace();
			return null;
		}
		if (buf != null) {
			for (int i = 0; i < buf.length; ++i) {
				if (i > 0) {
					lanMac += split;
				}
				lanMac += String.format("%02x", buf[i]);
			}
			lanMac = lanMac.substring(4);
		}
		if (lanMac != null && !"".equals(lanMac)) {
			Log.d(LOGTAG, "getLanMacAddress success : mac addr = " + lanMac);
			return lanMac.toLowerCase();
		} else {
			Log.e(LOGTAG, "Get ethernet mac address error!");
			return null;
		}
	}

	private static String getLocalIpAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface ni = en.nextElement();
				Enumeration<InetAddress> enIp = ni.getInetAddresses();
				while (enIp.hasMoreElements()) {
					InetAddress inet = enIp.nextElement();
					if (!inet.isLoopbackAddress() && (inet instanceof Inet4Address)) {
						return inet.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return "0";
	}
	
	/**
	 * 1.1.1查询用户物业信息接口
	 * @param jsonData
	 * @return
	 */
	public static String getPropertyUserUrl()
	{
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) {
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			propertyUserUrl = propertyUserUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "propertyUserUrl"+propertyUserUrl);
		return propertyUserUrl;
	}
	
	/**
	 * 1.1.2用户物业信息更新接口
	 * @param jsonData
	 * @return
	 */
	public static String getUpdatePropertyInfoUrl()
	{
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) 
		{
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return updatePropertyInfoUrl;
		}else{
			updatePropertyInfoUrl = updatePropertyInfoUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "updatePropertyInfoUrl"+updatePropertyInfoUrl);
		return updatePropertyInfoUrl;
	}
	
	/**
	 * 1.1.3查询会员信息接口
	 * @param jsonData
	 * @return
	 */
	public static String getMemberInfoUrl()
	{
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) 
		{
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			memberInfoUrl = memberInfoUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "memberInfoUrl"+memberInfoUrl);
		return memberInfoUrl;
	}
	
	/**
	 * 1.1.4会员积分更新接口
	 * @param jsonData
	 * @return
	 */
	public static String getUpdateMemberIntegralUrl()
	{
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) {
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			updateMemberIntegralUrl = updateMemberIntegralUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "updateMemberIntegralUrl"+updateMemberIntegralUrl);
		return updateMemberIntegralUrl;
	}
	
	/**
	 * 1.1.5会员信息更新接口
	 * @param jsonData
	 * @return
	 */
	public static String getUpdateMemberInfoUrl()
	{
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) {
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return updateMemberInfoUrl;
		}else{
			updateMemberInfoUrl = updateMemberInfoUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "updateMemberInfoUrl"+updateMemberInfoUrl);
		return updateMemberInfoUrl;
	}

	/**
	 * 1.1.6购买兑换商品的订单记录
	 * @param jsonData
	 * @return
	 */
	public static String getAddExchangeRecordUrl()
	{
		MemberInfo memberInfo = getMemberInfoData();
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) 
		{
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			addExchangeRecordUrl = memberInfo.addExchangeRecordUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "updateMemberInfoUrl"+addExchangeRecordUrl);
		return addExchangeRecordUrl;
	}
	
	/**
	 * 1.1.7查询会员兑换商品的记录
	 * @param jsonData
	 * @return
	 */
	public static String getSeleteExchangeRecordUrl()
	{
		MemberInfo memberInfo = getMemberInfoData();
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) 
		{
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			seleteExchangeRecordUrl = memberInfo.seleteExchangeRecordUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "seleteExchangeRecordUrl"+seleteExchangeRecordUrl);
		return seleteExchangeRecordUrl;
	}
	
	/**
	 * 1.1.8删除会员兑换商品的记录根据订单号
	 * @param jsonData
	 * @return
	 */
	public static String getDelExchangeRecordUrl()
	{
		MemberInfo memberInfo = getMemberInfoData();
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) 
		{
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			delExchangeRecordUrl = memberInfo.delExchangeRecordUrl + "?customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "delExchangeRecordUrl"+delExchangeRecordUrl);
		return delExchangeRecordUrl;
	}
	
	/**
	 * 1.1.9更新订单记录中的订单状态和提货码
	 * @param jsonData
	 * @return
	 */
	public static String getUpdateExchangeRecordStatusUrl()
	{
		MemberInfo memberInfo = getMemberInfoData();
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) {
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			updateExchangeRecordStatusUrl = memberInfo.updateExchangeRecordStatusUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "updateExchangeRecordStatusUrl"+updateExchangeRecordStatusUrl);
		return updateExchangeRecordStatusUrl;
	}
	
	/**
	 * 1.1.10更新商家信息
	 * @param jsonData
	 * @return
	 *//*
	public static String getUpdateMerchantsInfoUrl()
	{
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) {
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			updateMerchantsInfoUrl = updateMerchantsInfoUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "updateMerchantsInfoUrl"+updateMerchantsInfoUrl);
		return updateMerchantsInfoUrl;
	}
	
	*//**
	 * 1.1.13登陆后返回商家信息
	 * @param jsonData
	 * @return
	 *//*
	public static String getMerchantsLoginUrl(String url)
	{
		if (usertoken == null || mCustomerId == null || mCustomerId.equals("")) {
			Log.d(LOGTAG, "usertoken is empty || custorm id is null");
			return "";
		}else{
			merchantsLoginUrl = merchantsLoginUrl + "&customerid=" + mCustomerId + "&usertoken=" + usertoken;
		}
		Log.d(LOGTAG, "merchantsLoginUrl"+merchantsLoginUrl);
		return merchantsLoginUrl;
	}*/
}
