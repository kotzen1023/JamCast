package com.seventhmoon.jamcast.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.data.Constants;
import com.seventhmoon.jamcast.utils.LogHelper;
import com.seventhmoon.jamcast.utils.MediaIDHelper;

public class MediaItemViewHolder {
    private static final String TAG = LogHelper.makeLogTag(MediaItemViewHolder.class);

    public static final int STATE_INVALID = -1;
    public static final int STATE_NONE = 0;
    public static final int STATE_PLAYABLE = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_PLAYING = 3;

    private static ColorStateList sColorStatePlaying;
    private static ColorStateList sColorStateNotPlaying;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private View btnDelete;
    private SwipeLayout swipeLayout;
    private LinearLayout bottom_wrapper;
    private static Context mContext;

    // Returns a view for use in media item list.
    static View setupListView(Activity activity, View convertView, ViewGroup parent,
                              MediaBrowserCompat.MediaItem item, int position) {
        if (sColorStateNotPlaying == null || sColorStatePlaying == null) {
            initializeColorStateLists(activity);
        }

        mContext = activity;

        MediaItemViewHolder holder;

        Integer cachedState = STATE_INVALID;

        if (convertView == null) {
            //convertView = LayoutInflater.from(activity)
            //        .inflate(R.layout.media_list_item, parent, false);
            convertView = LayoutInflater.from(activity)
                    .inflate(R.layout.media_list_swipe_item, parent, false);
            holder = new MediaItemViewHolder();
            holder.mImageView = (ImageView) convertView.findViewById(R.id.play_eq);
            holder.mTitleView = (TextView) convertView.findViewById(R.id.title);
            holder.mDescriptionView = (TextView) convertView.findViewById(R.id.description);
            holder.btnDelete = convertView.findViewById(R.id.delete_list);
            holder.swipeLayout = convertView.findViewById(R.id.swipe_layout_list);
            holder.bottom_wrapper = convertView.findViewById(R.id.bottom_wrapper_list);

            convertView.setTag(holder);
        } else {
            holder = (MediaItemViewHolder) convertView.getTag();
            cachedState = (Integer) convertView.getTag(R.id.tag_mediaitem_state_cache);
        }

        MediaDescriptionCompat description = item.getDescription();
        holder.mTitleView.setText(description.getTitle());
        holder.mDescriptionView.setText(description.getSubtitle());

        //swipe layout
        holder.btnDelete.setOnClickListener(onDeleteListener(position, holder));
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, holder.bottom_wrapper);

        // If the state of convertView is different, we need to adapt the view to the
        // new state.
        int state = getMediaItemState(activity, item);
        if (cachedState == null || cachedState != state) {
            Drawable drawable = getDrawableByState(activity, state);
            if (drawable != null) {
                holder.mImageView.setImageDrawable(drawable);
                holder.mImageView.setVisibility(View.VISIBLE);
            } else {
                holder.mImageView.setVisibility(View.GONE);
            }
            convertView.setTag(R.id.tag_mediaitem_state_cache, state);
        }

        return convertView;
    }

    private static void initializeColorStateLists(Context ctx) {
        sColorStateNotPlaying = ColorStateList.valueOf(ctx.getResources().getColor(
                R.color.media_item_icon_not_playing));
        sColorStatePlaying = ColorStateList.valueOf(ctx.getResources().getColor(
                R.color.media_item_icon_playing));
    }

    public static Drawable getDrawableByState(Context context, int state) {
        if (sColorStateNotPlaying == null || sColorStatePlaying == null) {
            initializeColorStateLists(context);
        }

        switch (state) {
            case STATE_PLAYABLE:
                Drawable pauseDrawable = ContextCompat.getDrawable(context,
                        R.drawable.ic_play_arrow_black_36dp);
                DrawableCompat.setTintList(pauseDrawable, sColorStateNotPlaying);
                return pauseDrawable;
            case STATE_PLAYING:
                AnimationDrawable animation = (AnimationDrawable)
                        ContextCompat.getDrawable(context, R.drawable.ic_equalizer_white_36dp);
                DrawableCompat.setTintList(animation, sColorStatePlaying);
                animation.start();
                return animation;
            case STATE_PAUSED:
                Drawable playDrawable = ContextCompat.getDrawable(context,
                        R.drawable.ic_equalizer1_white_36dp);
                DrawableCompat.setTintList(playDrawable, sColorStatePlaying);
                return playDrawable;
            default:
                return null;
        }
    }

    public static int getMediaItemState(Activity context, MediaBrowserCompat.MediaItem mediaItem) {
        int state = STATE_NONE;
        // Set state to playable first, then override to playing or paused state if needed
        if (mediaItem.isPlayable()) {
            state = STATE_PLAYABLE;
            if (MediaIDHelper.isMediaItemPlaying(context, mediaItem)) {
                state = getStateFromController(context);
            }
        }

        return state;
    }

    public static int getStateFromController(Activity context) {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(context);
        PlaybackStateCompat pbState = controller.getPlaybackState();
        if (pbState == null ||
                pbState.getState() == PlaybackStateCompat.STATE_ERROR) {
            return MediaItemViewHolder.STATE_NONE;
        } else if (pbState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            return  MediaItemViewHolder.STATE_PLAYING;
        } else {
            return MediaItemViewHolder.STATE_PAUSED;
        }
    }

    private static View.OnClickListener onDeleteListener(final int position, final MediaItemViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHelper.e(TAG, "delete click "+position);

                Intent scanIntent = new Intent();
                scanIntent.setAction(Constants.ACTION.ACTION_USER_LIST_DELETE);
                scanIntent.putExtra("INDEX", String.valueOf(position));
                mContext.sendBroadcast(scanIntent);

                /*
                android.app.AlertDialog.Builder confirmdialog = new android.app.AlertDialog.Builder(mContext);
                confirmdialog.setIcon(R.drawable.ic_warning_black_48dp);
                confirmdialog.setTitle(mContext.getResources().getString(R.string.action_allocation_msg));
                confirmdialog.setMessage(mContext.getResources().getString(R.string.delete)+":\n"+items.get(position).getWork_order()+" ?");
                confirmdialog.setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        holder.swipeLayout.close();

                        String[] p_no = items.get(position).getWork_order().split("#");
                        String iss_no = p_no[0];

                        Intent deleteIntent = new Intent(mContext, DeleteMessageNoService.class);
                        deleteIntent.setAction(Constants.ACTION.ACTION_ALLOCATION_HANDLE_MSG_DELETE_ACTION);
                        deleteIntent.putExtra("MESSAGE_NO", iss_no);
                        deleteIntent.putExtra("USER_NO", emp_no);
                        deleteIntent.putExtra("DELETE_INDEX", String.valueOf(position));
                        mContext.startService(deleteIntent);


                    }
                });
                confirmdialog.setNegativeButton(mContext.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // btnScan.setVisibility(View.VISIBLE);
                        // btnConfirm.setVisibility(View.GONE);

                    }
                });
                confirmdialog.show();*/




            }
        };
    }
}
