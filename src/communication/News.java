package communication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import enums.NewsCategory;
import patterns.Observable;
import patterns.Observer;
import users.User;

public class News implements Observable, Serializable {
    private final int id;
    private final String title;
    private final String content;
    private final User author;
    private final LocalDateTime publishedAt;
    private final List<Comment> comments;
    private final Set<Observer> subscribers;
    private final boolean pinned;
    private final NewsCategory category;
    private final String topic;

    public News(int id, String title, String content, User author, boolean pinned, NewsCategory category, String topic) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishedAt = LocalDateTime.now();
        this.comments = new ArrayList<Comment>();
        this.subscribers = new LinkedHashSet<Observer>();
        this.pinned = pinned;
        this.category = category;
        this.topic = topic;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public User getAuthor() {
        return author;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public boolean isPinned() {
        return pinned;
    }

    public NewsCategory getCategory() {
        return category;
    }

    public String getTopic() {
        return topic;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void publish() {
        notifyObservers("News published: " + title + " [" + category + "]");
    }

    @Override
    public void addObserver(Observer observer) {
        subscribers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        subscribers.remove(observer);
    }

    @Override
    public void notifyObservers(String event) {
        for (Observer observer : subscribers) {
            observer.update(event);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof News)) {
            return false;
        }
        News news = (News) o;
        return id == news.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return (pinned ? "[PINNED] " : "") + "News{id=" + id + ", title='" + title + "', category=" + category
                + ", topic='" + topic + "', publishedAt=" + publishedAt + "}";
    }
}
