package com.worldchip.bbp.bbpawmanager.cn.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public class HttpClientHelper {
	private static String TAG = HttpClientHelper.class.getSimpleName();
	private static DefaultHttpClient mHttpClient = null;
	private static final int SO_TIMEOUT = 30000;
	private static final int CONNECT_TIMEOUT = 5000;
	private static final boolean DEBUG = false;

	/**
	 * 
	 * @param url
	 * @param headers
	 * @param pairs
	 * @param cookies
	 * @param soTimeout
	 * @return
	 */
	public static HttpResult get(String url, Header[] headers,
			NameValuePair[] pairs, Cookie[] cookies, int soTimeout) {
		
		mHttpClient = initHttpClient();
		mHttpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);
		HttpGet get = new HttpGet();
		mHttpClient.setRedirectHandler(new DefaultRedirectHandler());
		try {
			if (pairs != null && pairs.length > 0) {
				StringBuilder sb = new StringBuilder("?");
				for (int i = 0; i < pairs.length; i++) {
					if (i > 0) {
						sb.append("&");
					}
					sb.append(String.format("%s=%s", pairs[i].getName(),
							pairs[i].getValue()));
				}
				url = url + sb.toString();
			}
			if(DEBUG)Log.d(TAG, "HttpClientHelper....get url=" + url);
			get.setURI(new URI(url));

			if (headers != null && headers.length > 0) {
				get.setHeaders(headers);
			}

			if (cookies != null && cookies.length > 0) {
				BasicCookieStore cookieStore = new BasicCookieStore();
				cookieStore.addCookies(cookies);
				mHttpClient.setCookieStore(cookieStore);
			} else {
				mHttpClient.getCookieStore().clear();
			}
			HttpResponse response = mHttpClient.execute(get);
			return new HttpResult(response, mHttpClient.getCookieStore());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.abort();
		}
		return null;
	}

	public static HttpResult get(String url, Header[] headers) {
		return get(url, headers, null, null, SO_TIMEOUT);
	}

	public static HttpResult get(String url, Header[] headers,
			NameValuePair[] pairs) {
		return get(url, headers, pairs, null, SO_TIMEOUT);
	}

	public static HttpResult get(String url, NameValuePair[] pairs) {
		return get(url, null, pairs, null, SO_TIMEOUT);
	}

	public static HttpResult get(String url) {
		return get(url, null, null, null, SO_TIMEOUT);
	}

	public static HttpResult post(String url, Header[] headers,
			NameValuePair[] pairs, Cookie[] cookies, int soTimeout) {
//		Log.d(TAG, " post url=" + url);
		mHttpClient = initHttpClient();
		mHttpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
				soTimeout);
		HttpPost post = new HttpPost(url);
		try {
			// get 请求设置参数
			if (pairs != null && pairs.length > 0) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for (NameValuePair nameValuePair : pairs) {
					formParams.add(nameValuePair);
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
						formParams, HTTP.UTF_8);
				post.setEntity(entity);
			}
			if (headers != null && headers.length > 0) {
				post.setHeaders(headers);
			}
			if (cookies != null && cookies.length > 0) {
				BasicCookieStore cookieStore = new BasicCookieStore();
				cookieStore.addCookies(cookies);
				mHttpClient.setCookieStore(cookieStore);
			} else {
				mHttpClient.getCookieStore().clear();
			}
			HttpResponse response = mHttpClient.execute(post);
			return new HttpResult(response, mHttpClient.getCookieStore());

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.abort();
		}
		return null;
	}

	public static HttpResult post(String url, Header[] headers,
			NameValuePair[] pairs) {
		return post(url, headers, pairs, null, SO_TIMEOUT);
	}

	public static HttpResult post(String url, Header[] headers,
			NameValuePair[] pairs, Cookie[] cookies) {
		return post(url, headers, pairs, cookies, SO_TIMEOUT);
	}

	public static HttpResult post(String url, Header[] headers) {
		return post(url, headers, null, null, SO_TIMEOUT);
	}

	public static HttpResult post(String url, NameValuePair[] pairs) {
		return post(url, null, pairs, null, SO_TIMEOUT);
	}

	/**
	 * @param headers
	 * @param pairs
	 * @return
	 */
	private static String getResultRedirecUrl(String url, Header[] headers,
			NameValuePair[] pairs) {
		HttpURLConnection conn = null ;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			if(headers!=null&&headers.length>0){
				for (Header header : headers) {
					conn.setRequestProperty(header.getName(), header.getValue()) ;
				}
			}
			return conn.getURL().toString() ;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(conn!=null) 
				conn.disconnect();
		}

		return url;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public static DefaultHttpClient initHttpClient() {
		if (mHttpClient == null) {
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setHttpElementCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams
					.setUserAgent(
							params,
							"Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) "
									+ "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
			ConnManagerParams.setTimeout(params, 2000);
			HttpConnectionParams.setConnectionTimeout(params, CONNECT_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
			SchemeRegistry schReg = new SchemeRegistry();
			schReg.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			schReg.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
					params, schReg);
			mHttpClient = new DefaultHttpClient(conMgr, params);
			//client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(2, true)) ;
		}
		return mHttpClient;
	}

	/**
	 * 
	 * @param params
	 * @return
	 */
	public static NameValuePair[] mapToPairs(Map<String, String> params) {
		Set<String> keySet = params.keySet();
		if (keySet == null || keySet.size() == 0) {
			return null;
		}
		String[] keyArray = keySet.toArray(new String[0]);
		NameValuePair[] pairs = new NameValuePair[keyArray.length];
		for (int i = 0; i < keyArray.length; i++) {
			pairs[i] = new BasicNameValuePair(keyArray[i],
					params.get(keyArray[i]));
		}
		return pairs;
	}

	/**
	 * 
	 * 
	 */
	static class DynamicRedirectHandler extends DefaultRedirectHandler {
		@Override
		public boolean isRedirectRequested(HttpResponse response,
				HttpContext context) {
			return false;
		}
	}

}
