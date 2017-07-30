package com.mgle.member.util;

import java.lang.reflect.Field;

import android.graphics.BitmapFactory.Options;
import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ImageSizeUtil
{
	/**
	 * 鏍规嵁闇�眰鐨勫鍜岄珮浠ュ強鍥剧墖瀹為檯鐨勫鍜岄珮璁＄畻SampleSize
	 * 
	 * @param options
	 * @param width
	 * @param height
	 * @return
	 */
	public static int caculateInSampleSize(Options options, int reqWidth,
			int reqHeight)
	{
		int width = options.outWidth;
		int height = options.outHeight;

		int inSampleSize = 1;

		if (width > reqWidth || height > reqHeight)
		{
			int widthRadio = Math.round(width * 1.0f / reqWidth);
			int heightRadio = Math.round(height * 1.0f / reqHeight);

			inSampleSize = Math.max(widthRadio, heightRadio);
		}

		return inSampleSize;
	}

	/**
	 * 鏍规嵁ImageView鑾烽�褰撶殑鍘嬬缉鐨勫鍜岄珮
	 * 
	 * @param imageView
	 * @return
	 */
	public static ImageSize getImageViewSize(ImageView imageView)
	{

		ImageSize imageSize = new ImageSize();
		DisplayMetrics displayMetrics = imageView.getContext().getResources()
				.getDisplayMetrics();
		

		LayoutParams lp = imageView.getLayoutParams();

		int width = imageView.getWidth();// 鑾峰彇imageview鐨勫疄闄呭搴�
		if (width <= 0)
		{
			width = lp.width;// 鑾峰彇imageview鍦╨ayout涓０鏄庣殑瀹藉害
		}
		if (width <= 0)
		{
			 //width = imageView.getMaxWidth();// 妫�煡鏈�ぇ鍊�
			width = getImageViewFieldValue(imageView, "mMaxWidth");
		}
		if (width <= 0)
		{
			width = displayMetrics.widthPixels;
		}

		int height = imageView.getHeight();// 鑾峰彇imageview鐨勫疄闄呴珮搴�
		if (height <= 0)
		{
			height = lp.height;// 鑾峰彇imageview鍦╨ayout涓０鏄庣殑瀹藉害
		}
		if (height <= 0)
		{
			height = getImageViewFieldValue(imageView, "mMaxHeight");// 妫�煡鏈�ぇ鍊�
		}
		if (height <= 0)
		{
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;

		return imageSize;
	}

	public static class ImageSize
	{
		int width;
		int height;
	}
	
	/**
	 * 閫氳繃鍙嶅皠鑾峰彇imageview鐨勬煇涓睘鎬у�
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName)
	{
		int value = 0;
		try
		{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = field.getInt(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;
			}
		} catch (Exception e)
		{
		}
		return value;

	}

	
}
