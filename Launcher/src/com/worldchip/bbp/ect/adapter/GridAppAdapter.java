package com.worldchip.bbp.ect.adapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.MyApplication;
import com.worldchip.bbp.ect.db.AppData;
import com.worldchip.bbp.ect.entity.AppInfo;
import com.worldchip.bbp.ect.util.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAppAdapter extends BaseAdapter {

	private List<AppInfo> dataList;
	private DataCallback dataCallback = null;
	private Activity act;
	private List<AppInfo> shareList = new ArrayList<AppInfo>();
	private Handler mHandler;
	private boolean isDbClick = false; // �Ƿ����ڳ���״̬
	private String packageName = "";
    private Context mContext;
	public static interface DataCallback {
		public void onListen(List<AppInfo> infos);
	}

	public void setDataCallback(DataCallback listener) {
		Log.d("Wing", "setDataCallback----->>>>>");
		dataCallback = listener;
	}

	/**
	 * ��ȡѡ�з������APP
	 * 
	 * @return
	 */
	public List<AppInfo> getShareAppData() {
		return shareList;
	}

	/**
	 * ��ȡɾ���APP����
	 */
	public String getPackageName() {
		return packageName;
	}

	public GridAppAdapter(Activity act, List<AppInfo> mApps, Handler mHandler) {
		this.act = act;
		this.dataList = mApps;
		this.mHandler = mHandler;
		this.mContext=MyApplication.getAppContext();
	}

	@Override
	public int getCount() {
		int count = 0;
		if (dataList != null) {
			count = dataList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private ImageView appimage;
		private ImageView selected;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.item_app_grid, null);
			holder.appimage = (ImageView) convertView
					.findViewById(R.id.appimage);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.appisselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		final AppInfo info = dataList.get(position);
		holder.appimage
				.setImageDrawable(AppData.byteToDrawable(info.getIcon()));
		if (info.isSelected) {
			holder.selected.setImageResource(R.drawable.share_apk_right);
		} else {
			holder.selected.setImageResource(-1);
		}

		holder.appimage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isDbClick) {
					if (info.isSelected) {
						boolean a = AppData.delShareAppData(act,
								info.getPackageName());
						if (a) {
							info.isSelected = false;
							holder.selected.setImageResource(-1);
							Message message = Message.obtain(mHandler, 3);
							message.sendToTarget();
							shareList.remove(info);
						}
					} else {
						info.isSelected = true;
						ContentValues values = new ContentValues();
						values.put("packageName", info.getPackageName());
						values.put("icon", info.getIcon());
						AppData.addShareApp(act, values);
						holder.selected
								.setImageResource(R.drawable.share_apk_right);
						shareList.add(info);
					}
					if (dataCallback != null) {
						dataCallback.onListen(shareList);
					}
				} else {// ����ж��
					boolean flag = AppData.isSystemApp(info.getPackageName(),
							act);
					if (flag) {
						if (info.isSelected) {
							holder.selected
									.setImageResource(R.drawable.share_apk_right);
						} else {
							holder.selected.setImageResource(-1);
						}
						Utils.showToastMessage(act,mContext.getResources().getString(R.string.system_application_cannot_deleted) );
					} else {
						final Dialog dialog = new Dialog(act);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						View view = View.inflate(act,
								R.layout.patriarch_control_uninstall, null);
						dialog.setContentView(view);
						Window dialogWindow = dialog.getWindow();
						dialogWindow
								.setBackgroundDrawableResource(R.drawable.parent_model_bg);
						WindowManager.LayoutParams lp = dialogWindow
								.getAttributes();
						lp.width = 407;
						lp.height = 225;
						dialogWindow.setAttributes(lp);
						TextView cancel = (TextView) view
								.findViewById(R.id.uninstall_cancel);
						TextView confirm = (TextView) view
								.findViewById(R.id.uninstall_confirm);
						dialog.show();
						cancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								dialog.hide();
								notifyDataSetChanged();
							}
						});

						confirm.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								packageName = info.getPackageName();
								Uri packageURI = Uri.parse("package:"
										+ info.getPackageName());
								// ����Intent��ͼ
								Intent intent = new Intent(
										Intent.ACTION_DELETE, packageURI);
								// ִ��ж�س���
								act.startActivity(intent);
								boolean b = AppData.getShareAppByData(act,
										info.getPackageName());
								if (b) {
									boolean c = AppData.delShareAppData(act,
											info.getPackageName());
									if (c) {
										info.isSelected = false;
										shareList.remove(info);
									}
								}
								dialog.hide();
								Message message = Message.obtain(mHandler, 3);
								message.sendToTarget();
							}
						});
					}
					isDbClick = false;
				}
			}
		});

		holder.appimage.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				if (!isDbClick) {
					isDbClick = true;
					holder.selected
							.setImageResource(R.drawable.share_apk_error);
					return true;
				}
				return false;
			}
		});
		return convertView;
	}

	/**
	 * ɾ���б���
	 */
	public void delItem(String packageName) {
		Iterator<AppInfo> ite = dataList.iterator();
		while (ite.hasNext()) {
			AppInfo appInfo = ite.next();
			if (appInfo.getPackageName().equals(packageName)) {
				ite.remove();
				notifyDataSetChanged();
				break;
			}
		}
	}
}