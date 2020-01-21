package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import tech.zettervall.notes.Constants;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel for TrashFragment.
 */
public class TrashFragmentViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<PagedList<Note>> mTrash;

    public TrashFragmentViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        setNotes(null);
    }

    public LiveData<PagedList<Note>> getTrash() {
        return mTrash;
    }

    public void setNotes(@Nullable String query) {
        mTrash = new LivePagedListBuilder<>(mNoteRepository.getTrashedNotesPagedList(query),
                Constants.NOTE_LIST_PAGE_SIZE).build();
    }

    public long insertNote(Note note) {
        return mNoteRepository.insertNote(note);
    }

    public void updateNote(Note note) {
        mNoteRepository.updateNote(note);
    }

    public void emptyTrash() {
        mNoteRepository.deleteAllTrashedNotes();
    }

    public void deleteNote(Note note) {
        mNoteRepository.deleteNote(note);
    }
}
