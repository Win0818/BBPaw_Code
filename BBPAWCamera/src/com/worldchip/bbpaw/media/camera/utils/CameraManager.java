package com.worldchip.bbpaw.media.camera.utils;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.OrientationEventListener;

import com.worldchip.bbpaw.media.camera.R;
import com.worldchip.bbpaw.media.camera.Interface.CameraInterface;
import com.worldchip.bbpaw.media.camera.Interface.CameraOrientationListener;
import com.worldchip.bbpaw.media.camera.view.CameraPreview;

public class CameraManager implements Camera.PreviewCallback,
		CameraOrientationListener {
	private Camera mCamera;
	//private Camera.Parameters mParams;
	private boolean isPreviewing = false;
	private static CameraManager mCameraManager;
	private Context mContext;
	public int mCameraId = -1;
	private CameraInterface mCallBack;
	private boolean isOpened = false;
	private int mOrientation = OrientationEventListener.ORIENTATION_UNKNOWN;
	private int mDisplayRotation;
	private int mDisplayOrientation;
	private int mCameraDisplayOrientation;
	public interface CamOpenOverCallback {
		public void cameraHasOpened();
	}

	private CameraManager(Context context){
		mContext = context;
		
	}
	public static synchronized CameraManager getInstance(Context context){
		if(mCameraManager == null){
			mCameraManager = new CameraManager(context);
		}
		return mCameraManager;
	}
	
	public Camera getCamera() {
		return mCamera;
	}
	/**打开Camera
	 * @param callback
	 */
	public void doOpenCamera(CameraInterface callback, int facing) {
		if (mCameraId < 0) {
			mCameraId = findCamera(facing);
		}
		try {
			mCamera = Camera.open(mCameraId);
			Log.e("lee", "doOpenCamera  --------------------");
			setOpened(true);
		} catch (RuntimeException e) {
			e.printStackTrace();
			callback.onCameraOpened(false);
			return;
		}
		if (mCamera != null && callback != null) {
			callback.onCameraOpened(true);
			mCallBack = callback;
		} else {
			if ((callback != null))
				callback.onCameraOpened(false);
		}
		
	}
	
	/**
	 * 切换前后置摄像头
	 * @param callback
	 * @param facing
	 */
	
	public void changeCamera(CameraInterface callback,int facing) {
		mCameraId = findCamera(facing);
		try {
			mCamera.stopPreview();//停掉原来摄像头的预览
			mCamera.release();//释放资源
			mCamera = null;//取消原来摄像头
			isPreviewing = false;
			mCamera = Camera.open(mCameraId);
		} catch (RuntimeException e) {
			e.printStackTrace();
			callback.onCameraOpened(false);
			return;
		}
		if (mCamera != null && callback != null) {
			callback.onCameraOpened(true);
		} else {
			if ((callback != null))
				callback.onCameraOpened(false);
		}
		
	}
	
	
	private int findCamera(int facing) {
		int foundId = -1;
		// find the first front facing camera
		int numCams = Camera.getNumberOfCameras();
		for (int camId = 0; camId < numCams; camId++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(camId, info);
			if (facing == CameraConfig.CAMERA_FACING_FRONT) {
				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					foundId = info.facing;
					break;
				}
			} else {
				if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
					foundId = info.facing;
					break;
				}
			}
			
		}
		return foundId;
	}
	
	public static boolean isDoubleCamera() {
		return Camera.getNumberOfCameras() >1 ? true : false;
	}
	
	/**开启预览
	 * @param holder
	 * @param previewRate
	 */
	@SuppressLint("NewApi")
	public void doStartPreview(CameraPreview cameraPreview, CameraInterface callback){
		if(isPreviewing){
			mCamera.stopPreview();
			//return;
		}
		if(mCamera != null){
			Camera.Parameters params = mCamera.getParameters();
			params.setJpegQuality(100);// 照片质量
			Size pictureSize = CameraParameterUtil.getInstance().getPictureSize(params.getSupportedPictureSizes(),
										CameraConfig.CAMERA_PIC_SIZE_WIDTH);
			if (pictureSize != null) {
				params.setPictureSize(pictureSize.width, pictureSize.height);
			}
			Size previewSize = CameraParameterUtil.getInstance().getPreviewSize(params.getSupportedPreviewSizes(), CameraConfig.CAMERA_PIC_SIZE_WIDTH);
			if (previewSize != null) {
				params.setPreviewSize(previewSize.width, previewSize.height);
			}
			List<String> focusModes = params.getSupportedFocusModes();
			if(focusModes.contains("continuous-video")){
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			try {
				setDisplayOrientation();
		    	mCamera.setParameters(params);	
			} catch(RuntimeException e) {
				e.printStackTrace();
				if ((callback != null))
					callback.onError(mContext.getResources().getString(
							R.string.camera_set_parameters_error));
				return;
			}
			try {
				//mCamera.setPreviewCallback(this);
				//mCamera.setPreviewDisplay(holder);
	            
				if (cameraPreview != null) {
					mCamera.setPreviewTexture(cameraPreview.getSurfaceTexture());
				}
				mCamera.startPreview();//开启预览
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			isPreviewing = true;
		}
	}
	/**
	 * 停止预览，释放Camera
	 */
	public void doStopCamera(){
		if(null != mCamera)
		{
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview(); 
			isPreviewing = false; 
			setOpened(false);
			//mCamera.lock();
			mCamera.release();
			mCamera = null;     
		}
	}
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		//Log.e("lee", "onPreviewFrame ---- ");
		if (mCallBack !=null) {
			mCallBack.onPreviewFrame(data);
		}
	}
	public synchronized boolean isOpened() {
		return isOpened;
	}
	public synchronized void setOpened(boolean isOpened) {
		this.isOpened = isOpened;
	}

	public int getCameraId() {
		return mCameraId;
	}

	@Override
	public void onOrientationChanged(int orientation) {
		// TODO Auto-generated method stub
		if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN)
			return;
		mOrientation = CameraUtils.roundOrientation(orientation, mOrientation);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		// TODO Auto-generated method stub

	}
	
	

	
	private void setDisplayOrientation() {
		mDisplayRotation = 0;
		mDisplayOrientation = CameraUtils.getDisplayOrientation(
				mDisplayRotation, mCameraId);
		mCameraDisplayOrientation = mDisplayOrientation;
		// Change the camera display orientation
		if (mCamera != null) {
			Log.e("lee", "setDisplayOrientation == "+mCameraDisplayOrientation);
			mCamera.setDisplayOrientation(mCameraDisplayOrientation);
		}
	}
}
