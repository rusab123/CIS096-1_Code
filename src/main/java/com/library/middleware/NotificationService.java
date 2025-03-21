package com.library.middleware;

import com.library.utils.NotificationObserver;
import com.library.utils.NotificationSubject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for handling notifications in the library system
 * Implements the Observer design pattern
 */
public class NotificationService implements NotificationSubject {
    private static NotificationService instance;
    private final List<NotificationObserver> observers;
    private final List<String> notificationHistory;
    
    // Private constructor to prevent direct instantiation (Singleton pattern)
    private NotificationService() {
        observers = new ArrayList<>();
        notificationHistory = new ArrayList<>();
    }
    
    // Static method to get the singleton instance
    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }
    
    @Override
    public void registerObserver(NotificationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers(String message) {
        // Add to history
        notificationHistory.add(message);
        
        // Notify all observers
        for (NotificationObserver observer : observers) {
            observer.update(message);
        }
    }
    
    /**
     * Sends a notification to all registered observers
     * 
     * @param message the notification message
     */
    public void sendNotification(String message) {
        notifyObservers(message);
    }
    
    /**
     * Gets the notification history
     * 
     * @return the list of notification messages
     */
    public List<String> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }
    
    /**
     * Clears the notification history
     */
    public void clearNotificationHistory() {
        notificationHistory.clear();
    }
} 