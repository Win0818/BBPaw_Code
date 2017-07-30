package com.worldchip.bbpaw.bootsetting.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtils {

    public static final String DEBUG_TAG = "HttpUtils";
    
    private static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
	private static final String TAG = "CHRIS";
	public static HttpParams httpParams;
	public static final String  GET_VERIFY_CODE = "http://push.bbpaw.com.cn/interface/chatBackup/chatBackup.php?param=random_code";


    /**
     * Http post请求
     * @param xml 参数
     * @param callBack 回调
     */
    public static void doPost(final String httpUrl, final HttpResponseCallBack callBack,final String httpTag){
        new Thread(new Runnable(){
            @Override
            public void run() {
            	if (TextUtils.isEmpty(httpUrl)) {
	     	    	   if (callBack != null) {
	     	    		  callBack.onFinish(1, httpTag);
	     	    	   }
	     	    	   return;
	     	       }
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
            url = new URL(httpUrl.trim());
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
                urlConn.setConnectTimeout(Configure.HTTP_CONNECT_TIMEOUT);
                urlConn.setReadTimeout(Configure.HTTP_READ_TIMEOUT);

                // 设置POST方式
                urlConn.setRequestMethod("POST");
                // POST请求不能设置缓存
                urlConn.setUseCaches(false);
                urlConn.setInstanceFollowRedirects(false);
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
                urlConn.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
                // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成
                // 要注意的是connectio.getOutputStream会隐含的进行connect

                // DataOutputStream流
                out = new DataOutputStream(urlConn.getOutputStream());
                urlConn.setReadTimeout(2000);
                out.write(httpUrl.getBytes("UTF-8"));
                urlConn.connect();
                int code = urlConn.getResponseCode();
                //System.out.println("code　 " + code);
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
                		callBack.onSuccess("", httpTag);
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
                    e.printStackTrace();
                }
            }
        } else {
            Log.d(DEBUG_TAG, "URL is NULL");
        }
    }
    
    /** 
     * @param url 发送请求的URL 
     * @return 服务器响应字符串 
     * @throws Exception 
     */  
    public static String getRequest(final String url,Context ctx) throws IOException  
    {  
    	if (TextUtils.isEmpty(url))  {
    		Log.e(TAG, "--------URL为空---------");
    		return null;
    	}
    	HttpResponse httpResponse = null;
    	StringBuffer sb = new StringBuffer();
    	HttpClient httpClient = null;
        try{  
        	//创建HttpGet对象。  
            HttpGet get = new HttpGet(url);  
            httpParams = new BasicHttpParams();
            
            //设置连接超时和 Socket 超时，以及 Socket 缓存大小
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            
            //设置重定向，缺省为 true
            HttpClientParams.setRedirecting(httpParams, true);

            //设置 user agent
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);
            
            httpClient = new DefaultHttpClient(httpParams);
            HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
            
            //发送GET请求
            httpResponse = httpClient.execute(get);
            //如果服务器成功地返回响应  
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
            	Log.e(TAG, "--------服务器连接成功---------");
                //获取服务器响应字符串  
            	sb.append(EntityUtils.toString(httpResponse.getEntity()));
                return sb.toString();  
            }  
        }catch (ClientProtocolException e) {
            e.printStackTrace();
        }catch (IOException e) {
        	e.printStackTrace();
        }catch (Exception e){
        	e.printStackTrace();
        } finally{  
        	if(null != httpClient && null != httpClient.getConnectionManager())
        	{	
        		httpClient.getConnectionManager().shutdown();  
        	}
        }
        return null;  
    }
    
    public static boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        } else {
        	//如果仅仅是用来判断网络连接
        	//则可以使用 cm.getActiveNetworkInfo().isAvailable();  
            NetworkInfo[] info = cm.getAllNetworkInfo();   
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true;   
                    }   
                }   
            }   
        }   
        return false;   
    }
    
    public static boolean isWifiEnabled(Context context) {   
        ConnectivityManager mgrConn = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        TelephonyManager mgrTel = (TelephonyManager) context   
                .getSystemService(Context.TELEPHONY_SERVICE);   
        return ((mgrConn.getActiveNetworkInfo() != null && mgrConn   
                .getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) || mgrTel   
                .getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);   
    }
    
    public static boolean is3rd(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();   
        if (networkINfo != null   
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {   
            return true;   
        }   
        return false;   
    }
    
    public static boolean isWifi(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();   
        if (networkINfo != null   
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {   
            return true;   
        }   
        return false;   
    }
}


