package com.example.whist;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitingRoomActivity extends AppCompatActivity {

    private EditText mInput;
    private ListView mPlayerNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        mPlayerNameList = (ListView) findViewById(R.id.player_name_list);

        // Create a List from String Array elements
        final List<String> fruits_list = new ArrayList<String>();

        // Create an ArrayAdapter from List
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, fruits_list);



        // DataBind ListView with items from ArrayAdapter
        mPlayerNameList.setAdapter(arrayAdapter);

        mInput = findViewById(R.id.player_name);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Toast.makeText(getApplicationContext(), "am apasat pe enter", Toast.LENGTH_SHORT).show();

                // Hide keyboard after pressing Enter button on the keyboard

                InputMethodManager imm = (InputMethodManager) WaitingRoomActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = WaitingRoomActivity.this.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(WaitingRoomActivity.this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                fruits_list.add("Sergiu");
                arrayAdapter.notifyDataSetChanged();

                return true;
            }
        });


    }
}