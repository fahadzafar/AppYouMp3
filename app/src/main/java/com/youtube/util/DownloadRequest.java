package com.youtube.util;

import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.youtube.activity.R;

public class DownloadRequest {
	Context con;
	public static TextView Info_Title;
	public static TextView Info_Description;
	public static Button Dialogue_Cancel;

	Dialog dialog;

	public DownloadRequest(Context iCon) {
		// TODO Auto-generated constructor stub
		con = iCon;
	}

	public void DisplayDialogue() {

		final Dialog dialog = new Dialog(con);
		dialog.setContentView(R.layout.dialogue_download);
		dialog.setTitle("Downloading ...");

		// set the custom dialog components - text, image and button
		Info_Title = (TextView) dialog.findViewById(R.id.dialogInfoTitle);
		Info_Description = (TextView) dialog
				.findViewById(R.id.dialogInfoDescription);

		Dialogue_Cancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
		// if button is clicked, close the custom dialog
		Dialogue_Cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public boolean GET(String videoId, String videoTitle, Context con) {
		InputStream inputStream = null;
		String result = "";
		try {

			/*
			 * DisplayDialogue(); Info_Title.setText("Converting, please wait");
			 * Info_Description.setText("This might take a few seconds");
			 */
			// ------------------------------------------
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// http://www.flvto.biz/progress/yt_eus8WcBAV10/
			// URL url1 = new URL("http://www.flvto.biz/download/direct/yt_" +
			// videoId);
			// WebViewActivity.launchAddress = url1.toString();

			// Intent i = new Intent(con, WebViewActivity.class);
			// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// con.startActivity(i);

			/*
			 * URI uri = url1.toURI(); HttpGet httpGet = new HttpGet(uri);
			 * HttpResponse httpResponse = httpclient.execute(httpGet);
			 * inputStream = httpResponse.getEntity().getContent();
			 * 
			 * String resultOutput = SearchRequest
			 * .convertInputStreamToString(inputStream);
			 * 
			 * // convert inputstream to string if (inputStream != null) {
			 * SystemClock.sleep(7000); int count = 0; while
			 * (FileExistRequest.IsValid(videoId, videoTitle, con) == false) {
			 * 
			 * SystemClock.sleep(5000); count++;
			 * Dialogue_Cancel.setEnabled(true);
			 * 
			 * if (count == 10) { return false; } }
			 * 
			 * dialog.dismiss(); Toast.makeText(con,
			 * "File saved to disk successfully", Toast.LENGTH_LONG).show();
			 * 
			 * // Info_Title.setText("Downloading"); //
			 * Info_Description.setText("");
			 * 
			 * } else return false;
			 */
		} catch (Exception e) {
			int y = 0;
			// Log.d("InputStream", e.getLocalizedMessage());
		}
		return true;
	}

}
