package com.royal.qna.model;

public class QuizRequestDTO {
    private String title;
    private String description;
    private boolean active;

    // Constructors
    public QuizRequestDTO() {}

    public QuizRequestDTO(String title, String description, boolean active) {
        this.title = title;
        this.description = description;
        this.active = active;
    }

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
