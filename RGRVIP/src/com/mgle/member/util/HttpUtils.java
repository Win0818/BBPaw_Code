package com.mgle.member.util;

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

/**
 * 闁俺绻冪�鐟扮安閻ㄥ嫯顕Ч鍌濆箯瀵版”son閺佺増宓�
 * @author wangguoqing
 */
public class HttpUtils {
	
	private static final int REQUEST_TIMEOUT = 10*1000;//鐠佸墽鐤嗙拠閿嬬湴鐡掑懏妞�0缁夋帡鎸�
	private static final int SO_TIMEOUT = 10*1000;  //鐠佸墽鐤嗙粵澶婄窡閺佺増宓佺搾鍛閺冨爼妫�0缁夋帡鎸�
	public static HttpParams httpParams;
    
    /** 
     * @param url 閸欐垿锟界拠閿嬬湴閻ㄥ垊RL 
     * @return 閺堝秴濮熼崳銊ユ惙鎼存柨鐡х粭锔胯 
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
        	//閸掓稑缂揌ttpGet鐎电钖勯妴锟�
            HttpGet get = new HttpGet(url);  
            httpParams = new BasicHttpParams();
            //鐠佸墽鐤嗘潻鐐村复鐡掑懏妞傞崪锟絊ocket 鐡掑懏妞傞敍灞间簰閸欙拷Socket 缂傛挸鐡ㄦ径褍鐨�
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            
            //鐠佸墽鐤嗛柌宥呯暰閸氭埊绱濈紓铏规阜娑擄拷true
            HttpClientParams.setRedirecting(httpParams, true);

            //鐠佸墽鐤�user agent
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);

            //閸掓稑缂撴稉锟介嚋 HttpClient鐎圭偘绶�
            //濞夈劍鍓�HttpClient httpClient = new HttpClient(); 閺勭枌ommons HttpClient
            //娑擃厾娈戦悽銊︾《閿涘苯婀�Android 1.5 娑擃厽鍨滄禒顒勬付鐟曚椒濞囬悽锟紸pache 閻ㄥ嫮宸遍惇浣哥杽閻滐拷DefaultHttpClient
            httpClient = new DefaultHttpClient(httpParams);
            HttpClientParams.setCookiePolicy(httpClient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);
            //閸欐垿锟紾ET鐠囬攱鐪�
            httpResponse = httpClient.execute(get);
            
            //婵″倹鐏夐張宥呭閸ｃ劍鍨氶崝鐔锋勾鏉╂柨娲栭崫宥呯安  
            if (httpResponse.getStatusLine().getStatusCode() == 200) {  
                //閼惧嘲褰囬張宥呭閸ｃ劌鎼锋惔鏂跨摟缁楋缚瑕� 
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
     * @param url 閸欐垿锟界拠閿嬬湴閻ㄥ垊RL 
     * @param params 鐠囬攱鐪伴崣鍌涙殶 
     * @return 閺堝秴濮熼崳銊ユ惙鎼存柨鐡х粭锔胯 
     * @throws Exception 
     */ 
    public static String postRequest(String url, Map<String ,String> rawParams,Context ctx) throws Exception 
    {  
    	HttpClient httpClient = null;
    	
        try{  
            HttpPost post = new HttpPost(url);  
            List<NameValuePair> params = new ArrayList<NameValuePair>();  
            for(String key : rawParams.keySet())  
            {  
                params.add(new BasicNameValuePair(key , rawParams.get(key)));  
            }  
            post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));  
            
            httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
            HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
            
            HttpClientParams.setRedirecting(httpParams, true);

            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
            HttpProtocolParams.setUserAgent(httpParams, userAgent);

            httpClient = new DefaultHttpClient(httpParams);
            
            HttpResponse httpResponse = httpClient.execute(post);  
            if (httpResponse.getStatusLine().getStatusCode() == 200)  
            {  
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
     * 閼惧嘲褰囬張宥呭閸ｃ劌娴橀悧锟�     * @param image
     * @return
     */
    public static Bitmap getImage(String path)
	{
    	// 鐎规矮绠熸稉锟介嚋URL鐎电钖�
		URL url;
		Bitmap bitmap = null;
		try
		{
			System.out.println("image_url---"+(path));
			url = new URL(path);
			 //閼惧嘲绶辨潻鐐村复
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            //鐠佸墽鐤嗙搾鍛閺冨爼妫挎稉锟�000濮ｎ偆顬戦敍瀹憃nn.setConnectionTiem(0);鐞涖劎銇氬▽鈩冩箒閺冨爼妫块梽鎰煑
            conn.setConnectTimeout(10000);
            //鐠佸墽鐤嗘禒搴濆瘜閺堥缚顕伴崣鏍ㄦ殶閹诡喛绉撮弮璁圭礄閸楁洑缍呴敍姘嚑缁夋帪绱�
            conn.setReadTimeout(10000);
            //鏉╃偞甯寸拋鍓х枂閼惧嘲绶遍弫鐗堝祦濞达拷
            conn.setDoInput(true);
            //鐠佸墽鐤嗙拠閿嬬湴閺傜懓绱℃稉绡淥ST  
            conn.setRequestMethod("POST");  
            //娑撳秳濞囬悽銊х处鐎涳拷
            conn.setUseCaches(false);
			// 閹垫挸绱戠拠顧睷L鐎电懓绨查惃鍕カ濠ф劗娈戞潏鎾冲弳濞达拷
			InputStream is = conn.getInputStream();
			// 娴犲丢nputStream娑擃叀袙閺嬫劕鍤崶鍓у
			bitmap = BitmapFactory.decodeStream(is);
			//娴ｈ法鏁mageView閺勫墽銇氱拠銉ユ禈閻楋拷
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
     * 閸掋倖鏌囩純鎴犵捕閺勵垰鎯佹潻鐐猴拷 
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