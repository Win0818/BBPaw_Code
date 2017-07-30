package com.egreat.adlauncher.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.egreat.adlauncher.adapter.AppCategoryListAdapter;
import com.egreat.adlauncher.adapter.AppCommodityListAdapter;
import com.egreat.adlauncher.db.ApkInfo;
import com.egreat.adlauncher.entity.Category;
import com.egreat.adlauncher.util.AppTool;
import com.egreat.adlauncher.util.Utils;
import com.egreat.devicemanger.DeviceManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.mgle.launcher.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppActivity extends Activity implements View.OnFocusChangeListener, View.OnKeyListener {

	protected static final String TAG = "--AppActivity--";

	private static final int INIT_APPLICATION_APP = 1;
	private static final int PASER_NAV_CATEGORY = 0;
	public static final int GET_PEOPLELIVE_JSONDATA = 2;
	public static final int DEVICE_APK_DOWNLOAD_OK = 3;
	public static final int DEVICE_APK_DOWNLOAD_FAIL = 4;
	protected static final int LOOSE_FOCUS_LAYOUT = 5;
	protected static final int LOOSE_FOCUS_ITEM = 6;
	protected static final int GET_PEOPLE_LIVE_THREAD = 7;

	private ProgressBar mLoading;
	private WindowManager mWm;

	private RelativeLayout mMain;
	
	private Category mCurrentCategory;
	private ListView mCategoryList;
	private AppCategoryListAdapter mCategoryAdapter;

	private GridView mCommodityList;
	private AppCommodityListAdapter mCommodityAdapter;

	private Context mCtx;
	private ProgressDialog mProgressDialog;
	private TextView mTxtAppCount;

	private int mSelectIndex = -1;
	private int mCommoditySelectIndex = -1;
	private ImageView mImgJp;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);

		setContentView(R.layout.app_main_layout);

		init();
		initView();
		initData();
	}
	
	@Override
	protected void onResume() {
		initBackground();
		super.onResume();
	}

	private void initData() {

		mHandler.removeMessages(PASER_NAV_CATEGORY);
		mHandler.sendEmptyMessageDelayed(PASER_NAV_CATEGORY, 500);
	}

	private void initBackground() {
		try{
			File file = new File(Utils.getRootPath()+"app_background.jpg");
			Log.e(TAG, "initData...file.exist="+file.exists());
			if(file.exists()){
				mMain.setBackground(Drawable.createFromPath(file.getAbsolutePath()));
			}
		}catch(Exception err){
			err.printStackTrace();
		}
	}

	private void init() {
		mCtx = AppActivity.this;

		mLoading = (ProgressBar) LayoutInflater.from(mCtx).inflate(R.layout.loading_progress_layout, null);
		mWm = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);

		hideLoadingProgress();
	}

	private void initView() {

		mMain = (RelativeLayout)findViewById(R.id.main);
		
		mTxtAppCount = (TextView) findViewById(R.id.txt_app_count);
		mCategoryList = (ListView) findViewById(R.id.list_category);
		mImgJp = (ImageView) findViewById(R.id.img_up);
		mImgJp.setVisibility(View.INVISIBLE);
		mCommodityList = (GridView) findViewById(R.id.gridview_content);

		mCategoryList.setOnFocusChangeListener(this);
		mCommodityList.setOnFocusChangeListener(this);
		mCategoryList.setOnItemSelectedListener(mCategoryListSelectedListener);
		mCommodityList.setOnItemSelectedListener(mCommodityListSelectedListener);
		mCommodityList.setOnItemClickListener(mCommodityListClickListener);

		mCategoryList.setOnKeyListener(this);
		mCommodityList.setOnKeyListener(this);
		
		mCommodityList.setFocusable(false);
		mCommodityList.setFocusableInTouchMode(false);
		mCategoryList.requestFocus();
	}

	private OnItemSelectedListener mCategoryListSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			Log.e("mCategoryListSelectedListener", "------onItemSelected=" + mSelectIndex);
			if (mSelectIndex == -1 || mSelectIndex != position) {
				mSelectIndex = position;
				
				mCurrentCategory = mCategoryAdapter.getItem(position);
				
				mHandler.removeMessages(GET_PEOPLE_LIVE_THREAD);
				mHandler.sendEmptyMessageDelayed(GET_PEOPLE_LIVE_THREAD, 1000);
				
				if (mCategoryAdapter.getCount() > 7) {
					if (position == mCategoryAdapter.getCount() - 1) {
						mImgJp.setVisibility(View.VISIBLE);
						mImgJp.setImageResource(R.drawable.jt_down);
					} else if (position == 0) {
						mImgJp.setVisibility(View.VISIBLE);
						mImgJp.setImageResource(R.drawable.jt_up);
					}
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private OnItemSelectedListener mCommodityListSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			mCommoditySelectIndex = position;
			
			if(mCommodityAdapter.getCount() >0){
			    mTxtAppCount.setText("第"+(position+1)+"项/共"+mCommodityAdapter.getCount()+"项");
			}else{
				mTxtAppCount.setText("第0项/共0项");
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	};

	private OnItemClickListener mCommodityListClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			try {
				ApkInfo apkInfo = mCommodityAdapter.getItem(position);
				if (RunApp(apkInfo.getPackagename()) != true) {
					Log.d(TAG, "need to dowload apk " + apkInfo.getPackagename());
					downloadApk(apkInfo.getDownloadlink(), apkInfo.getPackagename() + ".apk");
				}
			} catch (Exception err) {
				err.printStackTrace();
				// Common.showMessage(mContext,"启动失败！");
			}
		}
	};

	private void showLoadingProgress() {
		try {
			if (mLoading.getParent() == null) {
				LayoutParams lp = new LayoutParams();
				lp.format = PixelFormat.TRANSPARENT;
				lp.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
				lp.width = LayoutParams.WRAP_CONTENT;
				lp.height = LayoutParams.WRAP_CONTENT;
				lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                
				mWm.addView(mLoading, lp);
			}
		} catch (Exception err) {
			err.printStackTrace();
		}

	}

	private void hideLoadingProgress() {
		try {
			if (mLoading.getParent() != null)
				mWm.removeView(mLoading);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		}
		return super.onKeyDown(keyCode, event);
	}

	private void changeNavCategoryData(List<Category> categorys) {
		Log.e(TAG, "changeNavCategoryData..categorys=" + categorys);
		try {
			if (categorys == null || categorys.size() < 1) {
				mImgJp.setVisibility(View.INVISIBLE);
				return;
			}

			if (categorys.size() > 7) {
				mImgJp.setVisibility(View.VISIBLE);
			}
			if (mCategoryAdapter == null) {
				mCategoryAdapter = new AppCategoryListAdapter(this, categorys);
			} else {
				mCategoryAdapter.changData(categorys);
			}

			mCategoryList.setAdapter(mCategoryAdapter);
			mCurrentCategory = mCategoryAdapter.getItem(0);
			mCategoryAdapter.setSelctItem(-1);
			if (mCurrentCategory != null) {
				showLoadingProgress();
				GetPeopleLiveThread mGetPeopleLiveThread = new GetPeopleLiveThread();
				mGetPeopleLiveThread.start();
			}
			//mCategoryList.setFocusableInTouchMode(true);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	class GetPeopleLiveThread extends Thread {
		@Override
		public void run() {

			Log.d("GetGridContentThread", "mCurrentCategory.programeListUrl=" + mCurrentCategory.programeListUrl);

			String peopleLiveJsonData = DeviceManager.getThirdCategoryJsonData(mCurrentCategory.programeListUrl);
			Log.d("GetPeopleLiveThread", "peopleLiveJsonData=" + peopleLiveJsonData);

			Message msg = mHandler.obtainMessage();
			msg.what = GET_PEOPLELIVE_JSONDATA;
			msg.obj = peopleLiveJsonData;

			mHandler.removeMessages(GET_PEOPLELIVE_JSONDATA);
			mHandler.sendMessage(msg);
		}
	}

	private void changeApksData(List<ApkInfo> apkInfos) {
		Log.e(TAG, "changeApksData...apkInfos=" + apkInfos + "; focus=" + focusItem + "; layout=" + frameLayout);
		// mCommodityList.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		mCommodityList.setSelector(new ColorDrawable(Color.TRANSPARENT));

		if (mCommodityAdapter == null) {
			mCommodityAdapter = new AppCommodityListAdapter(this, apkInfos);
			mCommodityList.setAdapter(mCommodityAdapter);
		} else {
			mCommodityAdapter.changData(apkInfos);
		}

		if (mCommodityAdapter.getCount() > 0) {
			mTxtAppCount.setText("第1项/共" + mCommodityAdapter.getCount() + "项");
			// itemRequestFocus(mCommodityAdapter.getView(0, null, null), 0);
		} else {
			mTxtAppCount.setText("第0项/共0项");
		}
		//mCommodityList.setFocusable(true);
		//mCommodityList.setFocusableInTouchMode(true);
	}

	public void paserCategoryJsonInfo() {
		Log.d(TAG, "paserCategoryJsonInfo=" + DeviceManager.getPeopleLiveJsonData());
		List<Category> categorys = new ArrayList<Category>();
		try {
			JSONTokener jsonParser = new JSONTokener(DeviceManager.getPeopleLiveJsonData());
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			JSONArray application = epgObj.getJSONArray("category");
			int sum = application.length();
			Log.d(TAG, "category sum=" + sum);
			for (int i = 0; i < sum; i++) {
				JSONObject subObject = application.getJSONObject(i);
				Category categroy = new Category();
				categroy.categoryName = subObject.getString("name");
				categroy.subCategoryUrl = subObject.getString("subCategoryUrl");
				categroy.programeListUrl = subObject.getString("programListUrl");
				categroy.hpbcUrl = subObject.getString("hpbcUrl");
				categorys.add(categroy);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception err) {
			err.printStackTrace();
		}
		changeNavCategoryData(categorys);
	}

	public void paserApplicationInfo(String jsonData) {
		Log.d(TAG, "paserApplicationInfo jsonData=" + jsonData);
		List<ApkInfo> appList = new ArrayList<ApkInfo>();
		try {
			JSONTokener jsonParser = new JSONTokener(jsonData);
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			JSONArray application = epgObj.getJSONArray("application");
			int sum = application.length();
			Log.d(TAG, "application sum=" + sum);
			for (int i = 0; i < sum; i++)
			{
				JSONObject subObject = application.getJSONObject(i);
				ApkInfo apk = new ApkInfo();
				apk.setId(i + 1);
				apk.setId_text(subObject.getInt("id") + "");
				apk.setName(subObject.getString("name"));
				apk.setIntro(subObject.getString("intro"));
				apk.setPackagename(subObject.getString("packagename"));
				apk.setEdition(subObject.getString("edition"));
				apk.setDownloadlink(subObject.getString("downloadlink"));
				apk.setApkconfig(subObject.getString("apkconfig"));
				apk.setPoster(subObject.getString("poster"));
				appList.add(apk);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		Message msg = mHandler.obtainMessage();
		msg.what = INIT_APPLICATION_APP;
		msg.obj = appList;

		mHandler.removeMessages(INIT_APPLICATION_APP);
		mHandler.sendMessage(msg);
	}

	private boolean RunApp(String packageName) {
		Log.e(TAG, "RunApp...packagename = " + packageName);
		PackageManager pm = this.getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(packageName);
		try {
			this.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// Toast.makeText(mCtx, "应用未安装!", Toast.LENGTH_LONG).show();
		}
		return false;
	}

	public void progress(String title, String message) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// progressDialog.setMessage(message);
		mProgressDialog.setTitle(title);
		mProgressDialog.setProgress(0);
		mProgressDialog.setMax(100);
		mProgressDialog.show();
	}

	public void downloadApk(String url, String fileName) {
		try {
			progress("下载中...", "下载APP文件： " + fileName);
			String filePath = "/sdcard/" + getPackageName() + fileName;
			File file = new File(filePath);
			if (file.exists())
				file.delete();
			HttpUtils http = new HttpUtils();
			HttpHandler handler = http.download(url, filePath, true, false, new RequestCallBack<File>() {

				@Override
				public void onStart() {
					if (mProgressDialog != null) {
						mProgressDialog.setProgress(0);
						// progressDialog.setMessage("start");
					}
				}

				@Override
				public void onLoading(long total, long current, boolean isUploading) {
					if (mProgressDialog != null) {
						mProgressDialog.setProgress((int) ((current * 100) / total));
					}
				}

				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					if (mProgressDialog != null) {
						mProgressDialog.setMessage("下载成功!");
						Message msg = new Message();
						msg.what = DEVICE_APK_DOWNLOAD_OK;
						Bundle data = new Bundle();
						Log.d(TAG, "responseInfo=" + responseInfo.result.getAbsolutePath());
						data.putString("file_path", responseInfo.result.getAbsolutePath());
						msg.setData(data);
						mHandler.sendMessageDelayed(msg, 2 * 1000);
					}
				}

				@Override
				public void onFailure(HttpException error, String msg) {
					if (mProgressDialog != null) {
						mProgressDialog.setMessage("download failure!");
						mHandler.sendEmptyMessageDelayed(DEVICE_APK_DOWNLOAD_FAIL, 2 * 1000);
					}
				}
			});
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	private void show(String msg) {
		Toast.makeText(mCtx, msg, Toast.LENGTH_LONG).show();
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOOSE_FOCUS_LAYOUT:
				frameLayout = (FrameLayout) msg.obj;
				break;
			case LOOSE_FOCUS_ITEM:
				focusItem = (ImageView) msg.obj;
				break;
			case PASER_NAV_CATEGORY:
				paserCategoryJsonInfo();
				break;
			case GET_PEOPLELIVE_JSONDATA:
				hideLoadingProgress();
				if (msg.obj != null) {
					String jsonData = (String) msg.obj;
					paserApplicationInfo(jsonData);
				}
				break;
			case GET_PEOPLE_LIVE_THREAD:
				if (mCurrentCategory != null) {
					mCategoryAdapter.setSelctItem(mSelectIndex);
					showLoadingProgress();
					GetPeopleLiveThread mGetPeopleLiveThread = new GetPeopleLiveThread();
					mGetPeopleLiveThread.start();
				}
				break;
			case INIT_APPLICATION_APP:
				if (msg.obj != null) {
					List<ApkInfo> appList = (List<ApkInfo>) msg.obj;
					changeApksData(appList);
				}
				break;
			case DEVICE_APK_DOWNLOAD_OK:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				Bundle date = msg.getData();
				String filePath = date.getString("file_path");
				Log.d(TAG, "need to install " + filePath);
				if (AppTool.isEmpty(filePath) != true) {
					installApk(filePath);
				}
				break;
			case DEVICE_APK_DOWNLOAD_FAIL:
				if (mProgressDialog != null) {
					mProgressDialog.dismiss();
				}
				break;
			}
		}
	};

	public void installApk(String filePath) {
		Log.i(TAG, "file path =" + filePath);
		File file = new File(filePath);
		if (file.exists() != true) {
			Toast.makeText(mCtx, "APK安装包下载不完整!", Toast.LENGTH_LONG).show();
			return;
		}
		PackageManager pm = mCtx.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
		ApplicationInfo appInfo = null;
		if (info != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
			startActivity(intent);
		} else {
			Toast.makeText(mCtx, "启动App失败!", Toast.LENGTH_LONG).show();
			if (file.exists())
				file.delete();
		}
	}

	private FrameLayout frameLayout = null;
	private ImageView focusItem = null;

	private void itemRequestFocus(View view, int position) {
		if (frameLayout != null && focusItem != null) {
			itemLostFocus();
		}
		if (view == null)
			return;

		FrameLayout frame_layout = (FrameLayout) view.findViewById(R.id.main_layout);
		ImageView item_focus = (ImageView) view.findViewById(R.id.commodity_item_fosus);
		focusItem = item_focus;
		frameLayout = frame_layout;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.application_selected);
		item_focus.setImageBitmap(bitmap);
		// item_focus.setBackgroundResource(R.drawable.application_selected);
		//
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.02f, 1.0f, 1.04f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(200);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setFillAfter(true);

		frameLayout.startAnimation(animationSet);
	}

	private void itemLostFocus() {
		if (focusItem != null && frameLayout != null) {
			focusItem.setImageBitmap(null);
			//
			AnimationSet animationSet = new AnimationSet(true);
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.02f, 1.0f, 1.04f, 1.0f, Animation.RELATIVE_TO_SELF,
					0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			animationSet.addAnimation(scaleAnimation);
			animationSet.setFillAfter(true);

			frameLayout.startAnimation(animationSet);
			frameLayout = null;
			focusItem = null;
		}
	}
	
	@Override
	public void onFocusChange(View view, boolean hasFocus)
	{
		switch (view.getId()) 
		{
		case R.id.list_category:
			Log.e("list_category", "list_category="+hasFocus);
			if (hasFocus) 
			{
				int state = 0;
				if (mCategoryAdapter != null && mCategoryAdapter.getSelectItemIndex() > -1) {
					state = mCategoryAdapter.getSelectItemIndex();
					mCategoryAdapter.setSelctItem(-1);
				}
				final int index = state;
				Log.e("index", "index=" + index);
				if (index > -1){
					mCategoryList.postDelayed(new Runnable() {
						@Override
						public void run() {
							//mCategoryList.requestFocusFromTouch();
							//mCategoryList.setSelection(index);
							//保存当前第一个可见的item的索引和偏移量
							//int position = mCategoryList.getFirstVisiblePosition();
							View v = mCategoryList.getChildAt(index);
							int height = (v == null) ? 0 : v.getHeight();
							Log.e("position", "mCategoryList----position="+index+";   top="+height);
							int y = height * (index % 6);
							//根据上次保存的index和偏移量恢复上次的位置
							mCategoryList.setSelectionFromTop(index, y);
							mCategoryList.setSelector(R.drawable.nav_category_selected);
							if(mCategoryAdapter!=null){
							   mCategoryAdapter.setSelctItem(mSelectIndex);
							}
						}
					}, 100);
				} else {
					mCategoryList.setSelector(new ColorDrawable(Color.TRANSPARENT));
				}
			} else {
				mCategoryList.setSelector(new ColorDrawable(Color.TRANSPARENT));
			}
			break;
		case R.id.gridview_content:
			Log.e("gridview_content", "gridview_content=" + hasFocus);
			Log.e("mSelectIndex", "content--mSelectIndex=" + mSelectIndex);
			if (hasFocus) 
			{
				mCategoryAdapter.setSelctItem(mSelectIndex);
				mCommodityList.setSelector(R.drawable.application_selected);
			} else {
				mCommodityList.setSelector(new ColorDrawable(Color.TRANSPARENT));
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKey(View view, int keyCode, KeyEvent event)
	{
		//Log.e("onKey", "onKey");
		switch (view.getId())
		{
			case R.id.list_category:
				Log.e("onKey", "onKey----list_category");
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && event.getAction() == KeyEvent.ACTION_DOWN) {
					mCommodityList.setFocusable(true);
					mCommodityList.requestFocus();
				}
				break;
			case R.id.gridview_content:
				Log.e("onKey", "onKey----list_category");
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN) {
					if((mCommoditySelectIndex % 3) == 0)
					{
						mCommodityList.setFocusable(false);
						mCategoryList.requestFocus();
					}
				}
				break;
			default:
				break;
		}
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}