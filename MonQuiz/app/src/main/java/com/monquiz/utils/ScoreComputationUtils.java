package com.monquiz.utils;

public class ScoreComputationUtils {

    public static double getScore(long timeTaken) {
        return ((double) (30000 - timeTaken))/5000;
    }

    public static int getScoreSuperOver(long timeTaken) {
        if(timeTaken <= 2000) {
            return 6;
        }else if (timeTaken <= 4000){
            return 4;
        }else if (timeTaken <= 6000){
           return 2;
        }else {
            return 1;
        }
    }

    public static double getDailyquizScore(long timeTaken) {
        return ((double) (60000 - timeTaken))/5000;
    }
}
