package com.youtube.activity;

import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.youtube.parse.ParseOperation;
import com.youtube.parse.SPManager;
import com.youtube.util.Helper;

public class InfoActivity extends Activity {

	AdView mAdView;
	ListView listview;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setHomeButtonEnabled(true);

		// --- Ads
		mAdView = (AdView) findViewById(R.id.info_banner);
		Helper.MakeAdDecision(mAdView);

		// Displaying the user download directory
		TextView tv = (TextView) findViewById(R.id.info_download_location);
		tv.setText(tv.getText() + SPManager.MUSIC_ROOT_DIR);

		tv = (TextView) findViewById(R.id.info_free_tokens);
		tv.setText(tv.getText()
				+ (SPManager.current_user.getInt("freeTokens") + ""));

		tv = (TextView) findViewById(R.id.info_paid_tokens);
		tv.setText(tv.getText()
				+ (SPManager.current_user.getInt("paidTokens") + ""));

		tv = (TextView) findViewById(R.id.info_total_tokens);
		tv.setText(tv.getText() + (Helper.GetUserTotalTokens() + ""));

		tv = (TextView) findViewById(R.id.info_free_tokens_reset_date);
		tv.setText(tv.getText()
				+ Helper.GetPrintableDate(SPManager.current_user
						.getDate("tokenResetDate")));

		tv = (TextView) findViewById(R.id.info_show_ads);
		tv.setText(tv.getText()
				+ (SPManager.current_user.getBoolean("showAds") ? "ON" : "OFF"));

		tv = (TextView) findViewById(R.id.info_download_duration_limit);
		tv.setText(tv.getText()
				+ (SPManager.current_user.getInt("downloadDurationLimit") + " mins"));

		tv = (TextView) findViewById(R.id.info_priority);
		tv.setText(tv.getText()
				+ (Helper.GetPriorityString(SPManager.current_user
						.getInt("priority"))));

		// ----------- list view handling
		listview = (ListView) findViewById(R.id.info_author_messages);

		List<ParseObject> answer = ParseOperation
				.GetAllAuthorMessages(getApplicationContext());

		ParseQuery<ParseObject> computeQuery = ParseQuery
				.getQuery("FromAuthor");
		computeQuery.orderByDescending("createdAt");

		try {
			computeQuery.findInBackground(new FindCallback<ParseObject>() {

				@Override
				public void done(List<ParseObject> answer, ParseException arg1) {
					UpdateList(answer);

				}
			});

		} catch (Exception er) {
			Helper.ShowDialogue(er.getMessage(),
					": Download error, try again later",
					getApplicationContext());
		}
		// --------------

		listview.setOnTouchListener(new ListView.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					// Disallow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(true);
					break;

				case MotionEvent.ACTION_UP:
					// Allow ScrollView to intercept touch events.
					v.getParent().requestDisallowInterceptTouchEvent(false);
					break;
				}

				// Handle ListView touch events.
				v.onTouchEvent(event);
				return true;
			}

		});

	}

	void UpdateList(List<ParseObject> answer) {

		String displayData[] = new String[answer.size()];

		for (int i = 0; i < answer.size(); i++) {
			String printDate = Helper.GetPrintableDate(answer.get(i)
					.getCreatedAt());
			displayData[i] = "(" + printDate + "): "
					+ answer.get(i).getString("title") + " \n";
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), android.R.layout.simple_list_item_1,
				displayData);
		listview.setAdapter(adapter);

	}

}
