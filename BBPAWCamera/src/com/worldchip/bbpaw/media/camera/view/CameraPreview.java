package com.worldchip.bbpaw.media.camera.view;

import com.worldchip.bbpaw.media.camera.Interface.CameraInterface;
import com.worldchip.bbpaw.media.camera.activity.CameraActivity;
import com.worldchip.bbpaw.media.camera.utils.CameraConfig;
import com.worldchip.bbpaw.media.camera.utils.CameraManager;
import com.worldchip.bbpaw.media.camera.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

@SuppressLint("NewApi")
public class CameraPreview extends TextureView implements
		TextureView.SurfaceTextureListener {

	private Context mContext;
	private CameraInterface mCallback;
	private Matrix mMatrix = null;
	
	public CameraPreview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture arg0, int width,
			int height) {
		// TODO Auto-generated method stub
		Log.e("lee", "onSurfaceTextureAvailable ------------- ");
		//解决预览时，图像被拉长
		setTransformMatrix(width, height);
		CameraManager cameraManager = CameraManager.getInstance(mContext);
		if (cameraManager != null && !cameraManager.isOpened()) {
			cameraManager.doOpenCamera(mCallback,
					CameraConfig.DEFAULT_CAMERA_FACING);
		} else {
			CameraManager.getInstance(mContext).doStartPreview(this, mCallback);
		}
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
		Log.e("lee", "onSurfaceTextureDestroyed ------------- ");
		CameraManager.getInstance(mContext).doStopCamera();
		return false;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0) {
		// TODO Auto-generated method stub
	}

	public void setCallback(CameraInterface callback) {
		mCallback = callback;
	}
	
	//private float mAspectRatio = 4f / 3f;//宽高比例
	//private float mAspectRatio = 6f / 5f;//宽高比例
	private float mAspectRatio = Utils.getCameraPreviewAspectRatio();//宽高比例
	    
	private void setTransformMatrix(int width, int height) {
        mMatrix = getTransform(mMatrix);
        float scaleX = 1f, scaleY = 1f;
        float scaledTextureWidth, scaledTextureHeight;
        if (width > height) {
            scaledTextureWidth = Math.max(width,
                    (int) (height * mAspectRatio));
            scaledTextureHeight = Math.max(height,
                    (int)(width / mAspectRatio));
        } else {
            scaledTextureWidth = Math.max(width,
                    (int) (height / mAspectRatio));
            scaledTextureHeight = Math.max(height,
                    (int) (width * mAspectRatio));
        }
        scaleX = scaledTextureWidth / width;
        scaleY = scaledTextureHeight / height;
        mMatrix.setScale(scaleX, scaleY, (float) width / 2, (float) height / 2);
        setTransform(mMatrix);
    }
	
	
}
