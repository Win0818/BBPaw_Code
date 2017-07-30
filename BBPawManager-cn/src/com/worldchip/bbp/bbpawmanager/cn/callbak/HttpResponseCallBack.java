package com.worldchip.bbp.bbpawmanager.cn.callbak;

public interface HttpResponseCallBack {

	void onStart(String httpTag);
	
	void onSuccess(String result,String httpTag);

    void onFailure(Exception e,String httpTag);

    void onFinish(int result,String httpTag);
}
