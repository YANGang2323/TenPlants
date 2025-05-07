package com.example.tenplants;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class StoryManager extends AppCompatActivity {        //류수민
    private MyGameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story);

        gameManager = new MyGameManager(this);

    }
}
