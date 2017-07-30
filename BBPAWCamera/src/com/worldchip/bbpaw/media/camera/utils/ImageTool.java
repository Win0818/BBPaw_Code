package com.worldchip.bbpaw.media.camera.utils;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import com.worldchip.bbpaw.media.camera.R;
import com.worldchip.bbpaw.media.camera.activity.MyApplication;
import com.worldchip.bbpaw.media.camera.view.GlobalProgressDialog;

public class ImageTool {

	private static final String TAG = "ImageTool";
	

	public static void savePic(Context context, Bitmap saveBitmap) {
		if (saveBitmap == null)
			return;
		File picOuputFile = Utils.createPicOuputFile(context);
		LogUtil.e(TAG, "take image == " + picOuputFile.getPath());
		SaveAsyncTask saveAsyncTask = new SaveAsyncTask(context);
		saveAsyncTask.execute(saveBitmap,picOuputFile.getPath(), null);
		
	}
	
	
	public static void savePic(Context context, byte[] jpegData) {
		// TODO Auto-generated method stub
		File picOuputFile = Utils.createPicOuputFile(context);
		LogUtil.e(TAG, "take image == " + picOuputFile.getPath());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(picOuputFile.getPath());
			out.write(jpegData);
		} catch (Exception e) {
			LogUtil.e(TAG, "Failed to write image --- ");
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}
	
	static class SaveAsyncTask extends AsyncTask<Object, Void, Void> {

		private Context context;
		private GlobalProgressDialog dialog;
		
		public SaveAsyncTask(Context context) {
			this.context = context;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = GlobalProgressDialog.getInstance(context);
			if (dialog != null) {
				dialog.setMessage(context.getString(R.string.saving_msg));
				dialog.startProgressDialog();
			}
			
		}
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			if (dialog != null) {
				dialog.stopProgressDialog();
			}
		}

		@Override
		protected Void doInBackground(Object... params) {
			// TODO Auto-generated method stub
			Bitmap saveBitmap =(Bitmap)params[0];
			String picOuputPath =(String)params[1];
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(picOuputPath);
				saveBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
				Utils.updateGallery(context, picOuputPath);
				// Release bitmap
				if (saveBitmap != null && !saveBitmap.isRecycled()) {
					saveBitmap.recycle();
					saveBitmap = null;
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "Failed to write image --- ");
			} finally {
				try {
					out.close();
				} catch (Exception e) {
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog != null) {
				dialog.stopProgressDialog();
			}
		}

	}
	
	
	
	
	public static Bitmap toHahajingConvex(Bitmap bmp, int radius) {
		if (bmp == null) {
			return null;
		}
		final int width = bmp.getWidth();
		final int height = bmp.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		int pixR = 0;
		int pixG = 0;
		int pixB = 0;
		int pixColor = 0;
		int srcColor = 0;	
		int newR = 0;
		int newG = 0;
		int newB = 0;
		//int radius = height/4;	
		int[] pixels = new int[width * height];
		int[] srcBuf = new int[width * height];
		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int pos = 0;
		int centerX = width/2 ;
		int centerY = height/2 ;
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				pos = j * width + i;
				pixColor = pixels[pos];

				pixR = Color.red(pixColor);
				pixG = Color.green(pixColor);
				pixB = Color.blue(pixColor);

				newR = pixR;
				newG = pixG;
				newB = pixB;

				int distance = (int) ((centerX - i) * (centerX - i) + (centerY - j) * (centerY - j));
				
				if (distance < radius * radius) {
					int src_x = i - centerX;
					int src_y = j - centerY;
			
					src_x = (int)(src_x * ((0.9 * (Math.sqrt(distance)) / radius ) + 0.1));
					src_y = (int)(src_y * ((0.9 * (Math.sqrt(distance)) / radius ) + 0.1));
								
					src_x = src_x + centerX;
					src_y = src_y + centerY;
					
					srcColor  = pixels[src_y * width + src_x] ;
										
					newR  = Color.red(srcColor);
					newG = Color.green(srcColor);
					newB = Color.blue(srcColor);						
					
				}
				newR = Math.min(255, Math.max(0, newR));
				newG = Math.min(255, Math.max(0, newG));
				newB = Math.min(255, Math.max(0, newB));
				srcBuf[pos] = Color.argb(255, newR, newG, newB);
				
			}			
		}
		bitmap.setPixels(srcBuf, 0, width, 0, 0, width, height);
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
		}
		return bitmap;
	}

	 /**
     * 相片变形
     * @param cx
     * @param cy
     */
    
  		
    public static Bitmap toWarp(Bitmap bitmap, float cx, float cy)
	{
    	
    	//获取图片宽度、高度
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();
		
		//定义两个常量，这两个常量指定该图片横向、纵向上都被划分为20格。
        int WIDTH = 20;
        int HEIGHT = 20;
    	int COUNT = (WIDTH + 1) * (HEIGHT + 1);
    	float[] verts = new float[COUNT * 2];
    	//定义一个数组，记录Bitmap上的101 * 101个点经过扭曲后的座标
    	//对图片进行扭曲的关键就是修改该数组里元素的值。
    	float[] orig = new float[COUNT * 2];
    	
		Bitmap tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);
		Canvas tempCanvas = new Canvas(tempBitmap);
		int index = 0;
		for (int y = 0; y <= HEIGHT; y++)
		{
			float fy = bitmapHeight * y / HEIGHT;
			for (int x = 0; x <= WIDTH; x++)
			{
				float fx = bitmapWidth * x / WIDTH;
				
				 // 初始化orig、verts数组。
				//初始化后，orig、verts两个数组均匀地保存了21 * 21个点的x,y座标
				 
				orig[index * 2 + 0] = verts[index * 2 + 0] = fx;
				orig[index * 2 + 1] = verts[index * 2 + 1] = fy;
				index += 1;
			}
		}
		/*for (int i = 0; i < COUNT * 2; i += 2)
		{
			float dx = cx - orig[i + 0];
			float dy = cy - orig[i + 1];
			float dd = dx * dx + dy * dy;
			//计算每个座标点与当前点（cx、cy）之间的距离
			float d = (float)Math.sqrt(dd);
			//计算扭曲度，距离当前点（cx、cy）越远，扭曲度越小
			float pull = 80000 / ((float) (dd * d));
			//对verts数组（保存bitmap上21 * 21个点经过扭曲后的座标）重新赋值
			if (pull >= 1)
			{
				verts[i + 0] = cx;
				verts[i + 1] = cy;
			}
			else
			{
				//控制各顶点向触摸事件发生点偏移
				verts[i + 0] = orig[i + 0] + dx * pull;
				verts[i + 1] = orig[i + 1] + dy * pull;
			}
		}*/
		
		int beginPosX =WIDTH / 3;
		int beginPosY =HEIGHT / 3;
		int countPosX=WIDTH / 3 + WIDTH % 3;
		int countPosY=HEIGHT / 3 + HEIGHT % 3;
		int endPosX=beginPosX + countPosX;
		int endPosY=beginPosY + countPosY;
		
			for (int x = beginPosX; x <= endPosX; x++) {
				
				for (int y = beginPosY; y <= endPosY; y++) {
					int posIndex = 2* (y * (WIDTH+1) + x);
					float dx = cx - orig[posIndex + 0];
					float dy = cy - orig[posIndex + 1];
					float dd = dx * dx + dy * dy;
					//计算每个座标点与当前点（cx、cy）之间的距离
					float d = (float)Math.sqrt(dd);
					//计算扭曲度，距离当前点（cx、cy）越远，扭曲度越小
					float pull = 80000 / ((float) (dd * d));
					//float pull = (float) (Math.abs(dx)/d);
					Log.e("lee", "d == "+d+" dx == "+dx+" pull == "+pull);
					//对verts数组（保存bitmap上21 * 21个点经过扭曲后的座标）重新赋值
					if (pull >= 1)
					{
						verts[posIndex + 0] = cx;
						verts[posIndex + 1] = cy;
					}
					else
					{
						//控制各顶点向触摸事件发生点偏移
						verts[posIndex + 0] = orig[posIndex + 0] + dx * pull;
						verts[posIndex + 1] = orig[posIndex + 1] + dy * pull;
					}
			}
		}
			
		tempCanvas.drawBitmapMesh(bitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
		return tempBitmap;
	}
	
    public static Bitmap toShrink(Bitmap bitmap, float cx, float cy) {
    			//获取图片宽度、高度
    			int bitmapWidth = bitmap.getWidth();
    			int bitmapHeight = bitmap.getHeight();
    			
    			//定义两个常量，这两个常量指定该图片横向、纵向上都被划分为20格。
    	        int WIDTH = 6;
    	        int HEIGHT = 6;
    	    	int COUNT = (WIDTH + 1) * (HEIGHT + 1);
    	    	float[] verts = new float[COUNT * 2];
    	    	//定义一个数组，记录Bitmap上的101 * 101个点经过扭曲后的座标
    	    	//对图片进行扭曲的关键就是修改该数组里元素的值。
    	    	float[] orig = new float[COUNT * 2];
    	    	
    			Bitmap tempBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);
    			Canvas tempCanvas = new Canvas(tempBitmap);
    			int index = 0;
    			for (int y = 0; y <= HEIGHT; y++)
    			{
    				float fy = bitmapHeight * y / HEIGHT;
    				for (int x = 0; x <= WIDTH; x++)
    				{
    					float fx = bitmapWidth * x / WIDTH;
    					
    					 // 初始化orig、verts数组。
    					//初始化后，orig、verts两个数组均匀地保存了21 * 21个点的x,y座标
    					 
    					orig[index * 2 + 0] = verts[index * 2 + 0] = fx;
    					orig[index * 2 + 1] = verts[index * 2 + 1] = fy;
    					index += 1;
    				}
    			}
    			
    			int beginPosX =WIDTH / 3;
    			int beginPosY =HEIGHT / 3;
    			int countPosX=WIDTH / 3 + WIDTH % 3;
    			int countPosY=HEIGHT / 3 + HEIGHT % 3;
    			int endPosX=beginPosX + countPosX;
    			int endPosY=beginPosY + countPosY;
    			for (int y = beginPosY; y <= endPosY; y++) {
    				for (int x = beginPosX; x <= endPosX; x++) {
    					int posIndex = 2* (y * (WIDTH+1) + x);
    					float dx = cx - orig[posIndex];
    					float dy = cy - orig[posIndex + 1];
    					float dd = dx * dx + dy * dy;
    					//计算每个座标点与当前点（cx、cy）之间的距离
    					float d = (float)Math.sqrt(dd);
    					//计算扭曲度，距离当前点（cx、cy）越远，扭曲度越大
    					float pull = ((float) (Math.abs(dx))) / (float)(bitmapWidth/2);
    					Log.e("lee", " pull == "+pull +" (d) == "+(dx));
    					if (pull >= 1)
    					{
    						verts[posIndex + 0] = cx;
    						verts[posIndex + 1] = cy;
    					} else {
    						//控制各顶点向触摸事件发生点偏移
    						verts[posIndex + 0] = orig[posIndex + 0] + dx * pull;
    						verts[posIndex + 1] = orig[posIndex + 1] + dy * pull;
    					}
    				}
    			}
    			tempCanvas.drawBitmapMesh(bitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
    			return tempBitmap;
   	}
    
    /**
     * 创建自定义相片
     */
    public static Bitmap creatCustomBitmap(Bitmap bmpSource, int WidthTarget, int HeightTarget) {
    	if (bmpSource == null) {
    		return null;
    	}
        int WidthSource = bmpSource.getWidth();
        int HeightSource = bmpSource.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(((float) WidthTarget) / WidthSource, ((float) HeightTarget) / HeightSource);
        return Bitmap.createBitmap(bmpSource, 0, 0, WidthSource, HeightSource, matrix, true);
    }
    
    
    public static Bitmap creatFinalPictureTakenBitmap(byte [] jpegData) {
    	BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
		int width = options.outWidth;
		int height = options.outHeight;
		Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
		Matrix matrix = new Matrix();
		Resources resources = MyApplication.getAppContext().getResources();
		
		float finalPicWidth = resources.getDimension(R.dimen.final_picture_width);
		float finalPicHeight = resources.getDimension(R.dimen.final_picture_height);
        matrix.postScale(((float) finalPicWidth) / width, ((float) finalPicHeight) / height);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }
    
    
    
    /**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
    public static Bitmap decodeSampledBitmap(byte [] jpegData, int width,
			int height) {
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		//BitmapFactory.decodeFile(path, options);
		BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);

		// 使用获得到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;
		//Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		Bitmap bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length, options);
		return bitmap;
	}
}
