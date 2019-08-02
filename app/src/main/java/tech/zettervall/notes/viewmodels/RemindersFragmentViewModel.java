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
 * ViewModel for RemindersFragment.
 */
public class RemindersFragmentViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private LiveData<PagedList<Note>> mReminders;

    public RemindersFragmentViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        setNotes(null);
    }

    public LiveData<PagedList<Note>> getReminders() {
        return mReminders;
    }

    public void setNotes(@Nullable String query) {
        mReminders = new LivePagedListBuilder<>(mNoteRepository.getReminderNotesPagedList(query),
                Constants.NOTE_LIST_PAGE_SIZE).build();
    }
}
