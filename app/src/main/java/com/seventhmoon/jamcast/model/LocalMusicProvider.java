package com.seventhmoon.jamcast.model;

import android.support.v4.media.MediaMetadataCompat;

import com.seventhmoon.jamcast.utils.LogHelper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocalMusicProvider {
    private static final String TAG = LogHelper.makeLogTag(LocalMusicProvider.class);

    private MusicProviderSource mSource;

    // Categorized caches for music track data:
    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByGenre;
    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;

    private final Set<String> mFavoriteTracks;

    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private volatile MusicProvider.State mCurrentState = MusicProvider.State.NON_INITIALIZED;

    public LocalMusicProvider() {
        this(new LocalJSONSource());
    }

    public LocalMusicProvider(MusicProviderSource source) {
        mSource = source;
        mMusicListByGenre = new ConcurrentHashMap<>();
        mMusicListById = new ConcurrentHashMap<>();
        mFavoriteTracks = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    }
}
