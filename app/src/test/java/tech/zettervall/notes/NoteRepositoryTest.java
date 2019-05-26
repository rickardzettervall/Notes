package tech.zettervall.notes;

import android.app.Instrumentation;
import android.content.Context;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import tech.zettervall.notes.data.NoteDb;
import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class NoteRepositoryTest {

    @Mock
    private Context context;

    @Mock
    private NoteDb noteDb;

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private Note note;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        noteDb = NoteDb.getInstance(context.getApplicationContext());
//        noteRepository = NoteRepository.getInstance(context.getApplicationContext());
//        note = new Note(Constants.TYPE_PLAIN, "title", "text");
    }

//    private long insertNote(Note note) {
//        return noteRepository.insertNote(note);
//    }

    @Test
    public void insertNote() {
        // Insert Note and get id from db
        long id = noteRepository.insertNote(note);

        // Check that Note was inserted correctly and returns the db id
        assertEquals(0L, id);
    }

    @Ignore
    @Test
    public void queryNote() {

    }

    @Ignore
    @Test
    public void deleteNote() {
        noteRepository.deleteNote(note);

        // todo: assert
    }

    @Ignore
    @Test
    public void deleteAllNotes() {
        noteRepository.deleteAllNotes();

        // todo: assert
    }

    @After
    public void tearDown() {
        noteDb = null;
        noteRepository = null;
    }
}
