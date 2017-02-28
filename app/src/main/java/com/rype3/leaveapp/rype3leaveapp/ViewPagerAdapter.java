package com.rype3.leaveapp.rype3leaveapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fragment01 fragment01 = new Fragment01();
                return fragment01;
            case 1:
                Fragment02 fragment02 = new Fragment02();
                return fragment02;
            default:
                return null;
       }
   }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}