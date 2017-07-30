package com.worldchip.bbp.bbpawmanager.cn.utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.worldchip.bbp.bbpawmanager.cn.callbak.HttpResponseCallBack;

public class HttpUtils {

    public static final String DEBUG_TAG = "HttpUtils";


    /**
     * Http post请求
     * @param xml 参数
     * @param callBack 回调
     */
    public static void doPost(final String httpUrl, final HttpResponseCallBack callBack,final String httpTag){
        new Thread(new Runnable(){
            @Override
            public void run() {
                refresh(httpUrl, callBack, httpTag);
            }
        }).start();
    }

    private static void refresh(String httpUrl,HttpResponseCallBack callBack,final String httpTag) {
        StringBuffer resultData = new StringBuffer();
        URL url = null;
        try {
        	if (callBack != null) {
        		callBack.onStart(httpTag);
        	}
            // 创建一个URL对象
            url = new URL(httpUrl);
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
        if (url != null) {
            try {
                // 使用HttpURLConnection打开连接
                urlConn = (HttpURLConnection) url.openConnection();
                // 因为这个是POST请求所以要设置为true
                urlConn.setDoInput(true);
                urlConn.setDoOutput(true);

                //设置连接主机及从主机读取数据超时时间
                urlConn.setConnectTimeout(Utils.HTTP_CONNECT_TIMEOUT);
                urlConn.setReadTimeout(Utils.HTTP_READ_TIMEOUT);

                // 设置POST方式
                urlConn.setRequestMethod("POST");
                // POST请求不能设置缓存
                urlConn.setUseCaches(false);
                urlConn.setInstanceFollowRedirects(false);
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                //urlConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
                // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成
                // 要注意的是connectio.getOutputStream会隐含的进行connect

                // DataOutputStream流
                out = new DataOutputStream(urlConn.getOutputStream());
                urlConn.setReadTimeout(2000);
                out.write(httpUrl.getBytes("UTF-8"));
                urlConn.connect();
                int code = urlConn.getResponseCode();
                System.out.println("code　 " + code);
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
                	if (callBack != null) {
                		callBack.onFailure(e,httpTag);
                	}
                    e.printStackTrace();
                }
            }
        } else {
            Log.d(DEBUG_TAG, "URL is NULL");
        }
    }
}


