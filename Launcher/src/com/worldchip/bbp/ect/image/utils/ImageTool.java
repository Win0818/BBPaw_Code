package com.worldchip.bbp.ect.image.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

public class ImageTool {

	/**
	 * 鍥剧墖鍊掑奖鏁堟灉瀹炵幇
	 */
	private static int reflectImageHeight = 100;

	/**
	 * 鑾峰緱鍦嗚鍥剧墖鐨勬柟锟�?
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPixels) {
		if (bitmap == null) {
			return null;
		}
		// 鍒涘缓锟�?锟斤拷鍜屽師濮嬪浘鐗囦竴鏍峰ぇ灏忎綅锟�?
		Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		// 鍒涘缓甯︽湁浣嶅浘roundConcerImage鐨勭敾锟�?
		Canvas canvas = new Canvas(roundConcerImage);
		// 鍒涘缓鐢荤瑪
		Paint paint = new Paint();
		// 鍒涘缓锟�?锟斤拷鍜屽師濮嬪浘鐗囦竴鏍峰ぇ灏忕殑鐭╁舰
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		// 鍘婚敮锟�?
		paint.setAntiAlias(true);
		// 鐢讳竴涓拰鍘熷鍥剧墖锟�?锟斤拷澶у皬鐨勫渾瑙掔煩锟�?
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
		// 璁剧疆鐩镐氦妯″紡
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// 鎶婂浘鐗囩敾鍒扮煩褰㈠幓
		canvas.drawBitmap(bitmap, null, rect, paint);
		return roundConcerImage;
	}

	/***
	 * 鍒涘缓鍥剧墖鍊掑奖鏁堟灉 鍙繑鍥烇拷?褰卞浘
	 * 
	 * @param bitmap
	 * @param cutHeight
	 * @return
	 */
	public static Bitmap createCutReflectedImage(Bitmap bitmap, int cutHeight) {

		int width = bitmap.getWidth();

		int height = bitmap.getHeight();
		int totleHeight = reflectImageHeight + cutHeight;

		if (height <= totleHeight) {
			return null;
		}

		Matrix matrix = new Matrix();

		matrix.preScale(1, -1);

		System.out.println(height - reflectImageHeight - cutHeight);
		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height
				- reflectImageHeight - cutHeight, width, reflectImageHeight,
				matrix, true);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				reflectImageHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(reflectionImage, 0, 0, null);
		LinearGradient shader = new LinearGradient(0, 0, 0,
				bitmapWithReflection.getHeight()

				, 0x80ffffff, 0x00ffffff, TileMode.CLAMP);

		Paint paint = new Paint();
		paint.setShader(shader);

		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, 0, width, bitmapWithReflection.getHeight(), paint);
		if (!reflectionImage.isRecycled()) {
			reflectionImage.recycle();
		}
		// if (!bitmap.isRecycled()) {
		// bitmap.recycle();
		// }
		System.gc();
		return bitmapWithReflection;

	}

	/**
	 * 鍒涘缓鍊掑奖鏁堟灉 杩斿洖鍘熷浘+鍊掑奖锟�?
	 * 
	 * @param originalImage
	 * @return
	 */
	public static Bitmap createReflectedImage(Bitmap originalImage) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		Matrix matrix = new Matrix();
		// 瀹炵幇鍥剧墖缈昏浆90锟�?
		matrix.preScale(1, -1);
		// 鍒涘缓鍊掑奖鍥剧墖
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				reflectImageHeight, width, reflectImageHeight, matrix, false);
		// 鍒涘缓鎬诲浘鐗囷紙鍘熷浘锟�?+ 鍊掑奖鍥剧墖锟�?
		Bitmap finalReflection = Bitmap.createBitmap(width,
				(height + reflectImageHeight), Config.ARGB_8888);
		// 鍒涘缓鐢诲竷
		Canvas canvas = new Canvas(finalReflection);
		canvas.drawBitmap(originalImage, 0, 0, null);
		// 鎶婏拷?褰卞浘鐗囩敾鍒扮敾甯冧笂
		canvas.drawBitmap(reflectionImage, 0, height + 1, null);
		Paint shaderPaint = new Paint();
		// 鍒涘缓绾匡拷?娓愬彉LinearGradient瀵硅薄
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, finalReflection.getHeight() + 1,
				0x70ffffff, 0x00ffffff, TileMode.MIRROR);
		shaderPaint.setShader(shader);
		shaderPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// 鐢诲竷鐢诲嚭鍙嶈浆鍥剧墖澶у皬鍖哄煙锛岀劧鍚庢妸娓愬彉鏁堟灉鍔犲埌鍏朵腑锛屽氨鍑虹幇浜嗗浘鐗囩殑鍊掑奖鏁堟灉锟�?
		canvas.drawRect(0, height + 1, width, finalReflection.getHeight(),
				shaderPaint);
		return finalReflection;
	}

	/**
	 * view鐣岄潰杞崲鎴恇itmap
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 鍙� drawable 鐨勯暱瀹�
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// 鍙� drawable 鐨勯鑹叉牸寮�
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 寤虹珛瀵瑰簲 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 寤虹珛瀵瑰簲 bitmap 鐨勭敾甯�
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 鎶� drawable 鍐呭鐢诲埌鐢诲竷涓�
		drawable.draw(canvas);
		return bitmap;
	}

	public static byte[] bitmap2Bytes(Bitmap bm) {
		if (bm == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static byte[] drawable2Bytes(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		Bitmap bitmap = drawableToBitmap(drawable);
		return bitmap2Bytes(bitmap);
	}

	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/**
	 * 杩斿洖璇诲彇瓒呭ぇ鍥剧墖鏃剁殑閲囨牱
	 * 
	 * @param f
	 * @return
	 *//*
	public static Bitmap decodeFile(File f, int measuredWidth) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE_H = Configure.IMAGE_MAX_HEIGHT_PX;
			int size_w = Configure.IMAGE_MAX_WIDTH_PX;
			if (measuredWidth > 0) {
				size_w = measuredWidth;
			}
			final int REQUIRED_SIZE_W = size_w;

			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE_W
						&& height_tmp / 2 < REQUIRED_SIZE_H)
					break;

				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			return null;
		}
	}*/

	/**
	 * 鏃嬭浆鍥剧墖
	 * 
	 * @param degreen
	 */
	public static Bitmap rotateImage(Bitmap bmOrg, int degree) {
		int width = bmOrg.getWidth();
		int height = bmOrg.getHeight();
		/* 璋冩暣瑙掑害 */
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		Bitmap bm = Bitmap.createBitmap(bmOrg, 0, 0, width, height, matrix,
				true);
		if (!bmOrg.isRecycled())
			bmOrg.recycle();
		bmOrg = null;
		return bm;
	}

	/**
	 * 鏃嬭浆鍥剧墖
	 * 
	 * @param degreen
	 */
	public static Bitmap rotateImageInView(View view, String path, int degree) {
		int rotateDegree = degree % 360;
		int viewWidth = view.getWidth();
		int viewHeight = view.getHeight();
		Bitmap bmOrg = BitmapFactory.decodeFile(path);
		Bitmap scaleImage = imageScaleAndRecycle(bmOrg, viewWidth, viewHeight);

		if (rotateDegree == 0) {
			return scaleImage;
		}
		int w = scaleImage.getWidth();
		int h = scaleImage.getHeight();
		/* 璋冩暣瑙掑害 */
		Matrix matrix = new Matrix();
		matrix.setRotate(rotateDegree, w / 2f, h / 2f);
		Bitmap bm = Bitmap.createBitmap(scaleImage, 0, 0,
				scaleImage.getWidth(), scaleImage.getHeight(), matrix, true);
		if (!scaleImage.isRecycled()) {
			scaleImage.recycle();
			scaleImage = null;
		}
		return bm;
	}

	/**
	 * 鍒涘缓缂╂斁鍥惧儚锛屽洖鏀跺師鍥�
	 */
	public static Bitmap imageScaleAndRecycle(Bitmap bmpSource,
			int WidthTarget, int HeightTarget) {
		if (bmpSource == null) {
			return null;
		}
		int WidthSource = bmpSource.getWidth();
		int HeightSource = bmpSource.getHeight();
		if (WidthSource < WidthTarget && HeightSource < HeightTarget) {
			return bmpSource;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(((float) WidthTarget) / WidthSource,
				((float) HeightTarget) / HeightSource);
		Bitmap bitmap = Bitmap.createBitmap(bmpSource, 0, 0, WidthSource,
				HeightSource, matrix, true);
		if (!bmpSource.isRecycled()) {
			bmpSource.recycle();
			bmpSource = null;
		}
		return bitmap;
	}

	/**
	 * 杩斿洖璇诲彇瓒呭ぇ鍥剧墖鏃剁殑閲囨牱
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap decodeFile(String filePath, int WidthTarget, int HeightTarget) {
        	//Bitmap bmpSource = BitmapFactory.decodeFile(filePath);
    	    File file = new File(filePath);
    	    if (!file.exists()) {
    	    	return null;
    	    }
    	    try {
        	BitmapFactory.Options tempOption = new BitmapFactory.Options();
        	tempOption.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null, tempOption);
        	
            int WidthSource = tempOption.outWidth;
            int HeightSource = tempOption.outHeight;
            if (WidthSource < WidthTarget && HeightSource < HeightTarget) {
            	return BitmapFactory.decodeFile(filePath);
            }
            Matrix matrix = new Matrix();
            matrix.postScale(((float) WidthTarget) / WidthSource, ((float) HeightTarget) / HeightSource);
            
            long time3 = System.currentTimeMillis();
            Bitmap bmpSource = BitmapFactory.decodeFile(filePath);
            Bitmap bitmap = Bitmap.createBitmap(bmpSource, 0, 0, WidthSource, HeightSource, matrix, true);
            long time4 = System.currentTimeMillis();
            Log.e("lee", "createBitmap == "+(time4-time3));
            if (!bmpSource.isRecycled()) {
            	bmpSource.recycle();
            	bmpSource = null;
            }
            return bitmap;
    	    }catch (FileNotFoundException e) {
    	    	e.printStackTrace();
    	    	return null;
    	    }
    }
	
	public static Bitmap getVideoThumb(final String path,int width, int height) {
		Bitmap bitmap  = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(path, Thumbnails.MICRO_KIND); 
		if (bitmap != null) {
			bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
					ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
		}
		return bitmap;
	}
}
