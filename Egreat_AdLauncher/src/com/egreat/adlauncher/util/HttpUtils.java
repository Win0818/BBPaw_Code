package com.egreat.adlauncher.util;

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
 * 閫氳繃瀵瑰簲鐨勮姹傝幏寰桱son鏁版嵁
 * @author wangguoqing
 */
public class HttpUtils {
	
	private static final int REQUEST_TIMEOUT = 10*1000;//璁剧疆璇锋眰瓒呮椂10绉掗挓
	private static final int SO_TIMEOUT = 10*1000;  //璁剧疆绛夊緟鏁版嵁瓒呮椂鏃堕棿10绉掗挓
	public static HttpParams httpParams;
    
    /** 
     * @param url 鍙戦�璇锋眰鐨刄RL 
     * @return 鏈嶅姟鍣ㄥ搷搴斿瓧绗︿覆 
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
        	//鍒涘缓HttpGet瀵硅薄銆� 
            HttpGet get = new HttpGet(url);  
            httpParams = new BasicHttpParams();
            //璁剧疆杩炴帴瓒呮椂鍜�Socket 瓒呮椂锛屼互鍙�Socket 缂撳瓨澶у皬
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            
            //璁剧疆閲嶅畾鍚戯紝缂虹渷涓�true
            HttpClientParams.setRedirecting(httpParams, true);

            //璁剧疆 user agent
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);

            //鍒涘缓涓�釜 HttpClient瀹炰緥
            //娉ㄦ剰 HttpClient httpClient = new HttpClient(); 鏄疌ommons HttpClient
            //涓殑鐢ㄦ硶锛屽湪 Android 1.5 涓垜浠渶瑕佷娇鐢�Apache 鐨勭己鐪佸疄鐜�DefaultHttpClient
            httpClient = new DefaultHttpClient(httpParams);
            HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
            //鍙戦�GET璇锋眰
            httpResponse = httpClient.execute(get);
            
            //濡傛灉鏈嶅姟鍣ㄦ垚鍔熷湴杩斿洖鍝嶅簲  
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
                //鑾峰彇鏈嶅姟鍣ㄥ搷搴斿瓧绗︿覆  
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
     * @param url 鍙戦�璇锋眰鐨刄RL 
     * @param params 璇锋眰鍙傛暟 
     * @return 鏈嶅姟鍣ㄥ搷搴斿瓧绗︿覆 
     * @throws Exception 
     */ 
    public static String postRequest(String url, Map<String ,String> rawParams,Context ctx) throws Exception 
    {  
    	HttpClient httpClient = null;
    	
        try{  
        	//鍒涘缓HttpPost瀵硅薄銆� 
            HttpPost post = new HttpPost(url);  
            //濡傛灉浼犻�鍙傛暟涓暟姣旇緝澶氱殑璇濆彲浠ュ浼犻�鐨勫弬鏁拌繘琛屽皝瑁� 
            List<NameValuePair> params = new ArrayList<NameValuePair>();  
            for(String key : rawParams.keySet())  
            {  
                //灏佽璇锋眰鍙傛暟  
                params.add(new BasicNameValuePair(key , rawParams.get(key)));  
            }  
            //璁剧疆璇锋眰鍙傛暟 
            post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
            
            httpParams = new BasicHttpParams();
            //璁剧疆杩炴帴瓒呮椂鍜�Socket 瓒呮椂锛屼互鍙�Socket 缂撳瓨澶у皬
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            
            //璁剧疆閲嶅畾鍚戯紝缂虹渷涓�true
            HttpClientParams.setRedirecting(httpParams, true);

            //璁剧疆 user agent
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);

            //鍒涘缓涓�釜 HttpClient 瀹炰緥
            //娉ㄦ剰 HttpClient httpClient = new HttpClient();鏄疌ommons HttpClient
            //涓殑鐢ㄦ硶锛屽湪 Android 1.5 涓垜浠渶瑕佷娇鐢�Apache 鐨勭己鐪佸疄鐜�DefaultHttpClient
            httpClient = new DefaultHttpClient(httpParams);
            
            //鍙戦�POST璇锋眰  
            HttpResponse httpResponse = httpClient.execute(post);  
            //濡傛灉鏈嶅姟鍣ㄦ垚鍔熷湴杩斿洖鍝嶅簲  
            if (httpResponse.getStatusLine().getStatusCode() == 200)  
            {  
                //鑾峰彇鏈嶅姟鍣ㄥ搷搴斿瓧绗︿覆  
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
     * 鑾峰彇鏈嶅姟鍣ㄥ浘鐗�
     * @param image
     * @return
     */
    public static Bitmap getImage(String path)
	{
    	// 瀹氫箟涓�釜URL瀵硅薄
		URL url;
		Bitmap bitmap = null;
		try
		{
			System.out.println("image_url---"+(path));
			url = new URL(path);
			 //鑾峰緱杩炴帴
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //璁剧疆瓒呮椂鏃堕棿涓�0000姣锛宑onn.setConnectionTiem(0);琛ㄧず娌℃湁鏃堕棿闄愬埗
            conn.setConnectTimeout(10000);
            //璁剧疆浠庝富鏈鸿鍙栨暟鎹秴鏃讹紙鍗曚綅锛氭绉掞級 
            conn.setReadTimeout(10000);
            //杩炴帴璁剧疆鑾峰緱鏁版嵁娴�
            conn.setDoInput(true);
            //璁剧疆璇锋眰鏂瑰紡涓篜OST  
            conn.setRequestMethod("POST");  
            //涓嶄娇鐢ㄧ紦瀛�
            conn.setUseCaches(false);
			// 鎵撳紑璇RL瀵瑰簲鐨勮祫婧愮殑杈撳叆娴�
			InputStream is = conn.getInputStream();
			// 浠嶪nputStream涓В鏋愬嚭鍥剧墖
			bitmap = BitmapFactory.decodeStream(is);
			//浣跨敤ImageView鏄剧ず璇ュ浘鐗�
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
     * 鍒ゆ柇缃戠粶鏄惁杩為� 
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