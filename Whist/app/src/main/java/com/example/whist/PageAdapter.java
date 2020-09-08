package com.example.whist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    private  int notabs;

    public PageAdapter(FragmentManager fm, int noOfTabs) {
        super(fm);
        this.notabs = noOfTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return  new GameTab();
            case 1:
                return  new ScoreTab();

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return notabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return  notabs;
    }

}
