package com.beanpai.egr.shopping.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.entity.MsgResult;
import com.beanpai.egr.shopping.image.utils.ImageLoader;
import com.beanpai.egr.shopping.secret.MD5Util;
import com.beanpai.egr.shopping.utils.MsgWhat;
import com.beanpai.egr.shopping.utils.Utils;
import com.beanpai.egr.shopping.utils.WebServiceLoader;
import com.egreat.devicemanger.DeviceManager;
import com.mgle.shopping.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class BuyPopupWindow extends PopupWindow implements OnClickListener, OnKeyListener{

	private static final String TAG = "BuyPopupWindow";
	private Context mContext;
	private View mParent;
	@SuppressWarnings("unused")
	private Resources mRes;
	private Button mBtnNextStep;
	@SuppressWarnings("unused")
	private DisplayMetrics mDm;
	private List<RelativeLayout> mStepLayoutList;
	private int mCurrentStepIndex =0;
	private WebServiceLoader mWebServiceLoader = null;
	private ImageLoader mLoader = null;
	
	//step1
	private EditText mEditUsrName;
	private EditText mEditUsrPhone;
	private EditText mEditUsrPhone2;
	private EditText mEditUsrAddress;
	@SuppressWarnings("unused")
	private EditText mEditUsrHome;
	@SuppressWarnings("unused")
	private EditText mEditUsrPart;
	
	//step 6
	private TextView mTxtUsrName;
	private TextView mTxtUsrPhone;
	private TextView mTxtUsrPhone2;
	private TextView mTxtUsrAddress;
	@SuppressWarnings("unused")
	private TextView mTxtUsrHome;
	@SuppressWarnings("unused")
	private TextView mTxtUsrPart;
	
	private TextView mTxtBuycount;
	private ImageView mImgPoster;
	
	@SuppressWarnings("unused")
	private TextView mExchangeCount;
	@SuppressWarnings("unused")
	private TextView mExchangeSum;
	
	private int mModel;
	
	private int mBuyNumber;
	private TextView mEditCount;
	private View mView;
	private MemberInfo mMemberInfo;
	private Map<String ,String> rawParams = null;
	private String mPosterUrl = null;
	
	public BuyPopupWindow(Context context,View parent,
			DisplayMetrics dm, String jsonString){
		this.mParent = parent;
		this.mContext = context;
		
		mPosterUrl  = jsonString;
		Log.e(TAG,"..BuyPopupWindow..mPosterUrl="+mPosterUrl);
		mDm = dm;
		mMemberInfo = new MemberInfo();
		mLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
		
		mView = init(context);
		initView();
		initData();
	}
	
	@SuppressLint("InflateParams")
	private View init(Context context) {
		mRes = context.getResources();
		View view = LayoutInflater.from(mContext).inflate(R.layout.popup_buy_layout, null);
		mWebServiceLoader = new WebServiceLoader();
		
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.MATCH_PARENT);
		setAnimationStyle(R.style.MenuPopupAnimation);
		setBackgroundDrawable(new ColorDrawable(0));
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(view);
		return view;
	}

	public void show(){
		showAtLocation(mParent,  Gravity.CENTER, 0, 0);
	}
	
	
	private void initView() {
		mBtnNextStep = (Button)mView.findViewById(R.id.btn_nextStep);
		mBtnNextStep.setOnClickListener(this);
		
		mEditUsrName = (EditText)mView.findViewById(R.id.edit_user_name);
		mEditUsrPhone = (EditText)mView.findViewById(R.id.edit_user_phone);
		mEditUsrPhone2 = (EditText)mView.findViewById(R.id.edit_user_phone2);
		mEditUsrAddress = (EditText)mView.findViewById(R.id.edit_user_address);
		mEditUsrHome = (EditText)mView.findViewById(R.id.edit_user_home);
		mEditUsrPart = (EditText)mView.findViewById(R.id.edit_user_home_part);
		mImgPoster = (ImageView)mView.findViewById(R.id.img_poster);
		
		mTxtUsrName = (TextView)mView.findViewById(R.id.txt_user_name);
		mTxtUsrPhone = (TextView)mView.findViewById(R.id.txt_user_phone);
		mTxtUsrPhone2 = (TextView)mView.findViewById(R.id.txt_user_phone2);
		mTxtUsrAddress = (TextView)mView.findViewById(R.id.txt_user_address);
		mTxtUsrHome = (TextView)mView.findViewById(R.id.txt_user_home);
		mTxtUsrPart = (TextView)mView.findViewById(R.id.txt_user_home_part);
		
		mTxtBuycount = (TextView)mView.findViewById(R.id.txt_buycount);
		
		mEditCount = (TextView)mView.findViewById(R.id.edit_count);
		mEditCount.setOnKeyListener(this);
		
		mStepLayoutList = new ArrayList<RelativeLayout>();
	}

	private void initData() {
		rawParams = new HashMap<String, String>();
		
		paserMemberInfo(DeviceManager.getMemoryJsonData());
	}
	
	@SuppressWarnings("unused")
	private void sendMessage(String phoneNum, String msg){
		rawParams.clear();
		rawParams.put("account", Utils.SEND_MSG_ACCOUNT);
		String pwd =  MD5Util.string2MD5(Utils.SEND_MSG_PASSWORD);
		rawParams.put("password",Utils.SEND_MSG_PASSWORD);
		rawParams.put("mobile", phoneNum);
		rawParams.put("content", msg);
		Log.e(TAG, "sendMessage..account="+Utils.SEND_MSG_ACCOUNT
			+"; pwd="+Utils.SEND_MSG_PASSWORD+"; md5 pwd="+pwd
			+"; phoneNum="+phoneNum+"; msg="+msg);
		mWebServiceLoader.sendMsgResult(mHandler,
				mContext, Utils.SEND_MSG_URL, rawParams);
	}
	
	public void paserMemberInfo(String jsonData){
		Log.d(TAG, "paserMemberInfo...jsonData=" + jsonData);
		
		try {
			JSONTokener jsonParser = new JSONTokener(jsonData);
			JSONObject epgObj = (JSONObject) jsonParser.nextValue();
			
			mMemberInfo.customername = epgObj.getString("customername");
			mMemberInfo.level = epgObj.getInt("level");
			mMemberInfo.mobile = epgObj.getString("mobile");
			mMemberInfo.onlineDuration = epgObj.getLong("onlineDuration");
			mMemberInfo.integral =  epgObj.getInt("integral");
			try{
				mMemberInfo.paypassword = epgObj.getString("paypassword");
			}catch(Exception err){
				err.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch(Exception err){
			err.printStackTrace();
		}
		
		initMemberInfo();
	}
	
	public void initMemberInfo(){
		if(mMemberInfo==null){
			return;
		}
		
		Log.e(TAG, "initMemberInfo...memberInfo.paypassword="+mMemberInfo.paypassword);
		
		if(mMemberInfo.paypassword==null || mMemberInfo.paypassword.equals("")){
			mCurrentStepIndex = 0;
			mModel = 0;
			mStepLayoutList.add((RelativeLayout)mView.findViewById(R.id.step_1));
			mStepLayoutList.add((RelativeLayout)mView.findViewById(R.id.step_2));
			mStepLayoutList.add((RelativeLayout)mView.findViewById(R.id.step_3));
			mStepLayoutList.add((RelativeLayout)mView.findViewById(R.id.step_4));
			mStepLayoutList.add((RelativeLayout)mView.findViewById(R.id.step_5));
			refreshUI();
			initUserInfo();
		}else{
			mModel = 1;
			mCurrentStepIndex = 0;
			mStepLayoutList.add((RelativeLayout)mView.findViewById(R.id.step_5));
			mStepLayoutList.add((RelativeLayout)mView.findViewById(R.id.step_6));
			showUserInfo();
			refreshUI();
		}
	}
	
	private void initUserInfo() {
		mEditUsrName.setText(mMemberInfo.customername);
		mEditUsrPhone.setText(mMemberInfo.mobile+"");
		mEditUsrPhone2.setText(mMemberInfo.mobile);
		mEditUsrAddress.setText(mMemberInfo.linkaddress+"");
	}
	
	private void showUserInfo(){
		mTxtUsrName.setText(mMemberInfo.customername);
		mTxtUsrPhone.setText(mMemberInfo.mobile+"");
		mTxtUsrPhone2.setText(mMemberInfo.mobile);
		mTxtUsrAddress.setText(mMemberInfo.linkaddress+"");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_nextStep:
				if(checkCurrentStep()){
					mCurrentStepIndex ++;
					if(mCurrentStepIndex >= mStepLayoutList.size()){
						mCurrentStepIndex --;
						return;
					}
					refreshUI();
				}
				break;
	
			default:
				break;
		}
	}
	
	@Override
	public boolean onKey(View view, int arg1, KeyEvent event) {
		if(event.getRepeatCount() != 0 || event.getAction() != KeyEvent.ACTION_DOWN){
			return false;
		}
		
		Log.e(TAG, "onKey..mCurrentStepIndex="+mCurrentStepIndex);
		if(view.getId()==R.id.edit_count && mCurrentStepIndex==4){
			switch(event.getKeyCode()){
				case KeyEvent.KEYCODE_DPAD_LEFT:
					try{
						int number = Integer.parseInt(mEditCount.getText().toString())-1;
						if(number <1){
							number =1;
						}
						mBuyNumber = number;
						mEditCount.setText(number+"");
					}catch(Exception err){
						err.printStackTrace();
					}
				   return true;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					try{
						int number = Integer.parseInt(mEditCount.getText().toString())+1;
						if(number > 1000){
							number =1000;
						}
						mBuyNumber = number;
						mEditCount.setText(number+"");
					}catch(Exception err){
						err.printStackTrace();
					}
			      return true;
			}
		}
		return false;
	}

	private void refreshUI() {
		for(int i=0; i<mStepLayoutList.size(); i++){
			if(i==mCurrentStepIndex){
			   mStepLayoutList.get(i).setVisibility(View.VISIBLE);
			}else{
				mStepLayoutList.get(i).setVisibility(View.GONE);
			}
		}
	}

	private boolean checkCurrentStep() {
		if(mModel == 0){
			switch(mCurrentStepIndex){
				case 0:
					if(mEditUsrName.getText()==null || mEditUsrName.getText().equals("")){
						showMsg("姓名为空！");
						return false;
					}
					if(mEditUsrName.getText()==null || mEditUsrName.getText().equals("")){
						showMsg("联系电话为空！");
						return false;
					}
					break;
			}
		}else if(mModel==1){
			switch(mCurrentStepIndex){
				case 0:
					try{
						int number = Integer.parseInt(mEditCount.getText().toString());
						if(number > 1000){
							number =1000;
						}
						mBuyNumber = number;
						this.mTxtBuycount.setText(mBuyNumber+"");
						mLoader.loadImage(mPosterUrl, mImgPoster, true, false);
					}catch(Exception err){
						err.printStackTrace();
					}
					break;
			}
		}
		
		return true;
	}

	private void showMsg(String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}

	@SuppressLint("HandlerLeak")
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgWhat.NO_NET:
				Log.e(TAG, "Net Error!");
				break;
			case MsgWhat.SEND_MSG_INFO_RESULT:
				if (msg.obj == null) {
					Log.e(TAG, "SEND_MSG_INFO_RESULT OBJ null!");
					return;
				}
				MsgResult result = (MsgResult) msg.obj;
				Log.e(TAG, "SEND_MSG_INFO_RESULT..result="+result);
				//showMessage(result.code);
				break;
			}
		}
	};
}