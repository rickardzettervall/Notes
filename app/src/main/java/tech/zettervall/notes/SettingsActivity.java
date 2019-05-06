package tech.zettervall.notes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.repositories.NoteRepository;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Create Settings Fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
    }

    /**
     * Settings Fragment
     */
    public static class SettingsFragment extends PreferenceFragmentCompat implements
            Preference.OnPreferenceClickListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            Preference deleteAllNotesPreference = findPreference(getString(R.string.delete_all_notes_key));
            Preference insertDummyData = findPreference(getString(R.string.insert_dummy_data_key));
            deleteAllNotesPreference.setOnPreferenceClickListener(this);
            insertDummyData.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == findPreference(getString(R.string.delete_all_notes_key))) {
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        NoteRepository.getInstance(getActivity().getApplication())
                                                .deleteAllNotes();
                                        Toast.makeText(getActivity(), "All Notes were deleted", Toast.LENGTH_SHORT).show();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getString(R.string.confirm_deletion))
                        .setPositiveButton(getString(R.string.delete), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                return true;
            } else if (preference == findPreference(getString(R.string.insert_dummy_data_key))) {
                NoteRepository.getInstance(getActivity().getApplication()).insertDummyData();
                Toast.makeText(getActivity(), "Dummy data inserted", Toast.LENGTH_SHORT).show();
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