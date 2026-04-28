package exceptions;

public class CourseCapacityException extends UniversitySystemException {
    public CourseCapacityException(String courseName) {
        super("Course '" + courseName + "' has reached its capacity.");
    }
}
