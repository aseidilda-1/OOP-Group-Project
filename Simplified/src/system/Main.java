package system;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import academic.Course;
import academic.CourseRegistration;
import communication.Message;
import communication.News;
import communication.TechSupportRequest;
import enums.LessonType;
import enums.NewsCategory;
import enums.TechSupportRequestStatus;
import enums.UserType;
import exceptions.AuthenticationException;
import exceptions.UniversitySystemException;
import research.ResearchDecorator;
import research.ResearchPaper;
import research.ResearchProject;
import users.Admin;
import users.GraduateStudent;
import users.Manager;
import users.Student;
import users.TechSupportSpecialist;
import users.Teacher;
import users.User;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final UniversitySystem SYSTEM = UniversitySystem.getInstance();

    public static void main(String[] args) {
        printWelcome();

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("===== Simplified Research-Oriented University System =====");
            System.out.println("1. Login");
            System.out.println("2. Exit");

            int choice = readInt("Choose an option: ");
            if (choice == 1) {
                loginFlow();
            } else if (choice == 2) {
                running = false;
            } else {
                System.out.println("Simplified menu: choose 1 or 2.");
            }
        }

        System.out.println("Goodbye.");
    }

    private static void printWelcome() {
        System.out.println("Welcome to the simplified stub version.");
        System.out.println("Demo accounts:");
        System.out.println("  admin / admin123");
        System.out.println("  manager / manager123");
        System.out.println("  teacher / teacher123");
        System.out.println("  student / student123");
        System.out.println("  grad / grad123");
        System.out.println("  support / support123");
    }

    private static void loginFlow() {
        String username = readLine("Username or email: ");
        String password = readLine("Password: ");

        try {
            User user = SYSTEM.authenticate(username, password);
            System.out.println("Login successful. Welcome, " + user.getName() + ".");
            userSession(user);
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void userSession(User user) {
        boolean active = true;
        while (active) {
            printMenuHeader(user);
            int choice = readInt("Select action: ");

            try {
                if (choice == 0) {
                    active = false;
                } else if (!handleCommonAction(user, choice)) {
                    System.out.println("Simplified version: role-specific logic is stubbed.");
                }
            } catch (UniversitySystemException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void printMenuHeader(User user) {
        System.out.println();
        System.out.println("===== " + user.getRole() + " Menu =====");
        System.out.println("0. Logout");
        System.out.println("1. View profile");
        System.out.println("2. View news");
        System.out.println("3. View notifications");
        System.out.println("4. View inbox");
        System.out.println("5. View all users");
        System.out.println("6. View all courses");
    }

    private static boolean handleCommonAction(User user, int choice) throws UniversitySystemException {
        switch (choice) {
            case 1:
                System.out.println(user);
                return true;
            case 2:
                printNews();
                return true;
            case 3:
                printNotifications(user);
                return true;
            case 4:
                printInbox(user);
                return true;
            case 5:
                printUsers(SYSTEM.getAllUsers());
                return true;
            case 6:
                printCourses(SYSTEM.getAllCourses());
                return true;
            default:
                return false;
        }
    }

    private static void handleAdminAction(Admin admin, int choice) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Admin action is simplified for demonstration.");
    }

    private static void handleManagerAction(Manager manager, int choice) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Manager action is simplified for demonstration.");
    }

    private static void handleTeacherAction(Teacher teacher, int choice) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Teacher action is simplified for demonstration.");
    }

    private static void handleTechSupportAction(TechSupportSpecialist specialist, int choice)
            throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Tech support action is simplified for demonstration.");
    }

    private static void handleStudentAction(Student student, int choice) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Student action is simplified for demonstration.");
    }

    private static void addUser() {
        // TODO: full implementation in final version
        System.out.println("Add user is not fully implemented in the simplified version.");
    }

    private static UserType selectUserType() {
        // TODO: full implementation in final version
        return UserType.STUDENT;
    }

    private static void removeUser() throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Remove user is not fully implemented in the simplified version.");
    }

    private static void updateUser() throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Update user is not fully implemented in the simplified version.");
    }

    private static void printLogs() {
        System.out.println("System logs:");
        for (String log : SYSTEM.getLogs()) {
            System.out.println(log);
        }
    }

    private static void grantResearcherRole() throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Grant researcher role is not fully implemented in the simplified version.");
    }

    private static void assignTeacherToCourse() throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Assign teacher to course is not fully implemented in the simplified version.");
    }

    private static LessonType selectLessonType() {
        // TODO: full implementation in final version
        return LessonType.LECTURE;
    }

    private static void approveRegistration() throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Approve registration is not fully implemented in the simplified version.");
    }

    private static void rejectRegistration() throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Reject registration is not fully implemented in the simplified version.");
    }

    private static void generateCoursePerformanceReport() throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Generate course performance report is simplified.");
    }

    private static void publishGeneralNews(User author) {
        // TODO: full implementation in final version
        System.out.println("Publish news is not fully implemented in the simplified version.");
    }

    private static void putMarks(Teacher teacher) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Put marks is not fully implemented in the simplified version.");
    }

    private static void addLesson(Teacher teacher) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Add lesson is not fully implemented in the simplified version.");
    }

    private static void sendComplaint(Teacher teacher) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Send complaint is not fully implemented in the simplified version.");
    }

    private static void registerForCourse(Student student) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Register for course is not fully implemented in the simplified version.");
    }

    private static void rateTeacher(Student student) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Rate teacher is not fully implemented in the simplified version.");
    }

    private static void setSupervisor(GraduateStudent graduateStudent) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Set supervisor is not fully implemented in the simplified version.");
    }

    private static void viewSupervisor(GraduateStudent graduateStudent) {
        System.out.println("Supervisor: " + graduateStudent.getSupervisor());
    }

    private static void printNews() {
        List<News> newsFeed = SYSTEM.getNewsFeed();
        if (newsFeed.isEmpty()) {
            System.out.println("No news available.");
            return;
        }

        for (News news : newsFeed) {
            System.out.println(news);
        }
    }

    private static void subscribeToTopic(User user) {
        // TODO: full implementation in final version
        System.out.println("Subscribe to topic is not fully implemented in the simplified version.");
    }

    private static void printNotifications(User user) {
        if (user.getNotifications().isEmpty()) {
            System.out.println("No notifications.");
            return;
        }

        for (String notification : user.getNotifications()) {
            System.out.println(notification);
        }
    }

    private static void printInbox(User user) {
        if (user.getInbox().isEmpty()) {
            System.out.println("Inbox is empty.");
            return;
        }

        for (Message message : user.getInbox()) {
            System.out.println(message);
        }
    }

    private static void sendMessage(User sender) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Send message is not fully implemented in the simplified version.");
    }

    private static void submitTechSupportRequest(User user) {
        // TODO: full implementation in final version
        System.out.println("Submit tech support request is not fully implemented in the simplified version.");
    }

    private static void addCommentToNews(User user) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Add comment to news is not fully implemented in the simplified version.");
    }

    private static void openResearchMenu(User user) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Research menu is not fully implemented in the simplified version.");
    }

    private static void printResearchPapers(ResearchDecorator researcher) {
        if (researcher == null || researcher.getPapers().isEmpty()) {
            System.out.println("No research papers available.");
            return;
        }

        for (ResearchPaper paper : researcher.getPapers()) {
            System.out.println(paper);
        }
    }

    private static void createResearchProject(User user) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Create research project is not fully implemented in the simplified version.");
    }

    private static void joinResearchProject(User user) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Join research project is not fully implemented in the simplified version.");
    }

    private static void printResearchProjects() {
        for (ResearchProject project : SYSTEM.getResearchProjects()) {
            System.out.println(project);
        }
    }

    private static void publishResearchPaper(User user) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Publish research paper is not fully implemented in the simplified version.");
    }

    private static List<Integer> parseIdList(String rawValues) {
        List<Integer> ids = new ArrayList<Integer>();
        if (rawValues == null || rawValues.trim().isEmpty()) {
            return ids;
        }

        String[] parts = rawValues.split(",");
        for (String part : parts) {
            String cleaned = part.trim();
            if (!cleaned.isEmpty()) {
                ids.add(Integer.valueOf(Integer.parseInt(cleaned)));
            }
        }
        return ids;
    }

    private static void printTechSupportRequests() {
        for (TechSupportRequest request : SYSTEM.getTechSupportRequests()) {
            System.out.println(request);
        }
    }

    private static void processTechSupportRequest(TechSupportSpecialist specialist) throws UniversitySystemException {
        // TODO: full implementation in final version
        System.out.println("Process tech support request is not fully implemented in the simplified version.");
    }

    private static void printUsers(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users available.");
            return;
        }

        for (User user : users) {
            System.out.println(user);
        }
    }

    private static void printTeachers() {
        for (Teacher teacher : SYSTEM.getTeachers()) {
            System.out.println(teacher);
        }
    }

    private static void printStudents(List<Student> students) {
        for (Student student : students) {
            System.out.println(student);
        }
    }

    private static void printCourses(List<Course> courses) {
        if (courses.isEmpty()) {
            System.out.println("No courses available.");
            return;
        }

        for (Course course : courses) {
            System.out.println(course);
        }
    }

    private static void printRegistrations(List<CourseRegistration> registrations) {
        for (CourseRegistration registration : registrations) {
            System.out.println(registration);
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(SCANNER.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine();
    }
}
