package academic;

import java.io.Serializable;
import java.util.Objects;

import users.Student;

public class Mark implements Serializable {
    private final double firstAttestation;
    private final double secondAttestation;
    private final double finalExam;
    private final Student student;
    private final Course course;

    public Mark(double firstAttestation, double secondAttestation, double finalExam, Student student, Course course) {
        this.firstAttestation = firstAttestation;
        this.secondAttestation = secondAttestation;
        this.finalExam = finalExam;
        this.student = student;
        this.course = course;
    }

    public double getFirstAttestation() {
        return firstAttestation;
    }

    public double getSecondAttestation() {
        return secondAttestation;
    }

    public double getFinalExam() {
        return finalExam;
    }

    public Student getStudent() {
        return student;
    }

    public Course getCourse() {
        return course;
    }

    public double calculateTotal() {
        return firstAttestation + secondAttestation + finalExam;
    }

    public boolean isPassing() {
        return calculateTotal() >= 50.0;
    }

    public String getLetterGrade() {
        double total = calculateTotal();
        if (total >= 95) {
            return "A";
        }
        if (total >= 90) {
            return "A-";
        }
        if (total >= 85) {
            return "B+";
        }
        if (total >= 80) {
            return "B";
        }
        if (total >= 75) {
            return "B-";
        }
        if (total >= 70) {
            return "C+";
        }
        if (total >= 65) {
            return "C";
        }
        if (total >= 60) {
            return "C-";
        }
        if (total >= 55) {
            return "D+";
        }
        if (total >= 50) {
            return "D";
        }
        return "F";
    }

    public double getGradePoints() {
        String letter = getLetterGrade();
        if ("A".equals(letter)) {
            return 4.0;
        }
        if ("A-".equals(letter)) {
            return 3.67;
        }
        if ("B+".equals(letter)) {
            return 3.33;
        }
        if ("B".equals(letter)) {
            return 3.0;
        }
        if ("B-".equals(letter)) {
            return 2.67;
        }
        if ("C+".equals(letter)) {
            return 2.33;
        }
        if ("C".equals(letter)) {
            return 2.0;
        }
        if ("C-".equals(letter)) {
            return 1.67;
        }
        if ("D+".equals(letter)) {
            return 1.33;
        }
        if ("D".equals(letter)) {
            return 1.0;
        }
        return 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Mark)) {
            return false;
        }
        Mark mark = (Mark) o;
        return Objects.equals(student, mark.student) && Objects.equals(course, mark.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, course);
    }

    @Override
    public String toString() {
        return "Mark{student=" + student.getName() + ", course=" + course.getCode() + ", total=" + calculateTotal()
                + ", letter='" + getLetterGrade() + "'}";
    }
}
