package com.worldchip.bbp.ect.util;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class DoorRotate3DAnimation extends Animation {

    /** 娌縔杞存鏂瑰悜锛屽姩鐢婚�嗘椂閽堟棆杞�? */ 
    public static final boolean ROTATE_DECREASE = true;  
    /** 娌縔杞存鏂瑰悜锛屽姩鐢婚�?�鏃堕拡鏃嬭�? */  
    public static final boolean ROTATE_INCREASE = false;  
    /** Z杞翠笂鏈�澶ф繁搴�? */  
    public static final float DEPTH_Z = 300.0f;  
    /** 鍥剧墖缈昏浆绫诲�? */ 
    private boolean type;  
    private final float centerX;  
    private final float centerY;  
    private Camera camera;  
    private ApplyTransHalfCallback mHalfCallback = null;
    
    
    public DoorRotate3DAnimation(float cX, float cY, boolean type) {  
        centerX = cX;  
        centerY = cY;  
        this.type = type;  
        setInterpolator(new LinearInterpolator());
        setRepeatCount(0);
    }  
  
    public void initialize(int width, int height, int parentWidth, int parentHeight) {  
    	// 鍦ㄦ瀯閫犲嚱鏁颁箣鍚庛�乬etTransformation()涔嬪墠璋冪敤鏈柟娉曘��?
        super.initialize(width, height, parentWidth, parentHeight);  
        camera = new Camera();  
    }  
  
    public void setOpenDoor() {
    	this.type = ROTATE_DECREASE;
    }
    
    public void setCloseDoor() {
    	this.type = ROTATE_INCREASE;
    }
    
    public void setOnOverHalfListener(ApplyTransHalfCallback callback){
    	mHalfCallback = callback;
    }
    
    
    protected void applyTransformation(float interpolatedTime, Transformation transformation) {  
    	// interpolatedTime:鍔ㄧ敾杩涘害鍊硷紝鑼冨洿涓篬0.0f,1.0f] 
        float from = 0.0f, to = 0.0f;  
        if (type == ROTATE_DECREASE) {  
            from = 0.0f;  
            to = 180.0f;  
        } else if (type == ROTATE_INCREASE) {  
            from = 180.0f;  
            to = 0.0f;  
        }  
        
        float degree = from + (to - from) * interpolatedTime;  
        boolean overHalf = (interpolatedTime > 0.5f);  
        if (overHalf) {
        	if (mHalfCallback != null) {
        		mHalfCallback.onOverHalf(interpolatedTime);
        	}
        }
        float depth = (0.5f - Math.abs(interpolatedTime - 0.5f)) * DEPTH_Z;
        final Matrix matrix = transformation.getMatrix();  
		camera.save();
		camera.translate(0.0f, 0.0f, depth);
		camera.rotateY(degree);
		camera.getMatrix(matrix);
		camera.restore();
		// 纭繚鍥剧墖鐨勭炕杞繃绋嬩竴鐩村浜庣粍浠剁殑涓績鐐逛綅缃�
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY); 
    }  
    
    public interface ApplyTransHalfCallback {
    	public void onOverHalf(float interpolatedTime);
    }
    
    
}
