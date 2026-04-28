package users;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import academic.Course;
import academic.Transcript;
import exceptions.CreditLimitExceededException;

public class Student extends User {
    private static final int MAX_CREDITS = 21;

    private String major;
    private final Set<Course> enrolledCourses;
    private final Set<Course> requestedCourses;
    private final Transcript transcript;
    private final Map<Integer, Integer> ratedTeachers;

    public Student(int id, String name, String email, String password, String major) {
        super(id, name, email, password);
        this.major = major;
        this.enrolledCourses = new LinkedHashSet<Course>();
        this.requestedCourses = new LinkedHashSet<Course>();
        this.transcript = new Transcript(this);
        this.ratedTeachers = new LinkedHashMap<Integer, Integer>();
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Set<Course> getEnrolledCourses() {
        return enrolledCourses;
    }

    public Set<Course> getRequestedCourses() {
        return requestedCourses;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public int getCurrentCredits() {
        int credits = 0;
        for (Course course : enrolledCourses) {
            credits += course.getCredits();
        }
        return credits;
    }

    public void requestCourse(Course course) throws CreditLimitExceededException {
        int requestedCredits = getCurrentCredits();
        for (Course requestedCourse : requestedCourses) {
            requestedCredits += requestedCourse.getCredits();
        }
        requestedCredits += course.getCredits();

        if (requestedCredits > MAX_CREDITS) {
            throw new CreditLimitExceededException(requestedCredits);
        }
        requestedCourses.add(course);
    }

    public void removeRequestedCourse(Course course) {
        requestedCourses.remove(course);
    }

    public void enrollCourse(Course course) {
        enrolledCourses.add(course);
        requestedCourses.remove(course);
    }

    public void dropCourse(Course course) {
        enrolledCourses.remove(course);
        requestedCourses.remove(course);
    }

    public boolean hasRatedTeacher(Teacher teacher) {
        return ratedTeachers.containsKey(teacher.getId());
    }

    public void storeTeacherRating(Teacher teacher, int rating) {
        ratedTeachers.put(teacher.getId(), Integer.valueOf(rating));
    }

    @Override
    public String getRole() {
        return "Student";
    }
}
