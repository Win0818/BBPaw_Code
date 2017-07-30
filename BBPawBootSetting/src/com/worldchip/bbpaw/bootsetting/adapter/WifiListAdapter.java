package com.worldchip.bbpaw.bootsetting.adapter;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldchip.bbpaw.bootsetting.R;
import com.worldchip.bbpaw.bootsetting.activity.MyApplication;
import com.worldchip.bbpaw.bootsetting.util.BBPawWifiManager;
import com.worldchip.bbpaw.bootsetting.util.WifiUtils;

public class WifiListAdapter extends BaseAdapter {
	private static final String TAG = WifiListAdapter.class.getSimpleName();
	private Context mContext;
	private List<ScanResult> mWifiList = null;
	private BBPawWifiManager mWifiManager = null;
	private String mConnectedSSID = "";
	private int mSelection = -1;
	
	class ViewHolder {
		View itemView;
		ImageView imgIslock;
		ImageView imgWifiSingal;
		ImageView listLine;
		TextView wifiName;
	}

	public WifiListAdapter(Context context, List<ScanResult> list) {
		mWifiManager = BBPawWifiManager.getInstance(MyApplication.getAppContext());
		mConnectedSSID = mWifiManager.getSSID();
		this.mContext = context;
		this.mWifiList = list;
	}

	@Override
	public int getCount() {
		if (mWifiList != null) {
			return mWifiList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return mWifiList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.wifi_item,
					null);
			holder.itemView = (View) convertView.findViewById(R.id.item_view);
			holder.imgIslock = (ImageView) convertView.findViewById(R.id.iv_isLock);
			holder.imgWifiSingal = (ImageView) convertView.findViewById(R.id.iv_wifi_signal);
			holder.listLine = (ImageView) convertView.findViewById(R.id.list_line);
			holder.wifiName = (TextView) convertView.findViewById(R.id.tv_wifiname);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position != 0) {
			holder.listLine.setVisibility(View.VISIBLE);
		} else {
			holder.listLine.setVisibility(View.GONE);
		}
		ScanResult scanResult = mWifiList.get(position);
		if (scanResult != null) {
			//4.0以后返回的wifi ssid名称格式为"weifi名称",所以要替换掉双引号
			if (mConnectedSSID != null && mConnectedSSID.replace("\"", "").equals(scanResult.SSID)) {
				holder.imgIslock.setBackgroundResource(R.drawable.ic_wifi_selected);
			} else if (WifiUtils.isSecurity(scanResult)) {
				holder.imgIslock.setBackgroundResource(R.drawable.ic_password_lock);
			} else {
				holder.imgIslock.setVisibility(View.INVISIBLE);
			}
			if (mSelection != -1 && mSelection == position) {
				//holder.itemView.setBackgroundResource(R.drawable.ic_language_select);
			} else {
				holder.itemView.setBackgroundDrawable(null);
			}
			int wifiSingalImgRes = WifiUtils.getWifiSingalImgRes(scanResult.level);
			if (wifiSingalImgRes != -1) {
				holder.imgWifiSingal.setBackgroundResource(wifiSingalImgRes);
			}
			holder.wifiName.setText(mWifiList.get(position).SSID);
			holder.wifiName.setSelected(true);
		}
		
		return convertView;
	}

/*	*//**
	 * According to the signal strength settings
	 * 
	 * @param img
	 * @param position
	 *//*
	private void setWifiImg(ImageView img, int position) {
		if (mWifiList.get(position).level > -50) {
			img.setBackgroundResource(R.drawable.wifi_signal3);

		} else if (mWifiList.get(position).level <= -50
				&& mWifiList.get(position).level > -70) {
			img.setBackgroundResource(R.drawable.wifi_signal2);
		} else {
			img.setBackgroundResource(R.drawable.wifi_signal1);
		}
	}*/
	
	
	public void setDatas(List<ScanResult> datas) {
		mWifiList = datas;
		if (mWifiManager != null) {
			mConnectedSSID = mWifiManager.getSSID();
		}
	}
	
	public List<ScanResult> getListDatas(List<ScanResult> datas) {
		return mWifiList;
	}
	
	public void selection(int position) {
		mSelection = position;
	}
	
	public ScanResult getSelectionInfo() {
		if (mSelection != -1 && mWifiList != null) {
			return mWifiList.get(mSelection);
		}
		return null;
	}
}
