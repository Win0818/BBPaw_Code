package com.worldchip.bbp.bbpawmanager.cn.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

/**
 * 通过对应的请求获得Json数据
 * @author wangguoqing
 */
public class HttpUtils {
	
	private static final int REQUEST_TIMEOUT = 10*1000;//设置请求超时10秒钟
	private static final int SO_TIMEOUT = 10*1000;  //设置等待数据超时时间10秒钟
	public static HttpParams httpParams;
    
    /** 
     * @param url 发送请求的URL 
     * @return 服务器响应字符串 
     * @throws Exception 
     */  
    public static String getRequest(final String url,Context ctx) throws IOException  
    {  
    	if (TextUtils.isEmpty(url))  {
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

            //创建一个 HttpClient实例
            //注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient
            //中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient
            httpClient = new DefaultHttpClient(httpParams);
            HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
            //发送GET请求
            httpResponse = httpClient.execute(get);
            
            //如果服务器成功地返回响应  
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
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
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
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
     * 获取服务器图片
     * @param image
     * @return
     */
    public static Bitmap getImage(String path)
	{
    	// 定义一个URL对象
		URL url;
		Bitmap bitmap = null;
		try
		{
			System.out.println("image_url---"+(path));
			url = new URL(path);
			 //获得连接
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //设置超时时间为30000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(10000);
            //设置从主机读取数据超时（单位：毫秒） 
            conn.setReadTimeout(10000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //设置请求方式为POST  
            conn.setRequestMethod("POST");  
            //不使用缓存
            conn.setUseCaches(false);
			// 打开该URL对应的资源的输入流
			InputStream is = conn.getInputStream();
			// 从InputStream中解析出图片
			bitmap = BitmapFactory.decodeStream(is);
			//使用ImageView显示该图片
			is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}
    
    public static void deleteFilesByDirectory(File directory) {  
        if (directory != null && directory.exists() && directory.isDirectory()) {  
            for (File item : directory.listFiles()) {  
                item.delete();  
            }  
        }  
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
    
    
public static String getContent(String url, Header[] headers, NameValuePair[] pairs ) {
		
		String content = null;
		HttpResult result = null;
		result = HttpClientHelper.get(url, headers, pairs);
		if (result != null && result.getStatuCode() == HttpStatus.SC_OK) {
			try {
				content = result.getHtml();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return content;
	}


	public static String getFileNameForUrl(String url) {
		String fileName = "";
		if (url != null && !TextUtils.isEmpty(url)) {
			int lastIndexOf = url.trim().lastIndexOf("/");
			if (lastIndexOf > -1) {
				fileName = url.substring(lastIndexOf+1, url.length());
			}
		}
		return fileName;
	}
	
	
	
}