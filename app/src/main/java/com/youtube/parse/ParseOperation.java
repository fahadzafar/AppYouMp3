package com.youtube.parse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.youtube.data.AppUser;
import com.youtube.util.Helper;

public class ParseOperation {

	public static void DeleteQueuedItem(ParseObject queuedSong,
			final Context con) {
		try {
			queuedSong.delete();
		} catch (Exception er) {
		}
	}

	public static List<ParseObject> GetAllAuthorMessages(final Context con) {
		List<ParseObject> answer = null;
		ParseQuery<ParseObject> computeQuery = ParseQuery
				.getQuery("FromAuthor");
		computeQuery.orderByDescending("createdAt");

		try {
			answer = computeQuery.find();
		} catch (Exception er) {
			Helper.ShowDialogue(er.getMessage(),
					": Download error, try again later", con);
		}
		return answer;
	}

	public static List<ParseObject> GetAllDownloadableFiles(final Context con) {
		List<ParseObject> answer = null;
		ParseQuery<ParseObject> computeQuery = ParseQuery.getQuery("Completed");
		computeQuery.whereEqualTo("userId", SPManager.current_user);

		// 1 is ready for download
		// 2000 = downloaded.
		computeQuery.whereEqualTo("status", "1");
		try {
			answer = computeQuery.find();
		} catch (Exception er) {
			Helper.ShowDialogue(er.getMessage(),
					": Download error, try again later", con);
		}
		return answer;
	}

	public static List<ParseObject> GetAllUserQueueedItems(final Context con) {
		List<ParseObject> answer = null;
		List<ParseObject> returnThis = new ArrayList<ParseObject>();
		ParseQuery<ParseObject> computeQuery = ParseQuery.getQuery("Queue");
		// computeQuery.whereEqualTo("userId", SPManager.current_user);
		computeQuery.orderByDescending("priority"); // (Highest priority first)
		computeQuery.addAscendingOrder("createdAt"); // (then amongst them,
														// Oldest submission
														// first)

		try {
			answer = computeQuery.find();
			if (answer != null) {
				// Place their ordering in the object.
				for (int i = 0; i < answer.size(); i++) {

					ParseObject queueItem = ParseObject.create("Queue");
					queueItem = answer.get(i).getParseObject("userId");
					String ansId = queueItem.getObjectId();
					String currentUserId = SPManager.current_user.getObjectId();
					if (ansId.equals(currentUserId)) {
						ParseObject item = answer.get(i);
						if (i == 0) {
							item.put("execPosition", "Being processed now ...");

						} else {
							item.put("execPosition", i + 1);
						}
						returnThis.add(item);
					}
				}
			}

		} catch (ParseException e) {
			Helper.ShowDialogue(e.getMessage(),
					": Download error, try again later", con);
			return null;
		}
		return returnThis;

	}

	public static boolean IsPresentInQueue(final DownloadRequestBean db,
			final Context con) {
		ParseQuery<ParseObject> computeQuery = ParseQuery.getQuery("Queue");
		ParseObject alreadyExists = null;

		computeQuery.whereEqualTo("userId", SPManager.current_user);
		computeQuery.whereEqualTo("videoId", db.videoId);
		try {

			alreadyExists = computeQuery.getFirst();
			if (alreadyExists == null)
				return false;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}

	public static void PostQueueRequest(final DownloadRequestBean db,
			final Context con) {
		ParseObject downloadRequest = new ParseObject("Queue");
		downloadRequest.put("videoId", db.videoId);
		downloadRequest.put("executioner", db.executioner);
		downloadRequest.put("title", db.title);
		downloadRequest.put("duration", db.duration);
		downloadRequest.put("priority", SPManager.priority);
		downloadRequest.put("userId", SPManager.current_user);

		try {
			if (IsPresentInQueue(db, con) == false) {
				downloadRequest.save();
				Helper.ShowDialogue("\"" + db.title,
						" \" added to download queue", con);

			} else {
				Helper.ShowDialogue("No duplicates, ",
						"Item already present in queue", con);
			}

		} catch (Exception e1) {
			Helper.ShowDialogue("Error", e1.getMessage(), con);

		}
	}

	public static boolean RegisterUser(final AppUser reg_user, final Context con) {
		ParseUser user = new ParseUser();
		user.setUsername(reg_user.username);
		user.setPassword(reg_user.password);
		try {
			user.setEmail(reg_user.email);
			user.put("allowVideo", false);
			user.put("showAds", true);
			user.put("priority", 5);
			user.put("freeTokens", SPManager.FreeMothlyTokens);
			user.put("paidTokens", 0);
			user.put("downloadDurationLimit", 10);

			user.signUp();

			Date resetTokenDate = Helper
					.AddMonthsToDate(user.getCreatedAt(), 1);
			user.put("tokenResetDate", resetTokenDate);
			user.save();

			// Hooray! Let them use the app now.
			Helper.ShowDialogue("Success", "User registered ^_^", con);
			return true;

		} catch (Exception e1) {
			Helper.ShowDialogue("Error", e1.getMessage(), con);

			// Sign up didn't succeed. Look at the ParseException
			// to figure out what went wrong
			return false;
		}
	}

	// Login a user after he enters a login and a password.
	public static boolean LoginUser(final AppUser reg_user, final Context con) {
		ParseUser user;
		try {
			user = ParseUser.logIn(reg_user.username, reg_user.password);
			if (user != null) {
				// Update new user in the shared objects.
				SPManager.current_user = user;

				// Add to shared preferences
				SPManager.setUserNameAndPassword(con, reg_user.username,
						reg_user.password);

				return true;
				// Hooray! The user is logged in.

			} else {
				return false;
				// Helper.ShowDialogue("Login", "Could not login", con);
			}

		} catch (Exception e1) {
			Helper.ShowDialogue("Password", "incorrect", con);
			e1.printStackTrace();
			return false;
		}
	}

	public static void LogOutCurrentUser(Context con) {
		ParseUser.logOut();
		SPManager.current_user = null;
		SPManager.clear(con);
	}

}
