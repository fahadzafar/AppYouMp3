package com.youtube.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Environment;

public class FileExistRequest {
	public static boolean IsValid(String videoId, String videoTitle, Context con) {
		String result = "";
		try {
			videoId = "d_c52lEFbsw";
			videoTitle  = "entity_test";
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

// httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false);

			URL url1 = new URL("http://www.flvto.biz/download/direct/yt_"
					+ videoId + "/");
			URI uri = url1.toURI();
			
			
			// ---------------
			
			 try {
				 
					String url =url1.toString();
				 
					URL obj = new URL(url);
					HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
					conn.setReadTimeout(5000);
					conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
					conn.addRequestProperty("User-Agent", "Mozilla");
					conn.addRequestProperty("Referer", "google.com");
				 
					System.out.println("Request URL ... " + url);
				 
					boolean redirect = false;
				 
					// normally, 3xx is redirect
					int status = conn.getResponseCode();
					if (status != HttpURLConnection.HTTP_OK) {
						if (status == HttpURLConnection.HTTP_MOVED_TEMP
							|| status == HttpURLConnection.HTTP_MOVED_PERM
								|| status == HttpURLConnection.HTTP_SEE_OTHER)
						redirect = true;
					}
				 
					System.out.println("Response Code ... " + status);
				 
					if (redirect) {
				 
						// get redirect url from "location" header field
						String newUrl = conn.getHeaderField("Location");
				 
						// get the cookie if need, for login
						String cookies = conn.getHeaderField("Set-Cookie");
				 
						// open the new connnection again
						conn = (HttpURLConnection) new URL(newUrl).openConnection();
						conn.setRequestProperty("Cookie", cookies);
						conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
						conn.addRequestProperty("User-Agent", "Mozilla");
						conn.addRequestProperty("Referer", "google.com");
				 
						System.out.println("Redirect to URL : " + newUrl);
				 
					}
				 
					BufferedReader in = new BufferedReader(
				                              new InputStreamReader(conn.getInputStream()));
					String inputLine;
					StringBuffer html = new StringBuffer();
				 
					while ((inputLine = in.readLine()) != null) {
						html.append(inputLine);
					}
					in.close();
				 
					System.out.println("URL Content... \n" + html.toString());
					System.out.println("Done");
				 
				    } catch (Exception e) {
					e.printStackTrace();
				    }
				 
			 
			//---------------
			/*
			HttpGet httpGet = new HttpGet(uri);
			HttpResponse httpResponse = httpclient.execute(httpGet);
	//		InputStream inputStream = httpResponse.getEntity().getContent();

			HttpEntity entity = null;
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
			    entity = httpResponse.getEntity();
			    if (entity != null) {
			        // return entity.getContent();
			    }
			}
			
			String pathToExternalStorage = Environment
					.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_MUSIC).toString();
			File appDirectory = new File(pathToExternalStorage + "/"
					+ "YouMp3");
			OutputStream output = new FileOutputStream(appDirectory.toString() + "/"
					+ videoTitle + ".mp3");
			
			entity.writeTo(output);
			output.close();
			
			if(entity.isStreaming() == true)
				System.out.println("Yes");
			
			/*
			// entity.writeTo(arg0);
			if (inputStream == null)
				return false;

			result = SearchRequest.convertInputStreamToString(inputStream);
			if (result.contains("404"))
				return false;
			else {
				DownloadRequest.Info_Title.setText("Downloading file");
				DownloadRequest.Info_Description.setText("");

				System.out.print("yes");
				String pathToExternalStorage = Environment
						.getExternalStoragePublicDirectory(
								Environment.DIRECTORY_MUSIC).toString();
				File appDirectory = new File(pathToExternalStorage + "/"
						+ "YouMp3");

				// Have the object build the directory structure, if needed.
				appDirectory.mkdirs();

				// Download logic..............
				DownloadRequest.Info_Description.setText("Saving to disk at:  "
						+ pathToExternalStorage + "/" + "YouMp3/");

				InputStream input = null;
				OutputStream output = null;
				HttpURLConnection connection = null;
				try {
					URL url = url1;
					connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					// expect HTTP 200 OK, so we don't mistakenly save error
					// report
					// instead of the file
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
						// return "Server returned HTTP " +
						// connection.getResponseCode()
						// + " " + connection.getResponseMessage();
					}

					// this will be useful to display download percentage
					// might be -1: server did not report the length
					int fileLength = connection.getContentLength();

					// download the file
					input = connection.getInputStream();
					output = new FileOutputStream(appDirectory.toString() + "/"
							+ videoTitle + ".mp3");
					
					entity.writeTo(output);

					byte data[] = new byte[4096];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1) {
						// allow canceling with back button
						
						//  if (isCancelled()) { input.close(); return null; }
						 
						total += count;
						// publishing the progress....
						if (fileLength > 0) // only if total length is known
							// publishProgress((int) (total * 100 /
							// fileLength));
							DownloadRequest.Info_Description
									.setText("Progress :"
											+ (total * 100 / fileLength));
						output.write(data, 0, count);
					}
				} catch (Exception e) {
					// return e.toString();
					return false;
				} finally {
					try {
						if (output != null)
							output.close();
						if (input != null)
							input.close();
					} catch (IOException ignored) {
						return false;
					}

					if (connection != null)
						connection.disconnect();
				}
				// return null;

				// ..........................

			}
*/
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}
