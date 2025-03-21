package com.library.utils;

/**
 * Observer interface for the Observer design pattern
 * Used for handling notifications in the library system
 */
public interface NotificationObserver {
    /**
     * This method is called when the subject sends a notification
     * 
     * @param message the notification message
     */
    void update(String message);
} 