package com.worldchip.bbp.ect.common;

import com.worldchip.bbp.ect.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.util.Log;
import android.view.View;

public class ChickenView extends View{

	/**
	 * ��ť����
	 */
	public static final short DIRECTION_LEFT = 21;
	public static final short DIRECTION_RIGHT = 22;
	
	@SuppressWarnings("unused")
	private Activity activity = null;
	private Bitmap map = null;//����Bitmap����
	private int rx, ry,  minPosition, maxPosition, mv;//С����ʼ���
	private int moveDirection = 0;// �ƶ�����
	private int dirction = DIRECTION_RIGHT; // Ŀǰ����
	private Paint mPaint = new Paint();
	private Bitmap rm[][] = new Bitmap[2][2];//����С������
	private int move_p = 0;				//�ƶ�����

	private boolean isStop = true;
	
	public ChickenView(Context context) 
	{
		super(context);
		moveDirection = DIRECTION_RIGHT;// �ƶ�����
		dirction = DIRECTION_RIGHT; // Ŀǰ����
		activity = (Activity) context;
		createImage();
		setFocusable(true);  //���ÿ��ƽ���
	}
	
	public void setPosition(int x, int y, int offset, int v){
		rx = x;
		ry = y;
		minPosition = rx;
		maxPosition = rx+offset;
		mv=v;
	}
	
	public void start(){
		isStop = false;
		new Thread(new Repaint()).start();
	}
	
	public void stop() {
		isStop = true;
	}
	
	/**
	 * ����С��
	 */
	public void createImage()
	{
		map = Bitmap.createBitmap(56, 51, Config.ARGB_8888);		//����ͼƬ
		Canvas canvas = new Canvas(map);		//��û�ͼ����
		canvas.drawColor(Color.parseColor("#00000000"));			//���ñ���ɫΪ͸��
		
		Resources res = getResources(); 
		
        rm[0][0] = BitmapFactory.decodeResource(res, R.drawable.chicken_one);
		rm[0][1] = BitmapFactory.decodeResource(res, R.drawable.chicken_two);
		rm[1][0] = BitmapFactory.decodeResource(res, R.drawable.chincken_3);
	    rm[1][1] = BitmapFactory.decodeResource(res, R.drawable.chincken_4);
	}
	

	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		if(map !=null)
		{
			canvas.drawBitmap(map, 0,0, mPaint);
			/**
			 * ��ͼ���ƣ�sd�±ꡣ
			 */
			short sd = 0;
			if (dirction == DIRECTION_LEFT)
			{
				sd = 1;
			} else if (dirction == DIRECTION_RIGHT) {
				sd = 0;
			}
		
			/**
			 * ����С��ͼ��
			 */
			int index = move_p % 2;
			switch (index) 
			{
				case 0:
					canvas.drawBitmap(rm[sd][0], rx,ry, mPaint);
					break;
				case 1:
					canvas.drawBitmap(rm[sd][1], rx,ry, mPaint);
					break;
				default:
					break;
			}
		}
	}
	
	private class Repaint implements Runnable{
		
		public void run() 
		{
			while(!isStop)
			{
				/**
				 * С��1�ƶ���
				 */
				if (moveDirection == DIRECTION_LEFT)
				{
					rx -= mv;
					if(rx < minPosition)
					{
						dirction = DIRECTION_RIGHT;
						moveDirection = DIRECTION_RIGHT;
						rx = minPosition; 
					}
				} else if (moveDirection == DIRECTION_RIGHT) {
					rx += mv;
					if(rx > maxPosition)
					{
						dirction = DIRECTION_LEFT;
						moveDirection = DIRECTION_LEFT;
						rx = maxPosition;
					}
				}
				
				/**
				 * ��ͼ���ƣ�move_p�±ꡣ
				 */
				if(move_p == 0)
				{
					move_p ++;
				}else{
					move_p = 0;
				}
				
				//ˢ��
				postInvalidate();
				try {
					Thread.sleep(800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}