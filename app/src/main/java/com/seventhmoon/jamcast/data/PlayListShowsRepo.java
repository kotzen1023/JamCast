package com.seventhmoon.jamcast.data;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class PlayListShowsRepo {
    private final PlayListDao playListDao;

    @Inject
    public PlayListShowsRepo(PlayListDao playListDao) {
        this.playListDao = playListDao;
    }

    public Single<List<PlayList>> getAllLists() {
        return playListDao.getAll();
    }

    public void insertPlayList(String title, String desc) {
        PlayList favoriteList = new PlayList(title, desc);

        playListDao.insertList(favoriteList);
    }

    public void deletePlayList(String title) {

    }
}
