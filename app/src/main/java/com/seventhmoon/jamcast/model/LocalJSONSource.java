package com.seventhmoon.jamcast.model;

import android.support.v4.media.MediaMetadataCompat;

import com.seventhmoon.jamcast.data.Song;
import com.seventhmoon.jamcast.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;

import static com.seventhmoon.jamcast.data.initData.addSongList;

public class LocalJSONSource implements MusicProviderSource {
    private static final String TAG = LogHelper.makeLogTag(LocalJSONSource.class);

    //private static final String JSON_MUSIC = "music";
    /*private static final String JSON_TITLE = "title";
    private static final String JSON_ALBUM = "album";
    private static final String JSON_ARTIST = "artist";
    private static final String JSON_GENRE = "genre";
    private static final String JSON_SOURCE = "source";
    private static final String JSON_IMAGE = "image";
    private static final String JSON_TRACK_NUMBER = "trackNumber";
    private static final String JSON_TOTAL_TRACK_COUNT = "totalTrackCount";
    private static final String JSON_DURATION = "duration";*/

    @Override
    public Iterator<MediaMetadataCompat> iterator() {
        try {
            LogHelper.e(TAG, "iterator start");

            //int slashPos = CATALOG_URL.lastIndexOf('/');
            //String path = CATALOG_URL.substring(0, slashPos + 1);
            //JSONObject jsonObj = fetchJSONFromUrl(CATALOG_URL);
            ArrayList<MediaMetadataCompat> tracks = new ArrayList<>();
            /*if (jsonObj != null) {
                JSONArray jsonTracks = jsonObj.getJSONArray(JSON_MUSIC);

                if (jsonTracks != null) {
                    for (int j = 0; j < jsonTracks.length(); j++) {
                        tracks.add(buildFromJSON(jsonTracks.getJSONObject(j), path));
                    }
                }
            }*/
            if (addSongList.size() > 0) {
                for (int i=0; i<addSongList.size(); i++) {
                    tracks.add(buildFromList(addSongList.get(i)));
                }
            }

            LogHelper.e(TAG, "iterator end");

            return tracks.iterator();
        } catch (Exception e) {
            LogHelper.e(TAG, e, "Could not retrieve music list");
            throw new RuntimeException("Could not retrieve music list", e);
        }
    }

    private MediaMetadataCompat buildFromList(Song song) {

        //LogHelper.e(TAG, "[buildFromJSON start]");

        //String title = json.getString(JSON_TITLE);
        String title = song.getName();
        String album = "";
        String artist = "";
        String genre = "";
        String source = "";
        String iconUrl = "";
        int trackNumber = 0;
        int totalTrackCount = 1;
        int duration = (int) song.getDuration_u() / 1000; // ms

        //LogHelper.e(TAG, "Found music track: ", json);

        // Media is stored relative to JSON file
        /*if (!source.startsWith("http")) {
            source = basePath + source;
        }
        if (!iconUrl.startsWith("http")) {
            iconUrl = basePath + iconUrl;
        }*/
        // Since we don't have a unique ID in the server, we fake one using the hashcode of
        // the music source. In a real world app, this could come from the server.
        String id = String.valueOf(source.hashCode());

        // Adding the music source to the MediaMetadata (and consequently using it in the
        // mediaSession.setMetadata) is not a good idea for a real world music app, because
        // the session metadata can be accessed by notification listeners. This is done in this
        // sample for convenience only.
        //noinspection ResourceType

        //LogHelper.e(TAG, "[buildFromJSON end]");

        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
                //.putString(MusicProviderSource.CUSTOM_METADATA_TRACK_SOURCE, source)

                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, iconUrl)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, trackNumber)
                .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, totalTrackCount)
                .build();
    }

    private JSONObject fetchJSONFromList(String urlString) throws JSONException {

        LogHelper.e(TAG, "fetchJSONFromList start");

        BufferedReader reader = null;
        try {
            URLConnection urlConnection = new URL(urlString).openConnection();
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream(), "iso-8859-1"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }



            return new JSONObject(sb.toString());
        } catch (JSONException e) {

            throw e;
        } catch (Exception e) {
            LogHelper.e(TAG, "Failed to parse the json for media list", e);

            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            LogHelper.e(TAG, "fetchJSONFromList end");
        }
    }
}
