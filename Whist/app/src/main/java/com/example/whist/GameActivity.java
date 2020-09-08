package com.example.whist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    // Date despre jucatorul curent
    private String myName;
    private Integer myIndex;

    // Date despre ceilalti jucatori
    private ArrayList<String> players;
    private int playerCount;

    // Referinta la baza de date
    private DatabaseReference turnReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        //////////////////////////////////////////////////////////////////////

        //implementare tab game + score

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        TabItem tab1 = findViewById(R.id.tab1);
        TabItem tab2 = findViewById(R.id.tab2);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.View_pager);
        PagerAdapter pagerAdapter = new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //////////////////////////////////////////////////////////////////////


        // Extragere continut din Bundle
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        myName = b.getString("myName");
        myIndex = b.getInt("myIndex");
        players = b.getStringArrayList("players");
        playerCount = players.size();

        turnReference = FirebaseDatabase.getInstance().getReference().child("Game").child("Turn");

        // Metoda in care se desfasoara flow-ul jocului
        runGame();

    }

    private void runGame() {

        // (Momentan) se realizeaza jocurile de 8
        for (int i = 0; i < players.size(); i++) {
            turn(i, 8);
        }
    }


    public void turn(final int currentPlayerIndex, int gameType) {

        // Afisare carti
        if (currentPlayerIndex + 1 == myIndex) {
            // Amestecare carti + trimitere la server
            ArrayList<String> shuffledCards = CardShuffler.shuffleCards(playerCount);

            Map<String, Object> map = new HashMap<>();

            // Pun pentru fiecare player intr-un map un sublist al listei de carti amestecate
            // (8 carti pentru Player1, urmatoarele 8 carti pentru Player2, etc)
            for (int i = 0; i < players.size(); i++) {
                map.put("Cards", shuffledCards.subList(gameType * i, gameType * (i + 1)));

                // Trimitere la server
                DatabaseReference currPlayerReference = turnReference.child("Player" + (i+1));
                currPlayerReference.updateChildren(map);
            }
        }

        // Setare listener pe intrarea Player <indicele meu>
        DatabaseReference myPlayerReference = turnReference.child("Player" + myIndex);
        myPlayerReference.addChildEventListener(new ChildEventListener() {
            // Extrag cartile mele de la intrarea mea si le afisez pe ecran
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                ArrayList<String> myCards = snapshot.getValue(t);

                Log.d("myCards", myCards.toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        /* TODO:
            - Afisare pe ecran a cartilor
            - adaugare de elemente pentru a intreba utilizatorul cate maini crede ca va lua
            - setare listeneri corespunzatori astfel incat dupa ce un utilizator a spus cate maini va lua,
            urmatorul sa fie intrebat
            -De rectificat problema afisarii cartilor
            -De facut tabel
            -De facut tabbarul sa dispara/apara la swipe

        */
    }
}