package com.beanpai.egr.shopping.view;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.beanpai.egr.shopping.entity.CommodityDetailInfo;
import com.beanpai.egr.shopping.entity.MemberInfo;
import com.beanpai.egr.shopping.utils.Common;
import com.beanpai.egr.shopping.utils.ToolPicture;
import com.google.zxing.WriterException;
import com.mgle.shopping.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import cn.trinea.android.common.entity.HttpRequest;
import cn.trinea.android.common.entity.HttpResponse;
import cn.trinea.android.common.util.HttpUtils;

/**
 * 二维码支付
 * @author WGQ
 */
public class QRCodeView extends FrameLayout {

	private Context mContext;
	private Handler mHandler;
	
	@SuppressWarnings("unused")
	private MemberInfo mMemberInfo;
	private CommodityDetailInfo mCommdityInfo;
	private String QRCodeUrl =  null;
	private String selectStatusUrl = null;
	
	private Bitmap qrImage,validateCodeImage;
	
	private ImageView mImgQrcodeSrc;
	
	@SuppressLint("HandlerLeak")
	private Handler mSuccessHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
				case 0:
					mSuccessHandler.postDelayed(runnable,180000);//每30秒执行一次runnable.
					break;
				case 1:
					mSuccessHandler.removeCallbacks(runnable);
					recovery();
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	public QRCodeView(Context context,Handler mHandler,MemberInfo mMemberInfo,CommodityDetailInfo mCommdityInfo) 
	{
		super(context);
		this.mContext = context;
		this.mHandler = mHandler;
		this.mMemberInfo = mMemberInfo;
		this.mCommdityInfo = mCommdityInfo;
		
		initView();
	}

	//初始化布局
	@SuppressLint("InflateParams")
	private void initView()
	{
		View localView = LayoutInflater.from(mContext).inflate(R.layout.qrcode_view,null);
		addView(localView);
		
		mImgQrcodeSrc = (ImageView) localView.findViewById(R.id.img_qrcode_src);
		
		initData();
	}

	private void initData() 
	{
		QRCodeUrl = "http://101.200.173.180:8390/pgw/api/pay/wx/UnifiedOrder?appid=wx2d8cbf4f8fc49601&body=" + mCommdityInfo.name + "&total_fee=" + mCommdityInfo.total_fee + "&orderNumbers=" + mCommdityInfo.id + "&detail=" + mCommdityInfo.name;
		Log.e("QRCodeUrl", "QRCodeUrl="+QRCodeUrl);
		new DataTask().execute();
	}

	private class DataTask extends AsyncTask<Void, Void, String> 
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(Void... params) 
		{
			String result = null;
			HttpRequest request = new HttpRequest(QRCodeUrl);
			HttpResponse respone = HttpUtils.httpGet(request);
			if (respone.getResponseCode() == 200)
			{
				result = respone.getResponseBody();
			}	
			Log.e("result", "result="+result);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			try {
				JSONTokener jsonParser = new JSONTokener(result);
				JSONObject obj = (JSONObject) jsonParser.nextValue();
				int res = Integer.parseInt(obj.getString("result"));
				if(res == 0)
				{
					JSONTokener json = new JSONTokener(obj.getString("resultObj"));
					JSONObject ob = (JSONObject) json.nextValue();
					//回收bitmap
					if(null != qrImage && !qrImage.isRecycled())
					{
						qrImage.recycle();
						qrImage = null;
					}
					selectStatusUrl = ob.getString("selectStatusUrl");
					qrImage = ToolPicture.makeQRImage(ob.getString("payUrl"), 300, 300);
					mImgQrcodeSrc.setImageBitmap(qrImage);
					mSuccessHandler.sendEmptyMessage(0);
				}else{
					Toast.makeText(mContext, obj.getString("description"), Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (WriterException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Runnable runnable = new Runnable()
	{
		@Override
		public void run() 
		{
			new Thread() 
			{
				@Override
				public void run() 
				{
					String result = "";
					Log.e("selectStatusUrl", "selectStatusUrl="+selectStatusUrl);
					HttpRequest request = new HttpRequest(selectStatusUrl);
					HttpResponse respone = HttpUtils.httpGet(request);
					if (respone.getResponseCode() == 200)
					{
						try {
							result = respone.getResponseBody();
							JSONTokener jsonParser = new JSONTokener(result);
							JSONObject obj = (JSONObject) jsonParser.nextValue();
							String res = obj.getString("message");
							Log.e("res", "res="+res);
							if(res.equals("订单状态:已支付"))
							{
								mHandler.sendEmptyMessage(Common.PURCHASE_COMPLETED);
								mSuccessHandler.sendEmptyMessage(1);
							}else {
								mHandler.sendEmptyMessage(Common.CLOSE_POPUPWINDOW);
								mSuccessHandler.sendEmptyMessage(1);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	};
	
	private void recovery()
	{
		//回收bitmap
		if(null != qrImage && !qrImage.isRecycled())
		{
			qrImage.recycle();
			qrImage = null;
		}
		
		if(null != validateCodeImage && !validateCodeImage.isRecycled()){
			validateCodeImage.recycle();
			validateCodeImage = null;
		}
		System.gc();
	}
}