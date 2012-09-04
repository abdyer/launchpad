package com.dandydev.components.launchpad;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.text.TextUtils;

import com.github.ignition.support.http.IgnitedHttp;
import com.github.ignition.support.http.IgnitedHttpResponse;
import com.github.ignition.support.http.cache.HttpResponseCache;

public class ApiRequest {

	public static final int REQUEST_TYPE_GET = 0;
	public static final int REQUEST_TYPE_PUT = 1;
	public static final int REQUEST_TYPE_POST = 2;
	public static final int REQUEST_TYPE_DELETE = 3;
	
	private IgnitedHttp http;
	private String url;
	private int requestType;
	private HttpEntity payload;
	
	public ApiRequest(Context context, String url, int requestType) {
		this(context, url, requestType, null);
	}
	
	public ApiRequest(Context context, String url, int requestType, String body) {
		this.url = url;
		this.requestType = requestType;
		if(!TextUtils.isEmpty(body))
			this.payload = buildPayload(body);
		http = new IgnitedHttp(context);
		http.enableResponseCache(context, 10, 30, 1,
                HttpResponseCache.DISK_CACHE_INTERNAL);
	}
	
	private HttpEntity buildPayload(String body) {
		StringEntity payload = null;
		try {
			payload = new StringEntity(body);
			payload.setContentType("application/json");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return payload;
	}
	
	public IgnitedHttpResponse execute() throws ConnectException {
		switch(requestType) {
		case REQUEST_TYPE_GET:
			return http.get(url, true).send();
		case REQUEST_TYPE_PUT:
			return http.put(url, payload).send();
		case REQUEST_TYPE_POST:
			return http.post(url, payload).send();
		case REQUEST_TYPE_DELETE:
			return http.delete(url).send();
		default:
			return null;
		}
	}
}
