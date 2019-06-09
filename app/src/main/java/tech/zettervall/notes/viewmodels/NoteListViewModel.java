package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import tech.zettervall.notes.Constants;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel for List (PagedList) of all Notes.
 */
public class NoteListViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<PagedList<Note>> mNotes;

    public NoteListViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        setNotes();
    }

    public LiveData<PagedList<Note>> getNotes() {
        return mNotes;
    }

    public void setNotes() {
        mNotes = new LivePagedListBuilder<>(mNoteRepository.getNotes(),
                Constants.NOTE_LIST_PAGE_SIZE).build();
    }

    public void setNotesSearch(String query) {
        mNotes = new LivePagedListBuilder<>(mNoteRepository.getNotes(query),
                Constants.NOTE_LIST_PAGE_SIZE).build();
    }

    public void setNotesSort(int sortBy, boolean descOrder, boolean favoritesOnTop) {

    }
}
