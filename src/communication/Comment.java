package communication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import users.User;

public class Comment implements Serializable {
    private final int id;
    private final User author;
    private String content;
    private final LocalDateTime createdAt;

    public Comment(int id, User author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void edit(String newContent) {
        this.content = newContent;
    }

    public void delete() {
        this.content = "[deleted]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comment)) {
            return false;
        }
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Comment{id=" + id + ", author=" + author.getName() + ", createdAt=" + createdAt + ", content='"
                + content + "'}";
    }
}
