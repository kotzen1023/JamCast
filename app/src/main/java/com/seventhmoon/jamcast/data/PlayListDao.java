package com.seventhmoon.jamcast.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface PlayListDao {

    @Query("SELECT * FROM "+PlayList.TABLE_NAME)
    Single<List<PlayList>> getAll();

    @Insert
    void insertAll(List<PlayList> playLists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertList(PlayList... playLists);

    @Delete
    void delete(PlayList playList);

    @Update
    public void updateList(List<PlayList> playLists);

}
