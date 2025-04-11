package com.library.models;

import java.util.UUID;

/**
 * Represents a book in the library system
 */
public class Book {
    private final String id;
    private String title;
    private String author;
    private String isbn;
    private String category;
    private int quantity;
    private boolean available;

    // Constructor for creating a new book
    public Book(String title, String author, String isbn, String category, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.quantity = quantity;
        this.available = quantity > 0;
    }

    // Constructor for loading from the database
    public Book(String id, String title, String author, String isbn,
                String category, int quantity, boolean available) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.quantity = quantity;
        this.available = available;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.available = quantity > 0;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean decreaseQuantity() {
        if (quantity > 0) {
            quantity--;
            available = quantity > 0;
            return true;
        }
        return false;
    }

    public void increaseQuantity() {
        quantity++;
        available = true;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                ", available=" + available +
                '}';
    }
}
