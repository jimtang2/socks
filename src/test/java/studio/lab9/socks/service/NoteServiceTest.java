package studio.lab9.socks.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import studio.lab9.socks.SocksApplication;
import studio.lab9.socks.config.TestConfig; // Add this import
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SocksApplication.class)
@ActiveProfiles("test")
@TestExecutionListeners(listeners = {TestConfig.DotenvTestExecutionListener.class})
class NoteServiceTest {
    @Autowired
    private NoteService noteService;

    @Test
    void testNotesTableHasRows() {
        List<NoteService.Note> notes = noteService.getAllNotes();
        assertTrue(notes.size() > 0);
        assertTrue(notes.size() == 3);
    }
}