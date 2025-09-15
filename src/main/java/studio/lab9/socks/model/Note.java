package studio.lab9.socks.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;

public class Note {
    private Long id;
    private String title;
    private String content;
    private JsonNode metadata; // Use JsonNode for JSONB
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    // Constructor
    public Note(Long id, String title, String content, JsonNode metadata, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.metadata = metadata;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public JsonNode getMetadata() { return metadata; }
    public void setMetadata(JsonNode metadata) { this.metadata = metadata; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}