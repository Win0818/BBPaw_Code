package com.worldchip.bbp.bbpawmanager.cn.server;


import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {

	private static final String TAG = "ImageLoader";
	private static final int MAX_CAPACITY = 10;// �?��缓存的最大空�?
	private static final long DELAY_BEFORE_PURGE = 10 * 1000;// 定时清理缓存

	
	// 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排�?
	private HashMap<String, Bitmap> mFirstLevelCache = new LinkedHashMap<String, Bitmap>(MAX_CAPACITY / 2, 0.75f, true) 
	{
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) 
		{
			if (size() > MAX_CAPACITY) 
			{
				mSecondLevelCache.put(eldest.getKey(),new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			}
			return false;
		};
	};
	// 二级缓存，采用的是软应用，只有在内存吃紧的时候软应用才会被回收，有效的避免了oom
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(MAX_CAPACITY / 2);

	// 定时清理缓存
	private Runnable mClearCache = new Runnable()
	{
		@Override
		public void run() {
			clear();
		}
	};
	private Handler mPurgeHandler = new Handler();

	// 重置缓存清理的timer
	private void resetPurgeTimer()
	{
		mPurgeHandler.removeCallbacks(mClearCache);
		mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
	}

	/**
	 * 清理缓存
	 */
	private void clear() 
	{
		mFirstLevelCache.clear();
		mSecondLevelCache.clear();
	}

	/**
	 * 返回缓存，如果没有则返回null
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url)
	{
		Bitmap bitmap = null;
		bitmap = getFromFirstLevelCache(url);// 从一级缓存中�?
		if (bitmap != null)
		{
			return bitmap;
		}
		bitmap = getFromSecondLevelCache(url);// 从二级缓存中�?
		return bitmap;
	}

	/**
	 * 从二级缓存中�?
	 * @param url
	 * @return
	 */
	private Bitmap getFromSecondLevelCache(String url) 
	{
		Bitmap bitmap = null;
		SoftReference<Bitmap> softReference = mSecondLevelCache.get(url);
		if (softReference != null) 
		{
			bitmap = softReference.get();
			//由于内存吃紧，软引用已经被gc回收�?
			if (bitmap == null) 
			{
				mSecondLevelCache.remove(url);
			}
		}
		return bitmap;
	}

	/**
	 * 从一级缓存中�?
	 * 
	 * @param url
	 * @return
	 */
	private Bitmap getFromFirstLevelCache(String url) 
	{
		Bitmap bitmap = null;
		synchronized (mFirstLevelCache)
		{
			bitmap = mFirstLevelCache.get(url);
			// 将最近访问的元素放到链的头部，提高下�?��访问该元素的�?��速度（LRU算法�?
			if (bitmap != null)
			{
				mFirstLevelCache.remove(url);
				mFirstLevelCache.put(url, bitmap);
			}
		}
		return bitmap;
	}

	/**
	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	public void loadImage(ImageView mImageView,String url) 
	{
		if (TextUtils.isEmpty(url)) return;
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null)
		{
			//mImageView.setImageResource(R.drawable.ic_launcher);//缓存没有设为默认图片
			ImageLoadTask imageLoadTask = new ImageLoadTask();
			imageLoadTask.execute(url,mImageView);
		} else {
			Bitmap newBitmap = ImageTool.getRoundedCornerBitmap(bitmap, 10);
			mImageView.setImageBitmap(newBitmap);
		}
	}

	/**
	 * 设置倒影图片
	 * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	public void setReflectedImage(ImageView mImageView,String url) 
	{
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null)
		{
			ReflectedImageLoadTask imageLoadTask = new ReflectedImageLoadTask();
			imageLoadTask.execute(url,mImageView);
		} else {
			mImageView.setImageBitmap(ImageTool.createCutReflectedImage(bitmap, 0));
		}

	}
	
	/**
	 * 放入缓存
	 * @param url
	 * @param value
	 */
	public void addImage2Cache(String url, Bitmap value)
	{
		if (value == null || url == null)
		{
			return;
		}
		synchronized (mFirstLevelCache)
		{
			mFirstLevelCache.put(url, value);
		}
	}

	class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		String url;
		ImageView mImageView;

		@Override
		protected Bitmap doInBackground(Object... params) 
		{
			url = (String) params[0];
			mImageView = (ImageView) params[1];
			Bitmap drawable = loadImageFromInternet(url);// 获取网络图片
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) 
		{
			if (bitmap == null) 
			{
				return;
			}
			addImage2Cache(url, bitmap);// 放入缓存
			Bitmap newBitmap = ImageTool.getRoundedCornerBitmap(bitmap, 10);
			mImageView.setImageBitmap(newBitmap);
		}
	}

	class ReflectedImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		String url;
		ImageView mImageView;

		@Override
		protected Bitmap doInBackground(Object... params) 
		{
			url = (String) params[0];
			mImageView = (ImageView) params[1];
			Bitmap drawable = loadImageFromInternet(url);// 获取网络图片
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) 
		{
			if (bitmap == null) 
			{
				return;
			}
			//addImage2Cache(url, bitmap);// 放入缓存
			mImageView.setImageBitmap(ImageTool.createCutReflectedImage(bitmap, 0));
		}
	}
	
	public Bitmap loadImageFromInternet(String url) 
	{
		Bitmap bitmap = null;
		HttpClient client = AndroidHttpClient.newInstance("Android");
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSocketBufferSize(params, 3000);
		HttpResponse response = null;
		InputStream inputStream = null;
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			response = client.execute(httpGet);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK)
			{
				Log.d(TAG, "func [loadImage] stateCode=" + stateCode);
				return bitmap;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) 
			{
				try {
					inputStream = entity.getContent();
					return bitmap = BitmapFactory.decodeStream(inputStream);
				} finally {
					if (inputStream != null) 
					{
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (ClientProtocolException e) {
			httpGet.abort();
			e.printStackTrace();
		} catch (IOException e) {
			httpGet.abort();
			e.printStackTrace();
		} finally {
			((AndroidHttpClient) client).close();
		}
		return bitmap;
	}
}