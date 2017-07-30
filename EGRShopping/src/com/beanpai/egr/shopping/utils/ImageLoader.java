package com.beanpai.egr.shopping.utils;


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
	private static final int MAX_CAPACITY = 10;// 锟�锟斤拷缂撳瓨鐨勬渶澶х┖锟�
	private static final long DELAY_BEFORE_PURGE = 10 * 1000;// 瀹氭椂娓呯悊缂撳瓨

	
	// 0.75鏄姞杞藉洜瀛愪负缁忛獙鍊硷紝true鍒欒〃绀烘寜鐓ф渶杩戣闂噺鐨勯珮浣庢帓搴忥紝false鍒欒〃绀烘寜鐓ф彃鍏ラ『搴忔帓锟�
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
	// 浜岀骇缂撳瓨锛岄噰鐢ㄧ殑鏄蒋搴旂敤锛屽彧鏈夊湪鍐呭瓨鍚冪揣鐨勬椂鍊欒蒋搴旂敤鎵嶄細琚洖鏀讹紝鏈夋晥鐨勯伩鍏嶄簡oom
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(MAX_CAPACITY / 2);

	// 瀹氭椂娓呯悊缂撳瓨
	private Runnable mClearCache = new Runnable()
	{
		@Override
		public void run() {
			clear();
		}
	};
	private Handler mPurgeHandler = new Handler();

	// 閲嶇疆缂撳瓨娓呯悊鐨則imer
	private void resetPurgeTimer()
	{
		mPurgeHandler.removeCallbacks(mClearCache);
		mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
	}

	/**
	 * 娓呯悊缂撳瓨
	 */
	private void clear() 
	{
		mFirstLevelCache.clear();
		mSecondLevelCache.clear();
	}

	/**
	 * 杩斿洖缂撳瓨锛屽鏋滄病鏈夊垯杩斿洖null
	 * @param url
	 * @return
	 */
	public Bitmap getBitmapFromCache(String url)
	{
		Bitmap bitmap = null;
		bitmap = getFromFirstLevelCache(url);// 浠庝竴绾х紦瀛樹腑锟�
		if (bitmap != null)
		{
			return bitmap;
		}
		bitmap = getFromSecondLevelCache(url);// 浠庝簩绾х紦瀛樹腑锟�
		return bitmap;
	}

	/**
	 * 浠庝簩绾х紦瀛樹腑锟�
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
			//鐢变簬鍐呭瓨鍚冪揣锛岃蒋寮曠敤宸茬粡琚玤c鍥炴敹锟�
			if (bitmap == null) 
			{
				mSecondLevelCache.remove(url);
			}
		}
		return bitmap;
	}

	/**
	 * 浠庝竴绾х紦瀛樹腑锟�
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
			// 灏嗘渶杩戣闂殑鍏冪礌鏀惧埌閾剧殑澶撮儴锛屾彁楂樹笅锟�锟斤拷璁块棶璇ュ厓绱犵殑锟�锟斤拷閫熷害锛圠RU绠楁硶锟�
			if (bitmap != null)
			{
				mFirstLevelCache.remove(url);
				mFirstLevelCache.put(url, bitmap);
			}
		}
		return bitmap;
	}

	/**
	 * 鍔犺浇鍥剧墖锛屽鏋滅紦瀛樹腑鏈夊氨鐩存帴浠庣紦瀛樹腑鎷匡紝缂撳瓨涓病鏈夊氨涓嬭浇
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	public void loadImage(ImageView mImageView,String url) 
	{
		if (TextUtils.isEmpty(url)) return;
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 浠庣紦瀛樹腑璇诲彇
		if (bitmap == null)
		{
			//mImageView.setImageResource(R.drawable.ic_launcher);//缂撳瓨娌℃湁璁句负榛樿鍥剧墖
			ImageLoadTask imageLoadTask = new ImageLoadTask();
			imageLoadTask.execute(url,mImageView);
		} else {
			Bitmap newBitmap = ImageTool.getRoundedCornerBitmap(bitmap, 10);
			mImageView.setImageBitmap(newBitmap);
		}
	}

	/**
	 * 璁剧疆鍊掑奖鍥剧墖
	 * 鍔犺浇鍥剧墖锛屽鏋滅紦瀛樹腑鏈夊氨鐩存帴浠庣紦瀛樹腑鎷匡紝缂撳瓨涓病鏈夊氨涓嬭浇
	 * @param url
	 * @param adapter
	 * @param holder
	 */
	public void setReflectedImage(ImageView mImageView,String url) 
	{
		Bitmap bitmap = getBitmapFromCache(url);// 浠庣紦瀛樹腑璇诲彇
		if (bitmap == null)
		{
			ReflectedImageLoadTask imageLoadTask = new ReflectedImageLoadTask();
			imageLoadTask.execute(url,mImageView);
		} else {
			mImageView.setImageBitmap(ImageTool.createCutReflectedImage(bitmap, 0));
		}

	}
	
	/**
	 * 鏀惧叆缂撳瓨
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
			Bitmap drawable = loadImageFromInternet(url);// 鑾峰彇缃戠粶鍥剧墖
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) 
		{
			if (bitmap == null) 
			{
				return;
			}
			addImage2Cache(url, bitmap);// 鏀惧叆缂撳瓨
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
			Bitmap drawable = loadImageFromInternet(url);// 鑾峰彇缃戠粶鍥剧墖
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) 
		{
			if (bitmap == null) 
			{
				return;
			}
			//addImage2Cache(url, bitmap);// 鏀惧叆缂撳瓨
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