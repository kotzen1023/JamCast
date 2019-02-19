package com.seventhmoon.jamcast.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = PlayList.TABLE_NAME)
public class PlayList {

    public static final String TABLE_NAME = "playlist";

    @PrimaryKey(autoGenerate = true)
    private int list_id;
    @ColumnInfo(name = "list_title") // column name will be "list_title" instead of "title" in table
    private String title;
    @ColumnInfo(name = "list_desc")
    private String desc;

    public PlayList(String title, String desc) {
        //this.list_id = list_id;
        this.title = title;
        this.desc = desc;
    }

    /*public int getList_id() {
        return list_id;
    }

    public void setList_id(int list_id) {
        this.list_id = list_id;
    }*/

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayList)) return false;

        PlayList playList = (PlayList) o;

        if (list_id != playList.list_id) return false;
        return title != null ? title.equals(playList.title) : playList.title == null;
    }

    @Override
    public int hashCode() {
        int result = list_id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PlayList{" +
                "list_id=" + list_id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
