package tech.zettervall.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import tech.zettervall.mNotes.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set Home menu item
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create Settings Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_settings_framelayout, new SettingsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            /* Don't use finish() here because MainActivity needs to be reloaded
             * when user changes theme. */
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        /* Don't use finish() here because MainActivity needs to be reloaded
         * when user changes theme. */
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Settings Fragment
     */
    public static class SettingsFragment extends PreferenceFragmentCompat implements
            Preference.OnPreferenceClickListener {

        private SharedPreferences mSharedPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            // Dark Theme
            Preference darkTheme = findPreference(getString(R.string.dark_theme_key));
            darkTheme.setOnPreferenceClickListener(this);

            // About (Other Apps)
            Preference aboutOtherApps = findPreference(getString(R.string.about_apps_key));
            aboutOtherApps.setOnPreferenceClickListener(this);

            // About (App)
            Preference aboutApp = findPreference(getString(R.string.about_simplenotes_key));
            aboutApp.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == findPreference(getString(R.string.dark_theme_key))) {
                boolean setDarkTheme = mSharedPreferences.getBoolean(getString(R.string.dark_theme_key), false);
                if (setDarkTheme) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                getActivity().recreate();
            } else if (preference == findPreference(getString(R.string.about_apps_key))) {
                Uri devPages = Uri.parse(Constants.GOOGLE_PLAY_STORE_PAGE);
                Intent intent = new Intent(Intent.ACTION_VIEW, devPages);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            } else if (preference == findPreference(getString(R.string.about_simplenotes_key))) {
                // Inflate View
                View dialogView = View.inflate(getActivity(), R.layout.dialog_about, null);

                // Set HTML link for 'built by'
                ((TextView) dialogView.findViewById(R.id.dialog_about_built_by_textview))
                        .setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) dialogView.findViewById(R.id.dialog_about_built_by_textview))
                        .setText(Html.fromHtml(getString(R.string.about_built_by)));

                // Set HTML link for 'libraries'
                ((TextView) dialogView.findViewById(R.id.dialog_about_libraries_textview))
                        .setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) dialogView.findViewById(R.id.dialog_about_libraries_textview))
                        .setText(Html.fromHtml(getString(R.string.about_libraries)));

                // Set HTML link for 'app icon'
                ((TextView) dialogView.findViewById(R.id.dialog_about_app_icon_textview))
                        .setMovementMethod(LinkMovementMethod.getInstance());
                ((TextView) dialogView.findViewById(R.id.dialog_about_app_icon_textview))
                        .setText(Html.fromHtml(getString(R.string.about_app_icon)));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.app_name));
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.confirm_done, null);
                builder.show();
            }
            return false;
        }
    }
}