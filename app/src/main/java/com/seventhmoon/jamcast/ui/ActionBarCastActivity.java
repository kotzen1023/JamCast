package com.seventhmoon.jamcast.ui;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.seventhmoon.jamcast.R;
import com.seventhmoon.jamcast.data.Constants;
import com.seventhmoon.jamcast.data.FileChooseItem;
import com.seventhmoon.jamcast.data.initData;
import com.seventhmoon.jamcast.utils.LogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.seventhmoon.jamcast.data.FileOperation.init_folder_and_files;

import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.currentDir;
import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.fileChooseArrayAdapter;
import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.fileChooseConfirm;
import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.fileChooseListView;
import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.FileChooseLongClick;
import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.FileChooseSelectAll;
import static com.seventhmoon.jamcast.ui.FileChooseBrowserFragment.fill;

public class ActionBarCastActivity extends AppCompatActivity {
    private static final String TAG = LogHelper.makeLogTag(ActionBarCastActivity.class);

    private static final int DELAY_MILLIS = 1000;

    private CastContext mCastContext;
    private MenuItem mMediaRouteMenuItem;
    private static Toolbar mToolbar;
    private Menu mMenu;
    public static MenuItem menuItemAdd;
    public static MenuItem menuItemSelectAll;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private boolean mToolbarInitialized;

    private int mItemToOpenWhenDrawerCloses = -1;
    private int activity_called = 0;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private static BroadcastReceiver mReceiver = null;
    private static boolean isRegister = false;

    private CastStateListener mCastStateListener = new CastStateListener() {
        @Override
        public void onCastStateChanged(int newState) {
            if (newState != CastState.NO_DEVICES_AVAILABLE) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mMediaRouteMenuItem.isVisible()) {
                            LogHelper.d(TAG, "Cast Icon is visible");
                            showFtu();
                        }
                    }
                }, DELAY_MILLIS);
            }
        }
    };

    private final DrawerLayout.DrawerListener mDrawerListener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerClosed(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerClosed(drawerView);
            if (mItemToOpenWhenDrawerCloses >= 0) {
                Bundle extras = ActivityOptions.makeCustomAnimation(
                        ActionBarCastActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                Class activityClass = null;
                switch (mItemToOpenWhenDrawerCloses) {
                    case R.id.navigation_allmusic:
                        activityClass = MusicPlayerActivity.class;
                        break;
                    case R.id.navigation_playlists:
                        activityClass = PlaceholderActivity.class;
                        break;
                    case R.id.navigation_mylist:
                        activityClass = MusicListActivity.class;
                        break;
                }
                if (activityClass != null) {
                    startActivity(new Intent(ActionBarCastActivity.this, activityClass), extras);
                    finish();
                }
            }
        }

        @Override
        public void onDrawerStateChanged(int newState) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerStateChanged(newState);
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerSlide(drawerView, slideOffset);
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            if (mDrawerToggle != null) mDrawerToggle.onDrawerOpened(drawerView);
            if (getSupportActionBar() != null) getSupportActionBar()
                    .setTitle(R.string.app_name);
        }
    };

    private final FragmentManager.OnBackStackChangedListener mBackStackChangedListener =
            new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    updateDrawerToggle();
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "ActionBarCastActivity onCreate");

        int playServicesAvailable =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (playServicesAvailable == ConnectionResult.SUCCESS) {
            mCastContext = CastContext.getSharedInstance(this);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            init_folder_and_files();

            if (!initData.isInit) {
                LogHelper.e(TAG, "initData");
                initData.isInit = true;
            }

        } else {
            if(checkAndRequestPermissions()) {
                init_folder_and_files();

                if (!initData.isInit) {
                    LogHelper.e(TAG, "initData");
                    initData.isInit = true;
                }
            }
        }


    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "ActionBarCastActivity onDestroy");



        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mToolbarInitialized) {
            throw new IllegalStateException("You must run super.initializeToolbar at " +
                    "the end of your onCreate method");
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mCastContext != null) {
            mCastContext.addCastStateListener(mCastStateListener);
        }

        // Whenever the fragment back stack changes, we may need to update the
        // action bar toggle: only top level screens show the hamburger-like icon, inner
        // screens - either Activities or fragments - show the "Up" icon instead.
        getFragmentManager().addOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mCastContext != null) {
            mCastContext.removeCastStateListener(mCastStateListener);
        }
        getFragmentManager().removeOnBackStackChangedListener(mBackStackChangedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        Log.d(TAG, "onCreateOptionsMenu start");

        getMenuInflater().inflate(R.menu.main, menu);

        if (mCastContext != null) {
            mMediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                    menu, R.id.media_route_menu_item);
        }

        menuItemAdd = menu.findItem(R.id.action_add);
        menuItemSelectAll = menu.findItem(R.id.action_selectAll);
        switch (activity_called) {
            case 0:
                menuItemAdd.setVisible(false);
                menuItemSelectAll.setVisible(false);
                break;
            case 1:
                menuItemAdd.setVisible(false);
                menuItemSelectAll.setVisible(false);
                break;
            case 2:
                menuItemAdd.setVisible(true);
                menuItemSelectAll.setVisible(false);
                break;
            case 3:
                menuItemAdd.setVisible(false);
                menuItemSelectAll.setVisible(true);
                break;
        }


        Log.d(TAG, "onCreateOptionsMenu end");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // If not handled by drawerToggle, home needs to be handled by returning to previous
        if (item != null && item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_add:
                Log.e(TAG, "action_add click");
                //Intent intent = new Intent(ActionBarCastActivity.this, FileChooseActivity.class);
                //startActivity(intent);

                Bundle extras = ActivityOptions.makeCustomAnimation(
                        ActionBarCastActivity.this, R.anim.fade_in, R.anim.fade_out).toBundle();

                Class activityClass = FileChooseListActivity.class;

                startActivity(new Intent(ActionBarCastActivity.this, activityClass), extras);
                finish();

                break;

            case R.id.action_selectAll:
                Log.e(TAG, "action_selectAll");

                if (!FileChooseLongClick) {
                    FileChooseLongClick = true;

                    if (!FileChooseSelectAll) {
                        FileChooseSelectAll = true;
                        item.setTitle(getResources().getString(R.string.unselect_all));
                        Log.d(TAG, "listView.getCount = "+fileChooseListView.getCount());
                        for (int i = 0; i < fileChooseListView.getCount(); i++) {
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

                        fileChooseConfirm.setVisibility(View.VISIBLE);
                    } else { //Data.FileChooseSelectAll == true
                        FileChooseSelectAll = false;
                        item.setTitle(getResources().getString(R.string.select_all));

                        for (int i = 0; i < fileChooseListView.getCount(); i++) {
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
                        fileChooseConfirm.setVisibility(View.GONE);
                    }

                } else { //long click == true
                    if (!FileChooseSelectAll) {
                        FileChooseSelectAll = true;
                        item.setTitle(getResources().getString(R.string.unselect_all));
                        Log.d(TAG, "listView.getCount = "+fileChooseListView.getCount());
                        for (int i = 0; i < fileChooseListView.getCount(); i++) {
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
                        fileChooseConfirm.setVisibility(View.VISIBLE);
                    } else { //Data.FileChooseSelectAll == true
                        FileChooseSelectAll = false;
                        item.setTitle(getResources().getString(R.string.select_all));

                        for (int i = 0; i < fileChooseListView.getCount(); i++) {
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

                        fileChooseConfirm.setVisibility(View.GONE);
                    }


                }

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the drawer is open, back will close it
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if (FileChooseLongClick) {
            //MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

            FileChooseLongClick = false;
            FileChooseSelectAll = false;
            menuItemSelectAll.setTitle(getResources().getString(R.string.select_all));


            for(int i=0;i<fileChooseListView.getCount(); i++) {
                FileChooseItem fileChooseItem = fileChooseArrayAdapter.getItem(i);

                if (fileChooseItem != null) {

                    if (fileChooseItem.getCheckBox() != null) {
                        fileChooseItem.getCheckBox().setVisibility(View.INVISIBLE);
                        fileChooseItem.getCheckBox().setChecked(false);
                    }
                    fileChooseArrayAdapter.mSparseBooleanArray.put(i, false);
                }
            }

            fileChooseConfirm.setVisibility(View.GONE);
        } else {
            //Log.e(TAG, "currentDir = "+currentDir+" root = "+Environment.getExternalStorageDirectory().getPath());
            if (currentDir!= null && !currentDir.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getPath())) {
                File parent = new File(currentDir.getParent());

                fill(parent);

                currentDir = new File(parent.getAbsolutePath());

                //MenuItem menuItem = actionmenu.findItem(R.id.action_selectall);

                FileChooseLongClick = false;
                FileChooseSelectAll = false;
                menuItemSelectAll.setTitle(getResources().getString(R.string.select_all));


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

                // Otherwise, it may return to the previous fragment stack
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                } else {
                    // Lastly, it will rely on the system behavior for back
                    super.onBackPressed();
                }

                //finish();
            }

        }



    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        mToolbar.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        mToolbar.setTitle(titleId);
    }

    public static void setPath(String s) {
        mToolbar.setTitle(s);

    }

    protected void initializeToolbar(int activity_id) {

        LogHelper.d(TAG, "initializeToolbar start");

        activity_called = activity_id;

        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar == null) {
            throw new IllegalStateException("Layout is required to include a Toolbar with id " +
                    "'toolbar'");
        }
        mToolbar.inflateMenu(R.menu.main);


        mDrawerLayout = findViewById(R.id.drawer_layout);
        if (mDrawerLayout != null) {
            NavigationView navigationView = findViewById(R.id.nav_view);
            if (navigationView == null) {
                throw new IllegalStateException("Layout requires a NavigationView " +
                        "with id 'nav_view'");
            }

            // Create an ActionBarDrawerToggle that will handle opening/closing of the drawer:
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                    mToolbar, R.string.open_content_drawer, R.string.close_content_drawer);
            mDrawerLayout.setDrawerListener(mDrawerListener);

            populateDrawerItems(navigationView);
            setSupportActionBar(mToolbar);
            updateDrawerToggle();
        } else {
            setSupportActionBar(mToolbar);
        }

        mToolbarInitialized = true;

        LogHelper.d(TAG, "initializeToolbar end");
    }



    private void populateDrawerItems(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mItemToOpenWhenDrawerCloses = menuItem.getItemId();
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
        if (MusicPlayerActivity.class.isAssignableFrom(getClass())) {
            navigationView.setCheckedItem(R.id.navigation_allmusic);
        } else if (PlaceholderActivity.class.isAssignableFrom(getClass())) {
            navigationView.setCheckedItem(R.id.navigation_playlists);
        } else if (MusicListActivity.class.isAssignableFrom(getClass())) {
            navigationView.setCheckedItem(R.id.navigation_mylist);
        }
    }

    protected void updateDrawerToggle() {
        if (mDrawerToggle == null) {
            return;
        }
        boolean isRoot = getFragmentManager().getBackStackEntryCount() == 0;
        mDrawerToggle.setDrawerIndicatorEnabled(isRoot);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(!isRoot);
            getSupportActionBar().setDisplayHomeAsUpEnabled(!isRoot);
            getSupportActionBar().setHomeButtonEnabled(!isRoot);
        }
        if (isRoot) {
            mDrawerToggle.syncState();
        }
    }

    /**
     * Shows the Cast First Time User experience to the user (an overlay that explains what is
     * the Cast icon)
     */
    private void showFtu() {
        Menu menu = mToolbar.getMenu();
        View view = menu.findItem(R.id.media_route_menu_item).getActionView();
        if (view != null && view instanceof MediaRouteButton) {
            IntroductoryOverlay overlay = new IntroductoryOverlay.Builder(this, mMediaRouteMenuItem)
                    .setTitleText(R.string.touch_to_cast)
                    .setSingleTime()
                    .build();
            overlay.show();
        }
    }

    private  boolean checkAndRequestPermissions() {

        //int accessNetworkStatePermission = ContextCompat.checkSelfPermission(this,
        //        Manifest.permission.ACCESS_NETWORK_STATE);

        //int accessWiFiStatePermission = ContextCompat.checkSelfPermission(this,
        //        Manifest.permission.ACCESS_WIFI_STATE);

        int readPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int writePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int networkPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);

        //int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (networkPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }

        /*if (accessNetworkStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_NETWORK_STATE);
        }

        if (accessWiFiStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_WIFI_STATE);
        }*/
        //if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.WRITE_CALENDAR);
        //}
        //if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        //}

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        //Log.e(TAG, "result size = "+grantResults.length+ "result[0] = "+grantResults[0]+", result[1] = "+grantResults[1]);


        /*switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    Log.i(TAG, "WRITE_CALENDAR permissions granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "READ_CONTACTS permissions denied");

                    RetryDialog();
                }
            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }*/
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                //perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                //perms.put(Manifest.permission.ACCESS_WIFI_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                        //&& perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED &&
                        //perms.get(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                            )

                    {
                        Log.d(TAG, "write permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted
                        init_folder_and_files();
                        //init_setting();
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.INTERNET )
                            //|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE )
                            //|| ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_WIFI_STATE )
                                ) {
                            showDialogOK("Warning",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}
