package com.seventhmoon.jamcast.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = PlayList.class, version = 1, exportSchema = false)
public abstract class PlayListDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "playlist.db";


    //private static volatile PlayListDatabase INSTANCE;

    public abstract PlayListDao playListDao();

    /*public static PlayListDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PlayListDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            PlayListDatabase.class, DATABASE_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }*/
}
