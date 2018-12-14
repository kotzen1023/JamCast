package com.seventhmoon.jamcast.ui;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.data.FileChooseArrayAdapter;
import com.seventhmoon.jamcast.data.FileChooseItem;
import com.seventhmoon.jamcast.utils.LogHelper;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class FileChooseBrowserFragment extends Fragment {
    private static final String TAG = LogHelper.makeLogTag(FileChooseBrowserFragment.class);

    public static FileChooseArrayAdapter fileChooseArrayAdapter;

    private ListView listView;
    private View mErrorView;
    private TextView mErrorMessage;
    private Context fragmentContext;

    private File currentDir;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogHelper.d(TAG, "fragment.onCreateView");

        fragmentContext = getContext();

        View rootView = inflater.inflate(R.layout.file_choose_list, container, false);

        mErrorView = rootView.findViewById(R.id.playback_error);
        mErrorMessage = mErrorView.findViewById(R.id.error_message);


        listView = rootView.findViewById(R.id.list_view);
        //listView.setAdapter(mBrowserAdapter);

        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        Log.e(TAG, "currentDir = "+Environment.getExternalStorageDirectory().getPath());
        //fileChooseArrayAdapter = new FileChooseArrayAdapter(this, R.layout.file_choose_in_row, );
        fill(currentDir);

        return rootView;
    }


    private void fill(File f)
    {
        final File[]dirs = f.listFiles();
        //this.setTitle("Current Dir: "+f.getName());
        //ActionBar actionBar = getSupportActionBar();
        //if (actionBar != null) {
        //    actionBar.setTitle(f.getAbsolutePath());
        //}
        //txtCurrentDir.setText(f.getAbsolutePath());

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
        listView.setAdapter(fileChooseArrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);




    }


}
