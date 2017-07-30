package com.worldchip.bbp.ect.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.worldchip.bbp.ect.R;
import com.worldchip.bbp.ect.activity.MyApplication;

public class HttpUtils {

    public static final String DEBUG_TAG = "HttpUtils";
    
    /**
   	 * 网络错误
   	 */
   	public static final int HTTP_RESULT_NETWORK_ERROR = -100;
   	
    /**
	 * 登录,操作,注册等其他失败  输入有错误
	 */
	public static final int HTTP_RESULT_CODE_ERROR = -1;
	/**
	 * 注册,登录,修改,操作成功
	 */
	public static final int HTTP_RESULT_CODE_SUCCESS = 200;
	/**
	 * 用户名不存在
	 */
	public static final int HTTP_RESULT_CODE_NOT_USER = 201;
	/**
	 * 密码不正确
	 */
	public static final int HTTP_RESULT_CODE_PSW_ERROR = 202;
	/**
	 * 邮箱不正确
	 */
	public static final int HTTP_RESULT_CODE_EMAIL_ERROR = 203;
	/**
	 * 旧密码不正确
	 */
	public static final int HTTP_RESULT_ORIGINAL_PSW_ERROR = 208;
	/**
	 * 验证码不正确
	 */
	public static final int HTTP_RESULT_VALIDATE_CODE_ERROR = 209;
	
	/**
	 * 用户名不正确
	 */
	public static final int HTTP_RESULT_ACCOUNT_ERROR = 212;
	public static HttpParams httpParams;
	
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
				if (!isNetworkConnected(MyApplication.getAppContext())) {
        		if (callBack != null) {
                	callBack.onFinish(HTTP_RESULT_NETWORK_ERROR, httpTag);
                	return;
                }
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
                urlConn.setConnectTimeout(20000);
                urlConn.setReadTimeout(20000);

                // 设置POST方式
                urlConn.setRequestMethod("POST");
                // POST请求不能设置缓存
                urlConn.setUseCaches(false);
                urlConn.setInstanceFollowRedirects(false);
                // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
               // urlConn.setRequestProperty("Content-Type", "text/xml;charset=utf-8");
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (callBack != null) {
                	callBack.onFinish(0,httpTag);
                }
            }
        } else {
            Log.d(DEBUG_TAG, "URL is NULL");
        }
    }
    
    public static void handleRequestExcption(int errorCode) {
    	String messageText = "";
    	Context context = MyApplication.getAppContext();
    	switch (errorCode) {
    	case HTTP_RESULT_NETWORK_ERROR:
    		messageText = context.getString(R.string.network_error_text);
    		break;
		case HTTP_RESULT_CODE_NOT_USER:
			messageText = context.getString(R.string.login_not_user_error);
			break;
		case HTTP_RESULT_ACCOUNT_ERROR:
			messageText = context.getString(R.string.account_error_text);
			break;
		case HTTP_RESULT_CODE_PSW_ERROR:
			messageText = context.getString(R.string.login_psw_error);
			break;
		case HTTP_RESULT_ORIGINAL_PSW_ERROR:
			messageText = context.getString(R.string.original_psw_error_text);
			break;
		case HTTP_RESULT_VALIDATE_CODE_ERROR:
			messageText = context.getString(R.string.validate_code_error_text);
			break;
		default:
			messageText = context.getString(R.string.req_excption_error_text);
			break;
		}
		Log.e("lee","handleRequestExcption == "+messageText);
    	if (!TextUtils.isEmpty(messageText)) {
			Utils.showToastMessage(context, messageText);
		}
    }
    /** 
     * @param url 发送请求的URL 
     * @param params 请求参数 
     * @return 服务器响应字符串 
     * @throws Exception 
     */ 
    public static String postRequest(String url, Map<String ,String> rawParams,Context ctx) throws Exception 
    {  
    	HttpClient httpClient = null;
    	
        try{  
        	//创建HttpPost对象。  
            HttpPost post = new HttpPost(url); 
            
            //如果传递参数个数比较多的话可以对传递的参数进行封装  
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            
            for(String key : rawParams.keySet())  
            {  
                //封装请求参数  
                params.add(new BasicNameValuePair(key , rawParams.get(key)));  
            }  
            //设置请求参数 
            post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
            
            httpParams = new BasicHttpParams();
            //设置连接超时和 Socket 超时，以及 Socket 缓存大小
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            
            //设置重定向，缺省为 true
            HttpClientParams.setRedirecting(httpParams, true);

            //设置 user agent
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);

            //创建一个 HttpClient 实例
            //注意 HttpClient httpClient = new HttpClient();是Commons HttpClient
            //中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient
            httpClient = new DefaultHttpClient(httpParams);
            //发送POST请求  
            HttpResponse httpResponse = httpClient.execute(post);  
            //如果服务器成功地返回响应  
            if (httpResponse.getStatusLine().getStatusCode() == 200)  
            {  
                //获取服务器响应字符串  
                String result = EntityUtils.toString(httpResponse.getEntity()); 
                Log.d("Wing", "------====--->>>" + result);
                return result;  
                
            }  
        }catch(Exception e){  
            e.printStackTrace();  
        }finally{  
        	if(null != httpClient && null != httpClient.getConnectionManager())
        		httpClient.getConnectionManager().shutdown();  
        }  
        return null; 
    } 
    
    /** 
     * 判断网络是否连通 
     * @param context 
     * @return 
     */ 
    public static boolean isNetworkConnected(Context context)
    {  
        @SuppressWarnings("static-access")
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);  
        NetworkInfo info = cm.getActiveNetworkInfo();  
        return info != null && info.isConnected();    
    }
    
}


