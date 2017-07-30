package com.worldchip.bbp.ect.util;

public interface HttpResponseCallBack {

	void onStart(String httpTag);
	
	void onSuccess(String result,String httpTag);

    void onFailure(Exception e,String httpTag);

    void onFinish(int result,String httpTag);
}
