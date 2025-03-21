package com.library.utils;

/**
 * Subject interface for the Observer design pattern
 * Used for handling notifications in the library system
 */
public interface NotificationSubject {
    /**
     * Registers an observer to receive notifications
     * 
     * @param observer the observer to register
     */
    void registerObserver(NotificationObserver observer);
    
    /**
     * Removes an observer from receiving notifications
     * 
     * @param observer the observer to remove
     */
    void removeObserver(NotificationObserver observer);
    
    /**
     * Notifies all registered observers with a message
     * 
     * @param message the notification message
     */
    void notifyObservers(String message);
} 