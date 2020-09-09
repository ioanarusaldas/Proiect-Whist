package com.example.whist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PageAdapter extends FragmentPagerAdapter {
    private  int notabs;

    private ArrayList<String> players;
    private Integer myIndex;

    public PageAdapter(FragmentManager fm, int noOfTabs, ArrayList<String> players, Integer myIndex) {
        super(fm);
        this.notabs = noOfTabs;
        this.players = players;
        this.myIndex = myIndex;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                GameTab tab =  new GameTab();
                Bundle b = new Bundle();
                b.putStringArrayList("players", players);
                b.putInt("myIndex", myIndex);
                tab.setArguments(b);
                return tab;



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
