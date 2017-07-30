package com.worldchip.bbpawphonechat.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.worldchip.bbpawphonechat.R;
import com.worldchip.bbpawphonechat.application.MyApplication;
import com.worldchip.bbpawphonechat.fragment.ChatSettingFragment.MyLogoutListen;

public class ExitAccountDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context mContext;
	private Button cancel, sure;
	private TextView mAccountName;

	private MyLogoutListen mLogoutListen;
	private RelativeLayout rl_exit;

	public ExitAccountDialog(Context context, MyLogoutListen logoutListen) {
		super(context, R.style.Aleart_Dialog_Style);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mLogoutListen = logoutListen;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_setting_exit);
		rl_exit = (RelativeLayout) findViewById(R.id.is_exit_account);
		cancel = (Button) findViewById(R.id.btn_exit_cancel);
		sure = (Button) findViewById(R.id.btn_exit_sure);

		imageAdapter();
		// mAccountName = (TextView) findViewById(R.id.tv_current_account);

		// mAccountName.setText(MyApplication.getInstance().getUserName());

		cancel.setOnClickListener(this);
		sure.setOnClickListener(this);
	}

	private void imageAdapter() {
		// TODO Auto-generated method stub
		MyApplication.getInstance().ImageAdapter(
				rl_exit,
				new int[] { R.drawable.dialog_exit_account_bg,
						R.drawable.dialog_exit_account_bg_es,
						R.drawable.dialog_exit_account_bg_en });
		MyApplication.getInstance().ImageAdapter(
				cancel,
				new int[] { R.drawable.selector_btn_cancle,
						R.drawable.selector_btn_cancle_es,
						R.drawable.selector_btn_cancle_en });
		MyApplication.getInstance().ImageAdapter(
				sure,
				new int[] { R.drawable.selector_btn_sure,
						R.drawable.selector_btn_sure_es,
						R.drawable.selector_btn_sure_en });
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_exit_cancel:
			super.onBackPressed();
			break;
		case R.id.btn_exit_sure:
			logout();
			break;

		default:
			break;
		}

	}

	void logout() {
		final MyProgressDialog pd = MyProgressDialog
				.createProgressDialog(mContext);
		String st = mContext.getResources().getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		MyApplication.getInstance().logout(new EMCallBack() {
			@Override
			public void onSuccess() {
				if (mLogoutListen != null) {
					mLogoutListen.isLogout();
					pd.dismiss();
				}
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				Toast.makeText(mContext, R.string.exit_error, Toast.LENGTH_LONG)
						.show();
				pd.dismiss();
			}
		});
	}

}
