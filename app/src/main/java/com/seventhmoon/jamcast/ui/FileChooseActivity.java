package com.seventhmoon.jamcast.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.data.Constants;
import com.seventhmoon.jamcast.data.FileChooseArrayAdapter;
import com.seventhmoon.jamcast.data.FileChooseItem;
import com.seventhmoon.jamcast.service.SearchFileService;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.seventhmoon.jamcast.ui.MusicListActivity.searchList;

public class FileChooseActivity extends AppCompatActivity {
    private static final String TAG = FileChooseActivity.class.getName();

    public static boolean FileChooseLongClick = false;
    public static boolean FileChooseSelectAll = false;

    //private Context context;

    public static FileChooseArrayAdapter fileChooseArrayAdapter;
    public ListView listView;
    public static Button confirm;
    private File currentDir;
    private Menu actionmenu;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    //private ArrayList<String> searchList = new ArrayList<>();

    //AudioOperation audioOperation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.file_choose_list);

        //Context context = getBaseContext();

        //audioOperation = new AudioOperation(context);

        listView = findViewById(R.id.listViewFileChoose);
        confirm = findViewById(R.id.btnFileChooseListConfirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchList.clear();

                for (int i = 0; i < listView.getCount(); i++) {
                    if (fileChooseArrayAdapter.mSparseBooleanArray.get(i)) {
                        FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                        if (fileChooseItem != null) {

                            Log.e(TAG, "select : " + fileChooseItem.getPath());
                            searchList.add(fileChooseItem.getPath());
                        }
                    }

                }


                Intent saveintent = new Intent(FileChooseActivity.this, SearchFileService.class);
                saveintent.setAction(Constants.ACTION.GET_SEARCHLIST_ACTION);
                startService(saveintent);

                //searchFiles();
                finish();
            }
        });

        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        Log.e(TAG, "currentDir = "+Environment.getExternalStorageDirectory().getPath());
        //fileChooseArrayAdapter = new FileChooseArrayAdapter(this, R.layout.file_choose_in_row, );
        fill(currentDir);

        IntentFilter filter;

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction() != null) {

                    if (intent.getAction().equalsIgnoreCase(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_SHOW)) {
                        //Log.d(TAG, "receive FILE_CHOOSE_CONFIRM_BUTTON_SHOW !");
                        confirm.setVisibility(View.VISIBLE);


                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_HIDE)) {
                        //Log.d(TAG, "receive FILE_CHOOSE_CONFIRM_BUTTON_SHOW !");
                        confirm.setVisibility(View.GONE);
                    }
                }
            }
        };

        if (!isRegister) {
            filter = new IntentFilter();
            filter.addAction(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_SHOW);
            filter.addAction(Constants.ACTION.FILE_CHOOSE_CONFIRM_BUTTON_HIDE);
            registerReceiver(mReceiver, filter);
            isRegister = true;
            Log.d(TAG, "registerReceiver mReceiver");
        }
    }

    @Override
    protected void onDestroy() {
        FileChooseLongClick = false;
        FileChooseSelectAll = false;

        if (isRegister && mReceiver != null) {
            try {
                unregisterReceiver(mReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            isRegister = false;
            mReceiver = null;
        }

        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }
    @Override
    public void onResume() {

        Log.i(TAG, "onResume");
        super.onResume();
    }

    public void onBackPressed() {
        if (FileChooseLongClick) {
            MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

            FileChooseLongClick = false;
            FileChooseSelectAll = false;
            menuItem.setTitle(getResources().getString(R.string.select_all));


            for(int i=0;i<listView.getCount(); i++) {
                FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                if (fileChooseItem != null) {

                    if (fileChooseItem.getCheckBox() != null) {
                        fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                        fileChooseItem.getCheckBox().setChecked(false);
                    }
                    fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                }
            }

            confirm.setVisibility(View.GONE);
        } else {
            //Log.e(TAG, "currentDir = "+currentDir+" root = "+Environment.getExternalStorageDirectory().getPath());
            if (!currentDir.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getPath())) {
                File parent = new File(currentDir.getParent());

                fill(parent);

                currentDir = new File(parent.getAbsolutePath());

                MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                FileChooseLongClick = false;
                FileChooseSelectAll = false;
                menuItem.setTitle(getResources().getString(R.string.select_all));


                for (int i = 0; i < listView.getCount(); i++) {
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
                finish();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.file_choose_menu, menu);

        MenuItem item_all;

        item_all = menu.findItem(R.id.action_selectall);

        item_all.setVisible(true);

        actionmenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case R.id.action_selectall:
                if (!FileChooseLongClick) {
                    FileChooseLongClick = true;

                    if (!FileChooseSelectAll) {
                        FileChooseSelectAll = true;
                        item.setTitle(getResources().getString(R.string.unselect_all));
                        Log.d(TAG, "listView.getCount = "+listView.getCount());
                        for (int i = 0; i < listView.getCount(); i++) {
                            FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                            if (fileChooseItem != null) {

                                if (fileChooseItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileChooseItem.getName().equals("..")) {
                                        fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileChooseItem.getCheckBox().setChecked(true);
                                        fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                                    } else {
                                        fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                                        fileChooseItem.getCheckBox().setChecked(false);
                                        fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                                    }

                                } else {
                                    fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                                }
                            }
                            //fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                        }

                        confirm.setVisibility(View.VISIBLE);
                    } else { //Data.FileChooseSelectAll == true
                        FileChooseSelectAll = false;
                        item.setTitle(getResources().getString(R.string.select_all));

                        for (int i = 0; i < listView.getCount(); i++) {
                            FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                            if (fileChooseItem != null) {

                                if (fileChooseItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileChooseItem.getName().equals("..")) {
                                        fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileChooseItem.getCheckBox().setChecked(false);
                                    } else {
                                        fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                                        fileChooseItem.getCheckBox().setChecked(false);
                                    }

                                }
                                fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                            }
                        }
                        confirm.setVisibility(View.GONE);
                    }

                } else { //long click == true
                    if (!FileChooseSelectAll) {
                        FileChooseSelectAll = true;
                        item.setTitle(getResources().getString(R.string.unselect_all));
                        Log.d(TAG, "listView.getCount = "+listView.getCount());
                        for (int i = 0; i < listView.getCount(); i++) {
                            FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                            if (fileChooseItem != null) {

                                if (fileChooseItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileChooseItem.getName().equals("..")) {
                                        //Log.e(TAG, "item["+i+"]="+fileChooseItem.getName());
                                        //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileChooseItem.getCheckBox().setChecked(true);
                                        fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                                    } else {
                                        fileChooseItem.getCheckBox().setChecked(false);
                                        fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                                    }

                                } else {
                                    //Log.e(TAG, "item["+i+"]="+fileChooseItem.getName());
                                    fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                                }
                            }
                            //fileChooseArrayAdapter.mSparseBooleanArray.put(i, true);
                        }
                        confirm.setVisibility(View.VISIBLE);
                    } else { //Data.FileChooseSelectAll == true
                        FileChooseSelectAll = false;
                        item.setTitle(getResources().getString(R.string.select_all));

                        for (int i = 0; i < listView.getCount(); i++) {
                            FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                            if (fileChooseItem != null) {

                                if (fileChooseItem.getCheckBox() != null) {
                                    //Log.e(TAG, "set item[" + i + "] visible");
                                    if (!fileChooseItem.getName().equals("..")) {
                                        //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);
                                        fileChooseItem.getCheckBox().setChecked(false);
                                    } else {
                                        fileChooseItem.getCheckBox().setChecked(false);
                                    }

                                }
                                fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                            }
                        }

                        confirm.setVisibility(View.GONE);
                    }


                }



                break;

            case android.R.id.home:

                break;

            default:
                break;
        }

        return true;
    }

    private void fill(File f)
    {
        final File[]dirs = f.listFiles();
        //this.setTitle("Current Dir: "+f.getName());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(f.getAbsolutePath());
        }
        //txtCurrentDir.setText(f.getAbsolutePath());

        ArrayList<FileChooseItem> dir = new ArrayList<>();
        ArrayList<FileChooseItem> fls = new ArrayList<>();
        try{
            if (dir.size() > 0) {
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
            } else {
                toast("dir array is zero");
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
        fileChooseArrayAdapter = new FileChooseArrayAdapter(FileChooseActivity.this,R.layout.file_choose_in_row,dir);
        listView.setAdapter(fileChooseArrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FileChooseItem o = fileChooseArrayAdapter.getItem(position);

                if (o != null) {

                    if (o.getPath() != null && (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up"))) {
                        currentDir = new File(o.getPath());
                        fill(currentDir);

                        MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                        FileChooseLongClick = false;
                        FileChooseSelectAll = false;
                        menuItem.setTitle("Select all");


                        for (int i = 0; i < listView.getCount(); i++) {
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e(TAG, "position = " + position + ", size = " + listView.getCount());
                FileChooseLongClick = true;
                //FileChooseItem fileChooseItem = (FileChooseItem) fileChooseArrayAdapter.getItem(position);
                //Log.i(TAG, "name = "+fileChooseItem.getName());
                //Log.e(TAG, "ck = " + fileChooseItem.getCheckBox());
                //fileChooseItem.getCheckBox().setVisibility(View.VISIBLE);

                for(int i=0;i<listView.getCount(); i++) {
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
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }
}
