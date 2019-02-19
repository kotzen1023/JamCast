package com.seventhmoon.jamcast.data;

import android.arch.persistence.room.Room;
import android.content.Context;


import com.seventhmoon.jamcast.utils.LogHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlayListModule {
    private static final String TAG = LogHelper.makeLogTag(PlayListModule.class);
    @Singleton
    @Provides
    public AppDatabase providePlayListDatabase(Context context) {

        LogHelper.e(TAG, "=> providePlayListDatabase");

        return Room.databaseBuilder(context,
                AppDatabase.class, AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }

    @Singleton
    @Provides
    public PlayListDao providePlayListDao(AppDatabase  playListDatabase) {
        LogHelper.e(TAG, "=> providePlayListDao");

        return playListDatabase.playListDao();
    }
}
