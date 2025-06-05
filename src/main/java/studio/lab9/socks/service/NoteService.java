package studio.lab9.socks.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {

    static class Note {
        public Long id;
        public String title;
        public String content;
        public LocalDateTime updatedAt;
        public LocalDateTime createdAt;

        public Note(Long id, String title, String content, LocalDateTime updatedAt, LocalDateTime createdAt) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.updatedAt = updatedAt;
            this.createdAt = createdAt;
        }
    }

    private final JdbcTemplate jdbcTemplate;

    public NoteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Note> noteMapper = (rs, rowNum) -> new Note(
        rs.getLong("id"),
        rs.getString("title"),
        rs.getString("content"),
        rs.getTimestamp("updated_at").toLocalDateTime(),
        rs.getTimestamp("created_at").toLocalDateTime()
    );

    public List<Note> getAllNotes() {
        String sql = "SELECT id, title, content, updated_at, created_at FROM notes";
        return jdbcTemplate.query(sql, noteMapper);
    }

    public Note getNoteById(Long id) {
        String sql = "SELECT id, title, content, updated_at, created_at FROM notes WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, noteMapper, id);
    }
}