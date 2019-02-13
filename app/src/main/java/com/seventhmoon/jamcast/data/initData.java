package com.seventhmoon.jamcast.data;

import java.util.ArrayList;

public class initData {
    public static boolean isInit = false;
    public static ArrayList<Song> songList = new ArrayList<>();
    //for add songs to list
    public static ArrayList<String> searchList = new ArrayList<>();
    public static ArrayList<Song> addSongList = new ArrayList<>();
    public static boolean songListChanged = false;
    public static int screen_width;
    public static int screen_height;
}
