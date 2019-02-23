package com.seventhmoon.jamcast.data;

import android.view.MenuItem;

import com.seventhmoon.jamcast.persistence.PlayList;
import com.seventhmoon.jamcast.persistence.PlayListDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class initData {
    public static boolean isInit = false;
    public static ArrayList<Song> songList = new ArrayList<>();
    //for add songs to list
    public static ArrayList<String> searchList = new ArrayList<>();
    public static ArrayList<Song> addSongList = new ArrayList<>();
    public static boolean songListChanged = false;
    public static int screen_width;
    public static int screen_height;
    //public static PlayListModule playListModule;
    public static PlayListDatabase db;

    public static String current_mMediaId;
    public static MenuItem globalMenuItemAdd;
    public static MenuItem globalMenuItemAddList;

    public static List<PlayList> playList = new ArrayList<>();
    public static HashMap<String, ArrayList<Song>> localSongList = new HashMap<>();
}
