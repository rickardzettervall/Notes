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
 * ViewModel for TagsFragment.
 */
public class TagsFragmentViewModel extends AndroidViewModel {

    private LiveData<PagedList<Tag>> mTags;
    private TagRepository mTagRepository;
    private NoteRepository mNoteRepository;

    public TagsFragmentViewModel(@NonNull Application application) {
        super(application);
        mTagRepository = TagRepository.getInstance(application);
        mNoteRepository = NoteRepository.getInstance(application);
        mTags = new LivePagedListBuilder<>(mTagRepository.getTagsPagedList(),
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
        List<Note> notes = mNoteRepository.getNotesList(tag.getId());
        for (Note note : notes) {
            List<Integer> tagIDs = note.getTagIDs();
            tagIDs.remove(tag.getId());
            note.setTagIDs(tagIDs);
        }
        mNoteRepository.updateNotes(notes.toArray(new Note[0]));
        mTagRepository.deleteTag(tag);
    }
}
