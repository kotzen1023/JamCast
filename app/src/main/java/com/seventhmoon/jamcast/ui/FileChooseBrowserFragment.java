package com.seventhmoon.jamcast.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.data.Constants;
import com.seventhmoon.jamcast.data.FileChooseArrayAdapter;
import com.seventhmoon.jamcast.data.FileChooseItem;
import com.seventhmoon.jamcast.utils.LogHelper;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.seventhmoon.jamcast.ui.ActionBarCastActivity.setPath;

public class FileChooseBrowserFragment extends Fragment {
    private static final String TAG = LogHelper.makeLogTag(FileChooseBrowserFragment.class);

    public static FileChooseArrayAdapter fileChooseArrayAdapter;

    public static ListView fileChooseListView;
    private View mErrorView;
    private TextView mErrorMessage;
    private static Context fragmentContext;
    public static Button fileChooseConfirm;
    public static File currentDir;

    public static boolean FileChooseLongClick = false;
    public static boolean FileChooseSelectAll = false;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogHelper.d(TAG, "fragment.onCreateView");

        fragmentContext = getContext();

        View rootView = inflater.inflate(R.layout.file_choose_list, container, false);

        mErrorView = rootView.findViewById(R.id.playback_error);
        mErrorMessage = mErrorView.findViewById(R.id.error_message);


        fileChooseListView = rootView.findViewById(R.id.listViewFileChoose);

        fileChooseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileChooseItem o = fileChooseArrayAdapter.getItem(position);

                if (o != null) {

                    if (o.getPath() != null && (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up"))) {
                        currentDir = new File(o.getPath());
                        fill(currentDir);

                        //MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                        FileChooseLongClick = false;
                        FileChooseSelectAll = false;
                        //menuItem.setTitle("Select all");


                        for (int i = 0; i < fileChooseListView.getCount(); i++) {
                            FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                            if (fileChooseItem != null) {

                                if (fileChooseItem.getCheckBox() != null) {
                                    fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                                    fileChooseItem.getCheckBox().setChecked(false);
                                }
                                fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                            }

                        }
                    } else {
                        //onFileClick(o);
                        Log.d(TAG, "click " + o.getName());
                    }
                }
            }
        });

        fileChooseListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e(TAG, "position = " + position + ", size = " + listView.getCount());
                FileChooseLongClick = true;
                //FileChooseItem fileChooseItem = (FileChooseItem) fileChooseArrayAdapter.getItem(position);
                //Log.i(TAG, "name = "+fileChooseItem.getName());
                //Log.e(TAG, "ck = " + fileChooseItem.getCheckBox());
                //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);

                for(int i=0;i<fileChooseListView.getCount(); i++) {
                    FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                    if (fileChooseItem != null) {

                        if (fileChooseItem.getCheckBox() != null) {
                            //Log.e(TAG, "set item[" + i + "] visible");
                            if (!fileChooseItem.getName().equals(".."))
                                fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                            else
                                fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                        }
                    }
                }


                return false;
            }
        });

        fileChooseConfirm = rootView.findViewById(R.id.btnFileChooseListConfirm);
        //listView.setAdapter(mBrowserAdapter);

        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() != null) {

                    if (intent.getAction().equalsIgnoreCase(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_SHOW)) {
                        //Log.d(TAG, "receive FILE_CHOOSE_CONFIRM_BUTTON_SHOW !");
                        fileChooseConfirm.setVisibility(View.VISIBLE);


                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_HIDE)) {
                        //Log.d(TAG, "receive FILE_CHOOSE_CONFIRM_BUTTON_SHOW !");
                        fileChooseConfirm.setVisibility(View.GONE);
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.FILE_CHOOSE_PATH_CHANGE)) {
                        //Log.d(TAG, "receive FILE_CHOOSE_PATH_CHANGE !");

                    }
                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_SHOW);
            filter.addAction(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_HIDE);
            filter.addAction(Constants.ACTION.FILE_CHOOSE_PATH_CHANGE);
            fragmentContext.registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }

        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        Log.e(TAG, "currentDir = "+Environment.getExternalStorageDirectory().getPath());
        //fileChooseArrayAdapter = new FileChooseArrayAdapter(this, R.layout.file_choose_in_row, );
        fill(currentDir);



        return rootView;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroy");

        FileChooseLongClick = false;
        FileChooseSelectAll = false;

        if (isRegister && mReceiver != null) {
            try {
                fragmentContext.unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
            Log.d(TAG, "unregisterReceiver mReceiver");
        }

        super.onDestroyView();
    }

    public static void fill(File f)
    {
        final File[]dirs = f.listFiles();

        //Intent scanIntent = new Intent();
        //scanIntent.setAction(Constants.ACTION.FILE_CHOOSE_PATH_CHANGE);
        //scanIntent.putExtra("PATH", f.getAbsolutePath());
        //fragmentContext.sendBroadcast(scanIntent);
        setPath(f.getAbsolutePath());

        ArrayList<FileChooseItem> dir = new ArrayList<>();
        ArrayList<FileChooseItem> fls = new ArrayList<>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                //CheckBox checkBox = new CheckBox(getApplicationContext());
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " "+"file";
                    else num_item = num_item + " "+"files";

                    //String formated = lastModDate.toString();
                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        dir.add(new FileChooseItem(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon"));
                }
                else
                {
                    char first = ff.getName().charAt(0);
                    if (first != '.')
                        fls.add(new FileChooseItem(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);
        if(//!f.getName().equalsIgnoreCase("sdcard") ||
                !f.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getPath())) {
            //CheckBox checkBox = new CheckBox(this);
            dir.add(0, new FileChooseItem("..", "Parent Directory", "", f.getParent(), "directory_up"));
        }
        fileChooseArrayAdapter = new FileChooseArrayAdapter(fragmentContext,R.layout.file_choose_in_row,dir);
        fileChooseListView.setAdapter(fileChooseArrayAdapter);
        fileChooseListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);




    }


}
