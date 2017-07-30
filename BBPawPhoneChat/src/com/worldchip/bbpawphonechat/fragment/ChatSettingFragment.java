package com.worldchip.bbpawphonechat.fragment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.activity.ChangePassword;
import com.worldchip.bbpawphonechat.activity.FeedbackAndSuggest;
import com.worldchip.bbpawphonechat.activity.MainActivity;
import com.worldchip.bbpawphonechat.activity.SettingActivity;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.comments.MyComment;
import com.worldchip.bbpawphonechat.comments.MySharePreData;
import com.worldchip.bbpawphonechat.dialog.CheckingDialog;
import com.worldchip.bbpawphonechat.dialog.EditNameDialog;
import com.worldchip.bbpawphonechat.dialog.ExitAccountDialog;
import com.worldchip.bbpawphonechat.dialog.MyProgressDialog;
import com.worldchip.bbpawphonechat.net.HttpUtils;
import com.worldchip.bbpawphonechat.net.PictureUploadUtils;
import com.worldchip.bbpawphonechat.net.PictureUploadUtils.HttpResponseCallBack;
import com.worldchip.bbpawphonechat.utils.CommonUtils;

public class ChatSettingFragment extends Fragment implements OnClickListener {
	protected static final String TAG = "CHRIS";
	private Context mcontext;
	private Button btn_edit;
	private TextView tv_name;
	private boolean updatenick;
	private HashMap<String, String> mChangeNickMap;
	private RelativeLayout rl_exit, rl_update, rl_feedback_suggest,
			rl_change_password, rl_setting;

	private Intent intent;
	private MyLogoutListen mLogoutListen;

	private ProgressDialog mUpdataProgress;

	private ProgressDialog mUpDataPd;
	private boolean isDownLoad = false;
	private String mUpdataAddress;
	private int total;

	private String updateHeadUrl = MyComment.CHANGE_MY_HEAD_URL + "&username="
			+ MyApplication.getInstance().getUserName();

	private String nick = "";
	private ImageView iv_head;
	private MyProgressDialog  mUpdateHead ;
	private String selectedImage;
	private String myHeadUrl = "";
	

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MyComment.CHANGE_NICK_NAME:
				nick = (String) msg.obj;
				changeNickName(nick);
				break;
			case MyComment.CHECK_NEW_VERSION_FAILED:
				noteIsNewVersion();
				break;
			case MyComment.UPDATA_NEW_VERSION_MESSAGE:
				mUpdataAddress = (String) msg.obj;
				downLoadApk();
				break;
			case MyComment.UPDATA_PROGRESS_BAR:
				mUpDataPd.setProgress(total);
				break;
			case MyComment.CHANGE_MY_HEAD_SUCCESS:
				headUpdateSuccess();
				break;
			case MyComment.CHANGE_MY_HEAD_FIALED:
				headUpdataFailed();
				break;
			case MyComment.UPDATA_HEAD_IMAGE:
				MyApplication
				.getInstance()
				.getImageLoader()
				.displayImage(myHeadUrl,
						iv_head,
						MyApplication.getInstance().getDisplayOptionsHead());
				break;
			default:
				break;
			}
		}
	};   
	
	
	
	
	public ChatSettingFragment(Context mcontext, MyLogoutListen logoutListen) {
		super();
		this.mcontext = mcontext;
		this.mLogoutListen = logoutListen;
	}

	public ChatSettingFragment(Context mcontext) {
		super();
		this.mcontext = mcontext;

	}

	public ChatSettingFragment() {
	}

	public interface MyLogoutListen {
		public void isLogout();
	}

	private ImageView iv_chat_setting_suggest, iv_chat_setting_update,
			iv_chat_setting_change_psw, iv_chat_setting_setting,
			iv_chat_setting_exit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_chat_setting, container,
				false);
		btn_edit = (Button) view.findViewById(R.id.btn_setting_edit);

		tv_name = (TextView) view.findViewById(R.id.tv_setting_name);
		rl_exit = (RelativeLayout) view.findViewById(R.id.rl_setting_exit);
		rl_update = (RelativeLayout) view
				.findViewById(R.id.rl_setting_check_update);
		rl_feedback_suggest = (RelativeLayout) view
				.findViewById(R.id.rl_feedback_suggest);
		rl_change_password = (RelativeLayout) view
				.findViewById(R.id.rl_change_password);
		rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);
		iv_head = (ImageView) view.findViewById(R.id.iv_setting_bar_my_head);

		if(mcontext != null)
		nick = MySharePreData.GetData(mcontext, MyComment.CHAT_SP_NAME,
				MyApplication.getInstance().getUserName());

		btn_edit.setOnClickListener(this);
		rl_exit.setOnClickListener(this);
		rl_update.setOnClickListener(this);
		rl_feedback_suggest.setOnClickListener(this);
		rl_change_password.setOnClickListener(this);
		rl_setting.setOnClickListener(this);
		iv_head.setOnClickListener(this);

		if (nick.equals("")) {
			tv_name.setText(MyApplication.getInstance().getUserName());
		} else {
			tv_name.setText(nick);
		}

		mChangeNickMap = new HashMap<String, String>();
		iv_chat_setting_suggest = (ImageView) view
				.findViewById(R.id.iv_chat_setting_suggest);
		iv_chat_setting_update = (ImageView) view
				.findViewById(R.id.iv_chat_setting_update);
		iv_chat_setting_change_psw = (ImageView) view
				.findViewById(R.id.iv_chat_setting_change_psw);
		iv_chat_setting_setting = (ImageView) view
				.findViewById(R.id.iv_chat_setting_setting);
		iv_chat_setting_exit = (ImageView) view
				.findViewById(R.id.iv_chat_setting_exit);

		imageAdapter();
        if(mcontext != null)
		myHeadUrl = MySharePreData.GetData(mcontext, MyComment.CHAT_SP_NAME, MyComment.My_HEAD_URL);
				
		MyApplication
		.getInstance()
		.getImageLoader()
		.displayImage(myHeadUrl,
				iv_head,
				MyApplication.getInstance().getDisplayOptionsHead());
	    
		return view;
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub

		MyApplication.getInstance().ImageAdapter(
				btn_edit,
				new int[] { R.drawable.selector_setting_edit_bg,
						R.drawable.selector_setting_edit_bg_es,
						R.drawable.selector_setting_edit_bg_en });

		MyApplication.getInstance().ImageAdapter(
				iv_chat_setting_suggest,
				new int[] { R.drawable.chat_setting_suggest,
						R.drawable.chat_setting_suggest_es,
						R.drawable.chat_setting_suggest_en });
		MyApplication.getInstance().ImageAdapter(
				iv_chat_setting_update,
				new int[] { R.drawable.chat_setting_update,
						R.drawable.chat_setting_update_es,
						R.drawable.chat_setting_update_en });
		MyApplication.getInstance().ImageAdapter(
				iv_chat_setting_change_psw,
				new int[] { R.drawable.chat_setting_change_psw,
						R.drawable.chat_setting_change_psw_es,
						R.drawable.chat_setting_change_psw_en });
		MyApplication.getInstance().ImageAdapter(
				iv_chat_setting_setting,
				new int[] { R.drawable.chat_setting_setting,
						R.drawable.chat_setting_setting_es,
						R.drawable.chat_setting_setting_en });
		MyApplication.getInstance().ImageAdapter(
				iv_chat_setting_exit,
				new int[] { R.drawable.chat_setting_exit,
						R.drawable.chat_setting_exit_es,
						R.drawable.chat_setting_exit_en });

	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("ChatFragmentseting-----onResume--设置界面---");
	}
	
	private void changeNickName(final String nick) {
		mUpdataProgress = new ProgressDialog(mcontext);
		mUpdataProgress.setMessage(getString(R.string.updateing_nikename));
		mUpdataProgress.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				mChangeNickMap.put("param", "change_nickname");
				mChangeNickMap.put("username", MyApplication.getInstance()
						.getUserName());
				mChangeNickMap.put("nickname", nick);
				String url = MyComment.CHANGE_MY_NICK;
				String updatenick = null;
				try {
					updatenick = HttpUtils.postRequest(url, mChangeNickMap,
							mcontext);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (updatenick.equals("true")) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mUpdataProgress.dismiss();
							MyApplication.currentUserNick = nick;
							tv_name.setText(nick);
							MySharePreData.SetData(mcontext,
									MyComment.CHAT_SP_NAME,
									MyApplication.getInstance().getUserName(), nick);
							Toast.makeText(mcontext, R.string.change_name_success,
									Toast.LENGTH_LONG).show();
						}
					});
				} else {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mUpdataProgress.dismiss();
							Toast.makeText(mcontext, R.string.change_name_fail,
									Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

	// is the new version
	private void noteIsNewVersion() {
		Dialog dialog = new Dialog(mcontext, R.style.Aleart_Dialog_Style);
		dialog.setCanceledOnTouchOutside(true);
		ImageView mDialogIv = new ImageView(mcontext);
		mDialogIv.setImageResource(R.drawable.is_new_version_dialog);
		dialog.setContentView(mDialogIv);
		dialog.show();
	}

	// updata new apk
	protected void downLoadApk() {
		mUpDataPd = new ProgressDialog(mcontext);
		mUpDataPd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mUpDataPd.setMessage(getString(R.string.setting_updateing));
		mUpDataPd.setCanceledOnTouchOutside(false);
		mUpDataPd.show();
		isDownLoad = true;
		new Thread() {
			@Override
			public void run() {
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					URL url;
					try {
						url = new URL(mUpdataAddress);
						Log.d(TAG, "=------downLoadApk---URL---"
								+ mUpdataAddress);
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setConnectTimeout(5000);
						// 获取到文件的大小
						mUpDataPd.setMax(conn.getContentLength());
						InputStream is = conn.getInputStream();

						File updatefile = new File(
						Environment.getExternalStorageDirectory()+ "/bbpawChat.apk");
						if (updatefile.exists()) {
							updatefile.delete();
							updatefile.createNewFile();
						} else {
							updatefile.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(updatefile);

						BufferedInputStream bis = new BufferedInputStream(is);
						byte[] buffer = new byte[1024];
						int len;
						total = 0;
						while ((len = bis.read(buffer)) != -1 && isDownLoad) {
							fos.write(buffer, 0, len);
							total += len;
							Log.d(TAG,"------下载进度------" + String.valueOf(total));
							// 获取当前下载量
							mHandler.sendEmptyMessage(MyComment.UPDATA_PROGRESS_BAR);
						}
						fos.close();
						bis.close();
						is.close();
						installApk();
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(MyComment.UPDATA_HAVE_ERROR);
					}
				}
			}
		}.start();
	}

	protected void installApk() {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		// 执行的数据类型
		intent.setDataAndType(
				Uri.fromFile(new File(Environment.getExternalStorageDirectory()
						+ "/bbpawChat.apk")),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_setting_edit:
			new EditNameDialog(mHandler, mcontext).show();
			break;
		case R.id.rl_setting_exit:
			new ExitAccountDialog(mcontext, mLogoutListen).show();
			break;
		case R.id.rl_setting_check_update:
			new CheckingDialog(mHandler, mcontext).show();
			break;
		case R.id.rl_feedback_suggest:
			intent = new Intent(mcontext, FeedbackAndSuggest.class);
			mcontext.startActivity(intent);
			break;
		case R.id.rl_change_password:
			intent = new Intent(mcontext, ChangePassword.class);
			mcontext.startActivity(intent);
			break;
		case R.id.rl_setting:
			intent = new Intent(mcontext, SettingActivity.class);
			mcontext.startActivity(intent);
			break;
		case R.id.iv_setting_bar_my_head:
			changeHead();
			break;

		default:
			break;
		}
	}

	public static final int REQUEST_CODE_CAMERA = 18;
	public static final int REQUEST_CODE_LOCAL = 19;

	private void changeHead() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
		} else {
			intent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		}

		startActivityForResult(intent, REQUEST_CODE_LOCAL);

	}

	private void updateHead(String selectedImage) {
		PictureUploadUtils.doPost(selectedImage, new HttpResponseCallBack() {
			public void onSuccess(String result, String httpTag) {
				mHandler.sendEmptyMessage(MyComment.CHANGE_MY_HEAD_SUCCESS);
                System.out.println("----更新头像成功返回--------"+result);
			}

			@Override
			public void onStart(String httpTag) {

			}

			@Override
			public void onFinish(int result, String httpTag) {
				
			}

			@Override
			public void onFailure(IOException e, String httpTag) {
				mHandler.sendEmptyMessage(MyComment.CHANGE_MY_HEAD_FIALED);
			}
		}, "updateHead", updateHeadUrl);

	}
	
	
	private void  headUpdateSuccess(){
		if(mUpdateHead != null){
			mUpdateHead.dismiss();
		}
		Bitmap bitmap = CommonUtils.getSimpleBitmipFromUri(selectedImage, iv_head);
		Log.e(TAG, selectedImage);
		if (bitmap != null) {
			iv_head.setImageBitmap(bitmap);
		}
		Toast.makeText(mcontext, R.string.my_change_head_success,
				Toast.LENGTH_LONG).show();
	}
	
	private void headUpdataFailed(){
		if(mUpdateHead != null){
			mUpdateHead.dismiss();
		}
		Toast.makeText(mcontext, R.string.my_change_head_failed,
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == REQUEST_CODE_LOCAL) {
				if (data != null) {
					Uri uri = data.getData();
					selectedImage = CommonUtils.getFilePathFromUri(uri);
					if (selectedImage != null){
						mUpdateHead = MyProgressDialog.createProgressDialog(mcontext);
						mUpdateHead.setMessage(getString(R.string.update_head_now));
						mUpdateHead.show();
					    updateHead(selectedImage);
					}
				}
		    }
		}
	}
}
