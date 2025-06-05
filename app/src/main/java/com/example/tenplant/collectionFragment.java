package com.example.tenplant;

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
    private static final Map<String, Integer> plantImageMap = Map.ofEntries(
            Map.entry("lv0_ficus_pumila", R.drawable.lv0_ficus_pumila),
            Map.entry("lv0_sansevieria", R.drawable.lv0_sansevieria),
            Map.entry("lv0_ardisia_pusilla", R.drawable.lv0_ardisia_pusilla),
            Map.entry("lv1_geranium_palustre", R.drawable.lv1_geranium_palustre),
            Map.entry("lv1_kerria_japonica", R.drawable.lv1_kerria_japonica),
            Map.entry("lv1_trigonotis_peduncularis", R.drawable.lv1_trigonotis_peduncularis),
            Map.entry("lv2_coreopsis_basalis", R.drawable.lv2_coreopsis_basalis),
            Map.entry("lv2_eglantine", R.drawable.lv2_eglantine),
            Map.entry("lv2_lavandula_angustifolia", R.drawable.lv2_lavandula_angustifolia),
            Map.entry("lv3_narcissus", R.drawable.lv3_narcissus),
            Map.entry("lv3_pansy", R.drawable.lv3_pansy),
            Map.entry("lv3_rhododendron_schlippenbachii", R.drawable.lv3_rhododendron_schlippenbachii)
    );

    //식물이름에 맞는 설명 띄우기
    private static final Map<String, String> plantProfileMap = Map.ofEntries(
            Map.entry( "lv0_ardisia_pusilla","이름: 아디시아 푸실라 / 아디시아속 식물\n" +
                    "특징: 작은 상록 관목으로 붉은 열매를 맺으며 실내 식물로 인기. 그늘에서도 잘 자람."),
            Map.entry("lv0_ficus_pumila", "이름: 아이비무화과 / 푸밀라 고무나무\n" +
                    "특징: 덩굴성 식물로 벽을 타고 자람. 음지에서도 생존 가능하며, 공기정화 효과가 뛰어남."),
            Map.entry("lv0_sansevieria","이름: 산세베리아 / 뱀꼬리풀\n" +
                    "특징: 공기 정화에 탁월하며 물을 적게 줘도 잘 자람. 실내에 최적화된 식물."),
            Map.entry("lv1_geranium_palustre","이름: 늪제라늄 / 야생 제라늄\n" +
                    "특징: 보라빛 꽃이 특징. 습지에서 자라며 향이 있음. 전통 약용 식물."),
            Map.entry("lv1_kerria_japonica","이름: 황매화\n" +
                    "특징: 봄에 노란 꽃을 피움. 관목 형태로 정원수로 인기가 많음."),
            Map.entry("lv1_trigonotis_peduncularis","이름: 개불알풀\n" +
                    "특징: 작고 푸른 꽃을 피우는 들꽃. 들판이나 길가에서 자주 보임. 봄꽃."),
            Map.entry("lv2_coreopsis_basalis","이름: 금계국 (Coreopsis)\n" +
                    "특징: 노란색 꽃으로 여름 정원을 장식. 해바라기처럼 햇빛을 좋아함."),
            Map.entry("lv2_eglantine","이름: 들장미 / 야생 장미\n" +
                    "특징: 향기로운 꽃과 빨간 열매(로즈힙)를 맺음. 전통 약용으로도 사용."),
            Map.entry("lv2_lavandula_angustifolia","이름: 라벤더\n" +
                    "특징: 향기로 유명. 스트레스 완화, 진정 효과. 햇볕을 좋아함."),
            Map.entry("lv3_narcissus","이름: 수선화\n" +
                    "특징: 봄꽃의 대표주자. 흰색 또는 노란색 꽃을 피우며, 구근 식물."),
            Map.entry("lv3_pansy","이름: 팬지\n" +
                    "특징: 다양한 색상의 꽃이 매력적. 겨울부터 봄까지 개화. 화단에 자주 쓰임."),
            Map.entry("lv3_rhododendron_schlippenbachii","이름: 철쭉 (정확히는 흰철쭉 또는 연철쭉)\n" +
                    "특징: 봄에 화려한 꽃을 피우며, 한국 산지에서 흔하게 볼 수 있음.")
    );
}