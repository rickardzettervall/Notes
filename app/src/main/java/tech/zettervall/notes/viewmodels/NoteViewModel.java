package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel for a single Note.
 */
public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<Note> mNote;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
    }

    public LiveData<Note> getNote() {
        return mNote;
    }

    public void setNote(int noteID) {
        this.mNote = mNoteRepository.getNote(noteID);
    }

    public long insertNote(Note note) {
        return mNoteRepository.insertNote(note);
    }
}
