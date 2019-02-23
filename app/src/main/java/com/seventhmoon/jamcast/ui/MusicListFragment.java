package com.seventhmoon.jamcast.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.utils.LogHelper;

import java.util.ArrayList;

public class MusicListFragment extends Fragment {
    private static final String TAG = LogHelper.makeLogTag(MusicListFragment.class);

    private Context fragmentContext;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;



    private MusicListFragmentListener mMusicListFragmentListener;
    private BrowseAdapter mBrowserAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogHelper.d(TAG, "fragment.onCreateView");
        View rootView = inflater.inflate(R.layout.music_list_fragment, container, false);

        //mErrorView = rootView.findViewById(R.id.playback_error);
        //mErrorMessage = (TextView) mErrorView.findViewById(R.id.error_message);

        mBrowserAdapter = new BrowseAdapter(getActivity());

        ListView listView = (ListView) rootView.findViewById(R.id.list_view);
        listView.setAdapter(mBrowserAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //checkForUserVisibleErrors(false);
                //MediaBrowserCompat.MediaItem item = mBrowserAdapter.getItem(position);
                //mMusicListFragmentListener.onMediaItemSelected(item);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");

        if (isRegister && mReceiver != null) {
            try {
                fragmentContext.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
        }

        super.onDestroyView();
    }

    // An adapter for showing the list of browsed MediaItem's
    private static class BrowseAdapter extends ArrayAdapter<MediaBrowserCompat.MediaItem> {

        public BrowseAdapter(Activity context) {
            super(context, R.layout.media_list_swipe_item, new ArrayList<MediaBrowserCompat.MediaItem>());
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            MediaBrowserCompat.MediaItem item = getItem(position);
            return MediaItemViewHolder.setupListView((Activity) getContext(), convertView, parent,
                    item, position);
        }
    }

    public interface MusicListFragmentListener extends MediaBrowserProvider {
        void onMediaItemSelected(MediaBrowserCompat.MediaItem item);
        void setToolbarTitle(CharSequence title);
    }
}
