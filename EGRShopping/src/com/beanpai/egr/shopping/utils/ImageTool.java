package com.beanpai.egr.shopping.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.View.MeasureSpec;

public class ImageTool {

	/**
	 */
	private static int reflectImageHeight = 100;
	
	/**
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPixels) 
	{
		if(bitmap == null)
		{
			return null;
		}
		//创建�?��和原始图片一样大小位�?
		Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888);
		//创建带有位图roundConcerImage的画�?
		Canvas canvas = new Canvas(roundConcerImage);
		//创建画笔
		Paint paint = new Paint();
		//创建�?��和原始图片一样大小的矩形
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		//去锯�?
		paint.setAntiAlias(true);
		//画一个和原始图片�?��大小的圆角矩�?
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
		//设置相交模式
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		//把图片画到矩形去
		canvas.drawBitmap(bitmap, null, rect, paint);
		return roundConcerImage;
	}

	/***
	 * 创建图片倒影效果 只返回�?影图
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
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, reflectImageHeight, width, reflectImageHeight, matrix, false);  
        // 创建总图片（原图�?+ 倒影图片�? 
        Bitmap finalReflection = Bitmap.createBitmap(width, (height + reflectImageHeight), Config.ARGB_8888);  
        // 创建画布  
        Canvas canvas = new Canvas(finalReflection);  
        canvas.drawBitmap(originalImage, 0, 0, null);  
        //把�?影图片画到画布上  
        canvas.drawBitmap(reflectionImage, 0, height + 1, null);  
        Paint shaderPaint = new Paint();  
        //创建线�?渐变LinearGradient对象  
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, finalReflection.getHeight() + 1, 0x70ffffff,  
                0x00ffffff, TileMode.MIRROR);  
        shaderPaint.setShader(shader);  
        shaderPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));  
        //画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果�? 
        canvas.drawRect(0, height + 1, width, finalReflection.getHeight(), shaderPaint);  
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
}
