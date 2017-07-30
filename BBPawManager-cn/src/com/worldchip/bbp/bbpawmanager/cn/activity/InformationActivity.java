package com.worldchip.bbp.bbpawmanager.cn.activity;

import java.io.File;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.R;
import com.worldchip.bbp.bbpawmanager.cn.adapter.InformationListAdapter;
import com.worldchip.bbp.bbpawmanager.cn.callbak.HttpResponseCallBack;
import com.worldchip.bbp.bbpawmanager.cn.db.DataManager;
import com.worldchip.bbp.bbpawmanager.cn.db.DataManager.InformMessage;
import com.worldchip.bbp.bbpawmanager.cn.image.utils.ImageLoader;
import com.worldchip.bbp.bbpawmanager.cn.json.InformationJsonParse;
import com.worldchip.bbp.bbpawmanager.cn.model.Information;
import com.worldchip.bbp.bbpawmanager.cn.push.PushMessageContents;
import com.worldchip.bbp.bbpawmanager.cn.utils.AnimationUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.Common;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;
import com.worldchip.bbp.bbpawmanager.cn.utils.DownloadUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.HttpUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.InformationDatasLoader;
import com.worldchip.bbp.bbpawmanager.cn.utils.LogUtil;
import com.worldchip.bbp.bbpawmanager.cn.utils.NetworkUtils;
import com.worldchip.bbp.bbpawmanager.cn.utils.ScaleAnimEffect;
import com.worldchip.bbp.bbpawmanager.cn.utils.Utils;
import com.worldchip.bbp.bbpawmanager.cn.utils.InformationDatasLoader.InformationCallback;
import com.worldchip.bbp.bbpawmanager.cn.view.DownloadView;
import com.worldchip.bbp.bbpawmanager.cn.view.GlobalProgressDialog;
import com.worldchip.bbp.bbpawmanager.cn.view.HelpView;
import com.worldchip.bbp.bbpawmanager.cn.view.InformGlobalDialog;
import com.worldchip.bbp.bbpawmanager.cn.view.InformVerticalSeekBar;
import com.worldchip.bbp.bbpawmanager.cn.view.DownloadView.DownloadState;

public class InformationActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener, InformationCallback,
		OnItemClickListener, HttpResponseCallBack,android.widget.CompoundButton.OnCheckedChangeListener{

	private static final String TAG =InformationActivity.class.getSimpleName();
	
	private ListView mListView;
	private RadioGroup mButtonGroup;
	private InformationListAdapter mListAdapter;
	private int mCurrButtonGroupCheck = -1;
	private InformationDatasLoader mDatasLoader = null;
	private int mFilterMsgType = Information.ALL;//默认全部显示
	private List<Information> mAllInformDatas = null;
	private InformVerticalSeekBar mScrollBar;
	private int mVisibleItemCount = 0;
	private int mCurrentPosition  = 0;
	private static final int JUMP_UP = 0;
	private static final int JUMP_DOWN = 1;
	private GlobalProgressDialog mGlobalProgressDialog;
	private static final int START_LOAD = 1;
	private static final int LOAD_COMPLETE = 2;
	private static final int REFRESH = 3;
	private TextView[] mMenuBtn = new TextView[4];
	private static final int MENU_BTN_ALL = 0;
	private static final int MENU_BTN_UNREAD = 1;
	private static final int MENU_BTN_READ = 2;
	private static final int MENU_BTN_FAVORITES = 3;
	private int mMessageState = Information.MENU_ALL;//默认全部显示
	private InformGlobalDialog mInformGlobalDialog = null;
	public static final int MSG_PROGRESS_UPDATE = 100;
	public static final int MSG_DOWNLOAD_COMPLETE = 101;
	public static final int MSG_DOWNLOAD_ERROR = 102;
	//private RoundProgress mRoundProgressBar;
	private DownloadReceiver mDownloadReceiver = null;
	//private String mCurrDownloadUrl="";
	private DownloadView mDownloadView = null;
	private ImageView mSearchBtn;
	private CheckBox mDeleteBtn;
	private EditText mSearchEdit;
	private boolean isShowSearchEidt = false;
	private HelpView mHelpView = null;
	private InformationPushMessageReceiver mPushMessageReceiver = null;
	private LinearLayout mScrollbarLinearLayout = null;
	private static final int MENU_TEXT_NORMAL_COLOR = Color.parseColor("#261B1B");
	private static final int MENU_TEXT_SELECT_COLOR = Color.parseColor("#45B7F8");
	
	private ScaleAnimEffect mAnimEffect = new ScaleAnimEffect();
	private static float big_x = 1.08F;
	private static float big_y = 1.08F;
	
	private boolean enableDelete = false;
	
	private final String mDefault_informationName = "default_information.json"; 
	private RelativeLayout mInformationLayout;
	
	private Handler mHandler = new Handler(){
		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case START_LOAD:
				startProgressDialog();
				break;
			case LOAD_COMPLETE:
			case REFRESH:
					if (mAllInformDatas != null) {
						mAllInformDatas.clear();
					}
					List<Information> infos = DataManager.getInformMessageDatas(InformationActivity.this);
					mAllInformDatas = infos;
					bindInformAdapter(mFilterMsgType);
					stopProgressDialog();
				break;
			case MSG_PROGRESS_UPDATE:
				if (msg.arg1 >= 0 ) {
					if (mDownloadView != null) {
						//RoundProgress roundProgressBar = mDownloadView.getRoundProgressBar();
						//if (roundProgressBar != null) {
						//	roundProgressBar.setProgress(msg.arg1);
						//}
						mDownloadView.onUpdateStartBtn();
						if (mDownloadView.getDownloadState() == DownloadState.DOWNLOADING) {
							TextView downloadProgress = mDownloadView.getDownloadProgress();
							downloadProgress.setText(msg.arg1+"%");
						}
					}
				}
				break;
			case MSG_DOWNLOAD_COMPLETE:
				if (mDownloadView != null) {
					String downloadFile="";
					if (msg.obj != null) {
						downloadFile = (String)msg.obj;
					}
					mDownloadView.onDownloadComplete(downloadFile);
					mDownloadView.stopRoundProgressAnim();
				}
				break;
				
			case MSG_DOWNLOAD_ERROR:
				if (mDownloadView != null) {
					mDownloadView.onStopDownload();
				}
				break;
				
			case DownloadView.DOWNLOAD_START:
				if (mDownloadView != null) {
					DownloadUtils.downLoad(MyApplication.getAppContext(), mDownloadView.getDownloadUrl());
				}
				break;
			case DownloadView.DOWNLOAD_CONTINUE:
				if (mDownloadView != null) {
					DownloadUtils.continueDownLoad(MyApplication.getAppContext(), mDownloadView.getDownloadUrl());
				}
				break;
			case DownloadView.DOWNLOAD_PAUSE:
				mHandler.removeMessages(MSG_PROGRESS_UPDATE);
				if (mDownloadView != null) {
					DownloadUtils.pauseDownLoad(MyApplication.getAppContext(), mDownloadView.getDownloadUrl());
				}
				break;
			case DownloadView.DOWNLOAD_CLOSE:
				mHandler.removeMessages(MSG_PROGRESS_UPDATE);
				dismissInformGlobalDialog();
				break;
			case DownloadView.CLOSE_DOWNLOAD_VIEW:
				mHandler.removeMessages(MSG_PROGRESS_UPDATE);
				dismissInformGlobalDialog();
				break;
			case DownloadView.DOWNLOAD_CANCEL:
				if (mDownloadView != null) {
					mHandler.removeMessages(MSG_PROGRESS_UPDATE);
					DownloadUtils.deleteDownLoad(MyApplication.getAppContext(), mDownloadView.getDownloadUrl());
				}
				break;
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information_layout);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		findViewById(R.id.information_rootview).setBackgroundResource(Common.getResourcesId(MyApplication.getAppContext(), "information_bg", "drawable"));
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.inform_refresh).getLayoutParams();
		params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.refresh_marginTop), getResources().getDimensionPixelSize(Common.getResourcesId(MyApplication.getAppContext(), "refresh_marginRight", "dimen")), 0);
		findViewById(R.id.inform_refresh).setLayoutParams(params);
		
		mButtonGroup = (RadioGroup)findViewById(R.id.clean_type_radio_group);
		mButtonGroup.setOnCheckedChangeListener(this);
		mListView = (ListView)findViewById(R.id.inform_listview);
		mMenuBtn[MENU_BTN_ALL] = (TextView)findViewById(R.id.menu_all_message);
		mMenuBtn[MENU_BTN_UNREAD] = (TextView)findViewById(R.id.menu_unread);
		mMenuBtn[MENU_BTN_READ] = (TextView)findViewById(R.id.menu_read);
		mMenuBtn[MENU_BTN_FAVORITES] = (TextView)findViewById(R.id.menu_favorites);
		mMenuBtn[MENU_BTN_ALL].setOnClickListener(this);
		mMenuBtn[MENU_BTN_UNREAD].setOnClickListener(this);
		mMenuBtn[MENU_BTN_READ].setOnClickListener(this);
		mMenuBtn[MENU_BTN_FAVORITES].setOnClickListener(this);
		
		findViewById(R.id.infrom_back_btn).setOnClickListener(this);
		findViewById(R.id.information_rootview).setOnClickListener(this);
		findViewById(R.id.help_btn).setOnClickListener(this);
		findViewById(R.id.inform_refresh).setOnClickListener(this);
		
		mInformationLayout = (RelativeLayout) findViewById(R.id.default_information_layout);
		
		/*
		 * 注册收到推送消息监听
		 */
		mPushMessageReceiver = new InformationPushMessageReceiver();
		IntentFilter pushMessageFilter = new IntentFilter();
		pushMessageFilter.addAction(Utils.RECEIVE_PUSH_MESSAGE_ACTION);
		registerReceiver(mPushMessageReceiver, pushMessageFilter);
		
		
		mSearchBtn = (ImageView)findViewById(R.id.infrom_search);
		mSearchBtn.setOnClickListener(this);
		mDeleteBtn = (CheckBox) findViewById(R.id.menu_delete);
		mDeleteBtn.setOnCheckedChangeListener(this);
		
		mSearchEdit = (EditText)findViewById(R.id.search_edit);
		mSearchEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean focus) {
				// TODO Auto-generated method stub
				Log.e("lee", "focus == "+focus);
				if (!focus) {
					Common.hideKeyboard(InformationActivity.this,mSearchEdit);
				}
			}
		});
		
		mScrollbarLinearLayout = (LinearLayout)findViewById(R.id.scrollbar_ll);
		
		mListView.setOnItemClickListener(this);
		mButtonGroup.check(R.id.all_btn);
		findViewById(R.id.scroll_up).setOnClickListener(this);
		findViewById(R.id.scroll_down).setOnClickListener(this);
		mScrollBar = (InformVerticalSeekBar)findViewById(R.id.inform_scrollbar);
		mScrollBar.setScrollListView(mListView);
		mListView.setOnScrollListener(new ListViewScrollListener());
		//PushMessageManager.getInstance().addObserver(this);
		loadDatas();
		mDownloadReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadUtils.BBPAWMANAGE_DOWNLOAD_ACTION);
        registerReceiver(mDownloadReceiver, filter);
	}

	private void loadDatas() {
		 String deviceId = Common.getCpuSerial(MyApplication.getAppContext());
		 Log.e("xiaolp","deviceId = "+deviceId);
		 updateMenu(MENU_BTN_ALL);
		 mMessageState = Information.MENU_ALL;
		 HttpUtils.doPost(Utils.HTTP_INFORM_ALL_REQ_URL+deviceId, this, Utils.HTTP_TAG_INFORMATION_LOAD);
	}
	
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		for (int i =0; i<group.getChildCount();i++) {
			View child = group.getChildAt(i);
			if (child != null && child.getId() == checkedId) {
				child.setSelected(true);
			} else {
				child.setSelected(false);
			}
		}
		if (mCurrButtonGroupCheck != checkedId) {
			mCurrButtonGroupCheck = checkedId;
			switch (checkedId) {
			case R.id.all_btn:
				mFilterMsgType = Information.ALL;
				break;
			case R.id.expert_btn:
				mFilterMsgType = Information.EXPERT;
				break;
			case R.id.recommend_btn:
				mFilterMsgType = Information.RECOMMEND;
				break;
			case R.id.activity_btn:
				mFilterMsgType = Information.ACTIVITY;
				break;
			case R.id.other_btn:
				mFilterMsgType = Information.OTHER;
				break;
			default:
				break;
			}
			if (mListAdapter != null) {
				mListAdapter.setInformStateFilter(mMessageState);
				if (mInformationLayout != null){
					mListAdapter.setDefaultInfoLayout(mInformationLayout);
				}
			}
			bindInformAdapter(mFilterMsgType);
		}
		
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		
		case R.id.information_rootview:
			Common.hideKeyboard(InformationActivity.this,mSearchEdit);
			break;
		case R.id.inform_refresh:
			loadDatas();
			break;
		case R.id.infrom_search:
			if (!isShowSearchEidt) {
				isShowSearchEidt = true;
				AnimationUtils.searchSpreadAnim(mSearchEdit);
			} else {
				if (mSearchEdit != null) {
					view.requestFocusFromTouch();
					Common.hideKeyboard(InformationActivity.this,mSearchEdit);
					search(mSearchEdit.getText().toString());
				}
			}
			break;
		case R.id.menu_delete:
			startAnimEffect(view);
			break;
		case R.id.scroll_up:
			jumpTo(JUMP_UP);
			break;
		case R.id.scroll_down:
			jumpTo(JUMP_DOWN);
			break;
		case R.id.help_btn:
			showHelpView();
			return;
		case R.id.infrom_back_btn:
			finish();
			return;
		case R.id.menu_all_message:
			updateMenu(MENU_BTN_ALL);
			mMessageState = Information.MENU_ALL;
			setStateFilter();
			break;
		case R.id.menu_unread:
			updateMenu(MENU_BTN_UNREAD);
			mMessageState = Information.MENU_UNREAD;
			setStateFilter();
			break;
		case R.id.menu_read:
			updateMenu(MENU_BTN_READ);
			mMessageState = Information.MENU_READ;
			setStateFilter();
			break;
		case R.id.menu_favorites:
			updateMenu(MENU_BTN_FAVORITES);
			mMessageState = Information.MENU_FAVORITES;
			setStateFilter();
			break;

		default:
			break;
		}
	}

	private void updateMenu(int menuIndex) {
		if (mMenuBtn != null && mMenuBtn.length > 0) {
			for (int i = 0;i<mMenuBtn.length;i++) {
				if (menuIndex == i) {
					mMenuBtn[i].setTextColor(MENU_TEXT_SELECT_COLOR);
					mMenuBtn[i].setTextSize(getResources().getDimension(R.dimen.inform_menus_select_textSize));
				} else {
					mMenuBtn[i].setTextColor(MENU_TEXT_NORMAL_COLOR);
					mMenuBtn[i].setTextSize(getResources().getDimension(R.dimen.inform_menus_textSize));
				}
			}
			
		}
	}
	
	
	
	private void showHelpView() {
		// TODO Auto-generated method stub
		if (mHelpView != null) {
			if (mHelpView.isShowing()) {
				mHelpView.dismiss();
			}
			mHelpView = null;
		}
		mHelpView = new HelpView(this);
		mHelpView.show();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//PushMessageManager.getInstance().deleteObserver(this);
		try {
			if (mDownloadReceiver != null) {
				unregisterReceiver(mDownloadReceiver);
				mDownloadReceiver = null;
			}
			if (mPushMessageReceiver != null) {
				unregisterReceiver(mPushMessageReceiver);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setStateFilter() {
		if (mListAdapter != null) {
			mListAdapter.setInformStateFilter(mMessageState);
			mListAdapter.getFilter().filter(String.valueOf(mFilterMsgType));
		}
	}
	
	
	@Override
	public void loadComplate(List<Information> result) {
		// TODO Auto-generated method stub
		mAllInformDatas = result;
		bindInformAdapter(mFilterMsgType);
	}
	
	private void bindInformAdapter(int msgType) {
		if (mScrollbarLinearLayout != null && mAllInformDatas != null 
				&& !mAllInformDatas.isEmpty()) {
			mScrollbarLinearLayout.setVisibility(View.GONE);
		}
		if (mListAdapter == null) {
    		mListAdapter = new InformationListAdapter(InformationActivity.this, mAllInformDatas);
    		mListView.setAdapter(mListAdapter);
    	} else {
    		mListAdapter.setDataList(mAllInformDatas);
    	}
		mListAdapter.getFilter().filter(String.valueOf(msgType));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		if(mListView != null) {
			InformationListAdapter adapter = (InformationListAdapter)mListView.getAdapter();
			if (adapter != null) {
				Information info = (Information)adapter.getItem(position);
				if (info != null) {
					if (!info.isRead()) {
						adapter.getFilterDataList().get(position).setRead(true);
						adapter.notifyReadOriginalData(info);
						DataManager.updateInformation(MyApplication.getAppContext(), InformMessage.ISREAD, "1", 
								InformMessage.ID, String.valueOf(info.getId()));
					}
					Common.replyStateToServer(info, Utils.REPLY_STATE_REDED);
					if (String.valueOf(info.getMainType()).equals(PushMessageContents.NOTIFY_MSG_MAIN_TYPE)) {
						Common.notifyMessageClick(InformationActivity.this, info);
					} else {
						showMessageDetail(position);
					}
				}
			}
		}
		
	}
	

	private class ListViewScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
			    int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				mCurrentPosition = firstVisibleItem;
				mVisibleItemCount = visibleItemCount;
			    if (totalItemCount - visibleItemCount > 0) {
			        int progress = mScrollBar.getMax() * firstVisibleItem
			                    / (totalItemCount - visibleItemCount);
			        mScrollBar.setProgress(progress);
			        mScrollBar.onScrollBarSizeChang();
			    }
		}

		@Override
		public void onScrollStateChanged(AbsListView arg0, int arg1) {}
	}
	
	
	private void jumpTo(int jump) {
		if (mListView != null) {
			int itemCount = mListView.getAdapter().getCount();
			int position = 0;
			if (jump == JUMP_UP){
				 position = mCurrentPosition - (mVisibleItemCount - 1);
			} else {
				 position = mCurrentPosition + (mVisibleItemCount - 1);
			}
			if (position < 0) {
	        	position = 0;
	        } else if (position >= itemCount){
	        	position = itemCount - 1;
	        }
			mListView.setSelection(position);
		}
	}

	@Override
	public void onStart(String httpTag) {
		// TODO Auto-generated method stub
		List<Information> infos = DataManager.getInformMessageDatas(InformationActivity.this);
		if (infos != null && infos.isEmpty()) {
			LoadDefaultInformMessage();
		}
		mHandler.sendEmptyMessage(START_LOAD);
	}

	@Override
	public void onSuccess(String result, String httpTag) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG, "InformationActivity  onSuccess : "+result);
		List<Information> infos = InformationJsonParse.doParseJsonToBean(result);
		if (infos != null && !infos.isEmpty()) {
			DataManager.updateInformMessageToDB(InformationActivity.this, infos);
		}
    	mHandler.sendEmptyMessage(LOAD_COMPLETE);
	}

	@Override
	public void onFailure(Exception e, String httpTag) {
		// TODO Auto-generated method stub
		if (e != null) {
            LogUtil.e(TAG, e.toString());
        }
    	mHandler.sendEmptyMessage(LOAD_COMPLETE);
	}

	@Override
	public void onFinish(int result, String httpTag) {
		// TODO Auto-generated method stub
	}
	
	private void LoadDefaultInformMessage() {
		String result = InformationJsonParse.getDefaultJson(InformationActivity.this, mDefault_informationName);
		LogUtil.e(TAG, "InformationActivity  LoadDefaultInform : "+result);
		List<Information> infos = InformationJsonParse.doParseJsonToBean(result);
		if (infos != null && !infos.isEmpty()) {
			DataManager.updateInformMessageToDB(InformationActivity.this, infos);
		}
	}
	
	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(this);
		}
		mGlobalProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			if (mGlobalProgressDialog.isShowing()) {
				mGlobalProgressDialog.dismiss();
			}
			mGlobalProgressDialog = null;
		}
	}
	
	
	private void showMessageDetail(int position) {
		// TODO Auto-generated method stub
		if (mListView != null) {
			InformationListAdapter adapter = (InformationListAdapter)mListView.getAdapter();
		if(adapter == null)
			return;
		Information info = adapter.getItem(position);
		if (info == null)
			return;
		dismissInformGlobalDialog();
		View detailView = createInformDetailView(info,position);
		if (detailView != null) {
			mInformGlobalDialog = new InformGlobalDialog.Builder(this)
			.setContentView(detailView)
			.setCancelable(true).create();
			mInformGlobalDialog.setCanceledOnTouchOutside(false);
			mInformGlobalDialog.show();
		}
		}
	}
	
	
	@SuppressLint("SetJavaScriptEnabled")
	private View createInformDetailView(Information info, int position) {
		View detailView;
		if (info.getMsgType() == Information.MESSAGE_TYPE_NEW) {
			detailView = LayoutInflater.from(this).inflate(R.layout.inform_photo_detail_layout, null);
		}else{
			detailView = LayoutInflater.from(this).inflate(R.layout.inform_detail_layout, null); 
		}
		ImageView closeButton = (ImageView)detailView.findViewById(R.id.close);
		closeButton.setOnClickListener(new InformDialogOnclickListener());
		CheckBox favoritesButton = (CheckBox)detailView.findViewById(R.id.favorites);
		favoritesButton.setTag(position);
		favoritesButton.setChecked(info.isFavorites());
		favoritesButton.setOnCheckedChangeListener(new FavoritesCheckChangeListener());
		
		TextView title = (TextView)detailView.findViewById(R.id.title);
		title.setText(info.getTitle());
		ImageView img = (ImageView)detailView.findViewById(R.id.detail_image);
		String msgImage = info.getMsgImage();
		String imagePath = !TextUtils.isEmpty(msgImage) ? msgImage : info.getIcon();
		WebView contentWebView = (WebView)detailView.findViewById(R.id.detail_content);
		if (imagePath.equals("ic_launcher")) {
			img.setVisibility(View.GONE);
			title.setVisibility(View.GONE);
			detailView.findViewById(R.id.space).setVisibility(View.VISIBLE);
		}else {
			detailView.findViewById(R.id.space).setVisibility(View.GONE);
			title.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().loadImage(imagePath, img, true, true);
		}
		WebSettings settings = contentWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("utf-8") ; 
		contentWebView.setBackgroundColor(0); // 设置背景色  
		contentWebView.setOnLongClickListener(new OnLongClickListener() {//屏蔽长按 
	          @Override  
	          public boolean onLongClick(View v) {  
	              return true;  
	          }  
	      });
		contentWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Common.openBBPawBrowser(view.getContext(), url);
				return true;
			}
		});
		
		if (info.getContent().contains("file:///android_asset/")) {
			String language = Locale.getDefault().getLanguage();
			if (language.contains("zh")) {
				contentWebView.loadUrl(info.getContent().toString()+"cn.htm");
			} else {
				contentWebView.loadUrl(info.getContent().toString()+"eng.htm");
			}
		}else {
			contentWebView.loadDataWithBaseURL(null, info.getContent(), "html/text", "utf-8", null);
		}
		
		View positiveButton = detailView.findViewById(R.id.positiveButton);
		positiveButton.setTag(info);
		positiveButton.setOnClickListener(new InformDialogOnclickListener());
		switch (info.getMsgType()) {
		case Information.MESSAGE_TYPE_NEW:// 图文消息 
			positiveButton.setVisibility(View.GONE);
			break;
		case Information.MESSAGE_TYPE_ACTIVITY:// 活动 
			positiveButton.setBackgroundResource(R.drawable.ic_join_btn);
			break;	
		case Information.MESSAGE_TYPE_AD_PUSH:// 广告推送 
			positiveButton.setVisibility(View.GONE);
			break;	
		case Information.MESSAGE_TYPE_SYSUPDATE:// 系统升级 
			positiveButton.setBackgroundResource(R.drawable.upgrade_btn_normal);
			break;	
		case Information.MESSAGE_TYPE_APK://apk 
			positiveButton.setBackgroundResource(R.drawable.ic_download_btn);
			break;	
		case Information.MESSAGE_TYPE_OTHER:// 其它资源
			positiveButton.setBackgroundResource(R.drawable.ic_download_btn);
			break;	
		}
		
		return detailView;
	}
	
	private class InformDialogOnclickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			startAnimEffect(view);
			/*switch (view.getId()) {
			case R.id.close:
				if (mInformGlobalDialog != null) {
		    		if (mInformGlobalDialog.isShowing()) {
		    			mInformGlobalDialog.dismiss();
		    		}
		    		mInformGlobalDialog = null;
		    	}
				break;
			case R.id.positiveButton:
				onclickPositiveButton(view);
				break;
			case R.id.dowmload_close_btn:
				dismissInformGlobalDialog();
				break;
				
			default:
				break;
			}*/
		}
		
	}
	
	private void onclickPositiveButton(View view) {
		//int position = (Integer) view.getTag();
		if (view == null) {
			return;
		}
		if (mListAdapter != null) {
			//Information info = mListAdapter.getItem(position);
			Information info = (Information)view.getTag();
			switch (info.getMsgType()) {
			case Information.MESSAGE_TYPE_ACTIVITY:// 活动
				String url = info.getActivitiesUrl();
				if (!TextUtils.isEmpty(url)) {
					Common.openBBPawBrowser(this, url);
				}
				break;
			case Information.MESSAGE_TYPE_SYSUPDATE:// 系统升级
				Common.systemUpgrade(this);
				break;
			case Information.MESSAGE_TYPE_APK:// apk
			case Information.MESSAGE_TYPE_OTHER:// 其它资源
				//String downloadUrl = info.getDownloadUrl();
				//if (downloadUrl != null) {
				//	String fileName = NetworkUtils.getFileNameFromUrl(downloadUrl);
					showDownloadView(info);
				//}
				break;
			}
		}
	}
	
	private void showDownloadView(Information info) {
		dismissInformGlobalDialog();
		if (info == null)
			return;
		
		String downloadUrl = info.getDownloadUrl();
		if (TextUtils.isEmpty(downloadUrl))
			return;
		String fileName = NetworkUtils.getFileNameFromUrl(downloadUrl);
		mDownloadView = new DownloadView(this,mHandler,info);
		View downloadView = mDownloadView.creatDownloadView();
		boolean downlodFileExists = DownloadUtils.checkDownlodFileExists(downloadUrl);
		if (downlodFileExists) {
			File localFile = DownloadUtils.getLocalFileFromUrl(downloadUrl);
			mDownloadView.onDownloadComplete(localFile.getAbsolutePath());
		}
		if(downlodFileExists) {
			mDownloadView.setDownloadTitle(getString(R.string.download_complete_title_text_format, fileName));
		} else {
			mDownloadView.setDownloadTitle(getString(R.string.download_title_text_format, fileName));
		}
		if (downloadView != null) {
			mInformGlobalDialog = new InformGlobalDialog.Builder(this)
			.setContentView(downloadView)
			.setCancelable(true).create();
			mInformGlobalDialog.setCanceledOnTouchOutside(false);
			mInformGlobalDialog.show();
		}
	}
	
	private void dismissInformGlobalDialog() {
		if (mInformGlobalDialog != null) {
    		if (mInformGlobalDialog.isShowing()) {
    			mInformGlobalDialog.dismiss();
    		}
    		mInformGlobalDialog = null;
    		mDownloadView = null;
    	}
	}
	
	
	private class FavoritesCheckChangeListener implements android.widget.CompoundButton.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, 
                boolean isChecked) {
			// TODO Auto-generated method stub
			int position = (Integer)buttonView.getTag();
			if (mListAdapter != null) {
				Information info= mListAdapter.getItem(position);
				mListAdapter.getFilterDataList().get(position).setFavorites(isChecked);
				mListAdapter.notifyFavoritesOriginalData(info);
				//mListAdapter.notifyDataSetChanged();
				DataManager.updateInformation(MyApplication.getAppContext(), InformMessage.ISFAVORITES, (isChecked ? "1":"0"), 
						InformMessage.ID, String.valueOf(info.getId()));
			}
		}
	}


	/*@Override
	public void update(String event, String messageType) {
		// TODO Auto-generated method stub
		if (event.equals(PushMessageManager.RECEIVE_MESSAGE_EVENT)) {
			if (messageType.equals(PushMessageContents.INFORMATION_MAIN_TYPE) ||
					messageType.equals(PushMessageContents.NOTIFY_MSG_MAIN_TYPE)) {
	        	mHandler.sendEmptyMessage(REFRESH);
			}
		}
	}*/
	
	private class InformationPushMessageReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Utils.RECEIVE_PUSH_MESSAGE_ACTION)) {
				String messageType = intent.getStringExtra(Utils.RECEIVE_PUSH_MESSAGE_TYPE_KEY);
				if (messageType.equals(PushMessageContents.INFORMATION_MAIN_TYPE) ||
						messageType.equals(PushMessageContents.NOTIFY_MSG_MAIN_TYPE)) {
		        	mHandler.sendEmptyMessage(REFRESH);
				}
			}
		}
	}
	
	
	public class DownloadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			handleIntent(intent);
		}

		private void handleIntent(Intent intent) {

			if (intent != null
					&& intent.getAction().equals(
							DownloadUtils.BBPAWMANAGE_DOWNLOAD_ACTION)) {
				int type = intent.getIntExtra(DownloadUtils.TYPE, -1);
				String url;
				String currDownloadUrl="";
				if (mDownloadView!= null) {
					currDownloadUrl = mDownloadView.getDownloadUrl();
				}
				switch (type) {
				case DownloadUtils.ACTION_DOWNLOAD_COMPLETE:
					url = intent.getStringExtra(DownloadUtils.URL);
					if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(currDownloadUrl)
							&& currDownloadUrl.equals(url)) {
						String filePath = intent.getStringExtra(DownloadUtils.FILE_PATH);
						Message msg = new Message();
						msg.what = MSG_DOWNLOAD_COMPLETE;
						msg.obj = filePath;
						mHandler.sendMessage(msg);
						Log.e("lee", " download complete -filePath >>>> "+filePath);
					}
					break;
				case DownloadUtils.ACTION_DOWNLOAD_PROCESS:
					url = intent.getStringExtra(DownloadUtils.URL);
					if (!TextUtils.isEmpty(currDownloadUrl) && currDownloadUrl.equals(url)) {
						String progress = intent.getStringExtra(DownloadUtils.PROCESS_PROGRESS);
						Message msg = new Message();
						msg.what = MSG_PROGRESS_UPDATE;
						msg.arg1 = Integer.valueOf(progress);
						mHandler.sendMessage(msg);
					}
					break;
				case DownloadUtils.ACTION_DOWNLOAD_ERROR:
					url = intent.getStringExtra(DownloadUtils.URL);
					if (!TextUtils.isEmpty(currDownloadUrl) && currDownloadUrl.equals(url)) {
						mHandler.sendEmptyMessage(MSG_DOWNLOAD_ERROR);
					}
					break;
				default:
					break;
				}
			}
		}
	}
	
	private void search(String constraint) {
		if (mListAdapter!=null && 
				!TextUtils.isEmpty(constraint)) {
			mListAdapter.getSearchFilter().filter(constraint.trim());
		}
		
	}
	
	private void startAnimEffect(final View view) {	
		mAnimEffect.setAttributs(1.0F, big_x, 1.0F, big_y, 200);
		Animation anim = mAnimEffect.createAnimation();
		anim.setFillBefore(true);
		anim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				// TODO Auto-generated method stub
				switch (view.getId()) {
				case R.id.close:
					if (mInformGlobalDialog != null) {
			    		if (mInformGlobalDialog.isShowing()) {
			    			mInformGlobalDialog.dismiss();
			    		}
			    		mInformGlobalDialog = null;
			    	}
					break;
				case R.id.positiveButton:
					onclickPositiveButton(view);
					break;
				case R.id.dowmload_close_btn:
					dismissInformGlobalDialog();
					break;
				case R.id.menu_delete:
					Log.e("xiaolp","delete");
					if (mListAdapter != null){
						if (enableDelete) {
							enableDelete = false;
							mListAdapter.setDeleteMode(false);
						}else{
							enableDelete = true;
							mListAdapter.setDeleteMode(true);
						}
						mListAdapter.notifyDataSetChanged();
					}
					break;		
				default:
					break;
				}
			}
		});
		view.startAnimation(anim);
	}
	
	
	@Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch (arg0.getId()) {
		case R.id.menu_delete:
			if (mListAdapter != null){
				if (arg1) {
					mListAdapter.setDeleteMode(true);
				}else{
					mListAdapter.setDeleteMode(false);
					List<Information> selectDatas = mListAdapter.getSelectDatas();
					if (selectDatas != null && selectDatas.size()>0) {
						for (int i = 0; i < selectDatas.size(); i++) {
							Information info = selectDatas.get(i);
							if (info != null) {
								DataManager.removeInformMessageToDB(InformationActivity.this, info);
								mListAdapter.removeItem(info);
							}
						}
					}
					mListAdapter.clearSelectDatas();
				}
				mListAdapter.notifyDataSetChanged();
			}
			break;
		default:
			break;
		}
	}
}
