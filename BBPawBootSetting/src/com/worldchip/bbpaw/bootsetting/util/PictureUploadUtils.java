package com.worldchip.bbpaw.bootsetting.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.text.TextUtils;
import android.util.Log;

public class PictureUploadUtils {

	 public static final String DEBUG_TAG = "PictureUploadUtils";
	

	    /**
	     * Http post请求
	     * @param xml 参数
	     * @param callBack 回调
	     */
	    public static void doPost(final String filePath, final HttpResponseCallBack callBack,final String httpTag){
	    	new Thread(new Runnable(){
	            @Override
	            public void run() {
	            	if (TextUtils.isEmpty(filePath)) {
	     	    	   if (callBack != null) {
	     	    		  callBack.onFinish(1, httpTag);
	     	    	   }
	     	    	   return;
	     	       }
	                refresh(filePath, callBack, httpTag);
	            }
	        }).start();
	    }

	    private static void refresh(String filePath,HttpResponseCallBack callBack,final String httpTag) {
	        StringBuffer resultData = new StringBuffer();
	        URL url = null;
	        String end = "\r\n";
	        String twoHyphens = "--";
	        String boundary = "*****";
	        try {
	        	if (callBack != null) {
	        		callBack.onStart(httpTag);
	        	}
	            // 创建一个URL对象
	            url = new URL(Configure.USER_PIC_UPLOAD_REQ_URL);
	        } catch (MalformedURLException e) {
	            Log.d(DEBUG_TAG, "create URL Exception");
	            if (callBack != null) {
	            	callBack.onFailure(e, httpTag);
	            }
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
	                // 因为这个是POST请求所以要设置为true
	                urlConn.setDoInput(true);
	                urlConn.setDoOutput(true);

	                //设置连接主机及从主机读取数据超时时间
	                urlConn.setConnectTimeout(Configure.HTTP_CONNECT_TIMEOUT);
	                urlConn.setReadTimeout(Configure.HTTP_READ_TIMEOUT);

	                // 设置POST方式
	                urlConn.setRequestMethod("POST");
	                // POST请求不能设置缓存
	                urlConn.setUseCaches(false);
	                urlConn.setInstanceFollowRedirects(false);
	                /* 设置请求属性 */
	                urlConn.setRequestProperty("Connection", "Keep-Alive");
	                urlConn.setRequestProperty("Charset", "UTF-8");
	                urlConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

	                // DataOutputStream流
	                out = new DataOutputStream(urlConn.getOutputStream());
	                out.writeBytes(twoHyphens + boundary + end);
	                out.writeBytes("Content-Disposition: form-data; " +
	                        "name=\"file\";filename=\"" +
	                        splitFileName(filePath) + "\"" + end);
	                out.writeBytes(end);
	                
	                /* 取得文件的FileInputStream */
	                fStream = new FileInputStream(filePath);
	                /* 设置每次写入8192bytes */
	                int bufferSize = 8192;
	                byte[] fileInput = new byte[bufferSize];   //8k
	                int length = -1;
	              /* 从文件读取数据至缓冲区 */
	                while ((length = fStream.read(fileInput)) != -1)
	                {
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
	                    resultData.append(inputLine).append("\n");
	                }
	                // 设置显示取的的内容
	                if (resultData != null && !resultData.equals("")) {
	                	if (callBack != null) {
	                		callBack.onSuccess(resultData.toString(),httpTag);
	                	}
	                } else {
	                	if (callBack != null) {
	                		callBack.onSuccess("",httpTag);
	                	}
	                }
	            } catch (IOException e) {
	            	if (callBack != null) {
	            		callBack.onFailure(e,httpTag);
	            	}
	                e.printStackTrace();
	            } finally {
	                try {
	                    if(out!=null) {
	                        // 刷新DataOutputStream流
	                        out.flush();
	                        // 关闭DataOutputStream流
	                        out.close();
	                    }
		                if (fStream != null) {
		                	 fStream.close();
		                }
	                    // 关闭InputStreamReader
	                    if(in!=null){
	                        in.close();
	                    }
	                    if(urlConn!=null) {
	                        // 关闭URL连接
	                        urlConn.disconnect();
	                    }
	                    if (callBack != null) {
	                    	callBack.onFinish(0,httpTag);
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
	        if (!TextUtils.isEmpty(filePath) && filePath.length() >0) {
	        	int lastIndexOf = filePath.lastIndexOf("/");
	        	if (lastIndexOf != -1) {
	        		fileName = filePath.substring(lastIndexOf + 1, filePath.length());
	        	}
	        }
			return fileName;
		}
}
