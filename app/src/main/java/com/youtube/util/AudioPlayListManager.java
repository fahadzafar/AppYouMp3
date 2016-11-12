package com.youtube.util;

import com.youtube.parse.SPManager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class AudioPlayListManager {

	public static int CreatePlaylist(Context con, String playlistName) {
		Cursor c;
		int mPlaylistId = -1;
		ContentResolver mCR = con.getContentResolver();
		ContentValues mInserts = new ContentValues();
		mInserts.put(MediaStore.Audio.Playlists.NAME,
				playlistName);
		mInserts.put(MediaStore.Audio.Playlists.DATE_ADDED,
				System.currentTimeMillis());
		mInserts.put(MediaStore.Audio.Playlists.DATE_MODIFIED,
				System.currentTimeMillis());
		Uri mUri = mCR.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
				mInserts);
		if (mUri != null) {
			// mPlaylistId = -1;
			// mResult = FM.SUCCESS;
			c = mCR.query(mUri, null, null, null,
					MediaStore.Audio.Playlists.DATE_MODIFIED);
			if (c != null) {
				c.moveToLast();
				// Save the newly created ID so it can be selected.
				// Names are allowed to be duplicated,
				// but IDs can never be.
				mPlaylistId = c.getInt(c
						.getColumnIndex(MediaStore.Audio.Playlists._ID));
				c.close();
			}
		}
		return mPlaylistId;
	}

	public static String GetPlayListId(String playlist, Context context) {
		Uri newuri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
		final String playlistid = MediaStore.Audio.Playlists._ID;
		final String playlistname = MediaStore.Audio.Playlists.NAME;

		String where = MediaStore.Audio.Playlists.NAME + "=?";
		String[] whereVal = { playlist };
		String[] projection = { playlistid, playlistname };
		ContentResolver resolver = context.getContentResolver();
		Cursor record = resolver.query(newuri, projection, where, whereVal,
				null);
		int recordcount = record.getCount();
		String foundplaylistid = "";

		if (recordcount > 0) {
			record.moveToFirst();
			int idColumn = record.getColumnIndex(playlistid);
			foundplaylistid = record.getString(idColumn);
			record.close();
		}
		return foundplaylistid;
	}
}
