package com.goatsandtigers.crypcross;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Locale;

public class EnterNicknameDialog extends LinearLayout {

    public static final String KEY_DEFAULT_NICKNAME = "KEY_DEFAULT_NICKNAME";

    private EditText nicknameTextField;
    private CheckBox countryCodeCheckBox;

    public EnterNicknameDialog(Context context) {
        super(context);
        setOrientation(VERTICAL);
        addView(createNicknameTextField());
        addView(createCountryCodeCheckBox());
    }

    private EditText createNicknameTextField() {
        nicknameTextField = new EditText(getContext());
        final String defaultNickname = getDefaultNickname();
        nicknameTextField.setText(defaultNickname);
        nicknameTextField.setSelection(0, defaultNickname.length());
        return nicknameTextField;
    }

    private CheckBox createCountryCodeCheckBox() {
        countryCodeCheckBox = new CheckBox(getContext());
        countryCodeCheckBox.setText("Display country on scoreboard");
        countryCodeCheckBox.setChecked(true);
        return countryCodeCheckBox;
    }

    private String getDefaultNickname() {
        String randomNickname = "Player " + (int) ((Math.random() * 999999) + 1);
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getString(KEY_DEFAULT_NICKNAME, randomNickname);
    }

    public String getNickName() {
        return nicknameTextField.getText().toString();
    }

    public String getCountryCode() {
        return countryCodeCheckBox.isChecked() ? Locale.getDefault().getCountry() : "";
    }
}
