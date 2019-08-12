package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import java.util.List;

import tech.zettervall.notes.Constants;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.repositories.TagRepository;

/**
 * ViewModel for MainActivity.
 * Used for updating Navigation Drawer note counters and also
 * for when user clicks a notification, this fetches the
 * clicked Note based on ID received from the notification.
 */
public class MainActivityViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private TagRepository mTagRepository;
    private LiveData<Note> mNotificationNote;
    private LiveData<PagedList<Note>> mNotes;
    private LiveData<PagedList<Note>> mFavorites;
    private LiveData<PagedList<Note>> mReminders;
    private LiveData<PagedList<Tag>> mTags;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        mTagRepository = TagRepository.getInstance(application);
        mNotes = new LivePagedListBuilder<>(mNoteRepository.getNotesPagedList(null),
                Constants.NOTE_LIST_PAGE_SIZE).build();
        mFavorites = new LivePagedListBuilder<>(mNoteRepository.getFavoritizedNotesPagedList(null),
                Constants.NOTE_LIST_PAGE_SIZE).build();
        mReminders = new LivePagedListBuilder<>(mNoteRepository.getReminderNotesPagedList(null),
                Constants.NOTE_LIST_PAGE_SIZE).build();
        mTags = new LivePagedListBuilder<>(mTagRepository.getTagsPagedList(),
                Constants.TAG_LIST_PAGE_SIZE).build();
    }

    public LiveData<Note> getNotificationNote() {
        return mNotificationNote;
    }

    public void setNotificationNote(int noteID) {
        this.mNotificationNote = mNoteRepository.getNoteLiveData(noteID);
    }

    public LiveData<PagedList<Note>> getNotes() {
        return mNotes;
    }

    public LiveData<PagedList<Note>> getFavorites() {
        return mFavorites;
    }

    public LiveData<PagedList<Note>> getReminders() {
        return mReminders;
    }

    public LiveData<PagedList<Tag>> getTags() {
        return mTags;
    }

    public List<Tag> getTagsList() {
        return mTagRepository.getTagsList();
    }

    public void updateNote(Note note) {
        mNoteRepository.updateNote(note);
    }
}
