package com.worldchip.bbp.bbpawmanager.cn.utils;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;

import com.worldchip.bbp.bbpawmanager.cn.model.Information;
import com.worldchip.bbp.bbpawmanager.cn.view.GlobalProgressDialog;

public class InformationDatasLoader extends AsyncTask<Object, Object, List<Information>> {

	private GlobalProgressDialog mGlobalProgressDialog;
	private Context mContext;
	private InformationCallback mComplateCallback = null;
	
	public interface InformationCallback {
		public void loadComplate(List<Information> result);
	}
	public InformationDatasLoader(Context context, InformationCallback callBack) {
		mContext = context;
		mComplateCallback = callBack;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		startProgressDialog();
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
		stopProgressDialog();
	}

	@Override
	protected void onPostExecute(List<Information> result) {
		// TODO Auto-generated method stub
		if (mComplateCallback != null) {
			mComplateCallback.loadComplate(result);
		}
		stopProgressDialog();
	}

	@Override
	protected List<Information> doInBackground(Object... arg0) {
		// TODO Auto-generated method stub
		return TestDataUtils.generateInformationDatas();
	}

	private void startProgressDialog() {
		if (mGlobalProgressDialog == null) {
			mGlobalProgressDialog = GlobalProgressDialog.createDialog(mContext);
		}
		mGlobalProgressDialog.show();
	}

	private void stopProgressDialog() {
		if (mGlobalProgressDialog != null) {
			mGlobalProgressDialog.dismiss();
			mGlobalProgressDialog = null;
		}
	}
	
	public void setLoadComplateCallback(InformationCallback callBack) {
		mComplateCallback = callBack;
	}
}
