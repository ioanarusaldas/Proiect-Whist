package com.example.whist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameTab extends Fragment {

    private ArrayList<String> players;
    private int playerCount;
    private int myIndex;

    private DatabaseReference turnReference;
    private DatabaseReference bidReference;

    // lista bid-urilor
    private final ArrayList<Integer> bids = new ArrayList<>();

    private View rootView;

    private Context mContext;

    public GameTab() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
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
        // creare intrare "Bids" pentru a intreba playerii cate maini vor lua
        bidReference = turnReference.child("Bids");
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

        // setare listeneri pe butoanele de bid
        setButtonListeners();

        // metoda prin care se ruleaza jocul
        runGame(rootView);

        return rootView;
    }

    private void runGame(View rootView) {
        // (Momentan) se realizeaza un joc de 8
        turn(0, 8, rootView);
    }


    public void turn(final int currentPlayerIndex, int gameType, final View rootView) {

        // Cod executat doar de jucatorul care este la rand sa faca bid
        if (currentPlayerIndex + 1 == myIndex) {
            // Amestecare carti + trimitere la server
            ArrayList<Integer> shuffledCards = CardShuffler.shuffleCards(playerCount);
            Map<String, Object> map = new HashMap<>();

            // Pun pentru fiecare player intr-un map un sublist al listei de carti amestecate
            // (8 carti pentru Player1, urmatoarele 8 carti pentru Player2, etc)
            for (int i = 0; i < playerCount; i++) {
                map.put("Cards", shuffledCards.subList(gameType * i, gameType * (i + 1)));

                // Trimitere la server playeri cu cartile lor amestecate
                DatabaseReference currPlayerReference = turnReference.child("Player" + (i + 1));
                currPlayerReference.updateChildren(map);

                // Trimitere la server Bids
                if(i != currentPlayerIndex) {
                    bidReference.child("Player" + (i + 1)).setValue("Pending");
                }

            }

            // setam pe intrarea jucatorului curent faptul ca el este cel care trebuie sa aleaga
            bidReference.child("Player" + (currentPlayerIndex + 1)).setValue("Current");
        }

        // Setare listener pe intrarea Player <indicele meu>
        DatabaseReference myPlayerReference = turnReference.child("Player" + myIndex);
        myPlayerReference.addChildEventListener(myPlayerListener());


        /// Inregistrarea bid-urilor jucatorilor
        // setare listener pe bidReference
        bidReference.addChildEventListener(bidListener());

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
                                        // setup


    // metoda care seteaza cate un listener pe fiecare buton de bid
    private void setButtonListeners() {

        GridLayout buttonsGrid = (GridLayout) rootView.findViewById(R.id.buttons_grid);
        final LinearLayout bidLayout = (LinearLayout) rootView.findViewById(R.id.bid_layout);
        int buttonsCount = buttonsGrid.getChildCount();

        for(int i = 0; i < buttonsCount; i++){
            Button b = (Button)buttonsGrid.getChildAt(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // extragem valoarea in functie de butonul apasat
                    // adaugam valoarea la lista bids
                    Button b = (Button) view;
                    Integer value = Integer.parseInt(b.getText().toString());
                    bids.add(value);

                    // ascundem bid_layout
                    bidLayout.setVisibility(View.INVISIBLE);

                    // setam bid-ul in baza de date
                    bidReference.child("Player" + myIndex).setValue(value);

                    // setam valoarea "Current" in baza de date pentru persoana urmatoare
                    // daca toata lumea a pariat, setam bid = true pentru a trece la partea de dat carti
                    if(myIndex < playerCount) {
                        bidReference.child("Player" + (myIndex + 1)).setValue("Current");
                    } else {
                        // aici: se executa codul de dupa partea de bid
                        turnReference.child("Next").setValue("Player1");
                        Log.d("bids are:", "Bids are:" + bids.toString());
                    }
                }
            });
        }
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
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.opponent_layout1));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.opponent_layout2));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.opponent_layout3));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.opponent_layout4));
        playerLinearLayout.add((LinearLayout) rootView.findViewById(R.id.opponent_layout5));


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

    ///////////////////////////////////////////////////////////////////////////////////////////////

    // listener pe intrarea "Player"
    private ChildEventListener myPlayerListener() {
        return new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Extragere carti de la intrarea Player cu indicele jucatorului curent
                GenericTypeIndicator<ArrayList<Integer>> t = new GenericTypeIndicator<ArrayList<Integer>>() {
                };
                ArrayList<Integer> myCards = snapshot.getValue(t);

                // Introducere carti pe slot-urile libere din fragment_game_tab.xml
                for (int i = 0; i < myCards.size(); i++) {
                    // extragere id al slot-ului

                    int resId = mContext.getResources().getIdentifier(
                            "card_slot_" + (i + 1),
                            "id",
                            mContext.getPackageName()
                    );

                    ImageView card = rootView.findViewById(resId);
                    // setare resursa pe slot
                    card.setImageResource(myCards.get(i));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }


    // listener pe intrarea de Bid
    private ChildEventListener bidListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String result = snapshot.getValue(String.class);

                if(key.equals("Player" + (myIndex)) && result.equals("Current")) {

                    // setam vizibilitatea layout-ului din mijloc pe true
                    LinearLayout bidLayout = (LinearLayout) rootView.findViewById(R.id.bid_layout);
                    bidLayout.setVisibility(View.VISIBLE);

                } else {
                    // adaugam in arraylist valorile numerice
                    if(result.equals("Current") == false && result.equals("Pending") == false) {
                        bids.add(Integer.parseInt(result));
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String result = snapshot.getValue().toString();

                // jucatorul curent trebuie sa faca bid
                // adaugam pe ecran partea de bid, intrebam utilizatorul cate maini ia, trimitem rezultatul la server, ascundem partea de bid, setam pt
                // urmatorul player Current (daca nu suntem ultimii)
                if(key.equals("Player" + (myIndex)) && result.equals("Current")) {

                    // setam vizibilitatea layout-ului din mijloc pe true
                    LinearLayout bidLayout = (LinearLayout) rootView.findViewById(R.id.bid_layout);
                    bidLayout.setVisibility(View.VISIBLE);

                } else {
                    // adaugam in arraylist valorile numerice
                    if(result.equals("Current") == false && result.equals("Pending") == false) {
                        bids.add(Integer.parseInt(result));
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
    }
}