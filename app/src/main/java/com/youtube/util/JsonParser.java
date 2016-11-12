package com.youtube.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youtube.data.VideoBean;

public class JsonParser {

	public static float ParseGetVideoDuration(String result) {
		int hours = 0, mins = 0, secs = 0;
		try {
			JSONObject jObject = new JSONObject(result);
			JSONArray jArray = jObject.getJSONArray("items");
			JSONObject oneObject = jArray.getJSONObject(0);
			// Pulling items from the array
			JSONObject snippetArray = oneObject.getJSONObject("contentDetails");
			String videoDuration = snippetArray.getString("duration");
			int start = videoDuration.indexOf("T") + 1;
			int end = videoDuration.length();
			videoDuration = videoDuration.substring(start, end);

			if (videoDuration.contains("H")) {
				hours = Integer.parseInt(videoDuration.substring(0,
						videoDuration.indexOf("H")));
				videoDuration = videoDuration.substring(
						videoDuration.indexOf("H") + 1, videoDuration.length());
			}
			if (videoDuration.contains("M")) {

				mins = Integer.parseInt(videoDuration.substring(0,
						videoDuration.indexOf("M")));

				videoDuration = videoDuration.substring(
						videoDuration.indexOf("M") + 1, videoDuration.length());
			}
			if (videoDuration.contains("S")) {
				end = videoDuration.indexOf("S");
				secs = Integer.parseInt(videoDuration.substring(0, end));
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ((float)hours * 60) + (float)mins + ((float)secs / 60);
	}

	public static List<VideoBean> ParseRequest(String result) {
		List<VideoBean> myList = new ArrayList();

		try {
			JSONObject jObject = new JSONObject(result);
			JSONArray jArray = jObject.getJSONArray("items");

			for (int i = 0; i < jArray.length(); i++) {
				try {
					JSONObject oneObject = jArray.getJSONObject(i);
					// Pulling items from the array
					JSONObject snippetArray = oneObject
							.getJSONObject("snippet");
					String videoTitle = snippetArray.getString("title");
					String videoDescription = snippetArray
							.getString("description");

					String videoLiveBroadcast = snippetArray
							.getString("liveBroadcastContent");

					snippetArray = oneObject.getJSONObject("id");
					String videoId = snippetArray.getString("videoId");

					myList.add(new VideoBean(videoId, videoTitle,
							videoLiveBroadcast));
					// String oneObjectsItem2 =
					// oneObject.getString("anotherSTRINGNAMEINtheARRAY");
				} catch (JSONException e) {
					// Oops
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myList;

	}
}
