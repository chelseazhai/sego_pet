package com.richitec.notify;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class Notifier {
	private static Log log = LogFactory.getLog(Notifier.class);

	private static final int timeoutConnection = 10000;
	private static final int timeoutSocket = 20000;

	private String notifyUrl;

	public Notifier() {
		this.notifyUrl = null;
	}

	public Notifier(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	
	public String getNotifyUrl() {
		return notifyUrl;
	}
	
	
	public void setNotifyUrl(String notifyUrl) {
		this.notifyUrl = notifyUrl;
	}

	public void notifyWithHttpPost(String notifyID, String msg) {
		log.info("notify - notify id: " + notifyID + " msg: " + msg);

		if (notifyUrl == null || notifyUrl.equals("")) {
			return;
		}
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

		HttpPost post = new HttpPost(notifyUrl + "/notify");

		NameValuePair[] data = { new BasicNameValuePair("cmd", "notify"),
				new BasicNameValuePair("topic", notifyID),
				new BasicNameValuePair("msg", msg) };
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (NameValuePair nvp : data) {
			params.add(nvp);
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		try {
			httpClient.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
