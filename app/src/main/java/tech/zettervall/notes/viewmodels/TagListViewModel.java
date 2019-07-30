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

public class TagListViewModel extends AndroidViewModel {

    private LiveData<PagedList<Tag>> mTags;
    private TagRepository mTagRepository;
    private NoteRepository mNoteRepository;

    public TagListViewModel(@NonNull Application application) {
        super(application);
        mTagRepository = TagRepository.getInstance(application);
        mNoteRepository = NoteRepository.getInstance(application);
        mTags = new LivePagedListBuilder<>(mTagRepository.getTags(),
                Constants.TAG_LIST_PAGE_SIZE).build();
    }

    public LiveData<PagedList<Tag>> getTags() {
        return mTags;
    }

    public void insertTag(Tag tag) {
        mTagRepository.insertTag(tag);
    }

    /**
     * Delete Tag and clear the tag from all Notes.
     *
     * @param tag Tag to delete
     */
    public void deleteTag(Tag tag) {
        // Remove tag from Notes
        List<Note> notes = mNoteRepository.getAllNotesByTagRaw(tag);
        for (Note note : notes) {
            List<Tag> tags = note.getTags();
            tags.remove(tag);
            mNoteRepository.updateNote(note);
        }
        // Delete tag
        mTagRepository.deleteTag(tag);
    }
}
