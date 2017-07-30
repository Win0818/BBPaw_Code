package com.worldchip.bbpawphonechat.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.methods.HttpPost;

import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbpawphonechat.comments.MyComment;

public class PictureUploadUtils {

	public static final String DEBUG_TAG = "CHRIS";
	private static IOException e;

	/**
	 * Http post请求
	 * 
	 * @param xml
	 *            参数
	 * @param callBack
	 *            回调
	 */
	public static void doPost(final String filePath,
			final HttpResponseCallBack callBack, final String httpTag,
			final String undateUrl) {
		Log.i("CHRIS", "-----PictureUploadUtils---doPost-----undateUrl-=----"
				+ undateUrl);
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (TextUtils.isEmpty(filePath)) {
					if (callBack != null) {
						callBack.onFinish(1, httpTag);
					}
					return;
				}
				refresh(filePath, callBack, httpTag, undateUrl);
			}
		}).start();
	}

	private static void refresh(String filePath, HttpResponseCallBack callBack,
			final String httpTag, String undateUrl) {
		StringBuffer resultData = new StringBuffer();
		URL url = null;
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "----WebKitFormBoundaryT1HoybnYeFOGFlBR";
		try {
			url = new URL(undateUrl);
			HttpPost post =  new HttpPost();
			System.out.println(url);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			if (callBack != null) {
				callBack.onFailure(e, httpTag);
			}
		}
		if (callBack != null) {
			callBack.onStart(httpTag);

		}
		// 声明HttpURLConnection对象
		HttpURLConnection urlConn = null;
		// 声明InputStreamReader对象
		InputStreamReader in = null;
		// 声明BufferedReader对象
		BufferedReader buffer = null;
		String inputLine = null;
		// 声明DataOutputStream流
		DataOutputStream out = null;
		FileInputStream fStream = null;
		if (url != null) {
			try {
				// 使用HttpURLConnection打开连接
				urlConn = (HttpURLConnection) url.openConnection();
				
				Log.i("CHRIS", "-------refresh-----urlConn----------" + urlConn);
				
				// 设置连接主机及从主机读取数据超时时间
				urlConn.setConnectTimeout(MyComment.HTTP_CONNECT_TIMEOUT);
				urlConn.setReadTimeout(MyComment.HTTP_READ_TIMEOUT);
                
				// 设置POST方式
				urlConn.setRequestMethod("POST");
				// POST请求不能设置缓存
				urlConn.setUseCaches(false);
				urlConn.setInstanceFollowRedirects(false);
				
				urlConn.setRequestProperty("ser-Agent", "Fiddler");
				

				// 设置请求属性
				urlConn.setRequestProperty("Connection", "Keep-Alive");
				urlConn.setRequestProperty("Charset", "utf-8");
				urlConn.setRequestProperty("Content-Type",
						"multipart/form-data;boundary=" + boundary);
				// 因为这个是POST请求所以要设置为true
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);

				// DataOutputStream流
				out = new DataOutputStream(urlConn.getOutputStream());
				out.writeBytes(twoHyphens + boundary + end);
				out.writeBytes("Content-Disposition: form-data; "
						+ "name=\"file\";filename=\"" + splitFileName(filePath)
						+ "\"" + end);
				out.writeBytes(end);

				/* 取得文件的FileInputStream */
				fStream = new FileInputStream(filePath);
				/* 设置每次写入8192bytes */
				int bufferSize = 8192;
				byte[] fileInput = new byte[bufferSize]; // 8k
				int length = -1;
				/* 从文件读取数据至缓冲区 */
				while ((length = fStream.read(fileInput)) != -1) {
					/* 将资料写入DataOutputStream中 */
					out.write(fileInput, 0, length);
				}
				out.writeBytes(end);
				out.writeBytes(twoHyphens + boundary + twoHyphens + end);

				fStream.close();
				/* 关闭DataOutputStream */
				out.close();
				urlConn.connect();

				// 得到读取的内容(流)
				in = new InputStreamReader(urlConn.getInputStream());
				// 创建BufferReader对象，输出时候用到
				buffer = new BufferedReader(in);
				// 使用循环来读取数据
				while ((inputLine = buffer.readLine()) != null) {
					// 在每一行后面加上换行
					resultData.append(inputLine);
				}
				// 设置显示取的的内容
				if (resultData != null && !resultData.equals("")) {
					if (callBack != null) {
						callBack.onSuccess(resultData.toString(), httpTag);
					}
				} else {
					if (callBack != null) {
						callBack.onSuccess("", httpTag);
					}
				}
			} catch (IOException e) {
				if (callBack != null) {
					callBack.onFailure(e, httpTag);
					Log.i(DEBUG_TAG,"--------onFailure---------" + e.getMessage());
				}
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						// 刷新DataOutputStream流
						out.flush();
						// 关闭DataOutputStream流
						out.close();
					}
					if (fStream != null) {
						fStream.close();
					}
					// 关闭InputStreamReader
					if (in != null) {
						in.close();
					}
					if (urlConn != null) {
						// 关闭URL连接
						urlConn.disconnect();
					}
					if (callBack != null) {
						callBack.onFinish(0, httpTag);
					}
				} catch (IOException e) {
					e.printStackTrace();

				}
			}
		} else {
			Log.d(DEBUG_TAG, "URL is NULL");
		}
	}

	public static String splitFileName(String filePath) {
		String fileName = "";
		if (!TextUtils.isEmpty(filePath) && filePath.length() > 0) {
			int lastIndexOf = filePath.lastIndexOf("/");
			if (lastIndexOf != -1) {
				fileName = filePath.substring(lastIndexOf + 1,
						filePath.length());
			}
		}
		return fileName;
	}

	public interface HttpResponseCallBack {

		public void onStart(String httpTag);

		public void onSuccess(String result, String httpTag);

		public void onFinish(int result, String httpTag);

		public void onFailure(IOException e, String httpTag);
	}

}
