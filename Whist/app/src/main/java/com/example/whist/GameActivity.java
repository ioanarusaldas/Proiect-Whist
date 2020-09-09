package com.example.whist;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    // Date despre jucatorul curent
    private String myName;
    private Integer myIndex;

    // Date despre ceilalti jucatori
    private ArrayList<String> players;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        // Extragere continut din Bundle
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        myName = b.getString("myName");
        myIndex = b.getInt("myIndex");
        players = b.getStringArrayList("players");


        // Setare listener pe tab-uri
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.View_pager);
        PagerAdapter pagerAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount(), players, myIndex);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }


        /* TODO:
            - adaugare de elemente pentru a intreba utilizatorul cate maini crede ca va lua
            - setare listeneri corespunzatori astfel incat dupa ce un utilizator a spus cate maini va lua,
            urmatorul sa fie intrebat
            -De facut tabel
            -De facut tabbarul sa dispara/apara la swipe

        */
}