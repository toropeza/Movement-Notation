package com.thomas.movementnotation;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Thomas on 5/31/2015.
 */
public class PageAdapter extends FragmentStatePagerAdapter {
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return MainActivity.numOfLists;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = new Fragment();
        switch (position) {
            case 0:
                fragment = new TechniquesListFragment();
                break;
            case 1:
                fragment = new FlowListFragment();
                break;

        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Techniques";
                break;
            case 1:
                title = "Flows";
                break;
        }
        return title;
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
