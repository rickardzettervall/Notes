package tech.zettervall.notes;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import tech.zettervall.notes.data.NoteDao;
import tech.zettervall.notes.data.NoteDb;

import static org.mockito.Mockito.*;

public class DbMockitoTest {

    @Mock
    private NoteDb noteDbMock;
    @Mock
    private NoteDao noteDaoMock;

    // Tells Mockito to create the mocks based on the @Mock annotation
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testQuery() {

    }

    @After
    public void tearDown() {
        // Close the db
        noteDbMock.close();
    }
}
