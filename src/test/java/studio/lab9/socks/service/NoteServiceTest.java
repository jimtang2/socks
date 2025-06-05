package studio.lab9.socks.service;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import studio.lab9.socks.SocksApplication;

@SpringBootTest(classes = SocksApplication.class)
class NoteServiceTest {
    @Autowired
    private NoteService noteService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void testNoteServiceNotNull() {
        assertNotNull(noteService, "NoteService should be autowired");
    }

    @Test
    void testNotesTableHasRows() {
        List<NoteService.Note> notes = noteService.getAllNotes();
        assertTrue(notes.size() > 0, "Table should have at least one row");

        for (NoteService.Note note : notes) {
            assertNotNull(note.title, "note should have a title");
        }
    }
}

