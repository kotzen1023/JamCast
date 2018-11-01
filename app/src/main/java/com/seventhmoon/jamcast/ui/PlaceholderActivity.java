package com.seventhmoon.jamcast.ui;

import android.os.Bundle;

import com.seventhmoon.jamcast.R;

public class PlaceholderActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placeholder);
        initializeToolbar();
    }
}
