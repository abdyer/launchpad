package com.dandydev.components.launchpad;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.ignition.core.tasks.IgnitedAsyncTask;
import com.github.ignition.support.http.IgnitedHttpResponse;
import com.github.ignition.support.http.cache.CachedHttpResponse;
import com.google.gson.Gson;

public class HttpTask<T> extends IgnitedAsyncTask<Context, Void, Void, T> {

	private ApiRequest request;
	private View progressView;
	private HttpTaskListener listener;
	private Class<T> type;
	private Callback<T> callback;
	
	public HttpTask(Context context, ApiRequest request, Class<T> type, Callback<T> callback) {
		this(context, request, type, null, callback);
	}

	public HttpTask(Context context, ApiRequest request, Class<T> type, View progressView, Callback<T> callback) {
		this.request = request;
		this.type = type;
		this.progressView = progressView;
		this.callback = callback;
		connect(context);
	}
	
	public void setListener(HttpTaskListener listener) {
		this.listener = listener;
	}

	@Override
	public void onPreExecute() {
		if(progressView != null)
			progressView.setVisibility(View.VISIBLE);
		if(listener != null)
			listener.onPreExecute();
	}

	@Override
	public T run(Void... params) throws Exception {
		Gson gson = new Gson();
		IgnitedHttpResponse response = request.execute();
		Log.d("HttpCache", Boolean.toString(response instanceof CachedHttpResponse));
		return gson.fromJson(response.getResponseBodyAsString(), type);
	}
	
	@Override
	public void onPostExecute(T response) {
		if(progressView != null)
			progressView.setVisibility(View.GONE);
		if(listener != null)
			listener.onPostExecute();
		callback.run(response);
	}

	@Override
	public boolean onTaskFailed(Context context, Exception error) {
		super.onTaskFailed(context, error); // prints a stack trace
		Toast.makeText(context, context.getText(R.string.server_error), Toast.LENGTH_LONG).show();
		return true;
	}
	
	public interface Callback<T> {
	    void run(T result);
	}
	
	public interface HttpTaskListener {
		public void onPreExecute();
		public void onPostExecute();
	}
}
