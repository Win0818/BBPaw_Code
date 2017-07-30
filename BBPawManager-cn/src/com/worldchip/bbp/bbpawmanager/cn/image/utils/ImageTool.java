package com.worldchip.bbp.bbpawmanager.cn.image.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.worldchip.bbp.bbpawmanager.cn.MyApplication;
import com.worldchip.bbp.bbpawmanager.cn.utils.Configure;

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
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

public class ImageTool {

	/**
	 * 图片倒影效果实现
	 */
	private static int reflectImageHeight = 100;

	/**
	 * 获得圆角图片的方�?
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPixels) {
		if (bitmap == null) {
			return null;
		}
		// 创建�?��和原始图片一样大小位�?
		Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		// 创建带有位图roundConcerImage的画�?
		Canvas canvas = new Canvas(roundConcerImage);
		// 创建画笔
		Paint paint = new Paint();
		// 创建�?��和原始图片一样大小的矩形
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		// 去锯�?
		paint.setAntiAlias(true);
		// 画一个和原始图片�?��大小的圆角矩�?
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
		// 设置相交模式
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// 把图片画到矩形去
		canvas.drawBitmap(bitmap, null, rect, paint);
		return roundConcerImage;
	}

	/***
	 * 创建图片倒影效果 只返回�?影图
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
	 * 创建倒影效果 返回原图+倒影�?
	 * 
	 * @param originalImage
	 * @return
	 */
	public static Bitmap createReflectedImage(Bitmap originalImage) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		Matrix matrix = new Matrix();
		// 实现图片翻转90�?
		matrix.preScale(1, -1);
		// 创建倒影图片
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				reflectImageHeight, width, reflectImageHeight, matrix, false);
		// 创建总图片（原图�?+ 倒影图片�?
		Bitmap finalReflection = Bitmap.createBitmap(width,
				(height + reflectImageHeight), Config.ARGB_8888);
		// 创建画布
		Canvas canvas = new Canvas(finalReflection);
		canvas.drawBitmap(originalImage, 0, 0, null);
		// 把�?影图片画到画布上
		canvas.drawBitmap(reflectionImage, 0, height + 1, null);
		Paint shaderPaint = new Paint();
		// 创建线�?渐变LinearGradient对象
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, finalReflection.getHeight() + 1,
				0x70ffffff, 0x00ffffff, TileMode.MIRROR);
		shaderPaint.setShader(shader);
		shaderPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// 画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果�?
		canvas.drawRect(0, height + 1, width, finalReflection.getHeight(),
				shaderPaint);
		return finalReflection;
	}

	/**
	 * view界面转换成bitmap
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
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
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
	 * 返回读取超大图片时的采样
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap decodeFile(File f, int measuredWidth) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE_H = Configure.SCREEN_WIDTH;
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
	}

	/**
	 * 旋转图片
	 * 
	 * @param degreen
	 */
	public static Bitmap rotateImage(Bitmap bmOrg, int degree) {
		int width = bmOrg.getWidth();
		int height = bmOrg.getHeight();
		/* 调整角度 */
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
	 * 旋转图片
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
		/* 调整角度 */
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
	 * 创建缩放图像，回收原图
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
	 * 创建缩放图像，回收原图
	 */
	public static Bitmap imageScaleAndRecycle(int resId,
			int WidthTarget, int HeightTarget) {
		if (resId <= 0) {
			return null;
		}
		Bitmap bmpSource = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(), resId);
		int WidthSource = bmpSource.getWidth();
		int HeightSource = bmpSource.getHeight();
		if (WidthSource <= WidthTarget && HeightSource <= HeightTarget) {
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
	 * 返回读取超大图片时的采样
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
}
