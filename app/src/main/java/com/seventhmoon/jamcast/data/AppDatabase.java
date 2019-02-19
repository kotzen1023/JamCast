package com.seventhmoon.jamcast.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {PlayList.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "playlist_db";

    public abstract PlayListDao playListDao();
}
