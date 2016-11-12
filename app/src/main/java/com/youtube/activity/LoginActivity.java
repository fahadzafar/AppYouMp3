package com.youtube.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.youtube.data.AppUser;
import com.youtube.parse.SPManager;
import com.youtube.util.Helper;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUser;
	private String mPassword;

	// UI references.
	private EditText mUserView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private CheckBox mRememberView;
	private TextView mLearnMoreView;

	// save password
	private AppUser registered_user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.activity_login);
		// Set up the login form.
		mUser = getIntent().getStringExtra(EXTRA_EMAIL);
		mUserView = (EditText) findViewById(R.id.email);
		mUserView.setText(mUser);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setHomeButtonEnabled(false);
		
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});

		TextView passReset = (TextView) findViewById(R.id.login_password_reset);
		passReset.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				showPasswordResetDialogue();
			}
		});

		// checkbox
		mRememberView = (CheckBox) findViewById(R.id.checkbox_me);
		if (SPManager.getRememberMe(getApplicationContext()) == true) {
			mUserView.setText(SPManager.getUserName(getApplicationContext()));
			mPasswordView.setText(SPManager
					.getUserPassword(getApplicationContext()));
			mRememberView.setChecked(true);
		}

		mLearnMoreView = (TextView) findViewById(R.id.login_learn_more);
		mLearnMoreView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// Intent i = new Intent(getApplicationContext(),
				// LearnMoreActivity.class);
				// startActivity(i);
			}
		});
	}

	void showPasswordResetDialogue() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Reset Password");

		// Set up the input
		final EditText login = new EditText(this);
		final EditText email = new EditText(this);

		login.setText("");
		// Specify the type of input expected; this, for example, sets the input
		// as a password, and will mask the text
		login.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_NORMAL);
		email.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);

		login.clearFocus();
		email.clearFocus();
		login.setHint("Username");
		email.setHint("Email Address");
		addDefaultText(login, "Username");
		addDefaultText(email, "Email Address");

		ll.addView(login);
		ll.addView(email);
		builder.setView(ll);

		builder.setCancelable(false);
		builder.setPositiveButton("Send email",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// ParseOperation.ResetPassword(
						// email.getText().toString(), login.getText()
						// .toString(), getApplicationContext());
					}
				});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	void addDefaultText(final EditText nameEdit, final String nameDefaultValue) {
		nameEdit.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (nameEdit.getText().toString().equals(nameDefaultValue)) {
					nameEdit.setText("");
				}
				return false;
			}

		});

		nameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus
						&& TextUtils.isEmpty(nameEdit.getText().toString())) {
					nameEdit.setText(nameDefaultValue);
				} else if (hasFocus
						&& nameEdit.getText().toString()
								.equals(nameDefaultValue)) {

				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// super.onCreateOptionsMenu(menu);
		// getMenuInflater().inflate(R.menu.menu_login_activity, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUserView.setError(null);
		mPasswordView.setError(null);


		// Store values at the time of the login attempt.
		mUser = mUserView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		/*
		// Store values at the time of the login attempt.
		registered_user = new AppUser();
		registered_user.username = SPManager
				.getUserName(getApplicationContext());
		registered_user.password = SPManager
				.getUserPassword(getApplicationContext());

		mUser = registered_user.username;
		mPassword = registered_user.password;*/

		if (TextUtils.isEmpty(mPassword) && TextUtils.isEmpty(mUser)
				&& registered_user == null) {
			Intent i = new Intent(getApplicationContext(), SignupActivity.class);
			startActivity(i);
		} else {
			boolean cancel = false;
			View focusView = null;

			// Check for a valid email address.
			if (TextUtils.isEmpty(mUser)) {
				mUserView.setError(getString(R.string.error_field_required));
				focusView = mUserView;
				cancel = true;
			}

			if (cancel) {
				// There was an error; don't attempt login and focus the first
				// form field with an error.
				focusView.requestFocus();
			} else {
				// Show a progress spinner, and kick off a background task to
				// perform the user login attempt.
				mLoginStatusMessageView
						.setText(R.string.login_progress_signing_in);
				showProgress(true);
				mAuthTask = new UserLoginTask();
				mAuthTask.execute((Void) null);
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	protected void onStart() {
		super.onStart();

		// Check to see if you are online.
		Boolean networkState = Helper.AmIOnline(getApplicationContext());

		if (networkState == false) {
			Helper.ShowDialogue("No internet detected",
					"Please connect to the internet and relaunch.",
					getApplicationContext());
			finish();
			return;
		}

		// Check to see if account exists
		if (SPManager.getUserName(LoginActivity.this).length() == 0) {
			Helper.ShowDialogue("No saved used", "No saved password",
					getApplicationContext());
		} else {
			 attemptLogin();
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private String errString = "";

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			SetLoginFormData();

			if (registered_user != null) {
				ParseUser user;
				try {
					user = ParseUser.logIn(registered_user.username,
							registered_user.password);
					if (user != null) {
						// Update new user in the shared objects.
						SPManager.current_user = user;

						if (mRememberView.isChecked()) {
							// Add to shared preferences
							SPManager.setUserNameAndPassword(
									getApplicationContext(),
									registered_user.username,
									registered_user.password);
						} else {
							SPManager.clear(getApplicationContext());
						}
						return true;
					} else {
						errString = "Something awful happened :(";
						return false;
					}
				} catch (ParseException e1) {
					errString = e1.getMessage();
					return false;
				}
			}

			// TODO: register the new account here.
			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				finish();
				Helper.LaunchActivity(getApplicationContext(),
						VideoListDashboardActivity.class);
			} else {
				SPManager.clear(getApplicationContext());
				registered_user = null;
				mPasswordView.setError(errString);
				mPasswordView.requestFocus();
			}
			showProgress(false);
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	public void SetLoginFormData() {
		if (registered_user == null) {
			registered_user = new AppUser();
			registered_user.username = mUserView.getText().toString();
			registered_user.password = mPasswordView.getText().toString();
		}
	}

	public void onCheckboxClicked(View view) {
		boolean checked = ((CheckBox) view).isChecked();

		if (view.getId() == R.id.checkbox_me) {
			Log.e("", "" + checked);
		}
	}


}
