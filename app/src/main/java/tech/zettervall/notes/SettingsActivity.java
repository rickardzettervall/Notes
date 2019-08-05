package tech.zettervall.notes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

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

            // About
            Preference about = findPreference(getString(R.string.about_simplenotes_key));
            about.setOnPreferenceClickListener(this);

//            SwitchPreference notificationsVibrate = findPreference(getString(R.string.notifications_vibrate_key));
//            notificationsVibrate.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == findPreference(getString(R.string.notifications_vibrate_key))) {
                ((SwitchPreference) preference).setSwitchTextOn(R.string.enabled);
                ((SwitchPreference) preference).setSwitchTextOff(R.string.disabled);
//                mSharedPreferences.edit().putBoolean(Constants.NOTIFICATIONS_ENABLE_VIBRATION_KEY)
                return true;
            } else if (preference == findPreference(getString(R.string.insert_dummy_data_key))) {
                // DEPRECATED
                // DEPRECATED
                // DEPRECATED
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}