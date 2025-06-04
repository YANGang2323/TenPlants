package com.example.tenplants;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Map;

public class collectionFragment extends DialogFragment {

    private static final String ARG_PLANT_CODE = "plant_code";

    public static collectionFragment newInstance(String plantCode) {
        collectionFragment fragment = new collectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLANT_CODE, plantCode);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection, container, false);

        ImageButton imageButton = view.findViewById(R.id.plant_image_button);
        TextView profileText = view.findViewById(R.id.plant_profile_text);

        String plantCode = getArguments().getString(ARG_PLANT_CODE);

        // 코드에 따라 이미지 선택 (예시)
        int imageRes = getImageResourceByCode(plantCode);
        imageButton.setImageResource(imageRes);

        // 프로필 텍스트 설정 (예시)
        String profileInfo = getPlantProfileByCode(plantCode);
        profileText.setText(profileInfo);

        imageButton.setOnClickListener(v -> {
            // 카드 반전 애니메이션
            flipCard(imageButton, profileText);
        });

        return view;
    }

    // 코드에 따라 이미지 선택
    private int getImageResourceByCode(String code) {
        Integer resId = plantImageMap.get(code);
        return resId != null ? resId : R.drawable.lv0_ardisia_pusilla; // 기본 이미지 fallback
    }

    // 프로필 텍스트 설정
    private String getPlantProfileByCode(String code) {
        String profile = plantProfileMap.get(code);
        return profile != null ? profile : "알 수 없는 식물입니다.";
    }

    //식물 클릭하면 카드 뒤집히는 것처럼 효과 나오고 상세 설명 나옴
    private void flipCard(View frontView, View backView) {
        float scale = frontView.getContext().getResources().getDisplayMetrics().density;
        frontView.setCameraDistance(8000 * scale);
        backView.setCameraDistance(8000 * scale);

        AnimatorSet flipOut = new AnimatorSet();
        AnimatorSet flipIn = new AnimatorSet();

        ObjectAnimator frontFlipOut = ObjectAnimator.ofFloat(frontView, "rotationY", 0f, 90f);
        frontFlipOut.setDuration(200);
        frontFlipOut.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator backFlipIn = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f);
        backFlipIn.setDuration(200);
        backFlipIn.setInterpolator(new DecelerateInterpolator());

        frontFlipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setVisibility(View.GONE);
                backView.setVisibility(View.VISIBLE);
            }
        });

        flipOut.play(frontFlipOut);
        flipIn.play(backFlipIn);

        flipOut.start();
        flipOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                flipIn.start();
            }
        });
    }

    //식물이름에 맞는 이미지 넣기
    private static final Map<String, Integer> plantImageMap = Map.of(
            "plant01", R.drawable.lv0_ficus_pumila,
            "plant02", R.drawable.lv0_sansevieria
    );

    //식물이름에 맞는 설명 띄우기
    private static final Map<String, String> plantProfileMap = Map.of(
            "plant01", "🌿 민들레: 햇빛을 좋아하고 빨리 자라요!",
            "plant02", "🌵 선인장: 물이 적어도 잘 자라요!"
    );
}