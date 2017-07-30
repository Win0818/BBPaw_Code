package com.worldchip.bbpawphonechat.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.easemob.EMCallBack;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;
import com.worldchip.bbpawphonechat.entity.User;
import com.worldchip.bbpawphonechat.net.HttpUtils;

@SuppressLint("NewApi")
public class MyApplication extends Application {
	private static final String TAG = "CHRIS";
	public static Context applicationContext;
	private static MyApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";

	private ImageLoader imageLoader;
	private DisplayImageOptions options, optionsHead;

	private String file_userHead = null;

	private String headUrl;

	private List<String> mContactheadUrls = new ArrayList<String>();

	private User mContolbabyUser;

	private String mControlHeadUrl;

	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static MyChatHXSDKHelper hxSDKHelper = new MyChatHXSDKHelper();

	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = this;
		instance = this;
		boolean initSDK = hxSDKHelper.onInit(applicationContext);
		getLanguageInfo();
		initImageLoader();
	}

	public static MyApplication getInstance() {
		return instance;
	}

	public List<String> getmContactheadUrls() {
		return mContactheadUrls;
	}

	public void setmContactheadUrls(List<String> mContactheadUrls) {
		this.mContactheadUrls = mContactheadUrls;
	}

	public User getmContolbabyUser() {
		return mContolbabyUser;
	}

	public void setmContolbabyUser(User mContolbabyUser) {
		this.mContolbabyUser = mContolbabyUser;
	}

	public String getmControlHeadUrl() {
		return mControlHeadUrl;
	}

	public void setmControlHeadUrl(String mControlHeadUrl) {
		this.mControlHeadUrl = mControlHeadUrl;
	}

	/**
	 * 获取内存中好友user list
	 *
	 * @return
	 */
	public Map<String, User> getContactList() {
		return hxSDKHelper.getContactList();
	}

	/**
	 * 设置好友user list到内存中
	 *
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		hxSDKHelper.setContactList(contactList);
	}

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */
	public String getUserName() {
		return hxSDKHelper.getHXId();
	}

	/**
	 * 获取密码
	 *
	 * @return
	 */
	public String getPassword() {
		return hxSDKHelper.getPassword();
	}

	/**
	 * 设置用户名
	 *
	 * @param user
	 */
	public void setUserName(String username) {
		hxSDKHelper.setHXId(username);
	}

	/**
	 * 设置密码 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference 环信sdk
	 * 内部的自动登录需要的密码，已经加密存储了
	 *
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		hxSDKHelper.setPassword(pwd);
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout(final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
		hxSDKHelper.logout(emCallBack);
	}

	private void initImageLoader() {
		imageLoader = ImageLoader.getInstance();

		String cachePath = null;

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cachePath = Environment.getDownloadCacheDirectory().getPath()
					+ "/bbpawphonechat/cache";
		} else {
			cachePath = getCacheDir().getPath() + "/bbpawphonechat/cache";
		}

		File cacheFile = new File(cachePath);

		cacheFile.mkdirs();

		int memoryCacheSize = (int) Runtime.getRuntime().maxMemory() / 8;

		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(
				getApplicationContext()).threadPoolSize(20)
				.memoryCacheSize(memoryCacheSize).discCacheFileCount(500)
				.discCache(new UnlimitedDiscCache(cacheFile)).build();

		imageLoader.init(configuration);
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.showImageOnFail(R.drawable.default_image)
				.showImageOnLoading(R.drawable.default_image)
				.showImageForEmptyUri(R.drawable.default_empty).build();
		optionsHead = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.showImageOnFail(R.drawable.setting_head_default)
				.showImageOnLoading(R.drawable.setting_head_default)
				.showImageForEmptyUri(R.drawable.setting_head_default).build();

	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public DisplayImageOptions getDisplayOptions() {
		return options;
	}

	public DisplayImageOptions getDisplayOptionsHead() {
		// TODO Auto-generated method stub
		return optionsHead;
	}

	// start
	public String getHeadFile() {
		file_userHead = getUserName() + "\\userHead.bin";
		return file_userHead;
	}

	private String getHeadUrl = null;
	// private Bitmap bitmap = null;

	public String getHeadImageUrl() {
		getHeadUrl = MyComment.GET_MY_HEAD_URL + "&username=" + getUserName();
		new Thread(new Runnable() {
			@Override
			public void run() {
				String result;
				try {
					result = HttpUtils.getRequest(getHeadUrl,
							MyApplication.this);
					if (result != null) {
						JSONObject jsobj = new JSONObject(result);
						headUrl = jsobj.getString("image");
						MySharePreData.SetData(applicationContext,MyComment.CHAT_SP_NAME, MyComment.My_HEAD_URL, headUrl);
					    System.out.println("-----Myapplication中获取头像地址保存--getHeadImageUrl----"+headUrl);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		return headUrl;
	}
	
	public void ImageAdapter(View iv, int[] resId) {
		//iv.setBackground(null);
		int currentLag = getLanguageInfo();
		switch (currentLag) {
		case 0:// 中
			iv.setBackgroundResource(resId[0]);
			break;
		case 1:// 西
			iv.setBackgroundResource(resId[1]);
			break;
		case 2:// 英
			iv.setBackgroundResource(resId[2]);
			break;
		default:
			break;
		}
	}

	public int system_local_language;

	private int getLanguageInfo() {
		String language = Locale.getDefault().getLanguage();
		if (language.contains("zh")) {
			system_local_language = 0;
		} else if (language.contains("es")) {
			system_local_language = 1;
		} else {
			system_local_language = 2;
		}
		return system_local_language;
	}
}
