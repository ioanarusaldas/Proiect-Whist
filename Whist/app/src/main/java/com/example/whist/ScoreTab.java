package com.example.whist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreTab extends Fragment {

    private ArrayList<String> players;
    private static final String ARG_PARAM1 = "players";

    private Context mContext;
    private View fragmentView;

    public ScoreTab() {}

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
    }


    // metoda care seteaza tabelul de scor
    private void setTable() {
        ArrayList<TextView> nameTextViews = new ArrayList<>();
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_1));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_2));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_3));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_4));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_5));
        nameTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_name_6));

        ArrayList<TextView> scoreTextViews = new ArrayList<>();
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_1));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_2));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_3));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_4));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_5));
        scoreTextViews.add((TextView) fragmentView.findViewById(R.id.score_player_6));



        for(int i = 5; i >= players.size(); i--) {
            nameTextViews.get(i).setVisibility(View.GONE);
            scoreTextViews.get(i).setVisibility(View.GONE);

            nameTextViews.remove(i);
            scoreTextViews.remove(i);
        }

        for(int i = 0; i < players.size(); i++) {
            nameTextViews.get(i).setText(players.get(i));
        }


    }
}