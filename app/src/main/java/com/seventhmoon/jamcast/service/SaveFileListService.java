package com.seventhmoon.jamcast.service;

import android.app.IntentService;
import android.content.Intent;

import android.util.Log;

import com.seventhmoon.jamcast.data.Constants;



import static com.seventhmoon.jamcast.data.FileOperation.append_record_local;
import static com.seventhmoon.jamcast.data.FileOperation.clear_record;

import static com.seventhmoon.jamcast.data.initData.localSongList;
import static com.seventhmoon.jamcast.data.initData.songList;

public class SaveFileListService extends IntentService {
    private static final String TAG = SaveFileListService.class.getName();

    public SaveFileListService() {
        super("SaveFileListService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");



    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "Handle");

        String filename = intent.getStringExtra("FILENAME");

        if (intent.getAction() != null) {
            if (intent.getAction().equals(Constants.ACTION.FILE_SAVE_LIST_ACTION)) {
                Log.i(TAG, "FILE_SAVE_LIST_ACTION");
            }

            String mediaId = filename;

            /*if (songList.size() > 0) {
                clear_record(filename);
                String record = "";
                for (int i=0;i<songList.size(); i++) {
                    if (i==0) {
                        record = songList.get(i).getPath();

                    } else {
                        record += "|"+songList.get(i).getPath();
                    }
                }

                append_record_local(record, filename);
            }*/

            if (localSongList.get(mediaId) != null) {
                clear_record(filename);

                String record = "";
                for (int i=0;i<localSongList.get(mediaId).size(); i++) {
                    if (i==0) {
                        record = localSongList.get(mediaId).get(i).getPath();

                    } else {
                        record += "|"+localSongList.get(mediaId).get(i).getPath();
                    }
                }

                append_record_local(record, filename);
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent intent = new Intent(Constants.ACTION.FILE_SAVE_LIST_COMPLETE);
        sendBroadcast(intent);
    }
}
