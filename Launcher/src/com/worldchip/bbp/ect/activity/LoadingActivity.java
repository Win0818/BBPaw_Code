package com.worldchip.bbp.ect.activity;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.util.HttpCommon;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.loading);
		
		HttpCommon.hideSystemUI(LoadingActivity.this,true);
	
		//����Handler��postDelayed�������ȴ�10000������ִ��run������
		//��Activity�����Ǿ�����Ҫʹ��Handler��������UI����ִ��һЩ��ʱ�¼���
		//����Handler��post�����ȿ���ִ�к�ʱ�¼�Ҳ������һЩUI���µ����飬�ȽϺ��ã��Ƽ�ʹ��
		new Handler().postDelayed(new Runnable()
		{
			public void run()
			{	
				//�ȴ�10000��������ٴ�ҳ�棬����ʾ��½�ɹ�
				LoadingActivity.this.finish();
			}
		}, 2000);
   }
}