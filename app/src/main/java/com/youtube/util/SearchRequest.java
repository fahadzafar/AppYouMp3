package com.youtube.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.youtube.parse.SPManager;

public class SearchRequest {

	public static String GET(String searchTerm) {
		InputStream inputStream = null;
		String result = "";
		try {

			// https://www.googleapis.com/youtube/v3/search?key={your_key_here}&channelId={channel_id_here}&part=snippet,id&order=date&maxResults=20"
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// URI uri = URIUtils.createURI("https", "www.googleapis.com", 443,
			// "/youtube/v3/search", query, null); //can be null
			URL url1 = new URL(
					"https://www.googleapis.com/youtube/v3/search?key="
							+SPManager.YOUTUBE_DEVELOPER_KEY + "&q="
							+ java.net.URLEncoder.encode(searchTerm, "utf-8") + // searchTerm.replaceAll(" ",
																				// "%20")
																				// +
							"&part=snippet,id&order=date&maxResults=50&alt=json"); // Some
																					// instantiated
																					// URL
																					// object
			URI uri = url1.toURI();

			trustAllHosts();

			HttpGet httpGet = new HttpGet(uri);

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpGet);

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			int y = 0;
			// Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	public static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// convert inputstream to String
	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

}
