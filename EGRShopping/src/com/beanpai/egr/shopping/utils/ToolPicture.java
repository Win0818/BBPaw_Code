package com.beanpai.egr.shopping.utils;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 图片工具类
 * @author 曾繁添
 * @version 1.0
 */
public class ToolPicture {
	
	
	/**
	 * 根据指定内容生成自定义宽高的二维码图片 
	 * @param content 需要生成二维码的内容
	 * @param width 二维码宽度
	 * @param height 二维码高度
	 * @throws WriterException 生成二维码异常
	 */
	public static Bitmap makeQRImage(String content, int width, int height)
			throws WriterException {
		// 判断生成二维码数据合法性
		if (!ToolString.isNoBlankAndNoNull(content))
			return null;

		//二维码参数设置
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		//容错率L=7% M=15% Q=25% H=30% 容错率越高，越容易被快速扫描
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);
		
		// 图像数据转换，使用了矩阵转换
		BitMatrix bitMatrix = new QRCodeWriter().encode(content,
				BarcodeFormat.QR_CODE, width, height, hints);
		int[] pixels = new int[width * height];
		// 按照二维码的算法，逐个生成二维码的图片，两个for循环是图片横列扫描的结果
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (bitMatrix.get(x, y))
					pixels[y * width + x] = 0xff000000;
				else
					pixels[y * width + x] = 0xffffffff;
			}
		}
		// 生成二维码图片的格式，使用ARGB_8888
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		
		return bitmap;
	}
	
	/**
	 * 生成带Logo的二维码
	 * @param strContent 二维码内容
	 * @param qrWH 二维码的宽高
	 * @param mLogoBitmap Logo图片
	 * @param logoWH LOGO的宽高(注意：LOGO的宽高最多只能是二维码图片宽高的0.2，否则生成的二维码无法扫描识别)
	 * @return
	 * @throws WriterException 生成二维码失败异常
	 */
	public static Bitmap makeQRImageWithLogo(String strContent,int qrWH,Bitmap mLogoBitmap,int logoWH) throws WriterException {

		// 判断生成二维码数据合法性
		if (!ToolString.isNoBlankAndNoNull(strContent))
			return null;
		
		//缩放Logo图片
		Matrix logoMatrix = new Matrix();
		float sx = (float) 2 * logoWH / mLogoBitmap.getWidth();
		float sy = (float) 2 * logoWH / mLogoBitmap.getHeight();
		logoMatrix.setScale(sx, sy);
		mLogoBitmap = Bitmap.createBitmap(mLogoBitmap, 0, 0,mLogoBitmap.getWidth(), mLogoBitmap.getHeight(), logoMatrix,false);
		
		//二维码参数设置
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		//容错率L=7% M=15% Q=25% H=30% 容错率越高，越容易被快速扫描
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);

		QRCodeWriter write = new QRCodeWriter();
		try {
			Field menuKeyField = QRCodeWriter.class.getDeclaredField("QUIET_ZONE_SIZE");
			if(menuKeyField != null) {
			    menuKeyField.setAccessible(true);
			    menuKeyField.setInt(write,1);//控制的边框的宽度
			}
		} catch (Exception e) {
			Log.e("cretaeBitmap", "反射设置QRCodeWriter.QUIET_ZONE_SIZE失败，原因："+e.getMessage());
		}
		BitMatrix matrix = write.encode(strContent,BarcodeFormat.QR_CODE,qrWH, qrWH,hints);
//		BitMatrix matrix = new MultiFormatWriter().encode(strContent,BarcodeFormat.QR_CODE, qrWH, qrWH, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		
		// 二维矩阵转为一维像素数组,也就是一直横着排了
		int halfW = width / 2;
		int halfH = height / 2;
		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (x > halfW - logoWH && x < halfW + logoWH && y > halfH - logoWH && y < halfH + logoWH) {
					pixels[y * width + x] = mLogoBitmap.getPixel(x - halfW + logoWH, y - halfH + logoWH);
				} else {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else { 
						//无信息设置像素点为白色
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		// 通过像素数组生成bitmap
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	/**
	 * 获取验证码值
	 * @return 验证码字符串
	 */
	public synchronized static String gainValidateCodeValue(){
		return ValidateCodeGenerator.getCode();
	}
	
	/**
	 * 获取验证码图片
	 * @param width 验证码宽度
	 * @param height 验证码高度
	 * @return 验证码Bitmap对象
	 */
	public synchronized static Bitmap makeValidateCode(int width, int height){
		return ValidateCodeGenerator.createBitmap(width, height);
	}
	
	/**
     * 随机生成验证码内部类
     *
     */
    final static class ValidateCodeGenerator{
    	private static final char[] CHARS = {
    		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
    		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
    		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    	};
    	
    	//default settings
    	private static final int DEFAULT_CODE_LENGTH = 4;
    	private static final int DEFAULT_FONT_SIZE = 20;
    	private static final int DEFAULT_LINE_NUMBER = 3;
    	private static final int BASE_PADDING_LEFT = 5, RANGE_PADDING_LEFT = 10, BASE_PADDING_TOP = 15, RANGE_PADDING_TOP = 10;
    	private static final int DEFAULT_WIDTH = 60, DEFAULT_HEIGHT = 30;
    	
    	//variables
    	private static String value;
    	private static int padding_left, padding_top;
    	private static Random random = new Random();
    	
    	public static Bitmap createBitmap(int width,int height) {
    		padding_left = 0;
    		//创建画布
    		Bitmap bp = Bitmap.createBitmap(width, height, Config.ARGB_8888); 
    		Canvas c = new Canvas(bp);

    		//随机生成验证码字符
    		StringBuilder buffer = new StringBuilder();
    		for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
    			buffer.append(CHARS[random.nextInt(CHARS.length)]);
    		}
    		value = buffer.toString();
    		
    		//设置颜色
    		c.drawColor(Color.WHITE);
    		
    		//设置画笔大小
    		Paint paint = new Paint();
    		paint.setTextSize(DEFAULT_FONT_SIZE);
    		for (int i = 0; i < value.length(); i++) {
    			//随机样式
    			randomTextStyle(paint);
        		padding_left += BASE_PADDING_LEFT + random.nextInt(RANGE_PADDING_LEFT);
        		padding_top = BASE_PADDING_TOP + random.nextInt(RANGE_PADDING_TOP);
    			c.drawText(value.charAt(i) + "", padding_left, padding_top, paint);
    		}
    		for (int i = 0; i < DEFAULT_LINE_NUMBER; i++) {
    			drawLine(c, paint);
    		}
    		//保存  
    		c.save(Canvas.ALL_SAVE_FLAG);
    		c.restore();
    		
    		return bp;
    	}
    	
    	public static String getCode() {
    		return value;
    	}
    	
    	private static void randomTextStyle(Paint paint) {
    		int color = randomColor(1);
    		paint.setColor(color);
    		paint.setFakeBoldText(random.nextBoolean());//true为粗体，false为非粗体
    		float skewX = random.nextInt(11) / 10;
    		skewX = random.nextBoolean() ? skewX : -skewX;
    		paint.setTextSkewX(skewX); //float类型参数，负数表示右斜，整数左斜
    		paint.setUnderlineText(true); //true为下划线，false为非下划线
    		paint.setStrikeThruText(true); //true为删除线，false为非删除线
    	}
    	
    	private static void drawLine(Canvas canvas, Paint paint) {
    		int color = randomColor(1);
    		int startX = random.nextInt(DEFAULT_WIDTH);
    		int startY = random.nextInt(DEFAULT_HEIGHT);
    		int stopX = random.nextInt(DEFAULT_WIDTH);
    		int stopY = random.nextInt(DEFAULT_HEIGHT);
    		paint.setStrokeWidth(1);
    		paint.setColor(color);
    		canvas.drawLine(startX, startY, stopX, stopY, paint);
    	}
    	
    	private static int randomColor(int rate) {
    		int red = random.nextInt(256) / rate;
    		int green = random.nextInt(256) / rate;
    		int blue = random.nextInt(256) / rate;
    		return Color.rgb(red, green, blue);
    	}
    }
}