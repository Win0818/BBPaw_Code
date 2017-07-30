package com.worldchip.bbp.ect.adapter;

import java.util.List;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.common.BitmapCache;
import com.worldchip.bbp.ect.common.BitmapCache.ImageCallback;
import com.worldchip.bbp.ect.entity.PictureInfo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MediaSharePicAdapter extends BaseAdapter {
	private Context mContext;
	private List<PictureInfo> mPicList;
	private LayoutInflater inflater;
	private BitmapCache cache;

	public MediaSharePicAdapter(Context context, List<PictureInfo> list) {
		this.mContext = context;
		this.mPicList = list;
		cache = new BitmapCache();
		this.inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		int count = 0;
		if (mPicList != null) {
			count = mPicList.size();
		}
		return count;
	}

	@Override
	public Object getItem(int position) {

		return mPicList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	ImageView img;
	@Override
	public View getView(final int position, View arg1, ViewGroup arg2) {

		View view = inflater.inflate(R.layout.fragment_pic_item, null);
		 img = (ImageView) view.findViewById(R.id.iv);
		final PictureInfo picture = mPicList.get(position);
		img.setTag(picture.getData());
		cache.displayBmp(img, null, picture.getData(), callback);
        img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setComponent(new ComponentName(
						"com.worldchip.bbp.ect",
						"com.worldchip.bbp.ect.activity.PictureAllScreen"));
				intent.putExtra("singless_path",mPicList.get(position).getData());
				 mContext.startActivity(intent);
			}
		});
		return view;
	}

	private ImageCallback callback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap,
				Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				}
			}
		}
	};
}
