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
 * ViewModel for List (PagedList) of all favoritized Notes.
 */
public class RemindersViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<PagedList<Note>> mReminders;

    public RemindersViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        setNotes(null);
    }

    public LiveData<PagedList<Note>> getReminders() {
        return mReminders;
    }

    public void setNotes(@Nullable String query) {
        mReminders = new LivePagedListBuilder<>(mNoteRepository.getAllReminderNotes(query),
                Constants.NOTE_LIST_PAGE_SIZE).build();
    }
}
