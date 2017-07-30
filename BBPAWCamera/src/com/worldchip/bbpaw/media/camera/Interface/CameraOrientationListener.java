package com.worldchip.bbpaw.media.camera.Interface;

import android.content.res.Configuration;

public interface CameraOrientationListener {
	
	public void onOrientationChanged(int orientation);

	public void onConfigurationChanged(Configuration config);
}
