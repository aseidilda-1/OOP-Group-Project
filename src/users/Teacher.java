package users;

import java.util.LinkedHashSet;
import java.util.Set;

import academic.Course;
import academic.Mark;
import enums.TeacherPosition;

public class Teacher extends Employee {
    private TeacherPosition position;
    private final Set<Course> assignedCourses;
    private double totalRating;
    private int ratingCount;

    public Teacher(int id, String name, String email, String password, String department, TeacherPosition position) {
        super(id, name, email, password, department);
        this.position = position;
        this.assignedCourses = new LinkedHashSet<Course>();
    }

    public TeacherPosition getPosition() {
        return position;
    }

    public void setPosition(TeacherPosition position) {
        this.position = position;
    }

    public Set<Course> getAssignedCourses() {
        return assignedCourses;
    }

    public void assignCourse(Course course) {
        assignedCourses.add(course);
    }

    public void unassignCourse(Course course) {
        assignedCourses.remove(course);
    }

    public boolean teachesCourse(Course course) {
        return assignedCourses.contains(course);
    }

    public Mark createMarkSnapshot(double firstAttestation, double secondAttestation, double finalExam, Student student, Course course) {
        return new Mark(firstAttestation, secondAttestation, finalExam, student, course);
    }

    public void addRating(int rating) {
        totalRating += rating;
        ratingCount++;
    }

    public double getRating() {
        if (ratingCount == 0) {
            return 0.0;
        }
        return totalRating / ratingCount;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    @Override
    public String getRole() {
        return "Teacher";
    }
}
