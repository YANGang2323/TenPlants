package com.example.tenplants;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class CollectionRoomManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_room);

        TableLayout tableLayout = findViewById(R.id.plant_table);


        // gameManager에서 성장완료된 식물 정보 읽기
        List<String> plantNames = MyGameManager.getInstance(this).getGrownPlants();
        for (String plantName : plantNames) {
            addPlantButton(plantName);
        }
    }


    //여기서 부터 성장완료 식물 이미지버튼 추가
    private int maxCols = 3; // 한 줄에 최대 3개
    private int plantCount = 0;
    private TableRow currentRow;

    /**
     * 식물 이름으로 drawable 리소스 ID 반환
     */
    private int getDrawableResId(String plantName) {
        int resId = getResources().getIdentifier(plantName, "drawable", getPackageName());
        return resId != 0 ? resId : R.drawable.lv0_ardisia_pusilla; // 기본 이미지 fallback
    }


    /**
     * 식물 이름에 해당하는 drawable 이미지로 ImageButton을 생성하여
     * TableLayout에 추가하는 함수
     */
    private void addPlantButton(String plantName) {
        // 1. drawable 리소스 ID 가져오기
        int imageRes = getDrawableResId(plantName);

        // 2. ImageButton 생성
        ImageButton plantBtn = new ImageButton(this);
        plantBtn.setImageResource(imageRes);
        plantBtn.setBackground(null); // 배경 제거
        plantBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 3. 크기와 여백 설정
        TableRow.LayoutParams btnParams = new TableRow.LayoutParams(200, 200);
        btnParams.setMargins(16, 16, 16, 16);
        plantBtn.setLayoutParams(btnParams);

        // 4. 클릭 이벤트 설정 (DialogFragment로 상세 보기)
        plantBtn.setOnClickListener(v -> {
            collectionFragment dialog = collectionFragment.newInstance(plantName);
            dialog.show(getSupportFragmentManager(), "PlantDetail");
        });

        // 5. 새로운 줄이 필요한 경우 TableRow 추가
        if (plantCount % maxCols == 0) {
            currentRow = new TableRow(this);
            ((TableLayout) findViewById(R.id.plant_table)).addView(currentRow);
        }

        // 6. 현재 Row에 버튼 추가
        currentRow.addView(plantBtn);
        plantCount++;
    }

    // 뒤로가기 눌렀을 때 story 말고 mainActivity select_content 창으로 이동
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(CollectionRoomManager.this, MainActivity.class);
            intent.putExtra("message", "select");
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}


//프로그래밍 기획
//lv0 - 산세베리아, 푸밀라, 산호수
//lv1 - 황매화, 꽃마리, 쥐손이풀
//lv2 - 해당화, 금계국, 라벤더
//lv3 - 팬지, 수선화, 철쭉