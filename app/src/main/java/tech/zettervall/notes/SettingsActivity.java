package tech.zettervall.notes;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import tech.zettervall.mNotes.BuildConfig;
import tech.zettervall.mNotes.R;
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.utils.DbUtil;
import tech.zettervall.notes.utils.DummyDataUtil;

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
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Settings Fragment
     */
    public static class SettingsFragment extends PreferenceFragmentCompat implements
            Preference.OnPreferenceClickListener {

        private static final int BACKUP_DB_REQUEST_CODE = 0;
        private static final int RESTORE_DB_REQUEST_CODE = 1;
        private SharedPreferences mSharedPreferences;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            // Dark Theme
            Preference darkTheme = findPreference(getString(R.string.dark_theme_key));
            darkTheme.setOnPreferenceClickListener(this);

            // Backup Database
            Preference backupDb = findPreference(getString(R.string.backup_key));
            backupDb.setSummary(getString(R.string.backup_summary, DbUtil.getDbTrailPath(getActivity())));
            backupDb.setOnPreferenceClickListener(this);

            // Restore Database
            Preference restoreDb = findPreference(getString(R.string.restore_key));
            restoreDb.setOnPreferenceClickListener(this);

            // About (Other Apps)
            Preference aboutOtherApps = findPreference(getString(R.string.about_other_apps_key));
            aboutOtherApps.setOnPreferenceClickListener(this);

            // About (App)
            Preference aboutApp = findPreference(getString(R.string.about_app_key));
            aboutApp.setOnPreferenceClickListener(this);
            aboutApp.setSummary(getString(R.string.app_version, BuildConfig.VERSION_NAME));

            if (BuildConfig.FLAVOR.equals("dev")) {
                // Dev (Dummy Data)
                Preference dummyData = findPreference(getString(R.string.dummy_data_key));
                dummyData.setOnPreferenceClickListener(this);

                // Dev (Clear DB)
                Preference clearDb = findPreference(getString(R.string.clear_db_key));
                clearDb.setOnPreferenceClickListener(this);
            }
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == findPreference(getString(R.string.dark_theme_key))) { // Dark Theme
                boolean nightMode = mSharedPreferences.getBoolean(getString(R.string.dark_theme_key),
                        getResources().getBoolean(R.bool.defaultNightMode));
                if (nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            } else if (preference == findPreference(getString(R.string.backup_key))) { // Backup Db
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, BACKUP_DB_REQUEST_CODE);
            } else if (preference == findPreference(getString(R.string.restore_key))) { // Restore Db
                DialogInterface.OnClickListener dialogClickListenerDelete =
                        (DialogInterface dialog, int which) -> {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                    requestPermissions(permissions, RESTORE_DB_REQUEST_CODE);
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.confirm_restore_db))
                        .setPositiveButton(getString(R.string.accept), dialogClickListenerDelete)
                        .setNegativeButton(getString(R.string.cancel), dialogClickListenerDelete)
                        .setMessage(getString(R.string.confirm_restore_db_message))
                        .show();
            } else if (preference == findPreference(getString(R.string.about_other_apps_key))) { // View other Apps
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_PLAY_STORE));
                intent.setPackage("com.android.vending");
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(intent);
                }
            } else if (preference == findPreference(getString(R.string.about_app_key))) { // About App
                // Inflate View
                View dialogView = View.inflate(getActivity(), R.layout.dialog_about, null);

                // Set HTML link for 'libraries'
                TextView libraries = dialogView.findViewById(R.id.dialog_about_libraries_textview);
                libraries.setMovementMethod(LinkMovementMethod.getInstance());
                libraries.setText(Html.fromHtml(getString(R.string.about_libraries)));

                // Set HTML link for 'app icon'
                TextView appIcon = dialogView.findViewById(R.id.dialog_about_app_icon_textview);
                appIcon.setMovementMethod(LinkMovementMethod.getInstance());
                appIcon.setText(Html.fromHtml(getString(R.string.about_app_icon)));

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.app_name));
                builder.setView(dialogView);
                builder.setPositiveButton(R.string.ok, null);
                builder.show();
            } else if (preference == findPreference(getString(R.string.dummy_data_key))) { // DEV TOOLS
                DummyDataUtil.insertDummyData(NoteRepository.getInstance(getActivity().getApplication()));
            } else if (preference == findPreference(getString(R.string.clear_db_key))) { // DEV TOOLS
                NoteRepository.getInstance(getActivity().getApplication()).deleteAllNotes();
            }
            return false;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case BACKUP_DB_REQUEST_CODE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        DbUtil.backupDb(getContext(), DbUtil.getDefaultBackupDirPath(getActivity()));
                        Toast.makeText(getActivity(), getString(R.string.backup_saved_to, DbUtil.getDbTrailPath(getActivity())), Toast.LENGTH_LONG).show();
                    }
                    break;
                case RESTORE_DB_REQUEST_CODE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        boolean result = DbUtil.restoreDb(getContext(), DbUtil.getDefaultBackupDirPath(getActivity()));
                        if (result) {
                            Toast.makeText(getActivity(), getString(R.string.backup_restored), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.backup_restored_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    }
}