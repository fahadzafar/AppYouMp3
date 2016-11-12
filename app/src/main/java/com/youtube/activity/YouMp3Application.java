package com.youtube.activity;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;
import com.youtube.parse.SPManager;
import com.youtube.util.AudioPlayListManager;
import com.youtube.util.Helper;

public class YouMp3Application extends Application {

	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		try {
			// Add your initialization code here
			Parse.initialize(this, SPManager.ApplicationId_,
					SPManager.ClientId_);
			ParseUser.enableAutomaticUser();
			ParseACL defaultACL = new ParseACL();

			// If you would like all objects to be private by default, remove
			// this line.
			defaultACL.setPublicReadAccess(true);
			defaultACL.setPublicWriteAccess(true);
			ParseACL.setDefaultACL(defaultACL, true);
			SPManager.current_user = ParseUser.getCurrentUser();

			String pathToExternalStorage = Environment
					.getExternalStorageDirectory().getAbsolutePath();
			File appDirectory = new File(pathToExternalStorage + "/" + "YouMp3");
			if (appDirectory.mkdirs()) {
				System.out.println("Yes");
			} else {
				System.out.println("No");
			}
			SPManager.MUSIC_ROOT_DIR = pathToExternalStorage + "/YouMp3/";
		} catch (Exception er) {
			Helper.ShowDialogue("Error", er.getMessage(),
					getApplicationContext());
		}
/*
		// Setup the audio playlist.
		int playlistId = AudioPlayListManager.CreatePlaylist(
				getApplicationContext(), SPManager.Music_Playlist_Title);
		String stringPlaylistId = "";
		if (playlistId == -1) {
			stringPlaylistId = AudioPlayListManager.GetPlayListId(
					SPManager.Music_Playlist_Title, getApplicationContext());
		} else {
			stringPlaylistId = playlistId + "";
		}
		SPManager.Music_Playlist_Id = Integer.parseInt(stringPlaylistId);
	*/}

	public static Context getContext() {
		return context;
	}

	public static String getMessage(int msgId) {
		return context.getResources().getString(msgId);
	}

	public static String getFormattedMessage(int msgId, Object... args) {
		String message = context.getResources().getString(msgId);
		return String.format(message, args);
	}

}