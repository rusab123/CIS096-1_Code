package com.library.backend;

import com.library.models.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service to handle book-related operations
 * Implemented using the Singleton pattern
 */
public class BookService {
    private static BookService instance;
    private final Map<String, Book> books;
    
    // Private constructor to prevent direct instantiation
    private BookService() {
        books = new HashMap<>();
        initializeDefaultBooks();
    }
    
    // Static method to get the singleton instance
    public static synchronized BookService getInstance() {
        if (instance == null) {
            instance = new BookService();
        }
        return instance;
    }
    
    private void initializeDefaultBooks() {
        // Add some default books
        Book book1 = new Book("Introduction to Java Programming", "John Smith", "978-0-12-345678-9", "Programming", 5);
        Book book2 = new Book("Data Structures and Algorithms", "Jane Doe", "978-0-98-765432-1", "Computer Science", 3);
        Book book3 = new Book("The Great Gatsby", "F. Scott Fitzgerald", "978-3-16-148410-0", "Fiction", 2);
        Book book4 = new Book("Physics for Scientists and Engineers", "Raymond A. Serway", "978-1-23-456789-0", "Science", 4);
        Book book5 = new Book("Calculus: Early Transcendentals", "James Stewart", "978-9-87-654321-0", "Mathematics", 3);
        
        books.put(book1.getId(), book1);
        books.put(book2.getId(), book2);
        books.put(book3.getId(), book3);
        books.put(book4.getId(), book4);
        books.put(book5.getId(), book5);
    }
    
    public Book addBook(Book book) {
        books.put(book.getId(), book);
        return book;
    }
    
    public Optional<Book> getBookById(String id) {
        return Optional.ofNullable(books.get(id));
    }
    
    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }
    
    public List<Book> getBooksByCategory(String category) {
        return books.values().stream()
                .filter(book -> book.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }
    
    public List<Book> searchBooks(String query) {
        String searchQuery = query.toLowerCase();
        
        return books.values().stream()
                .filter(book -> 
                    book.getTitle().toLowerCase().contains(searchQuery) ||
                    book.getAuthor().toLowerCase().contains(searchQuery) ||
                    book.getCategory().toLowerCase().contains(searchQuery) ||
                    book.getIsbn().toLowerCase().contains(searchQuery))
                .collect(Collectors.toList());
    }
    
    public boolean updateBook(Book book) {
        if (books.containsKey(book.getId())) {
            books.put(book.getId(), book);
            return true;
        }
        return false;
    }
    
    public boolean deleteBook(String id) {
        return books.remove(id) != null;
    }
    
    public boolean borrowBook(String bookId) {
        Book book = books.get(bookId);
        if (book != null && book.isAvailable()) {
            return book.decreaseQuantity();
        }
        return false;
    }
    
    public boolean returnBook(String bookId) {
        Book book = books.get(bookId);
        if (book != null) {
            book.increaseQuantity();
            return true;
        }
        return false;
    }
} 