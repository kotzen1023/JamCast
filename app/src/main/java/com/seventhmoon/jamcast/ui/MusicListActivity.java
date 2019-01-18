package com.seventhmoon.jamcast.ui;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;
import android.util.Log;

import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.data.Song;
import com.seventhmoon.jamcast.data.SongArrayAdapter;
import com.seventhmoon.jamcast.utils.LogHelper;

import java.util.ArrayList;

import static com.seventhmoon.jamcast.data.initData.addSongList;
import static com.seventhmoon.jamcast.data.initData.searchList;

public class MusicListActivity extends ListBaseActivity  {

    private static final String TAG = LogHelper.makeLogTag(MusicListActivity.class);
    private static final String SAVED_MEDIA_ID="com.seventhmoon.jamcast.MEDIA_ID";
    private static final String FRAGMENT_TAG = "music_list_container";
    public static final String EXTRA_START_FULLSCREEN =
            "com.seventhmoon.jamcast.EXTRA_START_FULLSCREEN";

    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.seventhmoon.jamcast.CURRENT_MEDIA_DESCRIPTION";

    private Bundle mVoiceSearchParams;

    //public static ArrayList<Song> songList = new ArrayList<>();
    //for add songs to list
    //public static ArrayList<String> searchList = new ArrayList<>();
    //public static ArrayList<Song> addSongList = new ArrayList<>();
    SongArrayAdapter songArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.e(TAG, "MusicListActivity onCreate");


        setContentView(R.layout.activity_music_list);
        initializeToolbar(2);
        initializeFromParams(savedInstanceState, getIntent());

        LogHelper.e(TAG, "check addSongList size = "+addSongList.size());




        // Only check if a full screen player is needed on the first time:
        /*if (savedInstanceState == null) {
            Log.d(TAG, "savedInstanceState = null");
            startFullScreenActivityIfNeeded(getIntent());
        }*/
    }

    @Override
    protected void onDestroy() {
        LogHelper.e(TAG, "MusicListActivity onDestroy");



        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String mediaId = getMediaId();
        if (mediaId != null) {
            outState.putString(SAVED_MEDIA_ID, mediaId);
        }
        super.onSaveInstanceState(outState);
    }











    /*@Override
    public void setToolbarTitle(CharSequence title) {
        Log.d(TAG, "Setting toolbar title to "+ title);
        if (title == null) {
            title = getString(R.string.app_name);
        }
        setTitle(title);
    }*/

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent, intent=" + intent);
        initializeFromParams(null, intent);
        startFullScreenActivityIfNeeded(intent);
    }

    private void startFullScreenActivityIfNeeded(Intent intent) {
        Log.e(TAG, "startFullScreenActivityIfNeeded");
        if (intent != null && intent.getBooleanExtra(EXTRA_START_FULLSCREEN, false)) {
            Log.e(TAG, "start FullScreenPlayerActivity");
            Intent fullScreenIntent = new Intent(this, FullScreenPlayerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION,
                            intent.getParcelableExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION));
            startActivity(fullScreenIntent);
        }
    }

    protected void initializeFromParams(Bundle savedInstanceState, Intent intent) {
        String mediaId = null;
        // check if we were started from a "Play XYZ" voice search. If so, we save the extras
        // (which contain the query details) in a parameter, so we can reuse it later, when the
        // MediaSession is connected.
        if (intent.getAction() != null
                && intent.getAction().equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
            mVoiceSearchParams = intent.getExtras();
            Log.d(TAG, "Starting from voice search query="+
                    mVoiceSearchParams.getString(SearchManager.QUERY));
        } else {
            if (savedInstanceState != null) {
                // If there is a saved media ID, use it
                mediaId = savedInstanceState.getString(SAVED_MEDIA_ID);
            }
        }
        //navigateToBrowser(mediaId);
        navigateToBrowser();
    }

    //private void navigateToBrowser(String mediaId) {
    private void navigateToBrowser() {

        //Log.d(TAG, "navigateToBrowser, mediaId=" + mediaId);
        /*MediaBrowserFragment fragment = getBrowseFragment();

        if (fragment == null || !TextUtils.equals(fragment.getMediaId(), mediaId)) {
            fragment = new MediaBrowserFragment();
            fragment.setMediaId(mediaId);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                    R.animator.slide_in_from_left, R.animator.slide_out_to_right);
            transaction.replace(R.id.container, fragment, FRAGMENT_TAG);
            // If this is not the top level media (root), we add it to the fragment back stack,
            // so that actionbar toggle and Back will work appropriately:
            if (mediaId != null) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }*/
    }

    public String getMediaId() {
        MediaBrowserFragment fragment = getBrowseFragment();
        if (fragment == null) {
            return null;
        }
        return fragment.getMediaId();
    }

    private MediaBrowserFragment getBrowseFragment() {
        return (MediaBrowserFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    /*@Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onMediaItemSelected, mediaId=" + item.getMediaId());
        if (item.isPlayable()) {
            MediaControllerCompat.getMediaController(MusicListActivity.this).getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        } else if (item.isBrowsable()) {
            navigateToBrowser(item.getMediaId());
        } else {
            Log.w(TAG, "Ignoring MediaItem that is neither browsable nor playable: "+
                    "mediaId="+ item.getMediaId());
        }
    }*/

    /*@Override
    protected void onMediaControllerConnected() {
        if (mVoiceSearchParams != null) {
            // If there is a bootstrap parameter to start from a search query, we
            // send it to the media session and set it to null, so it won't play again
            // when the activity is stopped/started or recreated:
            String query = mVoiceSearchParams.getString(SearchManager.QUERY);
            MediaControllerCompat.getMediaController(MusicListActivity.this).getTransportControls()
                    .playFromSearch(query, mVoiceSearchParams);
            mVoiceSearchParams = null;
        }
        getBrowseFragment().onConnected();
    }*/
}
