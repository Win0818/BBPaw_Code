package com.egreat.adlauncher.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;
import android.util.Log;

public class HttpResult {

	private static final String TAG = HttpResult.class.getSimpleName();

	private static final boolean DEBUG = true;

	private Cookie[] cookies; // cookie
	private Header[] headers;
	private byte[] response;
	private int statuCode = -1;

	public HttpResult(HttpResponse httpResponse, CookieStore cookieStore) {
		if (cookieStore != null) {
			this.cookies = cookieStore.getCookies().toArray(new Cookie[0]);
		}

		if (httpResponse != null) {
			this.headers = httpResponse.getAllHeaders();
			this.statuCode = httpResponse.getStatusLine().getStatusCode();
			if(DEBUG)Log.d(TAG, "response status code=this.statuCode");
			try {
				this.response = EntityUtils.toByteArray(httpResponse.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public Cookie[] getCookies() {
		return cookies;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public Header getHeader(String name) {
		if (this.headers == null || this.headers.length == 0) {
			return null;
		}
		for (int i = 0; i < headers.length; i++) {
			if (headers[i].getName().equalsIgnoreCase(name)) {
				return headers[i];
			}
		}
		return null;
	}

	public String getHtml() {
		try {
			return getText(HTTP.UTF_8);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
	}

	public String getHtml(String encoding) {
		return getText(encoding);
	}

	public byte[] getResponse() {
		if (this.response == null) {
			return null;
		}
		return Arrays.copyOf(this.response, this.response.length);
	}

	public int getStatuCode() {
		return this.statuCode;
	}

	public String getText(String encoding) {
		if (this.response == null) {
			return null;
		}
		if (TextUtils.isEmpty(encoding)) {
			encoding = "utf-8";
		}
		try {
			return new String(this.response, encoding);
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public HttpResult(HttpResponse httpResponse) {
		new HttpResult(httpResponse, null);
	}

	public Cookie getCookie(String name) {
		if (cookies == null || cookies.length == 0) {
			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookie.getName().equalsIgnoreCase(name)) {
				return cookie;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "HttpResult [cookies=" + Arrays.toString(cookies) + ", headers="
				+ Arrays.toString(headers) + ", response=" + getText("utf-8")
				+ ", statuCode=" + statuCode + "]";
	}

}
