package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

public class TrashViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<PagedList<Note>> mTrash;

    public TrashViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        // Create PagedList and load 10 items at a time.
        mTrash = new LivePagedListBuilder<>(mNoteRepository.getTrashedNotes(), 10).build();
    }

    public LiveData<PagedList<Note>> getTrash() {
        return mTrash;
    }

    public void emptyTrash() {
        mNoteRepository.deleteAllTrashedNotes();
    }
}
