package communication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import users.User;

public class Message implements Serializable {
    private final int id;
    private final User sender;
    private final User receiver;
    private final String content;
    private final LocalDateTime sentAt;
    private boolean read;

    public Message(int id, User sender, User receiver, String content) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sentAt = LocalDateTime.now();
        this.read = false;
    }

    public int getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{id=" + id + ", from=" + sender.getName() + ", to=" + receiver.getName() + ", sentAt="
                + sentAt + ", content='" + content + "'}";
    }
}
