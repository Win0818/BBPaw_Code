package com.worldchip.bbp.bbpawmanager.cn.callbak;

import com.worldchip.bbp.bbpawmanager.cn.view.CustomSeekBar;

public interface OnCustomSeekBarChangeListener {
	public void onStopTrackingTouch(CustomSeekBar seekbar);
	public void onStartTrackingTouch(CustomSeekBar seekbar);
	public void onProgressChanged(CustomSeekBar seekbar, int postion);
}
