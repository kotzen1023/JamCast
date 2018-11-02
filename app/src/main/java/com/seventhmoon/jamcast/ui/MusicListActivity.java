package com.seventhmoon.jamcast.ui;

import android.os.Bundle;
import android.util.Log;

import com.seventhmoon.jamcast.R;

public class MusicListActivity extends BaseActivity {

    private static final String TAG = MusicListActivity.class.getName();

    private Bundle mVoiceSearchParams;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity onCreate");


        setContentView(R.layout.activity_music_list);
        initializeToolbar(2);


    }
}
