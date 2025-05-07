package com.example.tenplants;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CollectionRoomManager extends AppCompatActivity {        //류수민
    private MyGameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_room);

        gameManager = new MyGameManager(this);

    }
}
