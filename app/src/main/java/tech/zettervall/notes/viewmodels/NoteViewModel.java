package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel for a single Note. Only handles inserting
 * and updating of a note with direct access to the
 * repository.
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
}
