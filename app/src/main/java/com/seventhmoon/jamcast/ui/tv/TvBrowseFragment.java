package com.seventhmoon.jamcast.ui.tv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.BrowseSupportFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;

import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.utils.LogHelper;
import com.seventhmoon.jamcast.utils.QueueHelper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class TvBrowseFragment extends BrowseSupportFragment {
    private static final String TAG = LogHelper.makeLogTag(TvBrowseFragment.class);

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mListRowAdapter;
    private MediaFragmentListener mMediaFragmentListener;

    private MediaBrowserCompat mMediaBrowser;
    private HashSet<String> mSubscribedMediaIds;

    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    if (metadata != null) {
                        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
                        long activeQueueId;
                        if (mediaController.getPlaybackState() == null) {
                            activeQueueId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
                        } else {
                            activeQueueId = mediaController.getPlaybackState().getActiveQueueItemId();
                        }
                        updateNowPlayingList(mediaController.getQueue(), activeQueueId);
                        mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                    }
                }

                @Override
                public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                    // queue has changed somehow
                    MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());

                    long activeQueueId;
                    if (mediaController.getPlaybackState() == null) {
                        activeQueueId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
                    } else {
                        activeQueueId = mediaController.getPlaybackState().getActiveQueueItemId();
                    }
                    updateNowPlayingList(queue, activeQueueId);
                    mRowsAdapter.notifyArrayItemRangeChanged(0, mRowsAdapter.size());
                }
            };

    private void updateNowPlayingList(List<MediaSessionCompat.QueueItem> queue, long activeQueueId) {
        if (mListRowAdapter != null) {
            mListRowAdapter.clear();
            if (activeQueueId != MediaSessionCompat.QueueItem.UNKNOWN_ID) {
                Iterator<MediaSessionCompat.QueueItem> iterator = queue.iterator();
                while (iterator.hasNext()) {
                    MediaSessionCompat.QueueItem queueItem = iterator.next();
                    if (activeQueueId != queueItem.getQueueId()) {
                        iterator.remove();
                    } else {
                        break;
                    }
                }
            }
            mListRowAdapter.addAll(0, queue);
        }
    }

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {

                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {

                    mRowsAdapter.clear();
                    CardPresenter cardPresenter = new CardPresenter(getActivity());

                    for (int i = 0; i < children.size(); i++) {
                        MediaBrowserCompat.MediaItem item = children.get(i);
                        String title = (String) item.getDescription().getTitle();
                        HeaderItem header = new HeaderItem(i, title);
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                        mRowsAdapter.add(new ListRow(header, listRowAdapter));

                        if (item.isPlayable()) {
                            listRowAdapter.add(item);
                        } else if (item.isBrowsable()) {
                            subscribeToMediaId(item.getMediaId(),
                                    new RowSubscriptionCallback(listRowAdapter));
                        } else {
                            LogHelper.e(TAG, "Item should be playable or browsable.");
                        }
                    }

                    MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());

                    if (mediaController.getQueue() != null
                            && !mediaController.getQueue().isEmpty()) {
                        // add Now Playing queue to Browse Home
                        HeaderItem header = new HeaderItem(
                                children.size(), getString(R.string.now_playing));
                        mListRowAdapter = new ArrayObjectAdapter(cardPresenter);
                        mRowsAdapter.add(new ListRow(header, mListRowAdapter));
                        long activeQueueId;
                        if (mediaController.getPlaybackState() == null) {
                            activeQueueId = MediaSessionCompat.QueueItem.UNKNOWN_ID;
                        } else {
                            activeQueueId = mediaController.getPlaybackState()
                                    .getActiveQueueItemId();
                        }
                        updateNowPlayingList(mediaController.getQueue(), activeQueueId);
                    }

                    mRowsAdapter.notifyArrayItemRangeChanged(0, children.size());
                }

                @Override
                public void onError(@NonNull String id) {
                    LogHelper.e(TAG, "SubscriptionCallback subscription onError, id=" + id);
                }
            };

    /**
     * This callback fills content for a single Row in the BrowseFragment.
     */
    private class RowSubscriptionCallback extends MediaBrowserCompat.SubscriptionCallback {

        private final ArrayObjectAdapter mListRowAdapter;

        public RowSubscriptionCallback(ArrayObjectAdapter listRowAdapter) {
            mListRowAdapter = listRowAdapter;
        }

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                                     @NonNull List<MediaBrowserCompat.MediaItem> children) {
            mListRowAdapter.clear();
            for (MediaBrowserCompat.MediaItem item : children) {
                mListRowAdapter.add(item);
            }
            mListRowAdapter.notifyArrayItemRangeChanged(0, children.size());
        }

        @Override
        public void onError(@NonNull String id) {
            LogHelper.e(TAG, "RowSubscriptionCallback subscription onError, id=", id);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogHelper.d(TAG, "onActivityCreated");

        mSubscribedMediaIds = new HashSet<>();

        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.tv_search_button));

        loadRows();
        setupEventListeners();
    }

    private void loadRows() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder viewHolder, Object clickedItem,
                                      RowPresenter.ViewHolder viewHolder2, Row row) {
                if (clickedItem instanceof MediaBrowserCompat.MediaItem) {
                    MediaBrowserCompat.MediaItem item = (MediaBrowserCompat.MediaItem) clickedItem;
                    if (item.isPlayable()) {
                        LogHelper.w(TAG, "Ignoring click on PLAYABLE MediaItem in" +
                                "TvBrowseFragment. mediaId=", item.getMediaId());
                        return;
                    }
                    Intent intent = new Intent(getActivity(), TvVerticalGridActivity.class);
                    intent.putExtra(TvBrowseActivity.SAVED_MEDIA_ID, item.getMediaId());
                    intent.putExtra(TvBrowseActivity.BROWSE_TITLE,
                            item.getDescription().getTitle());
                    startActivity(intent);

                } else if (clickedItem instanceof MediaSessionCompat.QueueItem) {
                    MediaSessionCompat.QueueItem item = (MediaSessionCompat.QueueItem) clickedItem;
                    MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());

                    if (!QueueHelper.isQueueItemPlaying(getActivity(), item)) {
                        mediaController.getTransportControls()
                                .skipToQueueItem(item.getQueueId());
                    }

                    Intent intent = new Intent(getActivity(), TvPlaybackActivity.class);
                    startActivity(intent);
                }
            }
        });

        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogHelper.d(TAG, "In-app search");
                // TODO: implement in-app search
                Intent intent = new Intent(getActivity(), TvBrowseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mMediaFragmentListener = (MediaFragmentListener) activity;
        } catch (ClassCastException ex) {
            LogHelper.e(TAG, "TVBrowseFragment can only be attached to an activity that " +
                    "implements MediaFragmentListener", ex);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            for (String mediaId : mSubscribedMediaIds) {
                mMediaBrowser.unsubscribe(mediaId);
            }
            mSubscribedMediaIds.clear();
        }
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
        if (mediaController != null) {
            mediaController.unregisterCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMediaFragmentListener = null;
    }

    public void initializeWithMediaId(String mediaId) {
        LogHelper.d(TAG, "subscribeToData");
        // fetch browsing information to fill the listview:
        mMediaBrowser = mMediaFragmentListener.getMediaBrowser();

        if (mediaId == null) {
            mediaId = mMediaBrowser.getRoot();
        }

        subscribeToMediaId(mediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
        if (mediaController != null) {
            mediaController.registerCallback(mMediaControllerCallback);
        }
    }

    private void subscribeToMediaId(String mediaId, MediaBrowserCompat.SubscriptionCallback callback) {
        if (mSubscribedMediaIds.contains(mediaId)) {
            mMediaBrowser.unsubscribe(mediaId);
        } else {
            mSubscribedMediaIds.add(mediaId);
        }
        mMediaBrowser.subscribe(mediaId, callback);
    }

    public interface MediaFragmentListener {
        MediaBrowserCompat getMediaBrowser();
    }
}
