package com.thomas.movementnotation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;


import com.software.shell.fab.ActionButton;

//TODO CHECKLIST
//comments
//private variables
//class comments
//reformat
//Change ids from int to long

public class MainActivity extends FragmentActivity {
    IntroDBHandler introDBHandler;
    public static final String TAG = "movementnotation";

    static final int numOfLists = 2;
    public final String materialColor_Green = "#43A047";

    PageAdapter mAdapter;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        introDBHandler = new IntroDBHandler(MainActivity.this);
        introDBHandler.open();
        Cursor introData = introDBHandler.getAllRows();
        if (introData.getCount() == 0) {
            Log.i(TAG, "onCreateINTRO");
            introDBHandler.insertRow(1);
            startActivity(new Intent(MainActivity.this, IntroSlidePagerActivity.class));
        } else {
            setContentView(R.layout.activity_main);
            Log.i(TAG, "onCreate");

            mAdapter = new PageAdapter(getSupportFragmentManager());

            mPager = (ViewPager) findViewById(R.id.main_view_pager);
            mPager.setAdapter(mAdapter);
        }

    }


}
