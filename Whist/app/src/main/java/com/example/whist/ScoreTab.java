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
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ScoreTab extends Fragment {

    private ArrayList<String> players;
    private static final String ARG_PARAM1 = "players";

    private Context mContext;
    private View fragmentView;


    ArrayList<TextView> nameTextViews = new ArrayList<>();
    private ArrayList<TextView> scoreTextViews = new ArrayList<>();


    private ScoreSingleton scoreSingletonInstance = ScoreSingleton.getInstance();

    public ScoreTab() {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            players = getArguments().getStringArrayList(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_score_tab, container, false);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        setTable();

        setTurnFinishedListener();

    }

    private void setTurnFinishedListener() {
        DatabaseReference handsLeftReference = FirebaseDatabase.getInstance().
                getReference().child("Game").child("Turn").child("TurnFinished");

        handsLeftReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String value = snapshot.getValue(String.class);
                if (value.equals("True")) {
                    setNewScores();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String value = snapshot.getValue(String.class);
                if (value.equals("True")) {
                    setNewScores();
                }

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

    private void setNewScores() {
        ArrayList<Integer> bids = scoreSingletonInstance.getBids();
        ArrayList<Integer> handsWon = scoreSingletonInstance.getHandsWon();

        for(int i = 0; i < players.size(); i++) {

            // extragem id-ul campului de scor al jucatorului
            int resId = mContext.getResources().getIdentifier(
                    "score_player_" + (i+1),
                    "id",
                    mContext.getPackageName()
            );

            TextView currentTextView = fragmentView.findViewById(resId);

            int score = Integer.parseInt(currentTextView.getText().toString());

            int currentBid = bids.get(i);
            int currentHandsWon = handsWon.get(i);

            if(currentBid == currentHandsWon) {
                score += (5 + currentBid);
            } else {
                score -= Math.abs(currentBid - currentHandsWon);
            }

            currentTextView.setText(Integer.toString(score));

        }

        Log.d("scoreLog", bids.toString());
        Log.d("scoreLog", handsWon.toString());
    }


    // metoda care seteaza tabelul de scor
    private void setTable() {

        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_1));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_2));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_3));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_4));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_5));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_6));

        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_1));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_2));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_3));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_4));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_5));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_6));


        for (int i = 5; i >= players.size(); i--) {
            nameTextViews.get(i).setVisibility(View.GONE);
            scoreTextViews.get(i).setVisibility(View.GONE);

            nameTextViews.remove(i);
            scoreTextViews.remove(i);
        }

        for (int i = 0; i < players.size(); i++) {
            nameTextViews.get(i).setText(players.get(i));
        }

    }
}