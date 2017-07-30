package com.worldchip.bbpaw.media.camera.utils;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;

public class CameraUtils {
	
	public static final int ORIENTATION_HYSTERESIS = 5;
	
	public static int roundOrientation(int orientation, int orientationHistory) {
        boolean changeOrientation = false;
        if (orientationHistory == OrientationEventListener.ORIENTATION_UNKNOWN) {
            changeOrientation = true;
        } else {
            int dist = Math.abs(orientation - orientationHistory);
            dist = Math.min( dist, 360 - dist );
            changeOrientation = ( dist >= 45 + ORIENTATION_HYSTERESIS );
        }
        if (changeOrientation) {
            return ((orientation + 45) / 90 * 90) % 360;
        }
        return orientationHistory;
    }
	
	  public static int getJpegRotation(int cameraId, int orientation) {
	        // See android.hardware.Camera.Parameters.setRotation for
	        // documentation.
	        int rotation = 0;
	        if (orientation != OrientationEventListener.ORIENTATION_UNKNOWN) {
	            CameraInfo info = new CameraInfo();
	            Camera.getCameraInfo(cameraId, info);
	            if (info.facing == CameraConfig.CAMERA_FACING_FRONT) {
	            	Log.e("lee", "getJpegRotation info.orientation == "+info.orientation +" orientation == "+orientation+" cameraId == "+cameraId);
	                rotation = (info.orientation - orientation + 360) % 360;
	            } else {  // back-facing camera
	                rotation = (info.orientation + orientation) % 360;
	            }
	        }
	        return rotation;
	    }
	  
	 public static int getDisplayRotation(Activity activity) {
	        int rotation = activity.getWindowManager().getDefaultDisplay()
	                .getRotation();
	        switch (rotation) {
	            case Surface.ROTATION_0: return 0;
	            case Surface.ROTATION_90: return 90;
	            case Surface.ROTATION_180: return 180;
	            case Surface.ROTATION_270: return 270;
	        }
	        return 0;
	    }
	 
	 public static int getDisplayOrientation(int degrees, int cameraId) {
	        // See android.hardware.Camera.setDisplayOrientation for
	        // documentation.
	        Camera.CameraInfo info = new Camera.CameraInfo();
	        Camera.getCameraInfo(cameraId, info);
	        int result;
	        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
	            result = (info.orientation + degrees) % 360;
	            result = (360 - result) % 360;  // compensate the mirror
	        } else {  // back-facing
	            result = (info.orientation - degrees + 360) % 360;
	        }
	        return result;
	    }
}
