package com.example.truyenchuvietsub;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugGenerator {
    public static String toSlug(String str) {
        if (str == null) {
            return null;
        }
        String lowerCase = str.toLowerCase().trim();
        String nfdNormalizedString = Normalizer.normalize(lowerCase, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String resultWithoutDiacritics = pattern.matcher(nfdNormalizedString).replaceAll("");
        String resultWithoutD = resultWithoutDiacritics.replaceAll("đ", "d");
        String resultWithoutSpecialCharacters = resultWithoutD.replaceAll("[^a-zA-Z0-9\\s]", "");
        String resultWithHyphens = resultWithoutSpecialCharacters.replaceAll("\\s+", "-");

        long random = Math.round(Math.random() * 100000);
        String finalResult = resultWithHyphens + "-" + random;
        return finalResult;
    }
}
