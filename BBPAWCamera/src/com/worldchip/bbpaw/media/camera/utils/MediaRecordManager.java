package com.worldchip.bbpaw.media.camera.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.util.Log;
import android.view.SurfaceHolder;

public class MediaRecordManager {

	private static MediaRecordManager mMediaRecordManager;
	private Context mContext;
	public MediaRecorder mMediaRecorder;
	public String mCurrVideoFile = null;
	private Camera mCamera;
	
	public MediaRecorderState mRecordState = MediaRecorderState.STOPPED;
	private OnInfoListener mInfoListener;
	private OnErrorListener mErrorListener;
	
	
	public enum MediaRecorderState
    {
		STOPPED, RECORDING, PAUSED
    }
	
	private MediaRecordManager(Context context) {
		mContext = context;
	}

	public static synchronized MediaRecordManager getInstance(Context context) {
		if (mMediaRecordManager == null) {
			mMediaRecordManager = new MediaRecordManager(context);
		}
		return mMediaRecordManager;
	}

	public void initializeRecorder() {
		if (mMediaRecorder == null) {
			mMediaRecorder = new MediaRecorder();
		}
		CameraManager cameraManager = CameraManager.getInstance(mContext);
		CamcorderProfile profile = CamcorderProfile.get(cameraManager.getCameraId(), CamcorderProfile.QUALITY_HIGH);
		mCamera = cameraManager.getCamera();
		if (null == mCamera)
			return;
		Camera.Parameters parameters = mCamera.getParameters(); 
		mCamera.unlock();
		mMediaRecorder.setCamera(mCamera);
		mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		mMediaRecorder.setProfile(profile);
		mMediaRecorder.setMaxDuration(0);// 设置为0时，录制时间为无限制
		
		//mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		//mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		//mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
		mCurrVideoFile = Utils.createVideoOuputFile(mContext).getPath();
		mMediaRecorder.setOutputFile(mCurrVideoFile);
		setVideoSizes(mMediaRecorder,parameters);
		mMediaRecorder.setMaxFileSize(Utils.getAvailableSpace());
		if (cameraManager.getCameraId() == CameraConfig.CAMERA_FACING_FRONT) {
			mMediaRecorder.setOrientationHint(180);
		}
		try {
			mMediaRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
			releaseMediaRecorder();
			mCamera.lock();
			Utils.delectTempVideo(mCurrVideoFile);
			return;
		}
		 mMediaRecorder.setOnErrorListener(mErrorListener);
		 mMediaRecorder.setOnInfoListener(mInfoListener);

	}

	
	public void startVideoRecording() {
		
		try {
            mMediaRecorder.start(); 
            mRecordState = MediaRecorderState.RECORDING;
        } catch (RuntimeException e) {
            releaseMediaRecorder();
            mCamera.lock();
            return;
        }
	}
	
	public void stopVideoRecording(){
		if (mMediaRecorder != null) {
			//停止录制之前先将listener设置为空，以防录制调用MediaRecorder的start()与stop()间隔小于1秒程序崩溃
			try {
				mMediaRecorder.setOnErrorListener(null);
				mMediaRecorder.setOnInfoListener(null);
				mRecordState = MediaRecorderState.STOPPED;
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
            	e.printStackTrace();
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	releaseMediaRecorder();
            }
		}
	}
	
	
	public void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
	
	/*public MediaRecorderState getRecordState () {
		
		return mRecordState;
		
	}*/
	
	public void setOnInfoListener(OnInfoListener infoListener) {
		this.mInfoListener = infoListener;
	}
	
	public void setOnErrorListener(OnErrorListener errorListener) {
		this.mErrorListener = errorListener;
	}
	
	public void discardRecording() {
		File file = new File(mCurrVideoFile);
		if (file.exists()) {
			file.delete();
			Log.e("lee", " discardRecording == "+mCurrVideoFile);
		}
	}
	
	public void onUpdateGallery() {
		if (mCurrVideoFile != null) {
			Utils.updateGallery(mContext, mCurrVideoFile);
		}
		
	}
	
	public boolean isRecording() {
		return mRecordState == MediaRecorderState.RECORDING;
	}
	
	private void setVideoSizes(MediaRecorder mediaRecorder, Parameters parameters) {
		Size preSizes = parameters.getPreviewSize();
		if (preSizes == null) {
			return;
		}
		mediaRecorder.setVideoSize(preSizes.width, preSizes.height);
	}
}
