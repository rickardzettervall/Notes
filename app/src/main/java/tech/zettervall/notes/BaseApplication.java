package tech.zettervall.notes;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import tech.zettervall.mNotes.R;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = sharedPreferences.getBoolean(getString(R.string.dark_theme_key),
                getResources().getBoolean(R.bool.defaultNightMode));
        setTheme(nightMode);
    }

    private void setTheme(boolean nightMode) {
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
