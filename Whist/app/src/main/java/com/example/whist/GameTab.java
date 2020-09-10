package com.example.whist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class GameTab extends Fragment {

    private ArrayList<String> players;
    private int myIndex;
    private DatabaseReference turnReference;
    private int playerCount;

    private View rootView;

    public GameTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            players = getArguments().getStringArrayList("players");
            myIndex = getArguments().getInt("myIndex");
            playerCount = players.size();
        }

        turnReference = FirebaseDatabase.getInstance().getReference().child("Game").child("Turn");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_game_tab, container, false);


        // setare nume playeri

        setPlayerNames(rootView);
        // ascundere avatari nefolositori
        hidePlayerAvatars(rootView);

        // metoda prin care se ruleaza jocul
        runGame(rootView);

        return rootView;
    }

    // metoda care extrage textview-urile din fragment_game_tab si seteaza numele jucatorilor
    private void setPlayerNames(View rootView) {

        int index = 0;

        ArrayList<TextView> playerTextView = new ArrayList<>(5);
        playerTextView.add((TextView) rootView.findViewById(R.id.player1_name));
        playerTextView.add((TextView) rootView.findViewById(R.id.player2_name));
        playerTextView.add((TextView) rootView.findViewById(R.id.player3_name));
        playerTextView.add((TextView) rootView.findViewById(R.id.player4_name));
        playerTextView.add((TextView) rootView.findViewById(R.id.player5_name));

        for (int i = 0; i < players.size(); i++) {
            if ((i + 1) != myIndex) {
                playerTextView.get(index++).setText(players.get(i));
            }
        }
    }

    // metoda care extrage layout-urile din fragment_game_tab si le asunde pe cele nefolositoare
    private void hidePlayerAvatars(View rootView) {

        ArrayList<LinearLayout> playerLinearLayout = new ArrayList<>(5);
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.layout1));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.layout2));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.layout3));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.layout4));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.layout5));


        switch (playerCount) {
            case 3:
                playerLinearLayout.get(2).setVisibility(View.GONE);
                playerLinearLayout.get(3).setVisibility(View.GONE);
                playerLinearLayout.get(4).setVisibility(View.GONE);
                break;
            case 4:
                playerLinearLayout.get(3).setVisibility(View.GONE);
                playerLinearLayout.get(4).setVisibility(View.GONE);
                break;
            case 5:
                playerLinearLayout.get(4).setVisibility(View.GONE);
                break;
        }
    }

    private void runGame(View rootView) {

        // (Momentan) se realizeaza jocurile de 8
        for (int i = 0; i < players.size(); i++) {
            turn(i, 8, rootView);
        }
    }


    public void turn(final int currentPlayerIndex, int gameType, final View rootView) {

        // Afisare carti
        if (currentPlayerIndex + 1 == myIndex) {
            // Amestecare carti + trimitere la server
            ArrayList<Integer> shuffledCards = CardShuffler.shuffleCards(playerCount);

            Map<String, Object> map = new HashMap<>();

            // Pun pentru fiecare player intr-un map un sublist al listei de carti amestecate
            // (8 carti pentru Player1, urmatoarele 8 carti pentru Player2, etc)
            for (int i = 0; i < players.size(); i++) {
                map.put("Cards", shuffledCards.subList(gameType * i, gameType * (i + 1)));

                // Trimitere la server
                DatabaseReference currPlayerReference = turnReference.child("Player" + (i + 1));
                currPlayerReference.updateChildren(map);
            }
        }

        // Setare listener pe intrarea Player <indicele meu>
        DatabaseReference myPlayerReference = turnReference.child("Player" + myIndex);
        myPlayerReference.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Extragere carti de la intrarea Player cu indicele jucatorului curent
                GenericTypeIndicator<ArrayList<Integer>> t = new GenericTypeIndicator<ArrayList<Integer>>() {
                };
                ArrayList<Integer> myCards = snapshot.getValue(t);

                // carpeala - asteptam pana cand fragmentul este atasat de activitate
                while (getActivity() == null) ;

                // Introducere carti pe slot-urile libere din fragment_game_tab.xml
                for (int i = 0; i < myCards.size(); i++) {
                    // extragere id al slot-ului

                    int resId = getActivity().getResources().getIdentifier(
                            "card_slot_" + (i + 1),
                            "id",
                            getActivity().getPackageName()
                    );

                    ImageView card = rootView.findViewById(resId);
                    // setare resursa pe slot
                    card.setImageResource(myCards.get(i));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
}