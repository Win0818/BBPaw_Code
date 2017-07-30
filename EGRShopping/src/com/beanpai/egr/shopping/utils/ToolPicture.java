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
 * ͼƬ������
 * @author ������
 * @version 1.0
 */
public class ToolPicture {
	
	
	/**
	 * ����ָ�����������Զ����ߵĶ�ά��ͼƬ 
	 * @param content ��Ҫ���ɶ�ά�������
	 * @param width ��ά����
	 * @param height ��ά��߶�
	 * @throws WriterException ���ɶ�ά���쳣
	 */
	public static Bitmap makeQRImage(String content, int width, int height)
			throws WriterException {
		// �ж����ɶ�ά�����ݺϷ���
		if (!ToolString.isNoBlankAndNoNull(content))
			return null;

		//��ά���������
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		//�ݴ���L=7% M=15% Q=25% H=30% �ݴ���Խ�ߣ�Խ���ױ�����ɨ��
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);
		
		// ͼ������ת����ʹ���˾���ת��
		BitMatrix bitMatrix = new QRCodeWriter().encode(content,
				BarcodeFormat.QR_CODE, width, height, hints);
		int[] pixels = new int[width * height];
		// ���ն�ά����㷨��������ɶ�ά���ͼƬ������forѭ����ͼƬ����ɨ��Ľ��
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (bitMatrix.get(x, y))
					pixels[y * width + x] = 0xff000000;
				else
					pixels[y * width + x] = 0xffffffff;
			}
		}
		// ���ɶ�ά��ͼƬ�ĸ�ʽ��ʹ��ARGB_8888
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		
		return bitmap;
	}
	
	/**
	 * ���ɴ�Logo�Ķ�ά��
	 * @param strContent ��ά������
	 * @param qrWH ��ά��Ŀ��
	 * @param mLogoBitmap LogoͼƬ
	 * @param logoWH LOGO�Ŀ��(ע�⣺LOGO�Ŀ�����ֻ���Ƕ�ά��ͼƬ��ߵ�0.2���������ɵĶ�ά���޷�ɨ��ʶ��)
	 * @return
	 * @throws WriterException ���ɶ�ά��ʧ���쳣
	 */
	public static Bitmap makeQRImageWithLogo(String strContent,int qrWH,Bitmap mLogoBitmap,int logoWH) throws WriterException {

		// �ж����ɶ�ά�����ݺϷ���
		if (!ToolString.isNoBlankAndNoNull(strContent))
			return null;
		
		//����LogoͼƬ
		Matrix logoMatrix = new Matrix();
		float sx = (float) 2 * logoWH / mLogoBitmap.getWidth();
		float sy = (float) 2 * logoWH / mLogoBitmap.getHeight();
		logoMatrix.setScale(sx, sy);
		mLogoBitmap = Bitmap.createBitmap(mLogoBitmap, 0, 0,mLogoBitmap.getWidth(), mLogoBitmap.getHeight(), logoMatrix,false);
		
		//��ά���������
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		//�ݴ���L=7% M=15% Q=25% H=30% �ݴ���Խ�ߣ�Խ���ױ�����ɨ��
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);

		QRCodeWriter write = new QRCodeWriter();
		try {
			Field menuKeyField = QRCodeWriter.class.getDeclaredField("QUIET_ZONE_SIZE");
			if(menuKeyField != null) {
			    menuKeyField.setAccessible(true);
			    menuKeyField.setInt(write,1);//���Ƶı߿�Ŀ��
			}
		} catch (Exception e) {
			Log.e("cretaeBitmap", "��������QRCodeWriter.QUIET_ZONE_SIZEʧ�ܣ�ԭ��"+e.getMessage());
		}
		BitMatrix matrix = write.encode(strContent,BarcodeFormat.QR_CODE,qrWH, qrWH,hints);
//		BitMatrix matrix = new MultiFormatWriter().encode(strContent,BarcodeFormat.QR_CODE, qrWH, qrWH, hints);
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		
		// ��ά����תΪһά��������,Ҳ����һֱ��������
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
						//����Ϣ�������ص�Ϊ��ɫ
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(width, height,Bitmap.Config.ARGB_8888);
		// ͨ��������������bitmap
		bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
		return bitmap;
	}
	
	/**
	 * ��ȡ��֤��ֵ
	 * @return ��֤���ַ���
	 */
	public synchronized static String gainValidateCodeValue(){
		return ValidateCodeGenerator.getCode();
	}
	
	/**
	 * ��ȡ��֤��ͼƬ
	 * @param width ��֤����
	 * @param height ��֤��߶�
	 * @return ��֤��Bitmap����
	 */
	public synchronized static Bitmap makeValidateCode(int width, int height){
		return ValidateCodeGenerator.createBitmap(width, height);
	}
	
	/**
     * ���������֤���ڲ���
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
    		//��������
    		Bitmap bp = Bitmap.createBitmap(width, height, Config.ARGB_8888); 
    		Canvas c = new Canvas(bp);

    		//���������֤���ַ�
    		StringBuilder buffer = new StringBuilder();
    		for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
    			buffer.append(CHARS[random.nextInt(CHARS.length)]);
    		}
    		value = buffer.toString();
    		
    		//������ɫ
    		c.drawColor(Color.WHITE);
    		
    		//���û��ʴ�С
    		Paint paint = new Paint();
    		paint.setTextSize(DEFAULT_FONT_SIZE);
    		for (int i = 0; i < value.length(); i++) {
    			//�����ʽ
    			randomTextStyle(paint);
        		padding_left += BASE_PADDING_LEFT + random.nextInt(RANGE_PADDING_LEFT);
        		padding_top = BASE_PADDING_TOP + random.nextInt(RANGE_PADDING_TOP);
    			c.drawText(value.charAt(i) + "", padding_left, padding_top, paint);
    		}
    		for (int i = 0; i < DEFAULT_LINE_NUMBER; i++) {
    			drawLine(c, paint);
    		}
    		//����  
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
    		paint.setFakeBoldText(random.nextBoolean());//trueΪ���壬falseΪ�Ǵ���
    		float skewX = random.nextInt(11) / 10;
    		skewX = random.nextBoolean() ? skewX : -skewX;
    		paint.setTextSkewX(skewX); //float���Ͳ�����������ʾ��б��������б
    		paint.setUnderlineText(true); //trueΪ�»��ߣ�falseΪ���»���
    		paint.setStrikeThruText(true); //trueΪɾ���ߣ�falseΪ��ɾ����
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