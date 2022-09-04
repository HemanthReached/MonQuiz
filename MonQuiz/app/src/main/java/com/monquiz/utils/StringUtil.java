package com.monquiz.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class StringUtil {

    public static String join(List<String> parts, String delim) {

        StringBuilder result = new StringBuilder();

        for (int index = 0; index < parts.size(); index++) {
            String part = parts.get(index);
            result.append(part);
            if (delim != null && index < parts.size() - 1) {
                result.append(delim);
            }
        }
        return result.toString();
    }

    public static String getHash25(String key, String algorithmName) {

        MessageDigest digest;
        String hash = "";
        try {
            digest = MessageDigest.getInstance(algorithmName);
            digest.update(key.getBytes());
            hash = bytesToHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return hash;

    }

    private static String bytesToHexString(byte[] bytes) {

        StringBuilder stringBuilder = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        return titleCase.toString();
    }

    public static String getConvertedText(String data) {
        if (data.length() >= 5) {
            double s3 = (double) Math.round((Double.parseDouble(data) / 100)) / 1000d;
            data = s3 + " Lakh";
        }
        if (data.length() >= 4 && data.length() <= 5) {
            double s4 = (double) Math.round(Double.parseDouble(data)) / 1000d;
            data = s4 + " K";
        }
        return data;
    }

    public static double getDecimalValue(double data) {
        double leaderbordscore = Math.round(data * 10.0) / 10.0;
        System.out.println("leaderbordscore (Math.round) : " + leaderbordscore);
        return leaderbordscore;
    }
}
