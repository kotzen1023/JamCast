package com.seventhmoon.jamcast.ui;

import android.app.ActivityOptions;
import android.app.Service;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.data.Constants;
import com.seventhmoon.jamcast.data.FileChooseArrayAdapter;
import com.seventhmoon.jamcast.utils.LogHelper;

import java.io.File;

import static com.seventhmoon.jamcast.data.initData.addSongList;
import static com.seventhmoon.jamcast.data.initData.searchList;
import static com.seventhmoon.jamcast.data.initData.songList;

public class FileChooseListActivity extends BaseActivity {

    private static final String TAG = LogHelper.makeLogTag(FileChooseListActivity.class);
    private static final String SAVED_MEDIA_ID="com.seventhmoon.jamcast.MEDIA_ID";
    private static final String FRAGMENT_TAG = "file_list_container";
    public static final String EXTRA_START_FULLSCREEN =
            "com.seventhmoon.jamcast.EXTRA_START_FULLSCREEN";

    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.seventhmoon.jamcast.CURRENT_MEDIA_DESCRIPTION";

    private Bundle mVoiceSearchParams;


    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;
    private Context context;


    //public static ListView fileChooselistView;
    //public static Button confirm;
    //private File currentDir;
    //private Menu actionmenu;

    //private static BroadcastReceiver mReceiver = null;
    //private static boolean isRegister = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.e(TAG, "MusicListActivity onCreate");


        setContentView(R.layout.activity_file_choose);

        context = getApplicationContext();

        initializeToolbar(3);
        initializeFromParams(savedInstanceState, getIntent());

        //fileChooselistView = findViewById(R.id.listViewFileChoose);

        // Only check if a full screen player is needed on the first time:
        /*if (savedInstanceState == null) {
            Log.d(TAG, "savedInstanceState = null");
            startFullScreenActivityIfNeeded(getIntent());
        }*/


        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() != null) {
                    if (intent.getAction().equalsIgnoreCase(Constants.ACTION.ADD_SONG_LIST_COMPLETE)) {
                        Log.d(TAG, "receive ADD_SONG_LIST_COMPLETE !");

                        LogHelper.e(TAG, "=== addSongList ===");
                        for (int i=0; i<addSongList.size(); i++) {
                            LogHelper.d(TAG, addSongList.get(i).getName());
                        }
                        LogHelper.e(TAG, "=== addSongList ===");
                        //check if search list's song was exist in songList




                        Bundle extras = ActivityOptions.makeCustomAnimation(
                                context, R.anim.fade_in, R.anim.fade_out).toBundle();

                        Class activityClass = null;
                        activityClass = MusicListActivity.class;
                        startActivity(new Intent(context, activityClass), extras);
                        finish();
                    }
                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.ADD_SONG_LIST_COMPLETE);

            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
    }

    @Override
    protected void onDestroy() {
        LogHelper.e(TAG, "MusicListActivity onDestroy");

        if (isRegister && mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
            Log.d(TAG, "unregisterReceiver mReceiver");
        }



        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //String mediaId = getMediaId();
        //if (mediaId != null) {
        //    outState.putString(SAVED_MEDIA_ID, mediaId);
        //}
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
        navigateToBrowser();
    }

    private void navigateToBrowser() {
        Log.d(TAG, "navigateToBrowser");

        //FileChooseBrowserFragment fragment = getFileChooseBrowserFragment();

        Fragment fragment = null;
        Class fragmentClass=null;

        fragmentClass = FileChooseBrowserFragment.class;

        if (fragmentClass != null) {

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "Exception");
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            fragmentManager.beginTransaction().replace(R.id.file_list_container, fragment).commitAllowingStateLoss();

            //fragment = new FileChooseBrowserFragment();
            //fragment.setMediaId(mediaId);
            //FragmentTransaction transaction = getFragmentManager().beginTransaction();
            //transaction.replace(R.id.container, (Fragment)fragment).commitAllowingStateLoss();
            //transaction.setCustomAnimations(
            //        R.animator.slide_in_from_right, R.animator.slide_out_to_left,
            //       R.animator.slide_in_from_left, R.animator.slide_out_to_right);
            //transaction.replace(R.id.container, fragment, FRAGMENT_TAG);
            // If this is not the top level media (root), we add it to the fragment back stack,
            // so that actionbar toggle and Back will work appropriately:
            //if (mediaId != null) {
            //    transaction.addToBackStack(null);
            //}
            //transaction.commit();
        }


        /*Fragment fragment = null;
        Class fragmentClass=null;

        fragmentClass = FileChooseBrowserFragment.class;

        if (fragmentClass != null ) {

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //fragment = new FileChooseBrowserFragment();
            //fragment.setMediaId(mediaId);
            FragmentManager fragmentManager = getSupportFragmentManager();

            android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();

            transaction.setCustomAnimations(
                    R.animator.slide_in_from_right, R.animator.slide_out_to_left,
                    R.animator.slide_in_from_left, R.animator.slide_out_to_right);

            fragmentManager.beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();

            //replace(R.id.flContent, fragment).commitAllowingStateLoss();
            // If this is not the top level media (root), we add it to the fragment back stack,
            // so that actionbar toggle and Back will work appropriately:
            //if (mediaId != null) {
            //    transaction.addToBackStack(null);
            //}
            transaction.commit();
        }*/
    }

    private FileChooseBrowserFragment getFileChooseBrowserFragment() {

        //getSupportFragmentManager().findFragmentById(R.id.file_list_container);

        //return  getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        return (FileChooseBrowserFragment) getSupportFragmentManager().findFragmentById(R.id.file_list_container);
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
