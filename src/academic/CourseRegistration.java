package academic;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import enums.RegistrationStatus;
import users.Student;

public class CourseRegistration implements Serializable {
    private final int id;
    private final Student student;
    private final Course course;
    private final LocalDateTime requestedAt;
    private RegistrationStatus status;

    public CourseRegistration(int id, Student student, Course course) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.requestedAt = LocalDateTime.now();
        this.status = RegistrationStatus.PENDING;
    }

    public int getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void approve() {
        this.status = RegistrationStatus.APPROVED;
    }

    public void reject() {
        this.status = RegistrationStatus.REJECTED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseRegistration)) {
            return false;
        }
        CourseRegistration that = (CourseRegistration) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CourseRegistration{id=" + id + ", student=" + student.getName() + ", course=" + course.getCode()
                + ", status=" + status + ", requestedAt=" + requestedAt + "}";
    }
}
