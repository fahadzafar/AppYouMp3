package com.youtube.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.youtube.parse.SPManager;

public class Helper {

	public static Intent shareOneImage(Bitmap shareBitmap, Context con) {
		File save_dir = Environment.getExternalStorageDirectory();
		FileOutputStream out;
		try {
			// Set the filename to the current time.
			String filename = save_dir + "/rai_image" + ".jpg";
			out = new FileOutputStream(filename);

			shareBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			Intent share = new Intent(Intent.ACTION_SEND);
			share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			share.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

			share.putExtra(Intent.EXTRA_SUBJECT, "Download YouMp3 from the Play Store, rip audios from any YouTube videio for free.");
			share.putExtra(Intent.EXTRA_TEXT,"http://play.google.com ");
			share.putExtra(Intent.EXTRA_TITLE, "Download YouMp3 from the Play Store, rip audios from any YouTube videio for free.");
			share.putExtra(Intent.EXTRA_STREAM,
					Uri.parse("file:///" + filename));
			share.setType("image/*");

			return share;
		} catch (FileNotFoundException e) {
			Helper.ShowDialogue("Sharing Issue",
					"Some error occured:" + e.getMessage(), con);
		}
		return null;
	}
	
	public static String GetPriorityString(int i) {

		return i + " (max = 10)";
	}

	public static Date AddMonthsToDate(Date createdAt, int months) {
		Calendar cal = Helper.DateToCalendar(createdAt);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}

	public static boolean GetFreeTokens() {
		Date currentDate = new Date();
		int months = 0;
		months = DateDifferenceInMonths(currentDate,
				SPManager.current_user.getDate("tokenResetDate"));
		if (months > 0) {
			return true;
		} else
			return false;

	}

	public static int DateDifferenceInMonths(Date first, Date second) {
		Calendar fDate = DateToCalendar(first);
		Calendar sDate = DateToCalendar(second);

		return fDate.get(Calendar.MONTH) - sDate.get(Calendar.MONTH);
	}

	public static String GetPrintableDate(Date date) {
		String tokens[] = TokenizeDate(date);
		return tokens[0] + ", " + tokens[2] + " " + tokens[1] + " "
				+ tokens[tokens.length - 1];
	}

	public static String[] TokenizeDate(Date date) {
		return date.toString().split(" ");
	}

	public static Calendar DateToCalendar(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static int GetUserTotalTokens() {
		int freeTokens = SPManager.current_user.getInt("freeTokens");
		int paidTokens = SPManager.current_user.getInt("paidTokens");
		return freeTokens + paidTokens;
	}

	public static void MakeAdDecision(AdView mAdView) {
		boolean showAds = SPManager.current_user.getBoolean("showAds");
		AdRequest adRequest = new AdRequest.Builder()// .addTestDevice("D6D0D8455AEA948B7342E6630A94A436")
				.build();

		if (showAds == true) {
			mAdView.loadAd(adRequest);
		} else {
			mAdView.setVisibility(View.GONE);
		}

	}

	public static String setFirstCharacterToCaps(String arg) {
		StringBuilder tokenFixer = null;
		if (!arg.equals("")) {
			char upperCaseCharacter = Character.toUpperCase(arg.charAt(0));
			tokenFixer = new StringBuilder(arg);
			tokenFixer.setCharAt(0, upperCaseCharacter);
		}
		return tokenFixer.toString();
	}

	public static String setFirstCharacterToNoCaps(String arg) {
		if (!arg.equals("")) {
			char lowerCaseCharacter = Character.toLowerCase(arg.charAt(0));
			StringBuilder tokenFixer = new StringBuilder(arg);
			tokenFixer.setCharAt(0, lowerCaseCharacter);
			return tokenFixer.toString();
		} else
			return "";

	}

	public static void ShowDialogue(String title, String data, Context cont) {
		try {
			Toast.makeText(cont, title + data, Toast.LENGTH_SHORT).show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void ShowDialogue(String title, String data, Context cont,
			int duration) {
		try {
			Toast.makeText(cont, title + data, duration).show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static boolean isEmpty(String value) {
		if (value.equals("")) {
			return true;
		}
		return false;
	}

	public static boolean passwordCheck(String password, final Context con) {

		String pass = password;
		if (pass.length() < 7) {
			Helper.ShowDialogue("Error",
					"Password must be at least 7 characters long", con);
			return false;
		} else {
			boolean lower = false;
			boolean number = false;
			for (char c : pass.toCharArray()) {
				if (Character.isLowerCase(c)) {
					lower = true;
				} else if (Character.isDigit(c)) {
					number = true;
				}
			}
			if (!lower) {
				Helper.ShowDialogue(
						"Error",
						"Password must contain at least one lowercase character",
						con);
				return false;
			} else if (!number) {
				Helper.ShowDialogue("Error",
						"Password must contain at least one number", con);
				return false;
			} else {
				return true;
			}
		}
	}

	public static Bitmap resizePostQuestionImage(Bitmap input, Context con) {
		File save_dir = Environment.getExternalStorageDirectory();
		FileOutputStream out;
		try {
			// Set the filename to teh current time.
			String filename = save_dir + "/Vote_Master_" + ".jpg";
			out = new FileOutputStream(filename);
			input.compress(Bitmap.CompressFormat.JPEG, 90, out);

			Uri imageUri = Uri.parse("file:///" + filename);
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					con.getContentResolver(), imageUri);
			return bitmap;
		} catch (Exception e) {
			return input;
		}

	}

	public static int max(double... arguments) {
		double max = 0;
		for (int i = 0; i < arguments.length; i++) {
			max = Math.max(arguments[i], max);
		}
		return (int) max;
	}

	public static boolean isSizeEqual(String[] arr, int[] arrSecond) {
		return (arr.length == arrSecond.length);
	}

	public static void LaunchActivity(Context con, Class<?> obj) {
		Intent intent = new Intent();
		intent.setClass(con, obj);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		con.startActivity(intent);
	}

	public static File getTempFile(Context cont) {
		// it will return /sdcard/image.tmp
		final File path = new File(Environment.getExternalStorageDirectory(),
				cont.getPackageName());
		if (!path.exists()) {
			path.mkdir();
		}
		return new File(path, "image.tmp");
	}

	public static String ConvertToDisplayTime(float duration) {
		duration *= 60;
		int ClampedDur = (int) duration;
		int secs = 0;
		int mins = 0;
		while (ClampedDur >= 60) {
			mins++;
			ClampedDur -= 60;
		}

		secs = ClampedDur;
		return (mins + ":" + secs);

	}

	public static String chopDecimalPlaces(float data, String format) {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(data);
	}

	public static Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 512;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	public static void PopulateSpinner(Spinner id, final Context con,
			String[] arr) {
		ArrayAdapter<String> adp = new ArrayAdapter<String>(con,
				android.R.layout.simple_list_item_1, arr);
		adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		id.setAdapter(adp);
	}

	public static Boolean AmIOnline(Context con) {
		final ConnectivityManager conMgr = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null && activeNetwork.isConnected()) {
			// notify user you are online
			return true;
		} else {
			// notify user you are not online
			return false;
		}
	}

	public static Bitmap ConvertByteArrayToBitmap(byte[] data) {
		Bitmap bmp;
		bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		return bmp;
	}

}
