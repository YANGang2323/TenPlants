//package com.example.tenplants;
//
//public class Plant {
//    public enum Grade {
//        BASIC(100, new int[]{20, 40, 100}, 10),
//        INTERMEDIATE(240, new int[]{50, 100, 240}, 20),
//        ADVANCED(300, new int[]{100, 200, 300}, 30);
//
//        private final int maxGrowth;
//        private final int[] stepThresholds; // 단계별 성장도 기준
//        private final int achievementPoint;
//
//        Grade(int maxGrowth, int[] stepThresholds, int achievementPoint) {
//            this.maxGrowth = maxGrowth;
//            this.stepThresholds = stepThresholds;
//            this.achievementPoint = achievementPoint;
//        }
//
//        public int getMaxGrowth() {
//            return maxGrowth;
//        }
//
//        public int[] getStepThresholds() {
//            return stepThresholds;
//        }
//
//        public int getAchievementPoint() {
//            return achievementPoint;
//        }
//    }
//
//    private String name;
//    private Grade grade;
//    private int growth;
//    private int step;
//
//    public Plant(String name, Grade grade) {
//        this.name = name;
//        this.grade = grade;
//        this.growth = 0;
//        this.step = 0;
//    }
//
//    public void grow(int amount) {
//        this.growth += amount;
//        if (this.growth > grade.getMaxGrowth()) {
//            this.growth = grade.getMaxGrowth();
//        }
//        updateStepAndAchievement();
//    }
//
//    public void updateStepAndAchievement() {
//        int[] thresholds = grade.getStepThresholds();
//
//        if (growth >= thresholds[2]) {
//            step = 3;
//        } else if (growth >= thresholds[1]) {
//            step = 2;
//        } else if (growth >= thresholds[0]) {
//            step = 1;
//        } else {
//            step = 0;
//        }
//
//    }
//
//    public boolean isFullyGrown() {
//        return growth >= grade.getMaxGrowth() && step == 3;
//    }
//
//    public int getAchievementPoint() {
//        return grade.getAchievementPoint();
//    }
//
//    // Getters
//    public String getName() {
//        return name;
//    }
//
//    public Grade getGrade() {
//        return grade;
//    }
//
//    public int getGrowth() {
//        return growth;
//    }
//
//    public int getStep() {
//        return step;
//    }
//
//    public int getMaxGrowth() {
//        return grade.getMaxGrowth();
//    }
//}
