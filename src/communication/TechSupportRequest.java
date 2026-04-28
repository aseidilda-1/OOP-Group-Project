package communication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import enums.TechSupportRequestStatus;
import users.TechSupportSpecialist;
import users.User;

public class TechSupportRequest implements Serializable {
    private final int id;
    private final User requester;
    private final String description;
    private TechSupportRequestStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private TechSupportSpecialist assignee;

    public TechSupportRequest(int id, User requester, String description) {
        this.id = id;
        this.requester = requester;
        this.description = description;
        this.status = TechSupportRequestStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public User getRequester() {
        return requester;
    }

    public String getDescription() {
        return description;
    }

    public TechSupportRequestStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public TechSupportSpecialist getAssignee() {
        return assignee;
    }

    public void markViewed() {
        status = TechSupportRequestStatus.VIEWED;
    }

    public void accept(TechSupportSpecialist specialist) {
        this.assignee = specialist;
        this.status = TechSupportRequestStatus.ACCEPTED;
    }

    public void reject(TechSupportSpecialist specialist) {
        this.assignee = specialist;
        this.status = TechSupportRequestStatus.REJECTED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void markDone(TechSupportSpecialist specialist) {
        this.assignee = specialist;
        this.status = TechSupportRequestStatus.DONE;
        this.resolvedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TechSupportRequest)) {
            return false;
        }
        TechSupportRequest that = (TechSupportRequest) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        String assigneeName = assignee == null ? "unassigned" : assignee.getName();
        return "TechSupportRequest{id=" + id + ", requester=" + requester.getName() + ", status=" + status
                + ", assignee=" + assigneeName + ", createdAt=" + createdAt + ", description='" + description + "'}";
    }
}
