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

import androidx.annotation.IntRange;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameTab extends Fragment {

    private ArrayList<String> players;
    private int playerCount;

    private int myIndex;
    private String color;
    private int handsLeft;

    private DatabaseReference turnReference;
    private DatabaseReference bidReference;
    private DatabaseReference handsReference;
    private DatabaseReference handsLeftReference;
    private DatabaseReference scoreReference;

    // lista bid-urilor
    private final ArrayList<Integer> bids = new ArrayList<>();
    private final ArrayList<Integer> cards = new ArrayList<>();

    private final ArrayList<Integer> points = new ArrayList<>();

    private View rootView;
    private Context mContext;


    // variabila folosita pentru a stabili ordinea in care se executa comenzi in joc
    private int lastPlayerIndex;


    public GameTab() {
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
            players = getArguments().getStringArrayList("players");
            myIndex = getArguments().getInt("myIndex");
            playerCount = players.size();
        }

        turnReference = FirebaseDatabase.getInstance().getReference().child("Game").child("Turn");
        // creare intrare "Bids" pentru a intreba playerii cate maini vor lua
        bidReference = turnReference.child("Bids");
        // creare intrare "Hands" pentru a gestiona jocurile, handsLeft pentru maini ramase, Score pentru a gestiona scorul (maini luate
        handsReference = turnReference.child("Hands");
        handsLeftReference = turnReference.child("HandsLeft");
        scoreReference = turnReference.child("Score");

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

        setBidFinishedListener();

        // metoda prin care se ruleaza jocul
        runGame();

        return rootView;
    }

    private void runGame() {
        // (Momentan) se realizeaza un joc de 8
        turn(2 , 8);
    }

    public void turn(final int firstPlayerIndex, int gameType) {

        // initializare lastPlayerIndex
        lastPlayerIndex = setLastPlayerIndex(firstPlayerIndex);

        // initializare points si cards
        pointsInit();
        cardsInit();


        Log.d("bug", new Integer(lastPlayerIndex).toString() + " " + new Integer(firstPlayerIndex).toString());


        // Cod executat doar de jucatorul care este la rand sa faca bid
        if (firstPlayerIndex + 1 == myIndex) {
            // Amestecare carti + trimitere la server
            ArrayList<String> shuffledCards = CardShuffler.shuffleCards(playerCount);
            Map<String, Object> map = new HashMap<>();

            // Pun pentru fiecare player intr-un map un sublist al listei de carti amestecate
            // (8 carti pentru Player1, urmatoarele 8 carti pentru Player2, etc)
            for (int i = 0; i < playerCount; i++) {
                map.put("Cards", shuffledCards.subList(gameType * i, gameType * (i + 1)));

                // Trimitere la server playeri cu cartile lor amestecate
                DatabaseReference currPlayerReference = turnReference.child("Player" + (i + 1));
                currPlayerReference.updateChildren(map);

                // Trimitere la server Bids
                if (i != firstPlayerIndex) {
                    bidReference.child("Player" + (i + 1)).setValue("Pending");
                    handsReference.child("Player" + (i + 1)).setValue("Pending");
                }
                scoreReference.child("Player" + (i + 1)).setValue(0);
            }

            // setam pe intrarea jucatorului curent faptul ca el este cel care trebuie sa aleaga
            bidReference.child("Player" + (firstPlayerIndex + 1)).setValue("Current");
            handsReference.child("Player" + (firstPlayerIndex + 1)).setValue("Current");
            handsReference.child("Color").setValue("null");
            handsLeftReference.child("HandsLeft").setValue(gameType);
            handsLeft = gameType;
        }

        // Setare listener pe intrarea Player <indicele meu>
        DatabaseReference myPlayerReference = turnReference.child("Player" + myIndex);
        myPlayerReference.addChildEventListener(myPlayerListener());


        /// Inregistrarea bid-urilor jucatorilor
        // setare listener pe bidReference si pe handsLeftReference si pe intrarea "Color"
        bidReference.addChildEventListener(bidListener());
        handsReference.addChildEventListener(colorChanged());
        handsLeftReference.addChildEventListener(handsLeftChanged());

    }

    ////////////////////////////////////////////////////////////////////////////////////////////
                                            // setup

    // initializare arraylist points
    private void pointsInit() {
        for(int i = 0; i < playerCount; i++) {
            points.add(0);
        }
    }

    private void cardsInit() {
        for(int i = 0; i < playerCount; i++) {
            cards.add(-1);
        }
    }



    // metoda care seteaza cate un listener pe fiecare buton de bid
    private void setButtonListeners() {

        GridLayout buttonsGrid = rootView.findViewById(R.id.buttons_grid);
        final LinearLayout bidLayout = rootView.findViewById(R.id.bid_layout);
        int buttonsCount = buttonsGrid.getChildCount();

        for (int i = 0; i < buttonsCount; i++) {
            Button b = (Button) buttonsGrid.getChildAt(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // extragem valoarea in functie de butonul apasat
                    // adaugam valoarea la lista bids
                    Button b = (Button) view;
                    Integer value = Integer.parseInt(b.getText().toString());

                    // ascundem bid_layout
                    bidLayout.setVisibility(View.GONE);

                    // setam bid-ul in baza de date
                    bidReference.child("Player" + myIndex).setValue(value);

                    // setam valoarea "Current" in baza de date pentru persoana urmatoare
                    // daca toata lumea a pariat, setam bid = true pentru a trece la partea de dat carti

                    // TODO: Testare cu firstPlayerIndex diferit de zero
                    if(myIndex - 1 == lastPlayerIndex) {
                        // aici: se executa codul de dupa partea de bid
                        turnReference.child("BidFinished").child("IsFinished").setValue("True");
                    } else {
                        if(myIndex < playerCount) {
                            bidReference.child("Player" + (myIndex + 1)).setValue("Current");
                        } else {
                            bidReference.child("Player1").setValue("Current");
                        }
                    }

//                    if (myIndex < playerCount) {
//                        bidReference.child("Player" + (myIndex + 1)).setValue("Current");
//
//                    } else if (myIndex == playerCount) {
//                        // aici: se executa codul de dupa partea de bid
//                        turnReference.child("BidFinished").child("IsFinished").setValue("True");
//                    }
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

    // metoda care extrage layout-urile din fragment_game_tab si le ascunde pe cele nefolositoare
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

    // metoda care seteaza pe ecran bid-urile date de fiecare jucator
    private void setBidTextViews() {
        // ArrayList cu textView-urile in care se pun bid-urile
        ArrayList<TextView> bidTextViews = new ArrayList<>(5);
        bidTextViews.add((TextView) rootView.findViewById(R.id.player1_bid));
        bidTextViews.add((TextView) rootView.findViewById(R.id.player2_bid));
        bidTextViews.add((TextView) rootView.findViewById(R.id.player3_bid));
        bidTextViews.add((TextView) rootView.findViewById(R.id.player4_bid));
        bidTextViews.add((TextView) rootView.findViewById(R.id.player5_bid));

        // textview-ul in care se pune bid-ul jucatorului curent
        TextView myBidTextView = rootView.findViewById(R.id.my_bid_text_view);
        myBidTextView.setVisibility(View.VISIBLE);
        myBidTextView.setText("Bid: " + bids.get(myIndex - 1));

        int index = 0;
        // setare bid pt adversari
        for (int i = 0; i < playerCount; i++) {
            if ((i + 1) != myIndex) {
                bidTextViews.get(index).setVisibility(View.VISIBLE);
                bidTextViews.get(index++).setText("Bid: " + bids.get(i));
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Listeneri

    // listener pe intrarea "Player"
    private ChildEventListener myPlayerListener() {
        return new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Extragere carti de la intrarea Player cu indicele jucatorului curent
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {
                };
                ArrayList<String> myCards = snapshot.getValue(t);

                // Introducere carti pe slot-urile libere din fragment_game_tab.xml
                for (int i = 0; i < myCards.size(); i++) {
                    // extragere id al slot-ului
                    int resId = mContext.getResources().getIdentifier(
                            "card_slot_" + (i + 1),
                            "id",
                            mContext.getPackageName()
                    );
                    // extragere id al slot-ului
                    int drawableId = mContext.getResources().getIdentifier(
                            myCards.get(i),
                            "drawable",
                            mContext.getPackageName()
                    );

                    ImageView card = rootView.findViewById(resId);
                    // setare resursa pe slot
                    card.setImageResource(drawableId);
                    card.setContentDescription(myCards.get(i));
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
        };
    }


    // listener pe intrarea de Bid
    private ChildEventListener bidListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String result = snapshot.getValue(String.class);

                // jucatorul curent trebuie sa faca bid
                // adaugam pe ecran partea de bid, intrebam utilizatorul cate maini ia
                if (key.equals("Player" + (myIndex)) && result.equals("Current")) {

                    // setam vizibilitatea layout-ului din mijloc pe true
                    LinearLayout bidLayout = rootView.findViewById(R.id.bid_layout);
                    bidLayout.setVisibility(View.VISIBLE);

                } else {
                    // adaugam in arraylist valorile numerice
                    if (!result.equals("Current") && !result.equals("Pending")) {
                        bids.add(Integer.parseInt(result));
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String result = snapshot.getValue().toString();

                // jucatorul curent trebuie sa faca bid
                // adaugam pe ecran partea de bid, intrebam utilizatorul cate maini ia
                if (key.equals("Player" + (myIndex)) && result.equals("Current")) {

                    // setam vizibilitatea layout-ului din mijloc pe true
                    LinearLayout bidLayout = rootView.findViewById(R.id.bid_layout);
                    bidLayout.setVisibility(View.VISIBLE);

                } else {
                    // adaugam in arraylist valorile numerice
                    if (!result.equals("Current") && !result.equals("Pending")) {
                        bids.add(Integer.parseInt(result));
                    }
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
        };
    }

    // listener pentru cand partea de bid s-a terminat
    // declanseaza partea de dat carti a jocului
    private void setBidFinishedListener() {
        turnReference.child("BidFinished").child("IsFinished").setValue("False");

        turnReference.child("BidFinished").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                // cand intrarea BidFinished are valoarea true, afisam bid-urile fiecarui jucator si
                // setam listener-ul de pe intrarea hands
                if (value.equals("True")) {
                    setBidTextViews();
                    handsReference.addChildEventListener(handsListener());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.getValue(String.class);
                // cand intrarea BidFinished are valoarea true, afisam bid-urile fiecarui jucator si
                // setam listener-ul de pe intrarea hands
                if (value.equals("True")) {
                    setBidTextViews();
                    handsReference.addChildEventListener(handsListener());
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


    // listener pe intrarea hands
    private ChildEventListener handsListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // setare onClick pe carti atunci cand este randul jucatorului curent
                String key = snapshot.getKey();
                String value = snapshot.getValue(String.class);

                if (key.equals("Player" + myIndex) && value.equals("Current")) {
                    setCardOnClick();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // setare onClick pe carti atunci cand este randul jucatorului curent
                String key = snapshot.getKey();
                String value = snapshot.getValue(String.class);

                if (key.equals("Player" + myIndex) && value.equals("Current")) {
                    setCardOnClick();
                }

                // daca alt jucator a dat o carte, afisam cartea in dreptul sau
                if (!key.equals("Player" + myIndex) && !value.equals("Current") && !key.equals("Color")) {

                    // extragem indicele
                    int playerIndex = Character.getNumericValue(key.charAt(key.length() - 1));


                    addCardValue(value, playerIndex - 1);

                    // ajustam indicele (pentru a pune cartea in slot-ul corect)
                    if (playerIndex > myIndex) {
                        playerIndex--;
                    }

                    // extragem id-ul slotului in care punem imaginea
                    int resId = mContext.getResources().getIdentifier(
                            "slot_player" + playerIndex,
                            "id",
                            mContext.getPackageName()
                    );

                    // extragem drawable
                    int drawableId = mContext.getResources().getIdentifier(
                            value,
                            "drawable",
                            mContext.getPackageName()
                    );

                    ImageView card = rootView.findViewById(resId);
                    // setare resursa pe slot
                    card.setImageResource(drawableId);
                    card.setVisibility(View.VISIBLE);
                    card.setContentDescription(value);

                    Log.d("testAddCard", cards.toString());
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
        };
    }

    // listener pentru intratea "Color" din turn
    private ChildEventListener colorChanged() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String value = snapshot.getValue(String.class);
                if (key.equals("Color")) {
                    color = value;
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String value = snapshot.getValue(String.class);
                if (key.equals("Color")) {
                    color = value;
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
        };
    }

    // listener pentru setarea variabilei handsLeft
    private ChildEventListener handsLeftChanged() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handsLeft = snapshot.getValue(Integer.class);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handsLeft = snapshot.getValue(Integer.class);

                int maxCard = Collections.max(cards);
                int maxCardIndex = cards.indexOf(maxCard);

                points.set(maxCardIndex, points.get(maxCardIndex) + 1);

                Log.d("points", points.toString());
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
        };
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Alte metode


    // mmetoda care seteaza onClick pe carti
    private void setCardOnClick() {
        boolean sameColorCard = false;
        for (int i = 0; i < 8; i++) {
            // extragere id al cartii in functie de nume
            int resId = mContext.getResources().getIdentifier(
                    "card_slot_" + (i + 1),
                    "id",
                    mContext.getPackageName()
            );

            // setare listener pe carte
            // se verifica daca s-a gasit vreo carte de aceeasi culoare cu cea care este deja data
            ImageView img = rootView.findViewById(resId);
            String cardColor = extractColor(img.getContentDescription().toString());
            if (!(color.equals("null")) && (cardColor.equals(color))) {
                img.setOnClickListener(cardOnClickListener());
                sameColorCard = true;
            }
            if (color.equals("null")) {
                img.setOnClickListener(cardOnClickListener());
            }

        }
        // daca nu s-a gasit nicio carte de aceeasi culoare, se seteaza onclick pe toate cartile
        if (!sameColorCard && !(color.equals("null"))) {
            for (int i = 0; i < 8; i++) {
                // extragere id al cartii in functie de nume
                int resId = mContext.getResources().getIdentifier(
                        "card_slot_" + (i + 1),
                        "id",
                        mContext.getPackageName()
                );

                // setare listener pe carte
                ImageView img = rootView.findViewById(resId);
                img.setOnClickListener(cardOnClickListener());
            }
        }

    }

    // onClick pentru o carte
    private View.OnClickListener cardOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imageView = (ImageView) view;
                // setare vizibilitate pe gone
                imageView.setVisibility(View.GONE);

                // extragere nume carte din descrierea ei
                String cardName = imageView.getContentDescription().toString();

                // trimitere culoare la server
                if (color.equals("null")) {
                    String newColor = extractColor(cardName);

                    handsReference.child("Color").setValue(newColor);
                    color = newColor;
                }

                // se trimite la baza de date numele cartii care a fost data
                handsReference.child("Player" + myIndex).setValue(cardName);
                // se sterge descrierea de pe carte
                imageView.setContentDescription(null);

                // cu exceptia cazului in care jucatorul este ultimul, se seteaza "Current" pe intrarea
                // urmatorului jucator pentru a-l anunta ca el trebuie sa dea carte
                // daca player-ul este ultimul, actualizam handsLeft


                if(myIndex - 1 == lastPlayerIndex) {
                    handsLeftReference.child("HandsLeft").setValue(handsLeft - 1);
                } else {
                    if(myIndex == playerCount) {
                        handsReference.child("Player1").setValue("Current");
                    } else {
                        handsReference.child("Player" + (myIndex + 1)).setValue("Current");
                    }
                }

                // se elimina metodele de onClick de pe imagini
                for (int i = 0; i < 8; i++) {
                    int resId = mContext.getResources().getIdentifier(
                            "card_slot_" + (i + 1),
                            "id",
                            mContext.getPackageName()
                    );
                    ImageView img = rootView.findViewById(resId);
                    img.setOnClickListener(null);
                }
                // setare imagine cu cartea data pe slotul jucatorului curent
                ImageView newImg = rootView.findViewById(R.id.my_card_slot);
                newImg.setVisibility(View.VISIBLE);
                newImg.setImageDrawable(imageView.getDrawable());

                addCardValue(cardName, myIndex - 1);

                Log.d("testAddCard", cards.toString());

            }
        };
    }

    private void addCardValue(String cardName, int index) {
        String cardColor = extractColor(cardName);

        if(cardColor.equals(color)) {
            cards.set(index, extractValue(cardName));
        }
    }

    // metoda care extrage un string cu culoarea unei carti
    private String extractColor(String cardName) {
        int index = cardName.indexOf('_');
        return cardName.substring(0, index);
    }

    private int extractValue(String cardName) {
        int index = cardName.indexOf('_');
        String value = cardName.substring(index + 1, cardName.length());

        switch (value){
            case "a":
                return 15;
            case "j":
                return 12;
            case "q":
                return 13;
            case "k":
                return 14;
            default:
                return Integer.parseInt(value);
        }
    }

    // functie care intoarce lastPlayerIndex
    private int setLastPlayerIndex(int firstPlayerIndex) {
        Log.d("test2", new Integer(firstPlayerIndex).toString() + " " + new Integer(playerCount).toString());
        if(firstPlayerIndex == 0) {
            return playerCount - 1;
        }
        return firstPlayerIndex - 1;
    }

}