package com.youtube.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdView;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.youtube.parse.SPManager;
import com.youtube.util.Helper;

public class FeedbackActivity extends Activity {
	AdView mAdView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_2);

		// --- Ads
		mAdView = (AdView) findViewById(R.id.feedback_banner);
		Helper.MakeAdDecision(mAdView);

		Button bn = (Button) findViewById(R.id.about_btn_back);
		bn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SubmitUserSuggestion();
			}
		});
	}

	// Submit the user suggestion/comments/feedback.
	public void SubmitUserSuggestion() {

		EditText ev = (EditText) findViewById(R.id.feedback_userdata);
		String data = ev.getText().toString();
		if (!data.equals("")) {
			ParseObject suggestion = new ParseObject("Feedback");
			suggestion.put("userId", SPManager.current_user);
			suggestion.put("feedbackText", data);

			suggestion.saveInBackground(new SaveCallback() {
				public void done(com.parse.ParseException e) {
					Helper.ShowDialogue("Feedback submitted,", " Thanks",
							getApplicationContext());
				}
			});
		}
		this.finish();
	}
}
