package tech.zettervall.notes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tech.zettervall.mNotes.R;
import tech.zettervall.notes.adapters.LibraryAdapter;
import tech.zettervall.notes.utils.LibrariesUtil;
import tech.zettervall.notes.utils.RecyclerViewUtil;

public class AboutActivity extends AppCompatActivity implements LibraryAdapter.OnLibraryClickListener {

    private LibraryAdapter mLibraryAdapter;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Find Views
        mRecyclerView = findViewById(R.id.about_libraries_rv);

        // Set Adapter / LayoutManager / Decoration
        mLibraryAdapter = new LibraryAdapter(this,
                LibrariesUtil.getLibraries(this));
        mLayoutManager = RecyclerViewUtil.getDefaultLinearLayoutManager(this);
        mRecyclerView.setAdapter(mLibraryAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerViewUtil.setRecyclerViewDecoration(mLayoutManager, mRecyclerView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLibraryClick(int index) {
        Uri url = Uri.parse(mLibraryAdapter.getLibraries().get(index).getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
