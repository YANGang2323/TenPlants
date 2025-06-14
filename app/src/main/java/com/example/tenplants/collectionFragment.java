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
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
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
            //애니메이션
            toggleProfile(profileText);
            //flipCard(imageButton, profileText);
        });
        profileText.setOnClickListener(v -> {
            toggleProfile(profileText);
            //flipCard(imageButton, profileText);
        }); // 텍스트도 클릭 시 다시 거꾸로

        //돌아가기 버튼
        ((ImageButton) view.findViewById(R.id.backButton)).setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
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



    private boolean isProfileVisible = false; // 상태 추적

    private void toggleProfile(TextView profileText) {
        if (!isProfileVisible) {
            // 뽀용하게 등장
            profileText.setVisibility(View.VISIBLE);
            profileText.setScaleX(0f);
            profileText.setScaleY(0f);
            profileText.setAlpha(0f);

            profileText.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(300)
                    .setInterpolator(new OvershootInterpolator())
                    .start();

            isProfileVisible = true;

        } else {
            // 뽀용하게 사라짐
            profileText.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setDuration(300)
                    .setInterpolator(new AnticipateInterpolator())
                    .withEndAction(() -> profileText.setVisibility(View.GONE))
                    .start();

            isProfileVisible = false;
        }
    }
    //식물 클릭하면 카드 뒤집히는 것처럼 효과 나오고 상세 설명 나옴
    private boolean isFlipped = false; // flip 상태 저장
    private void flipCard(View frontView, View backView) {
        float scale = frontView.getContext().getResources().getDisplayMetrics().density;
        frontView.setCameraDistance(8000 * scale);
        backView.setCameraDistance(8000 * scale);
        if (!isFlipped) {
            // 이미지 → 텍스트
        AnimatorSet flipOut = new AnimatorSet();
        AnimatorSet flipIn = new AnimatorSet();

        ObjectAnimator frontFlipOut = ObjectAnimator.ofFloat(frontView, "rotationY", 90f, 0f);
        frontFlipOut.setDuration(200);
        frontFlipOut.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator backFlipIn = ObjectAnimator.ofFloat(backView, "rotationY", -90f, 0f);
        backFlipIn.setDuration(200);
        backFlipIn.setInterpolator(new DecelerateInterpolator());

        frontFlipOut.addListener(new AnimatorListenerAdapter() {
            @Override
                public void onAnimationEnd(Animator animation) {
                    frontView.setVisibility(View.VISIBLE);
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
        isFlipped = true;
    }else {
            // 텍스트 → 이미지
            AnimatorSet flipOut = new AnimatorSet();
            AnimatorSet flipIn = new AnimatorSet();

//            ObjectAnimator frontFlipIn = ObjectAnimator.ofFloat(frontView, "rotationY", -90f, 0f);
//            frontFlipIn.setDuration(200);
//            frontFlipIn.setInterpolator(new DecelerateInterpolator());

            ObjectAnimator backFlipOut = ObjectAnimator.ofFloat(backView, "rotationY", 90f, 0f);
            backFlipOut.setDuration(200);
            backFlipOut.setInterpolator(new AccelerateInterpolator());

            backFlipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    frontView.setVisibility(View.VISIBLE);
                    backView.setVisibility(View.GONE);
                }
            });

            flipOut.play(backFlipOut);
            //flipIn.play(frontFlipIn);

            flipOut.start();
            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    flipIn.start();
                }
            });

            isFlipped = false;
        }
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
            Map.entry("lv1_eglantine", R.drawable.lv1_eglantine),
            Map.entry("lv2_lavandula_angustifolia", R.drawable.lv2_lavandula_angustifolia),
            Map.entry("lv1_narcissus", R.drawable.lv1_narcissus),
            Map.entry("lv2_pansy", R.drawable.lv2_pansy),
            Map.entry("lv2_rhododendron_schlippenbachii", R.drawable.lv2_rhododendron_schlippenbachii),
            Map.entry("story_ending_1", R.drawable.story_ending_1),
            Map.entry("story_ending_2", R.drawable.story_ending_2),
            Map.entry("story_ending_3", R.drawable.story_ending_3)
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
            Map.entry("lv1_eglantine","이름: 들장미 / 야생 장미\n" +
                    "특징: 향기로운 꽃과 빨간 열매(로즈힙)를 맺음. 전통 약용으로도 사용."),
            Map.entry("lv2_lavandula_angustifolia","이름: 라벤더\n" +
                    "특징: 향기로 유명. 스트레스 완화, 진정 효과. 햇볕을 좋아함."),
            Map.entry("lv1_narcissus","이름: 수선화\n" +
                    "특징: 봄꽃의 대표주자. 흰색 또는 노란색 꽃을 피우며, 구근 식물."),
            Map.entry("lv2_pansy","이름: 팬지\n" +
                    "특징: 다양한 색상의 꽃이 매력적. 겨울부터 봄까지 개화. 화단에 자주 쓰임."),
            Map.entry("lv2_rhododendron_schlippenbachii","이름: 철쭉 (정확히는 흰철쭉 또는 연철쭉)\n" +
                    "특징: 봄에 화려한 꽃을 피우며, 한국 산지에서 흔하게 볼 수 있음."),
            Map.entry("story_ending_1","평범한 정원사 엔딩: 축하합니다! 10개의 식물을 완전히 기르는 데 성공했습니다!\n" +
                    "평범해 보이는 식물들이지만 이렇게 기르는데도 많은 시간과 정성이 들었습니다.\n" +
                    "당신은 평범한 정원사지만 당신의 노력은 모두가 알아줄 겁니다!"),
            Map.entry("story_ending_2","열정의 정원사 엔딩: 축하합니다! 10개의 식물을 완전히 기르는 데 성공했습니다!\n" +
                    "잘 키운 좋은 품질의 식물들이 햇살에 건강하게 빛납니다.\n" +
                    "당신은 열정의 정원사, 당신의 열정이 이 식물들을 키웠습니다."),
            Map.entry("story_ending_3","전설의 정원사 엔딩: 축하합니다! 10개의 식물을 완전히 기르는 데 성공했습니다!\n" +
                    "당신의 손에서 가꾸어진 식물들은 흠잡을 곳 없이 완벽합니다..!\n" +
                    "당신은 전설의 정원사입니다. 당신이 키우지 못할 식물은 존재하지 않습니다.\n" +
                    "대부호의 정원사가 되어 행복하게 돈을 벌 수 있을지도?")
    );
}