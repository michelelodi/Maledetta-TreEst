package it.unimi.maledettatreest.services;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

public class KeyboardManager {

    private Activity activity;

    public KeyboardManager(Activity activity){
        this.activity = activity;
    }

    public void hideKeyboard() {
        InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                                                                .getWindowToken(), 0);
    }
}
