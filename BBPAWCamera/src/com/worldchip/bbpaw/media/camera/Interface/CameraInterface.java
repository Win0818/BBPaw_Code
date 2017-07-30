package com.worldchip.bbpaw.media.camera.Interface;

public interface CameraInterface {

	public void onCameraOpened(boolean isOpen);
	
	public void onError(String exception);
	
	public void onPreviewFrame(byte[] data);
}
