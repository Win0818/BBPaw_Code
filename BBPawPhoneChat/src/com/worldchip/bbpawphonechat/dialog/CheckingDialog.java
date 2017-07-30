package com.worldchip.bbpawphonechat.dialog;

import internal.org.apache.http.entity.mime.MinimalField;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MyJsonParserUtil;
import com.worldchip.bbpawphonechat.net.HttpUtils;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class CheckingDialog extends Dialog implements
		android.view.View.OnClickListener {
	public static final String TAG = "CHRIS";
	private Context context;
	private ImageView mIvAnim;
	private Animation operatingAnim;

	private String mCurrentVersion;
	private List<String> mNewVersionList;
	private Handler mDhaHandler;

	private RelativeLayout mRlDialogBg;
	private LinearLayout mLinearNewVersionInfos;
	private TextView mTvApkName, mTvVersionCode, mTvNewVersionInfo;
	private Button mBtnCancleUpdata, mBtnSureUpdata;

	public CheckingDialog(Handler handler, Context context) {
		super(context, R.style.Aleart_Dialog_Style);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mDhaHandler = handler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setting_checking);

		mRlDialogBg = (RelativeLayout) findViewById(R.id.rl_version_update_dialog_bg);

		MyApplication.getInstance().ImageAdapter(
				mRlDialogBg,
				new int[] { R.drawable.dialog_check_new_process,
						R.drawable.dialog_check_new_process_es,
						R.drawable.dialog_check_new_process_en });

		mLinearNewVersionInfos = (LinearLayout) findViewById(R.id.ll_get_new_version);

		mIvAnim = (ImageView) findViewById(R.id.iv_checking_logo);

		mTvApkName = (TextView) findViewById(R.id.iv_apk_name);
		mTvVersionCode = (TextView) findViewById(R.id.iv_apk_version_code);
		mTvNewVersionInfo = (TextView) findViewById(R.id.iv_apk_updata_note_info);
		mBtnCancleUpdata = (Button) findViewById(R.id.btn_dialog_cancel_updata_version);
		mBtnSureUpdata = (Button) findViewById(R.id.btn_dialog_sure_updata_version);
		
		MyApplication.getInstance().ImageAdapter(
				mBtnCancleUpdata,
				new int[] { R.drawable.selector_btn_cancle,
						R.drawable.selector_btn_cancle_es,
						R.drawable.selector_btn_cancle_en });
		MyApplication.getInstance().ImageAdapter(
				mBtnSureUpdata,
				new int[] { R.drawable.selector_btn_updata,
						R.drawable.selector_btn_updata_es,
						R.drawable.selector_btn_updata_en });

		mBtnCancleUpdata.setOnClickListener(this);
		mBtnSureUpdata.setOnClickListener(this);

		getVersionName();
		initUpdataVersion();
	}

	private void getVersionName() {
		try {
			mCurrentVersion = CommonUtils.getVersionName(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUpdataVersion() {
		MyUpdateTask myUpdateTask = new MyUpdateTask();
		myUpdateTask.execute();
	}

	private void startUpdateAnim(ImageView iv) {
		operatingAnim = AnimationUtils.loadAnimation(context,
				R.anim.dialog_chicking_spin);
		LinearInterpolator lin = new LinearInterpolator();
		operatingAnim.setInterpolator(lin);
		iv.startAnimation(operatingAnim);
	}

	private void stopUpdateAnim() {
		if (operatingAnim != null) {
			operatingAnim.cancel();
			mIvAnim.clearAnimation();
		}
	}

	class MyUpdateTask extends AsyncTask<String, Integer, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			startUpdateAnim(mIvAnim);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				String url = MyComment.UPDATA_NEW_VERSION;
				String result = HttpUtils.getRequest(url, context);
				Log.e(TAG, "-------MyUpdateTask---doInBackground--------"
						+ result);
				mNewVersionList = MyJsonParserUtil
						.praserNewVersionInfos(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mNewVersionList != null) {
				if (mNewVersionList.get(1).equals(mCurrentVersion)) {
					stopUpdateAnim();
					mDhaHandler
							.removeMessages(MyComment.CHECK_NEW_VERSION_FAILED);
					mDhaHandler
							.sendEmptyMessage(MyComment.CHECK_NEW_VERSION_FAILED);
					dismiss();
				} else {
					stopUpdateAnim();
					
					MyApplication.getInstance().ImageAdapter(
							mRlDialogBg,
							new int[] { R.drawable.dialog_check_new_version,
									R.drawable.dialog_check_new_version_es,
									R.drawable.dialog_check_new_version_en });

					mIvAnim.setVisibility(View.GONE);
					mLinearNewVersionInfos.setVisibility(View.VISIBLE);
					mTvApkName.setText(mNewVersionList.get(0));
					mTvVersionCode.setText("v:" + mNewVersionList.get(1));
					mTvNewVersionInfo.setText(mNewVersionList.get(2));
				}
			}

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_dialog_cancel_updata_version:
			dismiss();
			break;

		case R.id.btn_dialog_sure_updata_version:
			Message message = new Message();
			message.what = MyComment.UPDATA_NEW_VERSION_MESSAGE;
			message.obj = mNewVersionList.get(3);
			mDhaHandler.sendMessage(message);
			dismiss();
			break;

		default:
			break;
		}

	}

}
