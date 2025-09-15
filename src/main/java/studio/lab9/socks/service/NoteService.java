package studio.lab9.socks.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import studio.lab9.socks.model.Note;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteService {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NoteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Note> noteMapper = (rs, rowNum) -> {
        JsonNode metadata = null;
        if (rs.getString("metadata") != null) {
            try {
                metadata = objectMapper.readTree(rs.getString("metadata"));
            } catch (JsonProcessingException e) {
                // Log error or handle as needed; returning null for now
                metadata = null;
            }
        }
        return new Note(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            metadata,
            rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null,
            rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null
        );
    };

    public List<Note> getAllNotes() {
        String sql = "SELECT id, title, content, metadata, updated_at, created_at FROM notes";
        return jdbcTemplate.query(sql, noteMapper);
    }

    public Note getNoteById(Long id) {
        String sql = "SELECT id, title, content, metadata, updated_at, created_at FROM notes WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, noteMapper, id);
    }
}