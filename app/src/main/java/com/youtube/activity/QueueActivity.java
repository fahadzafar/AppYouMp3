package com.youtube.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.youtube.activity.adapter.QueueAdapter;
import com.youtube.parse.ParseOperation;
import com.youtube.parse.SPManager;
import com.youtube.util.Helper;

public class QueueActivity extends ListActivity {
	List<ParseObject> saveToDisk;
	List<ParseObject> values = new ArrayList<ParseObject>();
	QueueAdapter adapter;
	ProgressDialog barProgressDialog;
	AdView mAdView;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// --- Actionbar stuff
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		barProgressDialog = new ProgressDialog(QueueActivity.this);
		barProgressDialog.setTitle("-");
		barProgressDialog.setMessage("-");
		barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
		barProgressDialog.setProgress(0);
		barProgressDialog.setMax(1);

		barProgressDialog.setCanceledOnTouchOutside(false);
		barProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		barProgressDialog.setCancelable(false);
		/*	
		*/
		// -- initialize the progress dialogue
		getListView().addFooterView(
				getLayoutInflater().inflate(R.layout.queue_activity_footer,
						null));
		RefreshList();

		getListView().addHeaderView(
				getLayoutInflater().inflate(R.layout.queue_activity_header,
						null));
		RefreshList();

		// Show the ads on the header.
		mAdView = (AdView) findViewById(R.id.view_queue_banner);
		Helper.MakeAdDecision(mAdView);

		// Attach the information dialogue at the footer.
		TextView tv = (TextView) findViewById(R.id.queue_whats_this);
		tv.setClickable(true);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						QueueActivity.this);

				// set title
				alertDialogBuilder.setTitle("Information");

				// set dialog message
				alertDialogBuilder
						.setMessage(
								"This queue displays the status of your processing job. "
										+ "It will be executed based on user priority and time of submission. "
										+ "Come back later or press refresh after a some seconds and if the job was completed, it will be atuomatically downloaded to your phone and removed from this queue.")
						.setCancelable(false)
						.setPositiveButton("OK",
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

	@Override
	protected void onListItemClick(ListView l, View v, final int position,
			long id) {
		// String item = (String) getListAdapter().getItem(position);
		// Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete this queued Item ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (position == 0)
									return;
								// to perform on ok
								ParseOperation.DeleteQueuedItem(
										values.get(position  - 1),
										getApplicationContext());

								saveToDisk = ParseOperation
										.GetAllDownloadableFiles(getApplicationContext());
								RefreshList();

							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// ---------- Action bar stuff
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.queue_actionbar, menu);

		return super.onCreateOptionsMenu(menu);
	}

	int fileDLIndexer = 0;

	private class PerformDownload extends AsyncTask<String, Void, String> {
		ParseObject myObject;
		int myId;

		@Override
		protected String doInBackground(String... params) {
			myId = Integer.parseInt(params[0]);
			// downloading code
			ParseFile musicFile = (ParseFile) myObject.get("storedFile");
			// -------- Reading and saving file with updates to
			// progress
			try {
				byte data[] = musicFile.getData();
				SaveDownloadedFile(data, myObject);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (myId == (saveToDisk.size() - 1)) {
				Helper.ShowDialogue("Download and save status: ", "Completed",
						getApplicationContext());
			}
			// dismissProgressDialog();
			barProgressDialog.setProgress(myId + 1);
		}

		@Override
		protected void onPreExecute() {
			myObject = saveToDisk.get(fileDLIndexer);

			barProgressDialog.show();
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

	}

	void RefreshList() {
		saveToDisk = ParseOperation
				.GetAllDownloadableFiles(getApplicationContext());

		if (saveToDisk != null) {
			if (saveToDisk.size() > 0) {
				barProgressDialog.setMax(saveToDisk.size());
				barProgressDialog.setTitle(" Downloading " + saveToDisk.size()
						+ " files");
				String displayMessage = "";
				for (int i = 0; i < saveToDisk.size(); i++) {
					displayMessage += ((i + 1) + ". "
							+ saveToDisk.get(i).getString("title") + " \n");
				}
				displayMessage += " \n     ------------- \n";
				displayMessage += (" Download location: " + SPManager.MUSIC_ROOT_DIR);
				barProgressDialog.setMessage(displayMessage);

				for (fileDLIndexer = 0; fileDLIndexer < saveToDisk.size(); fileDLIndexer++) {
					new PerformDownload().execute(fileDLIndexer + "");
				}
			}
		}
		// sharedProgressTitle += "\n ---------------- \n";
		// sharedProgressTitle += " File Path: " + SPManager.MUSIC_ROOT_DIR;

		/*
		 * barProgressDialog.setTitle("Refreshing list");
		 * barProgressDialog.setMessage(" Please wait");
		 * barProgressDialog.setProgress(0);
		 * 
		 * saveToDisk = ParseOperation
		 * .GetAllDownloadableFiles(getApplicationContext()); if (saveToDisk !=
		 * null) { sharedProgressTitle = "Downloaded: \n";
		 * 
		 * 
		 * for (fileDLIndexer = 0; fileDLIndexer < saveToDisk.size();
		 * fileDLIndexer++) { sharedProgressTitle = sharedProgressTitle +
		 * (fileDLIndexer + 1) + ". " +
		 * saveToDisk.get(fileDLIndexer).getString("title") + ",\n"; new
		 * DownloadSave().execute("");
		 * 
		 * 
		 * //barProgressDialog.setMessage(sharedProgressTitle); ParseFile
		 * musicFile = (ParseFile) saveToDisk.get(fileDLIndexer)
		 * .get("storedFile");
		 * //barProgressDialog.setTitle("Downloading and Saving to disk");
		 * 
		 * // -------- Reading and saving file with updates to progress try {
		 * data = musicFile.getData(); } catch (ParseException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * SaveDownloadedFile(data);
		 * 
		 * } // for sharedProgressTitle += "\n ---------------- \n";
		 */

		// Update the execution queue display.
		values = ParseOperation.GetAllUserQueueedItems(getApplicationContext());
		adapter = new QueueAdapter(this, values);
		setListAdapter(adapter);

	}

	void SaveDownloadedFile(byte byteArray[], ParseObject downloaded) {
		String filenameTitle = downloaded.getString("title"); //
		final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f',
				'`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

		char replacement = '-';
		for (int ind = 0; ind < ILLEGAL_CHARACTERS.length; ind++) {
			filenameTitle = filenameTitle.replace(ILLEGAL_CHARACTERS[ind],
					replacement);
		} //
			// -------------
		try {
			String finalLocation = SPManager.MUSIC_ROOT_DIR + filenameTitle
					+ ".mp3";
			OutputStream output = new FileOutputStream(finalLocation, false);
			output.write(byteArray);
			output.flush();
			output.close();

			// Now change the cloud record since the file has been // //
			// downloaded successfully. //
			downloaded.put("status", "2000"); //
			downloaded.remove("storedFile"); //
			downloaded.save();

		} catch (Exception e1) {
			Helper.ShowDialogue(" Error: ", e1.getMessage(),
					getApplicationContext());
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.queue_refresh) {
			RefreshList();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		this.finish();
	}

	void LaunchMp3(){
		/*
		 * Try with:

ImageView.setImageURI(Uri.fromFile(new File("/sdcard/cats.jpg")));
Or with:

ImageView.setImageURI(Uri.parse(new File("/sdcard/cats.jpg").toString()));

		
		Intent intent = new Intent();  
		intent.setAction(android.content.Intent.ACTION_VIEW);  
		File file = new File(YOUR_SONG_URI);  
		intent.setDataAndType(Uri.fromFile(file), "audio/*");  
		startActivity(intent);*/
	}
}
