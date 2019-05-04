package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel for List (PagedList) of all Notes.
 */
public class NotesViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<PagedList<Note>> mNotes;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);

        // Create PagedList and load 10 items at a time.
        mNotes = new LivePagedListBuilder<>(mNoteRepository.getNotes(), 1).build();
    }

    public LiveData<PagedList<Note>> getNotes() {
        return mNotes;
    }

    public void deleteNote(Note note) {
        mNoteRepository.deleteNote(note);
    }
}
