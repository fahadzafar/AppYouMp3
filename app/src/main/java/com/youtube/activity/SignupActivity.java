package com.youtube.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.youtube.data.AppUser;
import com.youtube.parse.ParseOperation;
import com.youtube.util.Helper;

public class SignupActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set View to register.xml
		setContentView(R.layout.activity_signup);

		// The registration submit button to pass on the user information to
		// the server.
		Button registerButon = (Button) findViewById(R.id.btnRegister);
		registerButon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (SanityChecks()) {
					AppUser reg_user = GetRegistrationFormData();

					// After you extract the form data, you try to register the
					// user. If it works, close this activity and login.
					if (ParseOperation.RegisterUser(reg_user,
							getApplicationContext())) {

						if (ParseOperation.LoginUser(reg_user,
								getApplicationContext())) {
							// Launch the chat activity
							Intent intent = new Intent(SignupActivity.this,
									VideoListDashboardActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
						finish();
					}
				}
			}
		});

		// ----------------attach terms of service on click dialogue

		// Attach the information dialogue at the footer.
		TextView tv = (TextView) findViewById(R.id.reg_tos);
		tv.setClickable(true);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						SignupActivity.this);

				// set title
				alertDialogBuilder.setTitle("Information");
				String TOSString = getResources().getString(R.string.tos);
				// set dialog message
				alertDialogBuilder
						.setMessage(TOSString)
						.setCancelable(false)
						.setPositiveButton("ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity

									}
								});

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		});
	}

	public AppUser GetRegistrationFormData() {
		AppUser registered_user = new AppUser();

		// Extract all the values from the view input fields.
		EditText tv = (EditText) findViewById(R.id.reg_password);
		registered_user.password = tv.getText().toString();
		tv = (EditText) findViewById(R.id.reg_username);
		registered_user.username = tv.getText().toString();
		tv = (EditText) findViewById(R.id.reg_email);
		registered_user.email = tv.getText().toString();
		return registered_user;
	}

	// Check to see if the data was entered correctly before submission.
	public boolean SanityChecks() {

		EditText tv = (EditText) findViewById(R.id.reg_username);
		String username = tv.getText().toString();
		if (Helper.isEmpty(username)) {
			Helper.ShowDialogue("Incomplete", "Please enter a username",
					getApplicationContext());
			return false;
		}

		tv = (EditText) findViewById(R.id.reg_email);
		String email = tv.getText().toString();
		if (Helper.isEmpty(email)) {
			Helper.ShowDialogue("Incomplete", "Please enter an email address",
					getApplicationContext());
			return false;
		}

		tv = (EditText) findViewById(R.id.reg_password);
		String password1 = tv.getText().toString();

		if (Helper.isEmpty(password1)) {
			Helper.ShowDialogue("Incomplete", "Please enter a password",
					getApplicationContext());
			return false;
		}

		EditText tv_password_repeat = (EditText) findViewById(R.id.reg_password_repeat);
		String password_repeat = tv_password_repeat.getText().toString();

		if (password1.equals(password_repeat) == false) {
			Helper.ShowDialogue("Mismatch", "Passwords do not match.",
					getApplicationContext());
			return false;
		}

		CheckBox tos = (CheckBox) findViewById(R.id.reg_tos_chk);
		if (tos.isChecked() == false) {
			Helper.ShowDialogue("Terms of Service must be agreed to", "",
					getApplicationContext());
			return false;
		}
		return true;
	}

}