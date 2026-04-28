package patterns;

import enums.TeacherPosition;
import enums.UserType;
import users.Admin;
import users.Employee;
import users.GraduateStudent;
import users.Manager;
import users.Student;
import users.TechSupportSpecialist;
import users.Teacher;
import users.User;

public final class UserFactory {
    private UserFactory() {
    }

    public static User createUser(UserType userType, int id, String name, String email, String password) {
        switch (userType) {
            case STUDENT:
                return new Student(id, name, email, password, "Undeclared");
            case GRADUATE_STUDENT:
                return new GraduateStudent(id, name, email, password, "Research");
            case TEACHER:
                return new Teacher(id, name, email, password, "Academic Affairs", TeacherPosition.LECTOR);
            case MANAGER:
                return new Manager(id, name, email, password, "Management Office", "Academic Operations");
            case ADMIN:
                return new Admin(id, name, email, password, "Administration", 1);
            case EMPLOYEE:
                return new Employee(id, name, email, password, "Operations");
            case TECH_SUPPORT_SPECIALIST:
                return new TechSupportSpecialist(id, name, email, password, "IT Support");
            default:
                throw new IllegalArgumentException("Unsupported user type: " + userType);
        }
    }
}
