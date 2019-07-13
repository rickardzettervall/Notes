package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.repositories.TagRepository;

public class TagsViewModel extends AndroidViewModel {

    private LiveData<List<Tag>> mTags;
    private TagRepository mTagRepository;
    private NoteRepository mNoteRepository;
    private Application mApplication;

    public TagsViewModel(@NonNull Application application) {
        super(application);
        mTagRepository = TagRepository.getInstance(application);
        mNoteRepository = NoteRepository.getInstance(application);
        mTags = mTagRepository.getTags();
        mApplication = application;
    }

    public LiveData<List<Tag>> getTags() {
        return mTags;
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
