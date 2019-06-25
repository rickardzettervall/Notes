package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel for a single Note.
 * Handles inserting and updating a note.
 */
public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
    }

    public long insertNote(Note note) {
        return mNoteRepository.insertNote(note);
    }

    public void updateNote(Note note) {
        mNoteRepository.updateNote(note);
    }

    public void deleteNote(Note note) {
        mNoteRepository.deleteNote(note);
    }
}
