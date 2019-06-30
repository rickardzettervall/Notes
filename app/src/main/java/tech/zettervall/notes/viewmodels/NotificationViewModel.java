package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

/**
 * ViewModel used for when user clicks a notification, this
 * fetches the clicked Note based on ID received from the
 * notification.
 */
public class NotificationViewModel extends AndroidViewModel {

    private LiveData<Note> mNote;
    private NoteRepository mNoteRepository;

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
    }

    public void setNote(int noteID) {
        this.mNote = mNoteRepository.getNote(noteID);
    }

    public LiveData<Note> getNote() {
        return mNote;
    }
}
