package com.example.tenplants;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

            // ViewFlipper 또는 ObjectAnimator 사용 예시
            //AnimatorSet set = new AnimatorSet();
            //ObjectAnimator flip1 = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 90f);
            //flip1.setDuration(150);
            //flip1.addListener(new AnimatorListenerAdapter() {
            //    public void onAnimationEnd(Animator animation) {
            //        imageView.setImageResource(newImageRes); // 이미지 교체
            //        profileTextView.setVisibility(View.VISIBLE); // 프로필 노출
            //    }
            //});
            //ObjectAnimator flip2 = ObjectAnimator.ofFloat(imageView, "rotationY", 90f, 180f);
            //flip2.setDuration(150);
            //set.playSequentially(flip1, flip2);
            //set.start();
        });

        return view;
    }

    private void flipCard(View imageView, View textView) {
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(imageView, "rotationY", 0f, 90f);
        flipOut.setDuration(150);

        flipOut.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                textView.setVisibility(View.VISIBLE); // 텍스트 보이기
            }
        });

        ObjectAnimator flipIn = ObjectAnimator.ofFloat(imageView, "rotationY", 90f, 180f);
        flipIn.setDuration(150);

        set.playSequentially(flipOut, flipIn);
        set.start();
    }

    private static final Map<String, Integer> plantImageMap = Map.of(
            "plant01", R.drawable.lv0_ficus_pumila,
            "plant02", R.drawable.lv0_sansevieria
    );

    private static final Map<String, String> plantProfileMap = Map.of(
            "plant01", "🌿 민들레: 햇빛을 좋아하고 빨리 자라요!",
            "plant02", "🌵 선인장: 물이 적어도 잘 자라요!"
    );
}