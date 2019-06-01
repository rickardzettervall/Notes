package tech.zettervall.notes;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.NoteAdapter;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.utils.RecyclerViewHelper;
import tech.zettervall.notes.viewmodels.TrashViewModel;

// TODO: add recyclerview with trashed items
// TODO: add observer from TrashViewModel
public class TrashActivity extends BaseActivity implements NoteAdapter.OnNoteClickListener {

    private static final String TAG = TrashActivity.class.getSimpleName();
    private TrashViewModel mTrashViewModel;
    private NoteAdapter mNoteAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private DrawerLayout mNavDrawerLayout;
    private NavigationView mNavView;
    private TextView mTrashIsEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        // Initialize ViewModel
        mTrashViewModel = ViewModelProviders.of(this).get(TrashViewModel.class);

        // Find Views
        mToolbar = findViewById(R.id.toolbar);
        mNavDrawerLayout = findViewById(R.id.drawer_layout);
        mNavView = findViewById(R.id.nav_view);
        mRecyclerView = findViewById(R.id.trash_rv);
        mTrashIsEmpty = findViewById(R.id.trash_is_empty_tv);

        // Set ToolBar
        setSupportActionBar(mToolbar);

        // Set Drawer
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, mNavDrawerLayout, mToolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        mNavDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Set Adapter / LayoutManager / Decoration
        mNoteAdapter = new NoteAdapter(this);
        mLayoutManager = RecyclerViewHelper.getDefaultLinearLayoutManager(this);
        mRecyclerView.setAdapter(mNoteAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewHelper.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);

        // Set Listeners
        mNavView.setNavigationItemSelectedListener(this);

        // Subscribe Observers
        subscribeObservers();
    }

    private void subscribeObservers() {
        mTrashViewModel.getTrash().observe(this, new Observer<PagedList<Note>>() {
            @Override
            public void onChanged(PagedList<Note> notes) {
                mNoteAdapter.submitList(notes);
                if (notes.isEmpty()) {
                    mTrashIsEmpty.setVisibility(View.VISIBLE);
                } else {
                    mTrashIsEmpty.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onNoteClick(int index) {
        // TODO: delete or restore?
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DialogInterface.OnClickListener dialogClickListener;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (item.getItemId()) {
            case R.id.action_restore_trash:
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // TODO: change all notes to trash = false
                                Toast.makeText(TrashActivity.this,
                                        "Restored all Notes",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                builder.setMessage(getString(R.string.confirm_restore_trash))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                break;
            case R.id.action_empty_trash:
                dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                mTrashViewModel.emptyTrash();
                                Toast.makeText(TrashActivity.this,
                                        "Trash emptied",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                builder.setMessage(getString(R.string.confirm_empty_trash))
                        .setPositiveButton(getString(R.string.confirm), dialogClickListener)
                        .setNegativeButton(getString(R.string.abort), dialogClickListener).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
