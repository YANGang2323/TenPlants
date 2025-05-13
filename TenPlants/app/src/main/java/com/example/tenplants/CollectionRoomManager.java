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
//류수민
    //프로그래밍 기획
    //lv0 - 산세베리아, 푸밀라, 산호수
    //lv1 - 황매화, 꽃마리, 쥐손이풀
    //lv2 - 해당화, 금계국, 라벤더
    //lv3 - 팬지, 수선화, 철쭉

