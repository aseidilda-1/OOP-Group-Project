package academic;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

import users.Student;

public class Transcript implements Serializable {
    private final Student student;
    private final Map<Course, Mark> marks;

    public Transcript(Student student) {
        this.student = student;
        this.marks = new LinkedHashMap<Course, Mark>();
    }

    public Student getStudent() {
        return student;
    }

    public Map<Course, Mark> getMarks() {
        return marks;
    }

    public void addOrUpdateMark(Course course, Mark mark) {
        marks.put(course, mark);
    }

    public Mark getMarkForCourse(Course course) {
        return marks.get(course);
    }

    public Collection<Mark> getAllMarks() {
        return marks.values();
    }

    public double calculateGpa() {
        double weightedTotal = 0.0;
        int totalCredits = 0;

        for (Map.Entry<Course, Mark> entry : marks.entrySet()) {
            int credits = entry.getKey().getCredits();
            weightedTotal += entry.getValue().getGradePoints() * credits;
            totalCredits += credits;
        }

        if (totalCredits == 0) {
            return 0.0;
        }
        return weightedTotal / totalCredits;
    }

    public int getFailedCoursesCount() {
        int failures = 0;
        for (Mark mark : marks.values()) {
            if (!mark.isPassing()) {
                failures++;
            }
        }
        return failures;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(System.lineSeparator());
        joiner.add("Transcript for " + student.getName());
        for (Map.Entry<Course, Mark> entry : marks.entrySet()) {
            Mark mark = entry.getValue();
            joiner.add(entry.getKey().getCode() + " - " + entry.getKey().getName() + ": total="
                    + mark.calculateTotal() + ", letter=" + mark.getLetterGrade());
        }
        joiner.add("GPA: " + String.format("%.2f", calculateGpa()));
        return joiner.toString();
    }
}
