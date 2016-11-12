package com.youtube.activity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnFullscreenListener;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailLoader.ErrorReason;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.youtube.data.VideoBean;
import com.youtube.navdrawerhelper.NavDrawerItem;
import com.youtube.navdrawerhelper.NavDrawerListAdapter;
import com.youtube.parse.DownloadRequestBean;
import com.youtube.parse.ParseOperation;
import com.youtube.parse.SPManager;
import com.youtube.util.DownloadRequest;
import com.youtube.util.Helper;
import com.youtube.util.JsonParser;
import com.youtube.util.SearchRequest;
import com.youtube.util.VideoInfoRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.parse.ParseException;

public final class VideoListDashboardActivity extends Activity implements
		OnFullscreenListener, ListView.OnItemClickListener {

	public static Context con;
	/** The duration of the animation sliding up the video in portrait. */
	private static final int ANIMATION_DURATION_MILLIS = 300;
	/**
	 * The padding between the video list and the video in landscape
	 * orientation.
	 */
	private static final int LANDSCAPE_VIDEO_PADDING_DP = 5;
	private VideoListFragment listFragment;
	private VideoFragment videoFragment;

	private View videoBox;
	private View closeButton;
	private View searchContainer;

	private boolean isFullscreen;
	EditText SearchTextYT;

	// navigation bar variables
	private DrawerLayout mDrawerLayout;
	public static ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] mNavigationTitles;
	private TypedArray navMenuIcons;
	private ArrayList<NavDrawerItem> navDrawerItems;
	public NavDrawerListAdapter adapter;

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			selectItem(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_dashboard, menu);

		return super.onCreateOptionsMenu(menu);
	}

	void OpenStorageFolder() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		Uri uri = Uri.parse(SPManager.MUSIC_ROOT_DIR);
		// Uri uri =
		// Uri.parse(Environment.getExternalStorageDirectory().getPath() +
		// "/YouMp3/");
		intent.setDataAndType(uri, "file/*");
		startActivity(Intent.createChooser(intent, "Open folder"));

		intent.setDataAndType(uri, "text/csv");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		} else if (item.getItemId() == R.id.dashboard_view_queue) {
			Helper.LaunchActivity(getApplicationContext(), QueueActivity.class);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// ParseOperation.LogOutCurrentUser(getApplicationContext());
		// finish();
	}

	// -------------------------------------------

	@Override
	protected void onResume() {
		super.onResume();

	}

	AdView mAdView;

	public void PlaySongsFromAPlaylist(int playListID) {
		String[] proj = { MediaStore.Audio.Playlists.Members.AUDIO_ID,
				MediaStore.Audio.Playlists.Members.ARTIST,
				MediaStore.Audio.Playlists.Members.TITLE,
				MediaStore.Audio.Playlists.Members._ID };

		Cursor songsWithingAPlayList = getContentResolver().query(
				MediaStore.Audio.Playlists.Members.getContentUri("external",
						playListID), proj, null, null,
				MediaStore.Audio.Playlists.DATE_MODIFIED);

		if (songsWithingAPlayList != null) {
			if (songsWithingAPlayList.moveToFirst()) {
				do {
					Log.v("Vipul",
							songsWithingAPlayList.getString(songsWithingAPlayList
									.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)));
				} while (songsWithingAPlayList.moveToNext());
			}
		}
		/*
		 * int theSongIDIwantToPlay = 0; // PLAYING FROM THE FIRST SONG if
		 * (songsWithingAPlayList != null) {
		 * songsWithingAPlayList.moveToPosition(theSongIDIwantToPlay); String
		 * DataStream = songsWithingAPlayList.getString(3);
		 * PlayMusic(DataStream); songsWithingAPlayList.close(); }
		 */
	}

	public ArrayList<String> GetAllDevicePlayLists() {
		ArrayList<String> arrayList = new ArrayList<String>();
		String[] proj = { "*" };
		Uri tempPlaylistURI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

		// In the next line 'this' points to current Activity.
		// If you want to use the same code in other java file then activity,
		// then use an instance of any activity in place of 'this'.

		Cursor playListCursor = getContentResolver().query(tempPlaylistURI,
				proj, null, null, null);
		if (playListCursor == null) {
			System.out
					.println("Not having any Playlist on phone --------------");
			return arrayList;// don't have list on phone
		}

		System.gc();
		String playListName = null;
		HashMap<String, Integer> allPlaylists = new HashMap<String, Integer>();
		System.out
				.println(">>>>>>>  CREATING AND DISPLAYING LIST OF ALL CREATED PLAYLIST  <<<<<<");
		for (int i = 0; i < playListCursor.getCount(); i++) {
			playListCursor.moveToPosition(i);
			playListName = playListCursor.getString(playListCursor
					.getColumnIndex("name"));
			System.out.println("> " + i + "  : " + playListName);
			arrayList.add(playListName);
			allPlaylists.put(playListName,
					playListCursor.getInt(playListCursor.getColumnIndex("id")));
		}

		if (playListCursor != null)
			playListCursor.close();

		return arrayList;
	}

	public static Cursor GetTrackListFromPlaylist(Context context, long plid) {
		ContentResolver mCR = context.getContentResolver();
		return mCR.query(MediaStore.Audio.Playlists.Members.getContentUri(
				"external", plid), null, null, null,
				MediaStore.Audio.Playlists.DATE_MODIFIED);

		// mCR.query(context,
		// MediaStore.Audio.Playlists.Members.getContentUri("external", plid),
		// , null, null, MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER);
	}

	/*
	 * public static void addTracksToPlaylist(final long id, List<MediaData>
	 * tracks, final Context context) { int count = getPlaylistSize(id,
	 * context); ContentValues[] values = new ContentValues[tracks.size()]; for
	 * (int i = 0; i < tracks.size(); i++) { values[i] = new ContentValues();
	 * values[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, i + count +
	 * 1); values[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, tracks
	 * .get(i).getId()); } Uri uri =
	 * MediaStore.Audio.Playlists.Members.getContentUri("external", id);
	 * ContentResolver resolver = context.getContentResolver(); int num =
	 * resolver.bulkInsert(uri, values);
	 * resolver.notifyChange(Uri.parse("content://media"), null); // return
	 * String.format(context.getString(R.string.ADDED_TO_PLAYLIST), // num,
	 * context.getString(R.string.CURRENT)); }
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Helper.GetFreeTokens()) {
			SPManager.current_user.put("freeTokens", 25);
			SPManager.current_user.put("tokenResetDate",
					Helper.AddMonthsToDate(new Date(), 1));
			try {
				SPManager.current_user.save();
				Helper.ShowDialogue("You got " + SPManager.FreeMothlyTokens
						+ " free monthly tokens", "", getApplicationContext());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		setContentView(R.layout.video_list_demo);

		mAdView = (AdView) findViewById(R.id.view_dashboard_banner);
		Helper.MakeAdDecision(mAdView);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		// actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		con = getApplicationContext();
		searchContainer = findViewById(R.id.video_fragment_container);

		listFragment = (VideoListFragment) getFragmentManager()
				.findFragmentById(R.id.list_fragment);

		videoFragment = (VideoFragment) getFragmentManager().findFragmentById(
				R.id.video_fragment_container);

		videoBox = findViewById(R.id.video_box);
		closeButton = findViewById(R.id.close_button);

		videoBox.setVisibility(View.INVISIBLE);

		// Setup the search button functionality
		// Attach the listener on the button
		Button searchYT = (Button) findViewById(R.id.bt_search);
		SearchTextYT = (EditText) findViewById(R.id.et_search);

		searchYT.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {

				String searchKeyword = SearchTextYT.getText().toString();
				if (searchKeyword.equals("") == false)
					VideoListFragment.UpdateSearchList(searchKeyword);

			}
		});

		Button downloadYT = (Button) findViewById(R.id.button_download);
		downloadYT.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (VideoListFragment.SelectedPosition != -1) {
					String selectedVidId = VideoListFragment.VIDEO_LIST
							.get(VideoListFragment.SelectedPosition).videoId;

					String selectedVidTitle = VideoListFragment.VIDEO_LIST
							.get(VideoListFragment.SelectedPosition).title;
					// Check time limit
					float duration = VideoInfoRequest.isTimeLimitLessThan(
							SPManager.DL_Video_Duration_Limit, selectedVidId);

					if (duration > SPManager.DL_Video_Duration_Limit) {
						Toast.makeText(
								getApplicationContext(),
								"Please select a video with less than"
										+ SPManager.DL_Video_Duration_Limit
										+ " minutes in duration.",
								Toast.LENGTH_LONG).show();

						onClickClose(null);
						return;
					}
					DownloadRequest dr = new DownloadRequest(
							VideoListDashboardActivity.this);
					// Make the request object;
					DownloadRequestBean drb = new DownloadRequestBean();
					drb.videoId = selectedVidId;
					drb.title = selectedVidTitle;
					drb.duration = duration + "";
					drb.executioner = "none";

					ParseOperation.PostQueueRequest(drb, con);

					onClickClose(null);
					return;
				} // end if correct position

			} // end onClick function.
		});

		// ---------------------------- ADD A DRAWER

		// load navigation drawer items
		mNavigationTitles = getResources().getStringArray(
				R.array.navigation_options);
		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.addHeaderView(getLayoutInflater().inflate(
				R.layout.drawer_activity_header, null));

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Add Poll
		navDrawerItems.add(new NavDrawerItem(mNavigationTitles[0], navMenuIcons
				.getResourceId(0, -1)));
		// Search
		navDrawerItems.add(new NavDrawerItem(mNavigationTitles[1], navMenuIcons
				.getResourceId(1, -1), true, "(tokens: "
				+ Helper.GetUserTotalTokens() + ")"));
		navDrawerItems.add(new NavDrawerItem(mNavigationTitles[2], navMenuIcons
				.getResourceId(2, -1)));
		// Community
		navDrawerItems.add(new NavDrawerItem(mNavigationTitles[3], navMenuIcons
				.getResourceId(3, -1)));
		// Profile
		navDrawerItems.add(new NavDrawerItem(mNavigationTitles[4], navMenuIcons
				.getResourceId(4, -1)));
		// Feedback
		navDrawerItems.add(new NavDrawerItem(mNavigationTitles[5], navMenuIcons
				.getResourceId(5, -1)));

		// Fake Data
		// navDrawerItems.add(new NavDrawerItem(mNavigationTitles[8],
		// navMenuIcons
		// .getResourceId(8, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(this, navDrawerItems);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerList.setAdapter(adapter);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer_blue, // nav menu toggle icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();

			}

			public void onDrawerOpened(View drawerView) {
				// calling onPrepareOptionsMenu() to hide action bar icons
				mDrawerList.bringToFront();
				mDrawerLayout.requestLayout();
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// ---------------------------- DRAWER END
		layout();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		layout();
	}

	@Override
	public void onFullscreen(boolean isFullscreen) {
		this.isFullscreen = isFullscreen;
		mDrawerList.setVisibility(View.GONE);
		layout();
	}

	/**
	 * Sets up the layout programatically for the three different states.
	 * Portrait, landscape or fullscreen+landscape. This has to be done
	 * programmatically because we handle the orientation changes ourselves in
	 * order to get fluent fullscreen transitions, so the xml layout resources
	 * do not get reloaded.
	 */
	private void layout() {
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

		listFragment.getView().setVisibility(
				isFullscreen ? View.GONE : View.VISIBLE);
		listFragment.setLabelVisibility(isPortrait);
		closeButton.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

		if (isFullscreen) {
			videoBox.setTranslationY(0); // Reset any translation that was
											// applied in portrait.
			setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
			setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT,
					Gravity.TOP | Gravity.LEFT);
		} else if (isPortrait) {
			setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
			setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
			setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT,
					Gravity.BOTTOM);
		} else {
			videoBox.setTranslationY(0); // Reset any translation that was
											// applied in portrait.
			int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
			setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
			int videoWidth = screenWidth - screenWidth / 4
					- dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
			setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
			setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
					Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		}
	}

	@SuppressLint("NewApi")
	public void onClickClose(@SuppressWarnings("unused") View view) {
		listFragment.getListView().clearChoices();
		listFragment.getListView().requestLayout();
		videoFragment.pause();
		videoBox.animate().translationYBy(videoBox.getHeight())
				.setDuration(ANIMATION_DURATION_MILLIS)
				.withEndAction(new Runnable() {
					@Override
					public void run() {
						videoBox.setVisibility(View.INVISIBLE);
					}
				});
	}

	/**
	 * A fragment that shows a static list of videos.
	 */
	public static final class VideoListFragment extends ListFragment {
		public static int SelectedPosition = -1;
		private static List<VideoEntry> VIDEO_LIST;
		static {
			List<VideoEntry> list = new ArrayList<VideoEntry>();

			final String searchReq = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=term&key={"
					+ SPManager.YOUTUBE_DEVELOPER_KEY + "}";
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitNetwork().build();
			StrictMode.setThreadPolicy(policy);
			/*
			 * String res = SearchRequest.GET("vienna waits for you"); if
			 * (res.equals("") == false) { List<VideoBean> myList =
			 * JsonParser.ParseRequest(res);
			 * 
			 * for (int i = 0; i < myList.size(); i++) { VideoBean item =
			 * (VideoBean) myList.get(i); if (item.LiveBCContent.equals("none"))
			 * list.add(new VideoEntry(item.Title, item.VideoId)); } VIDEO_LIST
			 * = list; // Collections.unmodifiableList(list); } else
			 */
			VIDEO_LIST = new ArrayList<VideoListDashboardActivity.VideoEntry>();
		}

		private static PageAdapter adapter;
		private View videoBox;

		static void UpdateSearchList(String keyword) {
			List<VideoEntry> list = new ArrayList<VideoEntry>();

			String res = SearchRequest.GET(keyword);
			List<VideoBean> myList = JsonParser.ParseRequest(res);

			for (int i = 0; i < myList.size(); i++) {
				VideoBean item = (VideoBean) myList.get(i);
				// if (item.LiveBCContent.equals("none"))
				list.add(new VideoEntry(item.Title, item.VideoId));

			}
			VIDEO_LIST.clear();
			VIDEO_LIST.addAll(list);
			// adapter = new PageAdapter(VideoListDemoActivity.con, VIDEO_LIST);
			adapter.notifyDataSetChanged();
			SelectedPosition = -1;

		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			adapter = new PageAdapter(getActivity(), VIDEO_LIST);

		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			videoBox = getActivity().findViewById(R.id.video_box);
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			setListAdapter(adapter);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			String videoId = VIDEO_LIST.get(position).videoId;

			SelectedPosition = position;
			VideoFragment videoFragment = (VideoFragment) getFragmentManager()
					.findFragmentById(R.id.video_fragment_container);
			videoFragment.setVideoId(videoId);

			// The videoBox is INVISIBLE if no video was previously selected, so
			// we need to show it now.
			if (videoBox.getVisibility() != View.VISIBLE) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					// Initially translate off the screen so that it can be
					// animated in from below.
					videoBox.setTranslationY(videoBox.getHeight());
				}
				videoBox.setVisibility(View.VISIBLE);
			}

			// If the fragment is off the screen, we animate it in.
			if (videoBox.getTranslationY() > 0) {
				videoBox.animate().translationY(0)
						.setDuration(ANIMATION_DURATION_MILLIS);
			}
		}

		@Override
		public void onDestroyView() {
			super.onDestroyView();

			adapter.releaseLoaders();
		}

		public void setLabelVisibility(boolean visible) {
			adapter.setLabelVisibility(visible);
		}

	}

	/**
	 * Adapter for the video list. Manages a set of YouTubeThumbnailViews,
	 * including initializing each of them only once and keeping track of the
	 * loader of each one. When the ListFragment gets destroyed it releases all
	 * the loaders.
	 */
	private static final class PageAdapter extends BaseAdapter {

		private final List<VideoEntry> entries;
		private final List<View> entryViews;
		private final Map<YouTubeThumbnailView, YouTubeThumbnailLoader> thumbnailViewToLoaderMap;
		private final LayoutInflater inflater;
		private final ThumbnailListener thumbnailListener;

		private boolean labelsVisible;

		public PageAdapter(Context context, List<VideoEntry> entries) {
			this.entries = entries;

			entryViews = new ArrayList<View>();
			thumbnailViewToLoaderMap = new HashMap<YouTubeThumbnailView, YouTubeThumbnailLoader>();
			inflater = LayoutInflater.from(context);
			thumbnailListener = new ThumbnailListener();

			labelsVisible = true;
		}

		public void releaseLoaders() {
			for (YouTubeThumbnailLoader loader : thumbnailViewToLoaderMap
					.values()) {
				loader.release();
			}
		}

		public void setLabelVisibility(boolean visible) {
			labelsVisible = visible;
			for (View view : entryViews) {
				view.findViewById(R.id.text).setVisibility(
						visible ? View.VISIBLE : View.GONE);
			}
		}

		@Override
		public int getCount() {
			return entries.size();
		}

		@Override
		public VideoEntry getItem(int position) {
			return entries.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			VideoEntry entry = entries.get(position);

			// There are three cases here
			if (view == null) {
				// 1) The view has not yet been created - we need to initialize
				// the YouTubeThumbnailView.
				view = inflater
						.inflate(R.layout.video_list_item, parent, false);
				YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view
						.findViewById(R.id.thumbnail);
				thumbnail.setTag(entry.videoId);
				thumbnail.initialize(SPManager.YOUTUBE_DEVELOPER_KEY,
						thumbnailListener);
			} else {
				YouTubeThumbnailView thumbnail = (YouTubeThumbnailView) view
						.findViewById(R.id.thumbnail);
				YouTubeThumbnailLoader loader = thumbnailViewToLoaderMap
						.get(thumbnail);
				if (loader == null) {
					// 2) The view is already created, and is currently being
					// initialized. We store the
					// current videoId in the tag.
					thumbnail.setTag(entry.videoId);
				} else {
					// 3) The view is already created and already initialized.
					// Simply set the right videoId
					// on the loader.
					thumbnail.setImageResource(R.drawable.loading_thumbnail);
					loader.setVideo(entry.videoId);
				}
			}
			TextView label = ((TextView) view.findViewById(R.id.text));
			label.setText(entry.title);
			label.setVisibility(labelsVisible ? View.VISIBLE : View.GONE);
			return view;
		}

		private final class ThumbnailListener implements
				YouTubeThumbnailView.OnInitializedListener,
				YouTubeThumbnailLoader.OnThumbnailLoadedListener {

			@Override
			public void onInitializationSuccess(YouTubeThumbnailView view,
					YouTubeThumbnailLoader loader) {
				loader.setOnThumbnailLoadedListener(this);
				thumbnailViewToLoaderMap.put(view, loader);
				view.setImageResource(R.drawable.loading_thumbnail);
				String videoId = (String) view.getTag();
				loader.setVideo(videoId);
			}

			@Override
			public void onInitializationFailure(YouTubeThumbnailView view,
					YouTubeInitializationResult loader) {
				view.setImageResource(R.drawable.no_thumbnail);
			}

			@Override
			public void onThumbnailLoaded(YouTubeThumbnailView view,
					String videoId) {
			}

			@Override
			public void onThumbnailError(YouTubeThumbnailView view,
					ErrorReason errorReason) {
				view.setImageResource(R.drawable.no_thumbnail);
			}
		}

	}

	public static final class VideoFragment extends YouTubePlayerFragment
			implements OnInitializedListener {

		private YouTubePlayer player;
		private String videoId;

		public static VideoFragment newInstance() {
			return new VideoFragment();
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			initialize(SPManager.YOUTUBE_DEVELOPER_KEY, this);
		}

		@Override
		public void onDestroy() {
			if (player != null) {
				player.release();
			}
			super.onDestroy();
		}

		public void setVideoId(String videoId) {
			if (videoId != null && !videoId.equals(this.videoId)) {
				this.videoId = videoId;
				if (player != null) {
					player.cueVideo(videoId);
					player.play();
				}
			}
		}

		public void pause() {
			if (player != null) {
				player.pause();
			}
		}

		@Override
		public void onInitializationSuccess(Provider provider,
				YouTubePlayer player, boolean restored) {
			this.player = player;
			player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
			// player.setOnFullscreenListener((VideoListDashboardActivity)
			// getActivity());
			if (!restored && videoId != null) {
				player.cueVideo(videoId);
			}

			// TODO(Han)
			// THIS SHUTS DOWN THE FULLSCREEN MODE.
			// Tell the player you want to control the fullscreen change
			player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
			// Tell the player how to control the change
			player.setOnFullscreenListener(new OnFullscreenListener() {
				@Override
				public void onFullscreen(boolean arg0) {
					// do full screen stuff here, or don't. I started a
					// YouTubeStandalonePlayer
					// to go to full screen
				}
			});

		}

		@Override
		public void onInitializationFailure(Provider provider,
				YouTubeInitializationResult result) {
			this.player = null;
		}

	}

	private static final class VideoEntry {
		private final String title;
		private final String videoId;

		public VideoEntry(String text, String videoId) {
			this.title = text;
			this.videoId = videoId;
		}
	}

	// Utility methods for layouting.

	private int dpToPx(int dp) {
		return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
	}

	private static void setLayoutSize(View view, int width, int height) {
		LayoutParams params = view.getLayoutParams();
		params.width = width;
		params.height = height;
		view.setLayoutParams(params);
	}

	private static void setLayoutSizeAndGravity(View view, int width,
			int height, int gravity) {
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view
				.getLayoutParams();
		params.width = width;
		params.height = height;
		params.gravity = gravity;
		view.setLayoutParams(params);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		selectItem(position);
	}

	private void selectItem(int position) {
		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		position -= 1;
		if (position < 0)
			return;
		if (mNavigationTitles[position].equalsIgnoreCase("My Queue")) {
			Helper.LaunchActivity(getApplicationContext(), QueueActivity.class);
		} else if (mNavigationTitles[position].equalsIgnoreCase("Upgrades")) {
			Intent intent = new Intent();
			intent.setClass(con, UpgradeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else if (mNavigationTitles[position].equalsIgnoreCase("Info")) {
			Helper.LaunchActivity(getApplicationContext(), InfoActivity.class);
		} else if (mNavigationTitles[position].equalsIgnoreCase("Feedback")) {
			Helper.LaunchActivity(getApplicationContext(),
					FeedbackActivity.class);
		} else if (mNavigationTitles[position].equalsIgnoreCase("Share")) {

			Bitmap bm = BitmapFactory.decodeResource(getResources(),
					R.drawable.share_icon);
			Intent share = Helper.shareOneImage(bm, getApplicationContext());
			startActivity(Intent.createChooser(share, "Share to"));

		} else if (mNavigationTitles[position].equalsIgnoreCase("Log Out")) {
			ParseOperation.LogOutCurrentUser(getApplicationContext());
			Helper.LaunchActivity(getApplicationContext(), LoginActivity.class);
			finish();
		}
		mDrawerLayout.closeDrawer(mDrawerList);
	}
}
