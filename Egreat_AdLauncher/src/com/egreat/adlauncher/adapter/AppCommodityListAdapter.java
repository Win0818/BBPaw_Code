package com.egreat.adlauncher.adapter;
import java.util.List;

import com.egreat.adlauncher.db.ApkInfo;
import com.egreat.adlauncher.image.utils.ImageLoader;
import com.mgle.launcher.R;

import net.tsz.afinal.FinalBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class AppCommodityListAdapter extends BaseAdapter {
	private static final String TAG = "CommodityListAdapter---";
	private Context mCtx;
	private List<ApkInfo> mApks;
	//private FinalBitmap mFB;
	private ImageLoader mLoader = null;
	private FrameLayout mFrameLayout;
    private ImageView mFocusImageView;
	private boolean mFirstIn = true;
	public AppCommodityListAdapter(Context context, List<ApkInfo> apksInfos) {
		mApks = apksInfos;
		Log.e(TAG, "CommodityListAdapter...mCommoditys.size="+(mApks==null ? "0" : mApks.size()));
		mCtx = context;
		mLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
		//mFB = FinalBitmap.create(context, context.getCacheDir().toString());
		//mFB.configLoadingImage(R.drawable.loading);
		//mFB.configLoadfailImage(R.drawable.loading);
		//mFB.configTransitionDuration(500);
	}

	@Override
	public int getCount() {
		Log.i(TAG,"mCommoditys.size()= "+mApks.size());
		return mApks.size();
	}

	@Override
	public ApkInfo getItem(int position) {
		return mApks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = LayoutInflater.from(mCtx).inflate(
					R.layout.app_commodity_item, null);
			viewholder.poster = (ImageView) convertView.findViewById(R.id.commodity_item_poster);
			viewholder.name = (TextView)convertView.findViewById(R.id.txt_app_name);
			viewholder.introl = (TextView)convertView.findViewById(R.id.txt_app_introl);
			viewholder.focus = (ImageView) convertView.findViewById(R.id.commodity_item_fosus);
			viewholder.mLayout = (FrameLayout)convertView.findViewById(R.id.main_layout);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		
		ApkInfo apkInfo = mApks.get(position);
		//Log.e(TAG, "getView...name="+apkInfo.getName()+"; getPoster="+apkInfo.getPoster()
		//		+"; mFirstIn="+mFirstIn+"; position="+position);
		viewholder.name.setText(apkInfo.getName().trim());
		viewholder.introl.setText(apkInfo.getIntrol().trim());
		viewholder.poster.setImageResource(R.drawable.loading);
		
		mLoader.loadImage(apkInfo.getPoster(), viewholder.poster, true, false);

		/*if(mFirstIn   && position==0){
			itemRequestFocus(viewholder.mLayout, viewholder.focus);
			mFirstIn = false;
		}*/
		return convertView;
	}


	public void changData(List<ApkInfo> apksInfo) {
		mFirstIn = true;
		this.mApks = apksInfo;
		notifyDataSetChanged();
	}

	private void itemRequestFocus(FrameLayout frameLayout, ImageView focus) {
		mFrameLayout = frameLayout;
		mFocusImageView = focus;
		Bitmap bitmap = BitmapFactory.decodeResource(mCtx.getResources(), R.drawable.application_selected);
		mFocusImageView.setImageBitmap(bitmap);
		AnimationSet animationSet = new AnimationSet(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.02f, 1.0f, 1.05f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnimation.setDuration(200);
		animationSet.addAnimation(scaleAnimation);
		animationSet.setFillAfter(true);

		mFrameLayout.startAnimation(animationSet);
	}
	
	
	public void itemLostFocus() {
		Log.e(TAG, "mCommodityList..itemLostFocus..mFrameLayout="+mFrameLayout+";mFocusImageView="+mFocusImageView);
		if(mFrameLayout!=null && mFocusImageView!=null){
			mFocusImageView.setImageBitmap(null);
			
			AnimationSet animationSet = new AnimationSet(true);
			ScaleAnimation scaleAnimation = new ScaleAnimation(1.02f, 1.0f, 1.05f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			scaleAnimation.setDuration(200);
			animationSet.addAnimation(scaleAnimation);
			animationSet.setFillAfter(true);
	
			mFrameLayout.startAnimation(animationSet);
			mFrameLayout = null;
			mFocusImageView = null;
		}
	}
	
	class ViewHolder {
		private ImageView poster;
		private TextView name;
		private TextView introl;
		private FrameLayout mLayout;
		private ImageView focus;
	}
}
