package com.example.whist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity {

    private EditText mInput;
    private ListView mPlayerNameList;
    private DatabaseReference mPlayerReference;
    private TextView mConnectedPlayersView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        // ======

        mPlayerNameList = findViewById(R.id.player_name_list);

        // Create a List from String Array elements
        final List<String> playerList = new ArrayList<>();
        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                (this, android.R.layout.simple_list_item_1, playerList);
        // DataBind ListView with items from ArrayAdapter
        mPlayerNameList.setAdapter(arrayAdapter);

        // ======

        mPlayerReference = FirebaseDatabase.getInstance().getReference().child("Players");
        mPlayerReference.addChildEventListener(new ChildEventListener() {
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
                Toast.makeText(WaitingRoomActivity.this, newPlayer + " connected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                String disconnectedPlayer = snapshot.getValue(String.class);

                // eliminare din ArrayList a jucatorului
                playerList.remove(disconnectedPlayer);
                arrayAdapter.notifyDataSetChanged();

                // actualizare TextView-ul cu numarul de jucatori conectati
                mConnectedPlayersView = findViewById(R.id.connected_players);
                mConnectedPlayersView.setText(
                        String.format(getResources().getString(R.string.connected_players),
                                playerList.size())
                );

                Toast.makeText(WaitingRoomActivity.this, disconnectedPlayer + " disconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        

        // Set Listener on Input
        mInput = findViewById(R.id.player_name);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

//                playerList.add(mInput.getText().toString());
//                arrayAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }
}

/*
    TODO:

    1. - In onCreate, extrage valoarea pentru intrarea "Players", se extrag numele
    jucatorilor, se adauga in playerList si se actualizeaza arrayAdapter, pentru a se afisa
    jucatorii deja conectati    --- DONE

    2. - In onCreate se seteaza listener pe intrarea cu cheia "Players", astfel incat la orice
    modificare a valorii intrarii sa se modifice si playerList (astfel lista de jucatori afisata
    pe ecran sa se actualizeze in timp real     --- DONE

    3. - Pe butonul de enter al tastaturii, adauga la cheia "Players", un Map de forma:
    "Player1" -> X
    "Player2" -> Y
    unde Y este ceea ce a fost introdus in PlainText

    4. Daca utilizatorul schimba numele?
    - (varianta 1) Dupa apasarea butonului de Enter, dezactivam PlainText ca sa nu mai poata alege
    alt nume

    - (varianta 2) Retinem care este numarul jucatorului, la selectarea altui nume modificam doar
valoarea pentru jucatorul respectiv


    Bonus:

    5. - Adaugam un TextView in care specificam cati jucatori s-au conectat    -- DONE
    6. - In onDestroy - eliminam utilizatorul din lista
 */