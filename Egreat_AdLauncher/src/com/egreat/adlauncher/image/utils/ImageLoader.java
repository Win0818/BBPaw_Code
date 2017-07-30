package com.egreat.adlauncher.image.utils;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.egreat.adlauncher.image.utils.ImageSizeUtil.ImageSize;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;


/**
 * 鍥剧墖鍔犺浇绫�
 * 
 */
public class ImageLoader
{
	private static ImageLoader mInstance;

	/**
	 * 鍥剧墖缂撳瓨鐨勬牳蹇冨璞�
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 绾跨▼姹�
	 */
	private ExecutorService mThreadPool;
	private static final int DEAFULT_THREAD_COUNT = 1;
	/**
	 * 闃熷垪鐨勮皟搴︽柟寮�
	 */
	private Type mType = Type.LIFO;
	/**
	 * 浠诲姟闃熷垪
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 鍚庡彴杞绾跨▼
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;
	/**
	 * UI绾跨▼涓殑Handler
	 */
	private Handler mUIHandler;

	/** 淇″彿閲忥紝閫氳繃淇″彿閲忓疄鐜扮嚎绋嬬殑鍚屾**/
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	private Semaphore mSemaphoreThreadPool;

	private boolean isDiskCacheEnable = true;

	private static final String TAG = "ImageLoader";

	
	/**
	 * FIFO 鍏堝叆鍏堝嚭闃熷垪
	 * LIFO 鍚庡叆鍏堝嚭闃熷垪
	 *
	 */
	public enum Type
	{
		FIFO, LIFO;
	}

	private ImageLoader(int threadCount, Type type)
	{
		init(threadCount, type);
	}

	/**
	 * 鍒濆鍖�
	 * 
	 * @param threadCount
	 * @param type
	 */
	private void init(int threadCount, Type type)
	{
		initBackThread();

		// 鑾峰彇鎴戜滑搴旂敤鐨勬渶澶у彲鐢ㄥ唴瀛�
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMemory = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheMemory)
		{
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				return value.getRowBytes() * value.getHeight();
			}

		};

		// 鍒涘缓绾跨▼姹�
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);
		
		if (mUIHandler == null)
		{
			mUIHandler = new Handler()
			{
				public void handleMessage(Message msg)
				{
					// 鑾峰彇寰楀埌鍥剧墖锛屼负imageview鍥炶皟璁剧疆鍥剧墖
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageview = holder.imageView;
					String path = holder.path;
					// 灏唒ath涓巊etTag瀛樺偍璺緞杩涜姣旇緝
					 Object viewTag = imageview.getTag();
					if (viewTag != null && viewTag.toString().equals(path))
					{
						imageview.setImageBitmap(bm);
					}
				};
			};
		}
	}

	/**
	 * 鍒濆鍖栧悗鍙拌疆璇㈢嚎绋�
	 */
	private void initBackThread()
	{
		// 鍚庡彴杞绾跨▼
		mPoolThread = new Thread()
		{
			@Override
			public void run()
			{
				Looper.prepare();
				mPoolThreadHandler = new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						// 绾跨▼姹犲幓鍙栧嚭涓�釜浠诲姟杩涜鎵ц
						mThreadPool.execute(getTask());
						try
						{
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e)
						{
						}
					}
				};
				// 閲婃斁涓�釜淇″彿閲�
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			};
		};

		mPoolThread.start();
	}

	public static ImageLoader getInstance()
	{
		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(DEAFULT_THREAD_COUNT, Type.LIFO);
				}
			}
		}
		return mInstance;
	}

	public static ImageLoader getInstance(int threadCount, Type type)
	{
		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 鏍规嵁path涓篿mageview璁剧疆鍥剧墖
	 * 
	 * @param path
	 * @param imageView
	 * @param isRoundedCorner 鏄惁闇�鍦嗚
	 */
	public void loadImage(final String path, final ImageView imageView,
			final boolean isFromNet,final boolean isRoundedCorner)
	{
		imageView.setTag(path);
		// 鏍规嵁path鍦ㄧ紦瀛樹腑鑾峰彇bitmap
		Bitmap bm = getBitmapFromLruCache(path);

		if (bm != null)
		{
			if (isRoundedCorner) {
				bm = ImageTool.getRoundedCornerBitmap(bm, 10);
			}
			refreashBitmap(path, imageView, bm);
		} else
		{
			addTask(buildTask(path, imageView, isFromNet,isRoundedCorner));
		}

	}

	/**
	 * 鏍规嵁璧勬簮id涓篿mageview璁剧疆鍥剧墖
	 * 
	 * @param resId
	 * @param imageView
	 */
	public void loadImageFromRes(final Resources resources, final int resId, final ImageView imageView)
	{
		imageView.setTag(String.valueOf(resId));

		// 鏍规嵁path鍦ㄧ紦瀛樹腑鑾峰彇bitmap
		Bitmap bm = getBitmapFromLruCache(String.valueOf(resId));

		if (bm != null)
		{
			refreashBitmap(String.valueOf(resId), imageView, bm);
		} else
		{
			addTask(buildTask(resources, resId, imageView));
		}

	}
	
	private Runnable buildTask(final Resources resources,final int resId, final ImageView imageView)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				Bitmap bm = null;
				
				bm = loadImageFromResId(resources, resId, imageView);
				
				// 3銆佹妸鍥剧墖鍔犲叆鍒扮紦瀛�
				addBitmapToLruCache(String.valueOf(resId), bm);
				refreashBitmap(String.valueOf(resId), imageView, bm);
				mSemaphoreThreadPool.release();
			}
		};
	}
	
	
	private Bitmap loadImageFromResId(final Resources resources, final int resId,
			final ImageView imageView)
	{
		Bitmap bm = null;
		// 鍔犺浇鍥剧墖
		// 鍥剧墖鐨勫帇缂�
		// 1銆佽幏寰楀浘鐗囬渶瑕佹樉绀虹殑澶у皬
		ImageSize imageSize = ImageSizeUtil.getImageViewSize(imageView);
		// 2銆佸帇缂╁浘鐗�
		bm = decodeSampledBitmapFromRes(resources, resId, imageSize.width, imageSize.height);
		return bm;
	}
	
	
	/**
	 * 鏍规嵁浼犲叆鐨勫弬鏁帮紝鏂板缓涓�釜浠诲姟
	 * 
	 * @param path
	 * @param imageView
	 * @param isFromNet
	 * @return
	 */
	private Runnable buildTask(final String path, final ImageView imageView,
			final boolean isFromNet, final boolean isRoundedCorner)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				Bitmap bm = null;
				if (isFromNet)
				{
					File file = getDiskCacheDir(imageView.getContext(),
							md5(path));
					if (file.exists())// 濡傛灉鍦ㄧ紦瀛樻枃浠朵腑鍙戠幇
					{
						Log.e(TAG, "find image :" + path + " in disk cache .");
						bm = loadImageFromLocal(file.getAbsolutePath(),
								imageView);
					} else
					{
						if (isDiskCacheEnable)// 妫�祴鏄惁寮�惎纭洏缂撳瓨
						{
							boolean downloadState = DownloadImgUtils
									.downloadImgByUrl(path, file);
							if (downloadState)// 濡傛灉涓嬭浇鎴愬姛
							{
								Log.e(TAG, "download image :" + path
												+ " to disk cache . path is "
												+ file.getAbsolutePath());
								bm = loadImageFromLocal(file.getAbsolutePath(),
										imageView);
							}
						} else
						// 鐩存帴浠庣綉缁滃姞杞�
						{
							Log.e(TAG, "load image :" + path + " to memory.");
							bm = DownloadImgUtils.downloadImgByUrl(path,
									imageView);
						}
					}
				} else
				{
					bm = loadImageFromLocal(path, imageView);
				}
				// 3銆佹妸鍥剧墖鍔犲叆鍒扮紦瀛�
				addBitmapToLruCache(path, bm);
				if (isRoundedCorner) {
					bm = ImageTool.getRoundedCornerBitmap(bm, 10);
				}
				refreashBitmap(path, imageView, bm);
				mSemaphoreThreadPool.release();
			}
		};
	}
	
	private Bitmap loadImageFromLocal(final String path,
			final ImageView imageView)
	{
		Bitmap bm = null;
		// 鍔犺浇鍥剧墖
		// 鍥剧墖鐨勫帇缂�
		// 1銆佽幏寰楀浘鐗囬渶瑕佹樉绀虹殑澶у皬
		ImageSize imageSize = ImageSizeUtil.getImageViewSize(imageView);
		// 2銆佸帇缂╁浘鐗�
		bm = decodeSampledBitmapFromPath(path, imageSize.width,
					imageSize.height);
		return bm;
	}
	
	/**
	 * 浠庝换鍔￠槦鍒楀彇鍑轰竴涓柟娉�
	 * 
	 * @return
	 */
	private Runnable getTask()
	{
		try{
			if (mType == Type.FIFO)
			{
				return mTaskQueue.removeFirst();//绉婚櫎骞惰繑鍥炴鍒楄〃鐨勭涓�釜鍏冪礌銆�
			} else if (mType == Type.LIFO)
			{
				return mTaskQueue.removeLast();
			}
		}catch(Exception err){
			err.printStackTrace();
		}
		return null;
	}

	/**
	 * 鍒╃敤绛惧悕杈呭姪绫伙紝灏嗗瓧绗︿覆瀛楄妭鏁扮粍
	 * 
	 * @param str
	 * @return
	 */
	public String md5(String str)
	{
		if (TextUtils.isEmpty(str)) {
			return null;
		}
		byte[] digest = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("md5");
			digest = md.digest(str.getBytes());
			return bytes2hex02(digest);

		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 鏂瑰紡浜�
	 * 
	 * @param bytes
	 * @return
	 */
	public String bytes2hex02(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes)
		{
			// 灏嗘瘡涓瓧鑺備笌0xFF杩涜涓庤繍绠楋紝鐒跺悗杞寲涓�0杩涘埗锛岀劧鍚庡�鍔╀簬Integer鍐嶈浆鍖栦负16杩涘埗
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// 姣忎釜瀛楄妭8涓猴紝杞负16杩涘埗鏍囧織锛�涓�6杩涘埗浣�
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}

	private void refreashBitmap(final String path, final ImageView imageView,
			Bitmap bm)
	{
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.path = path;
		holder.imageView = imageView;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	/**
	 * 灏嗗浘鐗囧姞鍏ruCache
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLruCache(String path, Bitmap bm)
	{
		if (getBitmapFromLruCache(path) == null)
		{
			if (bm != null)
				mLruCache.put(path, bm);
		}
	}

	
	/**
	 * 鏍规嵁鍥剧墖闇�鏄剧ず鐨勫鍜岄珮瀵瑰浘鐗囪繘琛屽帇缂�
	 * 
	 * @param resId
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromRes(Resources resources, int resId, int width,
			int height)
	{
		// 鑾峰緱鍥剧墖鐨勫鍜岄珮锛屽苟涓嶆妸鍥剧墖鍔犺浇鍒板唴瀛樹腑
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, options);

		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);

		// 浣跨敤鑾峰緱鍒扮殑InSampleSize鍐嶆瑙ｆ瀽鍥剧墖
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, options);
		return bitmap;
	}
	
	/**
	 * 鏍规嵁鍥剧墖闇�鏄剧ず鐨勫鍜岄珮瀵瑰浘鐗囪繘琛屽帇缂�
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width,
			int height)
	{
		// 鑾峰緱鍥剧墖鐨勫鍜岄珮锛屽苟涓嶆妸鍥剧墖鍔犺浇鍒板唴瀛樹腑
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);

		// 浣跨敤鑾峰緱鍒扮殑InSampleSize鍐嶆瑙ｆ瀽鍥剧墖
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 鏍规嵁鍥剧墖闇�鏄剧ず鐨勫鍜岄珮瀵瑰浘鐗囪繘琛屽帇缂�
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	/*protected Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap, int width,
			int height)
	{
		if (bitmap == null) {
			return null;
		}
		// 鑾峰緱鍥剧墖鐨勫鍜岄珮锛屽苟涓嶆妸鍥剧墖鍔犺浇鍒板唴瀛樹腑
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);

		// 浣跨敤鑾峰緱鍒扮殑InSampleSize鍐嶆瑙ｆ瀽鍥剧墖
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}
	*/
	private synchronized void addTask(Runnable runnable)
	{
		mTaskQueue.add(runnable);
		// if(mPoolThreadHandler==null)wait();
		try
		{
			if (mPoolThreadHandler == null)
				mSemaphorePoolThreadHandler.acquire();
		} catch (InterruptedException e)
		{
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 鑾峰緱缂撳瓨鍥剧墖鐨勫湴鍧�
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName)
	{
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
		{
			cachePath = context.getExternalCacheDir().getPath();
		} else
		{
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 鏍规嵁path鍦ㄧ紦瀛樹腑鑾峰彇bitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key)
	{
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		return mLruCache.get(key);
	}

	private class ImgBeanHolder
	{
		Bitmap bitmap;
		ImageView imageView;
		String path;
		boolean isRoundedCorner = false;
	}
}
