package com.example.tenplants;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionRoomManager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_room);

        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);

        //game bgm
        SoundManager.playBGM("game");

        load();

    }
    //여기서 부터 잠금해제 식물 이미지버튼 추가
    private int maxCols = 3; // 한 줄에 최대 3개
    private int plantCount = 0;
    private TableRow currentRow;

    private void load() {
        TableLayout tableLayout = findViewById(R.id.plant_table);
        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);

        // 1. 식물 처리
        List<String> unlockedPlants = new ArrayList<>();
        try {
            unlockedPlants = dbHelper.getUnlockedPlantNames();
        } catch (Exception e) {
            Log.e("DB_ERROR", "UnlockedPlants 불러오기 실패: " + e.getMessage());
        }

        if (unlockedPlants != null && !unlockedPlants.isEmpty()) {
            int sizeInDp = 120;
            float scale = getResources().getDisplayMetrics().density;
            int sizeInPx = (int) (sizeInDp * scale + 0.5f);

            TableRow currentRow = null;

            for (int i = 0; i < unlockedPlants.size(); i++) {
                if (i % 3 == 0) {
                    currentRow = new TableRow(this);
                    tableLayout.addView(currentRow);
                }

                String plantName = unlockedPlants.get(i);
                int grade = dbHelper.getGrade(plantName);
                String imageName = "lv" + grade + "_" + plantName.toLowerCase();

                int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                int backResId = getResources().getIdentifier("green_box", "drawable", getPackageName());
                Drawable backDrawable = ContextCompat.getDrawable(this, backResId);

                ImageButton plantButton = new ImageButton(this);
                plantButton.setBackground(backDrawable);
                plantButton.setImageResource(resId);
                plantButton.setLayoutParams(new TableRow.LayoutParams(sizeInPx, sizeInPx));
                plantButton.setOnClickListener(v -> {
                    //openImageFragment(resId); // resId: 식물 이미지 리소스 ID
                    openImageFragment(imageName); // resId: 식물 이미지 리소스 NAME
                });

                if (currentRow != null) {
                    currentRow.addView(plantButton);
                }
            }
        }

        // 2. 엔딩 처리
        ImageButton end1 = findViewById(R.id.end1);
        ImageButton end2 = findViewById(R.id.end2);
        ImageButton end3 = findViewById(R.id.end3);
        List<String> unlockedEndings = new ArrayList<>();
        try {
            unlockedEndings = dbHelper.getUnlockedEnddingNames();
        } catch (Exception e) {
            Log.e("DB_ERROR", "UnlockedEndings 불러오기 실패: " + e.getMessage());
        }

        Map<String, String> endingImageMap = new HashMap<>();
        endingImageMap.put("평범한 정원사 엔딩", "story_ending_1");
        endingImageMap.put("열정의 정원사 엔딩", "story_ending_2");
        endingImageMap.put("전설의 정원사 엔딩", "story_ending_3");

        List<ImageButton> endingButtons = Arrays.asList(end1, end2, end3);

        // 해금된 엔딩 수만큼 버튼에 이미지 할당
        for (int i = 0; i < unlockedEndings.size() && i < endingButtons.size(); i++) {
            String endingName = unlockedEndings.get(i);
            String imageName = endingImageMap.getOrDefault(endingName, "story_ending_1");
            int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());

            endingButtons.get(i).setImageResource(resId);
            endingButtons.get(i).setBackground(null);

            endingButtons.get(i).setOnClickListener(v -> {
                //openImageFragment(resId); // resId: 엔딩 이미지 리소스 ID
                openImageFragment(imageName); // resId: 식물 이미지 리소스 NAME
            });

        }
    }

    //프래그먼트 여는 메서드
    private void openImageFragment(String resId) {
        collectionFragment fragment = collectionFragment.newInstance(resId);

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

//    /**
//     * 식물 이름으로 drawable 리소스 ID 반환
//     */
//    private int getDrawableResId(String plantName) {
//        int resId = getResources().getIdentifier(plantName, "drawable", getPackageName());
//        return resId != 0 ? resId : R.drawable.lv0_ardisia_pusilla; // 기본 이미지 fallback
//    }
//
//    /**
//     * 식물 이름에 해당하는 drawable 이미지로 ImageButton을 생성하여
//     * TableLayout에 추가하는 함수
//     */
//    private void addPlantButton(String plantName) {
//        // 1. drawable 리소스 ID 가져오기
//        int imageRes = getDrawableResId(plantName);
//
//        // 2. ImageButton 생성
//        ImageButton plantBtn = new ImageButton(this);
//        plantBtn.setImageResource(imageRes);
//        plantBtn.setBackground(null); // 배경 제거
//        plantBtn.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//        // 3. 크기와 여백 설정
//        TableRow.LayoutParams btnParams = new TableRow.LayoutParams(200, 200);
//        btnParams.setMargins(16, 16, 16, 16);
//        plantBtn.setLayoutParams(btnParams);
//
//        // 4. 클릭 이벤트 설정 (DialogFragment로 상세 보기)
//        plantBtn.setOnClickListener(v -> {
//            collectionFragment dialog = collectionFragment.newInstance(plantName);
//            dialog.show(getSupportFragmentManager(), "PlantDetail");
//        });
//
//        // 5. 새로운 줄이 필요한 경우 TableRow 추가
//        if (plantCount % maxCols == 0) {
//            currentRow = new TableRow(this);
//            ((TableLayout) findViewById(R.id.plant_table)).addView(currentRow);
//        }
//
//        // 6. 현재 Row에 버튼 추가
//        currentRow.addView(plantBtn);
//        plantCount++;
//    }

    // 뒤로가기 눌렀을 때 mainActivity select_content 창으로 이동
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