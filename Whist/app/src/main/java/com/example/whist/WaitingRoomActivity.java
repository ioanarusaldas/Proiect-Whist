package com.example.whist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity {

    private EditText mInput;
    private ListView mPlayerNameList;
    private DatabaseReference mPlayerReference;
    private DatabaseReference mStatusReference;
    private TextView mConnectedPlayersView;
    private Button mStartButton;

    private final ArrayList<String> playerList = new ArrayList<>();
    // Se retine index-ul si numele jucatorului
    private String playerName;
    private int playerIndex;

    private boolean disconnected = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        mPlayerNameList = findViewById(R.id.player_name_list);

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, playerList);
        // DataBind ListView with items from ArrayAdapter
        mPlayerNameList.setAdapter(arrayAdapter);

        // adaugam listener pe intrarea "Players" din baza de date
        mPlayerReference = FirebaseDatabase.getInstance().getReference().child("Players");
        mPlayerReference.addChildEventListener(playersListener(arrayAdapter));


        // setare listener pe mInput
        mInput = findViewById(R.id.player_name);
        mInput.setOnEditorActionListener(inputListener());

        mStartButton = findViewById(R.id.start_game_button);
    }

    /*
        Daca inchidem activitatea, eliminam din baza de date jucatorul si notificam ceilalti jucatori
        sa isi actualizeze UI-ul prin eliminarea noastra din lista
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // flag care indica ca modificarea bazei de date se realizeaza din cauza unei deconectari
        disconnected = true;
        playerList.remove(playerName);

        HashMap<String, Object> map = new HashMap<>();

        for (int i = 0; i < playerList.size(); i++) {
            map.put("Player" + (i + 1), playerList.get(i));
        }
        mPlayerReference.setValue(map);
    }

    // metoda care porneste activitatea GameActivity si notifica si pe ceilalti jucatori sa o deschida
    public void startGame(View view) {

        // actualizeaza intrarea Started cu true pentru a informa ceilalti jucatori ca jocul a inceput
        HashMap<String, Object> map = new HashMap<>();
        map.put("Started", "True");
        mStatusReference.setValue(map);

        // incepere joc
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("myIndex", playerIndex);
        intent.putStringArrayListExtra("players", playerList);
        startActivity(intent);
    }



    //////////////////////// Metode care seteaza listeneri //////////////////////////////

    // listener pentru intrarea Players din baza de date
    private ChildEventListener playersListener(final ArrayAdapter<String> arrayAdapter) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String newPlayer = snapshot.getValue(String.class);

                // adaugare la ArrayList noul jucator
                playerList.add(newPlayer);
                arrayAdapter.notifyDataSetChanged();

                // actualizare TextView-ul cu numarul de jucatori conectati
                mConnectedPlayersView = findViewById(R.id.connected_players);
                mConnectedPlayersView.setText(
                        String.format(getResources().getString(R.string.connected_players),
                                playerList.size())
                );

                if (playerList.size() == 3) {
                    mStartButton.setEnabled(true);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                // daca NU ne-am deconectat (la modificarea unui nume de jucator):
                if (disconnected == false) {

                    // la schimbarea numelui jucatorului, actualizam ListView
                    String changedPlayer = snapshot.getValue(String.class);
                    Integer modifiedIndex;
                    if (previousChildName == null) {
                        modifiedIndex = 0;
                    } else {
                        modifiedIndex = Integer.parseInt(previousChildName.substring(previousChildName.length() - 1));
                    }

                    playerList.set(modifiedIndex, changedPlayer);
                    arrayAdapter.notifyDataSetChanged();

                    if (playerList.size() == 2) {
                        mStartButton.setEnabled(false);
                    }

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                String disconnectedPlayer = snapshot.getValue(String.class);

                // eliminare din ArrayList a jucatorului
                playerList.remove(disconnectedPlayer);
                arrayAdapter.notifyDataSetChanged();

                playerIndex = playerList.indexOf(playerName);

                // actualizare TextView-ul cu numarul de jucatori conectati
                mConnectedPlayersView = findViewById(R.id.connected_players);
                mConnectedPlayersView.setText(
                        String.format(getResources().getString(R.string.connected_players),
                                playerList.size())
                );

                if (playerList.size() == 2) {
                    mStartButton.setEnabled(false);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
    }

    // Listener pentru intrarea Status din baza de date
    private ChildEventListener statusListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.getKey();
                String gameState = snapshot.getValue(String.class);
                // Daca altcineva a apasat butonul de start, acest lucru este semnalat de modificarea intrarii
                // "Status" - se creaza un intent si se porneste GameActivity
                if(key.equals("Started") && gameState.equals("True")) {

                    Intent intent = new Intent(WaitingRoomActivity.this, GameActivity.class);
                    intent.putExtra("myIndex", playerIndex);
                    intent.putStringArrayListExtra("players", playerList);
                    startActivity(intent);
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

    // listener pentru Input de la utilizator (pentru introducerea numelui)

    private TextView.OnEditorActionListener inputListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                // Hide keyboard after pressing Enter button on the keyboard

                InputMethodManager imm = (InputMethodManager) WaitingRoomActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = WaitingRoomActivity.this.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(WaitingRoomActivity.this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                //  =====

                if (playerList.size() < 6) {
                    // determinare pozitie pentru player in lista
                    if (playerName == null) {
                        playerIndex = playerList.size() + 1;
                    }
                    // extragere nume din mInput
                    playerName = mInput.getText().toString();

                    // mapare PlayerX -> numele extras
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("Player" + playerIndex, playerName);

                    // actualizare copii pentru intrarea Players
                    mPlayerReference.updateChildren(map);
                    mStartButton.setVisibility(Button.VISIBLE);

                    // setare listener pentru intrarea "Status"
                    mStatusReference = FirebaseDatabase.getInstance().getReference().child("Game").child("Status");
                    mStatusReference.addChildEventListener(statusListener());
                } else {
                    // Restrictionam numarul maxim de jucatori la 6
                    Toast.makeText(WaitingRoomActivity.this, "Maximum number of players (6) reached!", Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        };
    }
}