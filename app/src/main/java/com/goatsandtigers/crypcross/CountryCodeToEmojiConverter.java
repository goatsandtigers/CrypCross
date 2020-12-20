package com.goatsandtigers.crypcross;

public class CountryCodeToEmojiConverter {

    public static String getEmojiForCountryCode(String countryCode) {
        try {
            if (countryCode != null && countryCode.length() == 2) {
                int firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
                int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;
                return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
            } else if (countryCode != null) {
                return countryCode;
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

}
