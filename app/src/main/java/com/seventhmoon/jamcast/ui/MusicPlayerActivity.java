package com.seventhmoon.jamcast.ui;


import android.app.FragmentTransaction;
import android.app.SearchManager;

import android.content.Intent;

import android.provider.MediaStore;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.utils.LogHelper;



public class MusicPlayerActivity extends BaseActivity
        implements MediaBrowserFragment.MediaFragmentListener {

    private static final String TAG = LogHelper.makeLogTag(MusicPlayerActivity.class);
    private static final String SAVED_MEDIA_ID="com.seventhmoon.android.jamcast.MEDIA_ID";
    private static final String FRAGMENT_TAG = "music_list_container";

    public static final String EXTRA_START_FULLSCREEN =
            "com.seventhmoon.android.jamcast.EXTRA_START_FULLSCREEN";

    /**
     * Optionally used with {@link #EXTRA_START_FULLSCREEN} to carry a MediaDescription to
     * the {@link FullScreenPlayerActivity}, speeding up the screen rendering
     * while the {@link android.support.v4.media.session.MediaControllerCompat} is connecting.
     */
    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.seventhmoon.android.jamcast.CURRENT_MEDIA_DESCRIPTION";



    private Bundle mVoiceSearchParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.d(TAG, "MusicPlayerActivity onCreate");

        setContentView(R.layout.activity_player);

        initializeToolbar(0);
        initializeFromParams(savedInstanceState, getIntent());



        // Only check if a full screen player is needed on the first time:
        if (savedInstanceState == null) {
            Log.d(TAG, "savedInstanceState = null");
            startFullScreenActivityIfNeeded(getIntent());
        }


    }

    @Override
    protected void onDestroy() {
        LogHelper.d(TAG, "MusicPlayerActivity onDestroy");



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

    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem item) {
        Log.d(TAG, "onMediaItemSelected, mediaId=" + item.getMediaId());
        if (item.isPlayable()) {
            MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getTransportControls()
                    .playFromMediaId(item.getMediaId(), null);
        } else if (item.isBrowsable()) {
            navigateToBrowser(item.getMediaId());
        } else {
            Log.w(TAG, "Ignoring MediaItem that is neither browsable nor playable: "+
                    "mediaId="+ item.getMediaId());
        }
    }

    @Override
    public void setToolbarTitle(CharSequence title) {
        LogHelper.d(TAG, "Setting toolbar title to "+ title);
        if (title == null) {
            title = getString(R.string.app_name);
        }
        setTitle(title);
    }

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

        LogHelper.e(TAG, "mediaId = "+mediaId);

        navigateToBrowser(mediaId);
    }

    private void navigateToBrowser(String mediaId) {
        Log.d(TAG, "navigateToBrowser, mediaId=" + mediaId);
        MediaBrowserFragment fragment = getBrowseFragment();

        if (fragment == null || !TextUtils.equals(fragment.getMediaId(), mediaId)) {
            fragment = new MediaBrowserFragment();
            fragment.setMediaId(mediaId);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                    R.animator.slide_in_from_left, R.animator.slide_out_to_right);
            transaction.replace(R.id.music_list_container, fragment, FRAGMENT_TAG);
            // If this is not the top level media (root), we add it to the fragment back stack,
            // so that actionbar toggle and Back will work appropriately:
            if (mediaId != null) {
                transaction.addToBackStack(null);
            }
            transaction.commit();
        }
    }

    public String getMediaId() {
        MediaBrowserFragment fragment = getBrowseFragment();
        if (fragment == null) {
            return null;
        }

        LogHelper.e(TAG, "fragment.getMediaId() = "+fragment.getMediaId());

        return fragment.getMediaId();
    }

    private MediaBrowserFragment getBrowseFragment() {
        return (MediaBrowserFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    protected void onMediaControllerConnected() {
        if (mVoiceSearchParams != null) {
            // If there is a bootstrap parameter to start from a search query, we
            // send it to the media session and set it to null, so it won't play again
            // when the activity is stopped/started or recreated:
            String query = mVoiceSearchParams.getString(SearchManager.QUERY);
            MediaControllerCompat.getMediaController(MusicPlayerActivity.this).getTransportControls()
                    .playFromSearch(query, mVoiceSearchParams);
            mVoiceSearchParams = null;
        }
        getBrowseFragment().onConnected();
    }



}
