package users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import communication.Message;
import patterns.Observer;

public abstract class User implements Observer, Serializable {
    private final int id;
    private String name;
    private String email;
    private String password;
    private final List<Message> inbox;
    private final List<String> notifications;
    private final Set<String> subscriptions;

    protected User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.inbox = new ArrayList<Message>();
        this.notifications = new ArrayList<String>();
        this.subscriptions = new LinkedHashSet<String>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }

    public void receiveMessage(Message message) {
        inbox.add(message);
    }

    public List<Message> getInbox() {
        return inbox;
    }

    public List<Message> getUnreadMessages() {
        List<Message> unreadMessages = new ArrayList<Message>();
        for (Message message : inbox) {
            if (!message.isRead()) {
                unreadMessages.add(message);
            }
        }
        return unreadMessages;
    }

    public void subscribeToTopic(String topic) {
        subscriptions.add(topic);
    }

    public void unsubscribeFromTopic(String topic) {
        subscriptions.remove(topic);
    }

    public Set<String> getSubscriptions() {
        return subscriptions;
    }

    public List<String> getNotifications() {
        return notifications;
    }

    public void clearNotifications() {
        notifications.clear();
    }

    @Override
    public void update(String event) {
        notifications.add(event);
    }

    public String getLoginIdentifier() {
        return email;
    }

    public abstract String getRole();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getRole() + "{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}
