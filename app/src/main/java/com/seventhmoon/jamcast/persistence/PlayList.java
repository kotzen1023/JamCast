package com.seventhmoon.jamcast.persistence;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.UUID;

@Entity(tableName = PlayList.TABLE_NAME)
public class PlayList {

    public static final String TABLE_NAME = "playlist";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "list_id")
    private String listId;

    @ColumnInfo(name = "list_title") // column name will be "list_title" instead of "title" in table
    private String title;

    @ColumnInfo(name = "list_desc")
    private String desc;

    /*@Ignore
    public PlayList(String mTitle, String mDesc) {
        listId = UUID.randomUUID().toString();
        title = mTitle;
        desc = mDesc;
    }*/

    public PlayList(String listId, String title, String desc) {
        this.listId = listId;
        this.title = title;
        this.desc = desc;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
