package tech.zettervall.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import tech.zettervall.mNotes.R;

public class NoteActivity extends BaseActivity {

    private static final String TAG = NoteActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;
    private Integer mNoteID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Find Views
        mToolbar = findViewById(R.id.toolbar);

        // Set ToolBar
        setSupportActionBar(mToolbar);

        // Find Views
        mToolbar = findViewById(R.id.toolbar);
        mNavDrawerLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);

        // Set ToolBar
        setSupportActionBar(mToolbar);

        // Set Drawer
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mNavDrawerLayout, mToolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mNavDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Set Listeners
        mNavView.setNavigationItemSelectedListener(this);

        // Retrieve existing data
        if (getIntent().getExtras() != null) {
            mNoteID = getIntent().getExtras().getInt(Constants.NOTE_ID);
        }

        // Fragment handling
        if(mNoteID != null) {
            setNoteFragment(getNoteFragmentWithBundle(mNoteID));
        } else {
            setNoteFragment(new NoteFragment());
        }
    }

    @Override
    public NoteFragment getNoteFragmentWithBundle(int noteID) {
        return super.getNoteFragmentWithBundle(noteID);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Note ID
        if(mNoteID != null) {
            outState.putInt(Constants.NOTE_ID, mNoteID);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                DialogInterface.OnClickListener dialogClickListener =
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Constants.deleteNote = true;
//                                Note note = mNoteViewModel.getNote().getValue();
//                                if(note != null) {
//                                    note.setTrash(true);
//                                    Toast.makeText(NoteActivity.this,
//                                            "Moved '" + note.getTitle() + "' to trash",
//                                            Toast.LENGTH_SHORT).show();
//                                }
//                                // TODO: enable deletion here
//                                NoteRepository.getInstance(getApplication()).deleteNote(mNote);
//                                if(mNote.getTitle() != null && !mNote.getTitle().isEmpty()) {
//                                    Toast.makeText(NoteActivity.this,
//                                            "Deleted '" + mNote.getTitle() + "'",
//                                            Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(NoteActivity.this,
//                                            "Deleted Note",
//                                            Toast.LENGTH_SHORT).show();
//                                }
                                finish();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.confirm_deletion))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}