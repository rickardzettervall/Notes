package tech.zettervall.notes.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.models.Tag;
import tech.zettervall.notes.repositories.NoteRepository;
import tech.zettervall.notes.repositories.TagRepository;

/**
 * ViewModel for a single Note.
 * Handles inserting and updating a note.
 */
public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mNoteRepository;
    private TagRepository mTagRepository;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        mNoteRepository = NoteRepository.getInstance(application);
        mTagRepository = TagRepository.getInstance(application);
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

    /**
     * Get Tags in List, used in AlertDialog for single Note.
     */
    public List<Tag> getTags() {
        return mTagRepository.getTagsRaw();
    }
}
