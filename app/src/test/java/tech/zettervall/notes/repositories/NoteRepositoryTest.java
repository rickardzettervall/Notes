package tech.zettervall.notes.repositories;

import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.room.Room;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import tech.zettervall.notes.Constants;
import tech.zettervall.notes.TestHelper;
import tech.zettervall.notes.data.AppDb;
import tech.zettervall.notes.models.Note;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricTestRunner.class)
public class NoteRepositoryTest {

    @Rule
//    public InstantTaskExecutorRule mInstantTaskExecutorRule = new InstantTaskExecutorRule();
    private AppDb mAppDb;
    private NoteRepository mNoteRepository;

    /**
     * Create NoteRepository for testing.
     */
    @Before
    public void init() {
        mAppDb = Room.inMemoryDatabaseBuilder(TestHelper.getContext(), AppDb.class).build();
        mNoteRepository = new NoteRepository(mAppDb, TestHelper.getContext());
    }

    /**
     * Close DB after testing.
     */
    @After
    public void release() {
        mAppDb.close();
    }

    /**
     * Insert Note and get Note ID.
     */
    @Test
    public void insertNote() {
        long noteID = mNoteRepository.insertNote(TestHelper.getMockNotes()[0]);
        assertEquals(1, noteID);
    }

    /**
     * Insert Array of Notes and get an Array with Note IDs.
     */
    @Test
    public void insertNotes() {
        long[] noteIDs = mNoteRepository.insertNotes(TestHelper.getMockNotes());
        long[] expectedIDs = {1, 2, 3};
        assertArrayEquals(expectedIDs, noteIDs);
    }

    /**
     * Delete Note and try to get it, should return null.
     */
    @Test
    public void deleteNote() {
        // Insert Notes
        mNoteRepository.insertNotes(TestHelper.getMockNotes());

        // Assert that Note is in db
        Note note = mNoteRepository.getNote(1);
        assertNotNull(note);

        // Delete Note
        mNoteRepository.deleteNote(note);

        // Assert that Note was deleted from db
        note = mNoteRepository.getNote(1);
        assertNull(note);
    }

    /**
     * Delete all Notes which are trashed.
     */
    @Test
    public void deleteAllTrashedNotes() {
        // Insert Notes
        mNoteRepository.insertNote(TestHelper.getTrashedNote());
        mNoteRepository.insertNotes(TestHelper.getMockNotes());

        // Assert that trashed Note is in db
        Note trashedNote = mNoteRepository.getNote(1);
        assertNotNull(trashedNote);

        // Delete trashed Notes
        mNoteRepository.deleteAllTrashedNotes();

        // Assert that trashed Note was deleted from db and all other Notes remain
        Note[] otherNotes = {
                mNoteRepository.getNote(2),
                mNoteRepository.getNote(3),
                mNoteRepository.getNote(4)
        };
        trashedNote = mNoteRepository.getNote(1);
        assertNull(trashedNote);
        for (Note note : otherNotes) {
            assertNotNull(note);
        }
    }

    /**
     * Delete all Notes.
     */
    @Test
    public void deleteAllNotes() {
        // Insert Notes
        mNoteRepository.insertNotes(TestHelper.getMockNotes());

        // Assert that Notes are in db
        Note[] notes = {
                mNoteRepository.getNote(1),
                mNoteRepository.getNote(2),
                mNoteRepository.getNote(3)
        };
        for (Note note : notes) {
            assertNotNull(note);
        }

        // Delete all Notes
        mNoteRepository.deleteAllNotes();

        // Assert that Notes were deleted from db
        Note[] notesDeleted = {
                mNoteRepository.getNote(1),
                mNoteRepository.getNote(2),
                mNoteRepository.getNote(3)
        };
        for (Note note : notesDeleted) {
            assertNull(note);
        }
    }

    /**
     * Update Note.
     */
    @Test
    public void updateNote() {
        // Insert Note
        Note note = TestHelper.getMockNotes()[0];
        long id = mNoteRepository.insertNote(note);
        note.setId((int) id);

        // Assert that Note is in db
        assertEquals(note, mNoteRepository.getNote(note.getId()));

        // Update Note
        note.setTitle("X");
        mNoteRepository.updateNote(note);

        // Assert that Note was updated
        assertEquals("X", mNoteRepository.getNote(note.getId()).getTitle());
    }

    /**
     * Update Notes.
     */
    @Test
    public void updateNotes() {
        // Insert Notes
        Note[] notes = TestHelper.getMockNotes();
        long[] noteIDs = mNoteRepository.insertNotes(notes);
        for (int i = 0; i < notes.length; i++) {
            notes[i].setId((int) noteIDs[i]);
        }

        // Assert that Notes are in db
        for (int i = 0; i < notes.length; i++) {
            assertEquals(notes[i], mNoteRepository.getNote(notes[i].getId()));
        }

        // Update Notes
        for (Note note : notes) {
            note.setTitle("X");
        }
        mNoteRepository.updateNotes(notes);

        // Assert that Notes were updated
        for (Note note : notes) {
            assertEquals(note, mNoteRepository.getNote(note.getId()));
        }
    }

    /**
     * Get Note.
     */
    @Test
    public void getNote() {
        // Insert Note
        Note note = TestHelper.getMockNotes()[0];
        long noteID = mNoteRepository.insertNote(note);
        note.setId((int) noteID);

        // Get Note
        Note retrievedNote = mNoteRepository.getNote((int) noteID);

        // Assert that Notes are same
        assertEquals(note, retrievedNote);
    }

    /**
     * Get Note as LiveData.
     */
    @Test
    public void getNoteLiveData() {
        // Insert Note
        final Note mockNote = TestHelper.getMockNotes()[0];
        long noteID = mNoteRepository.insertNote(mockNote);
        mockNote.setId((int) noteID);

        // Assert that Note is in db
        assertEquals(mockNote, mNoteRepository.getNote(mockNote.getId()));

        // Initialize TestObserver / TestLifeCycle
//        TestObserver<Note> testObserver = TestObserver.create();
//        TestLifecycle testLifecycle = TestLifecycle.initialized();
//
//        LiveData<Note> x = mNoteRepository.getNoteLiveData(mockNote.getId());
//
//        x.observe(testLifecycle, testObserver);
//
//        testObserver
//                .assertHasValue()
//                .assertValue(mockNote);


//        try {
//            TestObserver.test(mNoteRepository.getNoteLiveData(note.getId()))
//                    .awaitValue(1L, TimeUnit.SECONDS)
//                    .assertHasValue()
//                    .assertHistorySize(1)
//                    .assertValue(note);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // Get Note
        final LiveData<Note> noteLiveData = mNoteRepository.getNoteLiveData(mockNote.getId());


//        assertEquals(mockNote, noteLiveData.getValue());
    }

    @Test
    public void getNotesPagedList() {
        // Insert Notes
        Note[] notes = TestHelper.getMockNotes();
        long[] noteIDs = mNoteRepository.insertNotes(notes);
        for (int i = 0; i < notes.length; i++) {
            notes[i].setId((int) noteIDs[i]);
        }

        // Assert that Notes are in db
        for (int i = 0; i < notes.length; i++) {
            assertEquals(notes[i], mNoteRepository.getNote(notes[i].getId()));
        }

        // Create LiveData PagedList
        LiveData<PagedList<Note>> notesPagedList =
                new LivePagedListBuilder<>(mNoteRepository.getNotesPagedList(null),
                        Constants.NOTE_LIST_PAGE_SIZE).build();

        Object obj = mNoteRepository.getNotesPagedList(null);


//        mNoteRepository.getNoteLiveData(note.getId()).observe(testLifecycle, testObserver);
//
//        testObserver
//                .assertHasValue()
//                .assertValue(note);
//
//        mNoteRepository.getNotesPagedList(null);
    }

    /**
     * Get NotesList.
     */
    @Test
    public void getNotesList() {
        // Insert Notes
        Note[] notes = TestHelper.getMockNotes();
        long[] noteIDs = mNoteRepository.insertNotes(TestHelper.getMockNotes());
        for (int i = 0; i < notes.length; i++) {
            notes[i].setId((int) noteIDs[i]);
        }

        // Get Notes with TagID: 1 (should return note1, note2)
        List<Note> notesList = mNoteRepository.getNotesList(1);

        // Assert that correct Notes were fetched
        assertEquals(2, notesList.size());
        for (int i = 0; i < notesList.size(); i++) {
            assertEquals(notes[i], notesList.get(i));
        }
    }
}
