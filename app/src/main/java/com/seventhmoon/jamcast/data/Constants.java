package com.seventhmoon.jamcast.data;

public class Constants {
    public interface ACTION {
        String ADD_SONG_LIST_COMPLETE = "com.seventhmoon.JamCast.AddSongListComplete";
        String ADD_SONG_LIST_CHANGE = "com.seventhmoon.JamCast.AddSongListChange";
        String GET_PLAY_COMPLETE = "com.seventhmoon.JamCast.GetPlayComplete";
        String GET_SONGLIST_ACTION = "com.seventhmoon.JamCast.GetSongListAction";

        String GET_SEARCHLIST_ACTION = "com.seventhmoon.JamCast.GetSearchListAction";

        String GET_SONGLIST_FROM_RECORD_FILE_COMPLETE = "com.seventhmoon.JamCast.GetSongListFromRecordFileComplete";
        String SAVE_SONGLIST_ACTION = "com.seventhmoon.JamCast.SaveSongListAction";
        String SAVE_SONGLIST_TO_FILE_COMPLETE = "com.seventhmoon.JamCast.SaveSongListToFileComplete";

        String MEDIAPLAYER_STATE_PLAYED = "com.seventhmoon.JamCast.MediaPlayerStatePlayed";
        String MEDIAPLAYER_STATE_PAUSED = "com.seventhmoon.JamCast.MediaPlayerStatePaused";

        String FILE_CHOOSE_CONFIRM_BUTTON_SHOW = "com.seventhmoon.JamCast.FileChooseConfirmButtonShow";
        String FILE_CHOOSE_CONFIRM_BUTTON_HIDE = "com.seventhmoon.JamCast.FileChooseConfirmButtonHide";

        String FILE_CHOOSE_PATH_CHANGE = "com.seventhmoon.JamCast.FileChoosePathChange";
        String FILE_SAVE_LIST_START = "com.seventhmoon.JamCast.FileSaveListStart";
        String FILE_SAVE_LIST_ACTION = "com.seventhmoon.JamCast.FileSaveListAction";
        String FILE_SAVE_LIST_COMPLETE = "com.seventhmoon.JamCast.FileSaveListComplete";
        String FILE_LOAD_LIST_ACTION = "com.seventhmoon.JamCast.FileLoadListAction";
        String FILE_LOAD_LIST_COMPLETE = "com.seventhmoon.JamCast.FileLoadListComplete";

        String ACTION_USER_LIST_ADD = "com.seventhmoon.JamCast.ActionUserListAdd";
        String ACTION_USER_LIST_DELETE = "com.seventhmoon.JamCast.ActionUserListDelete";

        String ACTION_USER_LIST_ADD_SONG_SHOW = "com.seventhmoon.JamCast.ActionUserListAddSongShow";
        String ACTION_USER_LIST_ADD_SONG_HIDE = "com.seventhmoon.JamCast.ActionUserListAddSongHide";
    }
}
