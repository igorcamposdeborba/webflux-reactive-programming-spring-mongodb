package com.igorborba.webflux.entities;

import java.time.Instant;

public class Comment {

    private String text;
    private Instant date;
    private Author author;

    public Comment() {
    }

    public Comment(String text, Instant date, Author author) {
        this.text = text;
        this.date = date;
        this.author = author;
    }

    public Comment(String text, Instant date, String authorId, String authorName) {
        this.text = text;
        this.date = date;
        this.author = new Author(authorId, authorName);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
