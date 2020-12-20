package com.goatsandtigers.crypcross;

import android.content.Context;
import android.preference.PreferenceManager;

public enum FontSize {

    MEDIUM(24, 18), LARGE(36, 28);

    public static final String KEY_SELECTED_FONT_SIZE = "KEY_SELECTED_FONT_SIZE";

    private FontSize(int clueHeaderFontSize, int clueBodyFontSize) {
        this.clueHeaderFontSize = clueHeaderFontSize;
        this.clueBodyFontSize = clueBodyFontSize;
    }

    private final int clueHeaderFontSize;
    private final int clueBodyFontSize;

    public static int getClueHeaderFontSize(Context context) {
        return getSelectedFontSize(context).clueHeaderFontSize;
    }

    public static int getClueBodyFontSize(Context context) {
        return getSelectedFontSize(context).clueBodyFontSize;
    }

    public static FontSize getSelectedFontSize(Context context) {
        String selectedFontSize = PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_SELECTED_FONT_SIZE, MEDIUM.name());
        return valueOf(selectedFontSize);
    }

    public static void setSelectedFontSize(Context context, FontSize fontSize) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_SELECTED_FONT_SIZE, fontSize.name()).commit();
    }

}
