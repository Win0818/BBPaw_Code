package com.worldchip.bbp.ect.view;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.util.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class SlipButton extends View implements OnTouchListener {

    private float DownX, NowX;// ����ʱ��x,��ǰ��x
    private float btn_on_left = 0;
    private float btn_off_left = 0;

    private boolean NowChoose = false;// ��¼��ǰ��ť�Ƿ��,trueΪ��,flaseΪ�ر�
    private boolean isChecked;
    private boolean OnSlip = false;// ��¼�û��Ƿ��ڻ����ı���
    private boolean isChgLsnOn = false;

    private OnChangedListener ChgLsn;
    private Bitmap bg_on;
    private Bitmap bg_off;
    private Bitmap slip_btn;

    public SlipButton(Context context) 
    {
        super(context);
        init();
    }

    public SlipButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    // ��ʼ��
    private void init()
    {   
    	
    	if(Utils.getLanguageInfo(getContext())==1){
    		bg_on = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_bg_on_cn);
            bg_off = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_bg_cn);
            slip_btn = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_src);
            btn_off_left = bg_off.getWidth() - slip_btn.getWidth() - getResources().getDimension(R.dimen.slipbuttom_off_x);
    	}else{
        bg_on = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_bg_on);
        bg_off = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_bg);
        slip_btn = BitmapFactory.decodeResource(getResources(),R.drawable.togglebutton_src);
        btn_off_left = bg_off.getWidth() - slip_btn.getWidth() - getResources().getDimension(R.dimen.slipbuttom_off_x);
    	}
    	//bg_off = bg_off.createBitmap(bg_off, 150, 150, 100, 100);
        setOnTouchListener(this); // ���ü�����,Ҳ����ֱ�Ӹ�дOnTouchEvent
    }

    // ��ͼ����
    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) 
    {
        super.onDraw(canvas);

        Matrix matrix = new Matrix();
        Paint paint = new Paint();
        float x;

        // ������ǰ�������εı�����ͬ,�ڴ����ж�
        if (NowX < (bg_on.getWidth() / 2)) 
        { 
            x = NowX - slip_btn.getWidth() / 2;
            canvas.drawBitmap(bg_off, matrix, paint);// �����ر�ʱ�ı���
        } else {
            x = bg_on.getWidth() - slip_btn.getWidth() / 2;
            canvas.drawBitmap(bg_on, matrix, paint);// ������ʱ�ı���
        }

        // �Ƿ����ڻ���״̬,
        if (OnSlip) 
        {
        	// �Ƿ񻮳�ָ����Χ,�������α��ܵ���ͷ,����������ж�
            if (NowX >= bg_on.getWidth()) 
            {
                x = bg_on.getWidth() - slip_btn.getWidth() / 2;// ��ȥ�α�1/2�ĳ���...
            } else if (NowX < 0) {
                x = 0;
            } else {
                x = NowX - slip_btn.getWidth() / 2;
            }
        } else {// �ǻ���״̬
        	// ������ڵĿ���״̬���û��α��λ��
            if (NowChoose)
            {
                x = btn_off_left;
                canvas.drawBitmap(bg_on, matrix, paint);// ��ʼ״̬ΪtrueʱӦ�û�����״̬ͼƬ
            } else {
                x = btn_on_left;
            }
        }
        if (isChecked) 
        {
            canvas.drawBitmap(bg_on, matrix, paint);
            x = btn_off_left;
            isChecked = !isChecked;
        }

        // ���α�λ�ý����쳣�ж�...
        if (x < 0) 
        {
            x = 0;
        } else if (x > bg_on.getWidth() - slip_btn.getWidth()) {
            x = bg_on.getWidth() - slip_btn.getWidth();
        }
        canvas.drawBitmap(slip_btn, x + getResources().getDimension(R.dimen.slipbuttom_x), 
        		getResources().getDimension(R.dimen.slipbuttom_top), paint);// �����α�.

    }

    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction()) // ��ݶ�����ִ�д���
        {
            case MotionEvent.ACTION_DOWN:// ����
                if (event.getX() > bg_on.getWidth() || event.getY() > bg_on.getHeight()) 
                {
                    return false;
                }
                OnSlip = true;
                DownX = event.getX();
                NowX = DownX;
                break;

            case MotionEvent.ACTION_MOVE:// ����
                NowX = event.getX();
                boolean LastChoose = NowChoose;

                if (NowX >= (bg_on.getWidth() / 2))
                {
                    NowChoose = true;
                } else {
                    NowChoose = false;
                }

                // ��������˼�����,�͵����䷽��..
                if (isChgLsnOn && (LastChoose != NowChoose)) 
                {
                    ChgLsn.OnChanged(NowChoose);
                }
                break;

            case MotionEvent.ACTION_UP:// �ɿ�
                OnSlip = false;
                break;
            default:
        }
        invalidate();// �ػ��ؼ�
        return true;
    }

    // ���ü�����,��״̬�޸ĵ�ʱ��
    public void setOnChangedListener(OnChangedListener l) 
    {
        isChgLsnOn = true;
        ChgLsn = l;
    }

    public interface OnChangedListener
    {
        abstract void OnChanged(boolean CheckState);
    }

    public void setCheck(boolean isChecked) 
    {
        this.isChecked = isChecked;
        NowChoose = isChecked;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {

        int measuredHeight = measureHeight(heightMeasureSpec);
        int measuredWidth = measureWidth(widthMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measureHeight(int measureSpec)
    {
        return bg_on.getHeight();
    }

    private int measureWidth(int measureSpec) 
    {
        return bg_on.getWidth();
    }
    

}