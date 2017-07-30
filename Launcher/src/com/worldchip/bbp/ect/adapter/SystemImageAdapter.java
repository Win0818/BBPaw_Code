package com.worldchip.bbp.ect.adapter;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.LoadingActivity;
import com.worldchip.bbp.ect.activity.SettingActivity;
import com.worldchip.bbp.ect.util.HttpCommon;
import com.worldchip.bbp.ect.util.ResourceManager;
import com.worldchip.bbp.ect.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class SystemImageAdapter extends BaseAdapter {

	Activity act;

	public SystemImageAdapter(Activity act) {
		this.act = act;
	}

	@Override
	public int getCount() {
		return ResourceManager.SYSTME_IMAGE.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class Holder {
		private ImageView iv;
		private ImageView selected;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder holder;
		if (convertView == null) {
			holder = new Holder();
			convertView = View.inflate(act, R.layout.setting_album_system_item,
					null);
			holder.iv = (ImageView) convertView.findViewById(R.id.image);
			holder.selected = (ImageView) convertView
					.findViewById(R.id.isselected);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.iv.setImageResource(ResourceManager.SYSTME_IMAGE[position]);
		holder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.selected
						.setImageResource(R.drawable.setting_select_image);
				Intent intent_loading = new Intent();
				intent_loading.setClass(act, LoadingActivity.class);// ��ת�����ؽ���
				act.startActivity(intent_loading);
				final int index = position;
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Resources res = act.getResources();
						Bitmap bmp = BitmapFactory.decodeResource(res,
								ResourceManager.SYSTME_IMAGE[index]);
						HttpCommon.SavaImage(bmp, act.getPackageName());
						Intent intent = new Intent();
						intent.setClass(act, SettingActivity.class);
						act.setResult(Utils.LOAD_SYSTEM_IMAGE_REQUEST_CODE, intent);// ���ûش���ݡ�resultCodeֵ��800�����ֵ�������ڽ�������ֻش���ݵ���Դ��������ͬ�Ĵ���
						act.finish();// �ر��Ӵ���ChildActivity
					}
				}, 1000);
			}
		});
		return convertView;
	}
}