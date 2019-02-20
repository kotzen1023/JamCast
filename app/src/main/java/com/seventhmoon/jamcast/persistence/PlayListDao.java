package com.seventhmoon.jamcast.persistence;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;



import java.util.List;


@Dao
public interface PlayListDao {

    @Query("SELECT * FROM "+ PlayList.TABLE_NAME)
    List<PlayList> getAll();

    //@Insert
    //void insertAll(List<PlayList> playLists);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PlayList playList);

    @Delete
    void delete(PlayList playList);

    @Update
    public void update(PlayList playList);


    //@Insert
    //void insertAll(List<PlayList> playLists);


}
