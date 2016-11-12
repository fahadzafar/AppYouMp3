package com.youtube.util;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.youtube.parse.SPManager;

public class VideoInfoRequest {
	public static float isTimeLimitLessThan(int value, String videoId) {
		InputStream inputStream = null;
		String result = "";
		float numOfMins = -1;
		try {
			// https://www.googleapis.com/youtube/v3/videos?id=9bZkp7q19f0&part=contentDetails&key={YOUR_API_KEY}
			HttpClient httpclient = new DefaultHttpClient();

			URL url1 = new URL(
					"https://www.googleapis.com/youtube/v3/videos?id="
							+ videoId + "&part=contentDetails&key="
							+  SPManager.YOUTUBE_DEVELOPER_KEY);

			URI uri = url1.toURI();

			SearchRequest.trustAllHosts();

			HttpGet httpGet = new HttpGet(uri);

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpGet);

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null) {
				result = SearchRequest.convertInputStreamToString(inputStream);
				numOfMins = JsonParser.ParseGetVideoDuration(result);
			} else
				return numOfMins;
		} catch (Exception e) {

		}

		return numOfMins;
	}
}
