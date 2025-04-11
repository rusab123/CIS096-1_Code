package com.library.middleware;

import com.library.utils.NotificationObserver;
import com.library.utils.NotificationSubject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for handling notifications in the library system.
 * Implements the Observer design pattern and uses Singleton.
 */
public class NotificationService implements NotificationSubject {
    private static NotificationService instance;
    private final List<NotificationObserver> observers;
    private final List<String> notificationHistory;

    // Private constructor for singleton
    private NotificationService() {
        observers = new ArrayList<>();
        notificationHistory = new ArrayList<>();
    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    @Override
    public void registerObserver(NotificationObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(NotificationObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        if (message != null && !message.isBlank()) {
            notificationHistory.add(message);
            for (NotificationObserver observer : observers) {
                observer.update(message);
            }
        }
    }

    /**
     * Sends a notification to all registered observers.
     *
     * @param message the notification message
     */
    public void sendNotification(String message) {
        notifyObservers(message);
    }

    /**
     * Returns an unmodifiable copy of the notification history.
     */
    public List<String> getNotificationHistory() {
        return Collections.unmodifiableList(notificationHistory);
    }

    /**
     * Clears all stored notifications.
     */
    public void clearNotificationHistory() {
        notificationHistory.clear();
    }
}
