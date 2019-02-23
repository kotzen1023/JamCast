package com.seventhmoon.jamcast.service;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import com.seventhmoon.jamcast.data.Constants;
import com.seventhmoon.jamcast.data.Song;

import java.io.File;
import java.io.IOException;

import static com.seventhmoon.jamcast.data.FileOperation.read_record_local;
import static com.seventhmoon.jamcast.data.initData.addSongList;
import static com.seventhmoon.jamcast.data.initData.searchList;
import static com.seventhmoon.jamcast.data.initData.songList;

public class LoadFileListService extends IntentService {
    private static final String TAG = LoadFileListService.class.getName();

    public LoadFileListService() {
        super("LoadFileListService");
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
            if (intent.getAction().equals(Constants.ACTION.FILE_LOAD_LIST_ACTION)) {
                Log.i(TAG, "FILE_LOAD_LIST_ACTION");
            }

            String message = read_record_local(filename);
            //Log.d(TAG, "message = "+ message);
            String msg[] = message.split("\\|");

            if (msg.length > 0) {
                songList.clear();
                for (int i=0; i<msg.length;i++) {
                    String s = getAudioInfo(msg[i]);

                }
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        Intent intent = new Intent(Constants.ACTION.FILE_LOAD_LIST_COMPLETE);
        sendBroadcast(intent);
    }

    public String getAudioInfo(String filePath) {
        Log.e(TAG, "<getAudioInfo>");
        String infoMsg = null;
        boolean hasFrameRate = false;

        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(filePath);// the adresss location of the sound on sdcard.
        } catch (IOException e) {

            e.printStackTrace();
        }



        File file = new File(filePath);
        Log.d(TAG, "file name: "+file.getName());

        if (mex != null) {

            try {
                MediaFormat mf = mex.getTrackFormat(0);
                Log.d(TAG, "file: "+file.getName()+" mf = "+mf.toString());
                infoMsg = mf.getString(MediaFormat.KEY_MIME);
                Log.d(TAG, "type: "+infoMsg);

                if (infoMsg.contains("audio")) {

                    Log.d(TAG, "duration(us): " + mf.getLong(MediaFormat.KEY_DURATION));
                    Log.d(TAG, "channel: " + mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
                    if (mf.toString().contains("channel-mask")) {
                        Log.d(TAG, "channel mask: " + mf.getInteger(MediaFormat.KEY_CHANNEL_MASK));
                    }
                    if (mf.toString().contains("aac-profile")) {
                        Log.d(TAG, "aac profile: " + mf.getInteger(MediaFormat.KEY_AAC_PROFILE));
                    }

                    Log.d(TAG, "sample rate: " + mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));

                    if (infoMsg != null) {
                        Song song = new Song();
                        song.setName(file.getName());
                        song.setPath(file.getAbsolutePath());
                        //song.setDuration((int)(mf.getLong(MediaFormat.KEY_DURATION)/1000));
                        song.setDuration_u(mf.getLong(MediaFormat.KEY_DURATION));
                        song.setChannel((byte) mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT));
                        song.setSample_rate(mf.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                        song.setMark_a(0);
                        song.setMark_b((int) (mf.getLong(MediaFormat.KEY_DURATION) / 1000));
                        songList.add(song);

                    }
                } else if (infoMsg.contains("video")) { //video
                    try {
                        Log.d(TAG, "frame rate : " + mf.getInteger(MediaFormat.KEY_FRAME_RATE));
                        hasFrameRate = true;
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    Log.d(TAG, "height : " + mf.getInteger(MediaFormat.KEY_HEIGHT));
                    Log.d(TAG, "width : " + mf.getInteger(MediaFormat.KEY_WIDTH));
                    Log.d(TAG, "duration(us): " + mf.getLong(MediaFormat.KEY_DURATION));

                    /*if (infoMsg != null) {
                        VideoItem video = new VideoItem();
                        video.setName(file.getName());
                        video.setPath(file.getAbsolutePath());
                        if (hasFrameRate)
                            video.setFrame_rate(mf.getInteger(MediaFormat.KEY_FRAME_RATE));

                        video.setHeight(mf.getInteger(MediaFormat.KEY_HEIGHT));
                        video.setWidth(mf.getInteger(MediaFormat.KEY_WIDTH));
                        video.setDuration_u( mf.getLong(MediaFormat.KEY_DURATION));
                        video.setMark_a(0);
                        video.setMark_b((int) (mf.getLong(MediaFormat.KEY_DURATION) / 1000));
                        addVideoList.add(video);


                    }*/

                } else {
                    Log.e(TAG, "Unknown type");
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "file: "+file.getName()+" not support");
        }

        Log.e(TAG, "</getAudioInfo>");




        return infoMsg;
    }
}
