package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel for List of all Notes.
 */
public class NotesViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<List<Note>> mNotes;

    public NotesViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        mNotes = mNoteRepository.getNotes();
    }

    public LiveData<List<Note>> getNotes() {
        return mNotes;
    }

    public void deleteNote(Note note) {
        mNoteRepository.deleteNote(note);
    }
}
