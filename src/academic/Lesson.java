package academic;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import enums.LessonType;
import users.Teacher;

public class Lesson implements Serializable {
    private final int id;
    private final String topic;
    private final LessonType lessonType;
    private final Teacher teacher;
    private final LocalDateTime schedule;
    private final String room;

    public Lesson(int id, String topic, LessonType lessonType, Teacher teacher, LocalDateTime schedule, String room) {
        this.id = id;
        this.topic = topic;
        this.lessonType = lessonType;
        this.teacher = teacher;
        this.schedule = schedule;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public String getTopic() {
        return topic;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public LocalDateTime getSchedule() {
        return schedule;
    }

    public String getRoom() {
        return room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lesson)) {
            return false;
        }
        Lesson lesson = (Lesson) o;
        return id == lesson.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Lesson{id=" + id + ", topic='" + topic + "', type=" + lessonType + ", teacher=" + teacher.getName()
                + ", schedule=" + schedule + ", room='" + room + "'}";
    }
}
