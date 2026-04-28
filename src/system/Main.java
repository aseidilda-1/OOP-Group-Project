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
            System.out.println("===== Research-Oriented University System =====");
            System.out.println("1. Login");
            System.out.println("2. Exit");

            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    loginFlow();
                    break;
                case 2:
                    running = false;
                    break;
                default:
                    System.out.println("Unknown option.");
            }
        }

        System.out.println("Goodbye.");
    }

    private static void printWelcome() {
        System.out.println("Welcome to the Research-Oriented University System.");
        System.out.println("Demo logins on a fresh start:");
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
            System.out.println("Login successful. Welcome, " + user.getName() + " (" + user.getRole() + ").");
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
                } else if (handleCommonAction(user, choice)) {
                    continue;
                } else if (user instanceof Admin) {
                    handleAdminAction((Admin) user, choice);
                } else if (user instanceof Manager) {
                    handleManagerAction((Manager) user, choice);
                } else if (user instanceof Teacher) {
                    handleTeacherAction((Teacher) user, choice);
                } else if (user instanceof TechSupportSpecialist) {
                    handleTechSupportAction((TechSupportSpecialist) user, choice);
                } else if (user instanceof Student) {
                    handleStudentAction((Student) user, choice);
                } else {
                    System.out.println("No role-specific actions are available.");
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
        System.out.println("1. View news");
        System.out.println("2. Subscribe to topic or journal");
        System.out.println("3. View notifications");
        System.out.println("4. View inbox");
        System.out.println("5. Send message");
        System.out.println("6. Submit tech support request");
        System.out.println("7. Add comment to news");

        if (user instanceof Admin) {
            System.out.println("10. Add user");
            System.out.println("11. Remove user");
            System.out.println("12. Update user");
            System.out.println("13. View logs");
            System.out.println("14. Grant researcher role");
            System.out.println("15. List users");
        } else if (user instanceof Manager) {
            System.out.println("10. Assign teacher to course");
            System.out.println("11. Approve course registration");
            System.out.println("12. Reject course registration");
            System.out.println("13. Generate GPA report");
            System.out.println("14. Generate course performance report");
            System.out.println("15. Publish news");
            System.out.println("16. View registrations");
        } else if (user instanceof Teacher) {
            System.out.println("10. View assigned courses");
            System.out.println("11. Put marks");
            System.out.println("12. Add lesson to course");
            System.out.println("13. Send complaint");
            System.out.println("14. View average rating");
            System.out.println("15. Research menu");
        } else if (user instanceof TechSupportSpecialist) {
            System.out.println("10. View tech support requests");
            System.out.println("11. Process tech support request");
        } else if (user instanceof Student) {
            System.out.println("10. Register for course");
            System.out.println("11. View transcript");
            System.out.println("12. Rate teacher");
            System.out.println("13. View enrolled courses");
            System.out.println("14. Research menu");
            if (user instanceof GraduateStudent) {
                System.out.println("15. Set supervisor");
                System.out.println("16. View supervisor");
            }
        }
    }

    private static boolean handleCommonAction(User user, int choice) throws UniversitySystemException {
        switch (choice) {
            case 1:
                printNews();
                return true;
            case 2:
                subscribeToTopic(user);
                return true;
            case 3:
                printNotifications(user);
                return true;
            case 4:
                printInbox(user);
                return true;
            case 5:
                sendMessage(user);
                return true;
            case 6:
                submitTechSupportRequest(user);
                return true;
            case 7:
                addCommentToNews(user);
                return true;
            default:
                return false;
        }
    }

    private static void handleAdminAction(Admin admin, int choice) throws UniversitySystemException {
        switch (choice) {
            case 10:
                addUser();
                break;
            case 11:
                removeUser();
                break;
            case 12:
                updateUser();
                break;
            case 13:
                printLogs();
                break;
            case 14:
                grantResearcherRole();
                break;
            case 15:
                printUsers(SYSTEM.getAllUsers());
                break;
            default:
                System.out.println("Unknown admin action.");
        }
    }

    private static void handleManagerAction(Manager manager, int choice) throws UniversitySystemException {
        switch (choice) {
            case 10:
                assignTeacherToCourse();
                break;
            case 11:
                approveRegistration();
                break;
            case 12:
                rejectRegistration();
                break;
            case 13:
                System.out.println(SYSTEM.generateGpaReport());
                break;
            case 14:
                generateCoursePerformanceReport();
                break;
            case 15:
                publishGeneralNews(manager);
                break;
            case 16:
                printRegistrations(SYSTEM.getRegistrations());
                break;
            default:
                System.out.println("Unknown manager action.");
        }
    }

    private static void handleTeacherAction(Teacher teacher, int choice) throws UniversitySystemException {
        switch (choice) {
            case 10:
                printCourses(new ArrayList<Course>(teacher.getAssignedCourses()));
                break;
            case 11:
                putMarks(teacher);
                break;
            case 12:
                addLesson(teacher);
                break;
            case 13:
                sendComplaint(teacher);
                break;
            case 14:
                System.out.println("Average rating: " + String.format("%.2f", teacher.getRating()) + " ("
                        + teacher.getRatingCount() + " ratings)");
                break;
            case 15:
                openResearchMenu(teacher);
                break;
            default:
                System.out.println("Unknown teacher action.");
        }
    }

    private static void handleTechSupportAction(TechSupportSpecialist specialist, int choice)
            throws UniversitySystemException {
        switch (choice) {
            case 10:
                printTechSupportRequests();
                break;
            case 11:
                processTechSupportRequest(specialist);
                break;
            default:
                System.out.println("Unknown tech support action.");
        }
    }

    private static void handleStudentAction(Student student, int choice) throws UniversitySystemException {
        switch (choice) {
            case 10:
                registerForCourse(student);
                break;
            case 11:
                System.out.println(student.getTranscript());
                break;
            case 12:
                rateTeacher(student);
                break;
            case 13:
                printCourses(new ArrayList<Course>(student.getEnrolledCourses()));
                break;
            case 14:
                openResearchMenu(student);
                break;
            case 15:
                if (student instanceof GraduateStudent) {
                    setSupervisor((GraduateStudent) student);
                    break;
                }
                System.out.println("Unknown student action.");
                break;
            case 16:
                if (student instanceof GraduateStudent) {
                    viewSupervisor((GraduateStudent) student);
                    break;
                }
                System.out.println("Unknown student action.");
                break;
            default:
                System.out.println("Unknown student action.");
        }
    }

    private static void addUser() {
        String name = readLine("Name: ");
        String email = readLine("Email: ");
        String password = readLine("Password: ");
        UserType userType = selectUserType();
        User user = SYSTEM.createUser(userType, name, email, password);
        System.out.println("Created user: " + user);
    }

    private static UserType selectUserType() {
        System.out.println("User types:");
        UserType[] userTypes = UserType.values();
        for (int i = 0; i < userTypes.length; i++) {
            System.out.println((i + 1) + ". " + userTypes[i]);
        }
        int choice = readInt("Choose type: ");
        return userTypes[Math.max(0, Math.min(choice - 1, userTypes.length - 1))];
    }

    private static void removeUser() throws UniversitySystemException {
        printUsers(SYSTEM.getAllUsers());
        int userId = readInt("User id to remove: ");
        SYSTEM.removeUser(userId);
        System.out.println("User removed.");
    }

    private static void updateUser() throws UniversitySystemException {
        printUsers(SYSTEM.getAllUsers());
        int userId = readInt("User id to update: ");
        String name = readLine("New name (leave blank to keep): ");
        String email = readLine("New email (leave blank to keep): ");
        String password = readLine("New password (leave blank to keep): ");
        SYSTEM.updateUser(userId, name, email, password);
        System.out.println("User updated.");
    }

    private static void printLogs() {
        List<String> logs = SYSTEM.getLogs();
        if (logs.isEmpty()) {
            System.out.println("No logs available.");
            return;
        }
        for (String log : logs) {
            System.out.println(log);
        }
    }

    private static void grantResearcherRole() throws UniversitySystemException {
        printUsers(SYSTEM.getAllUsers());
        int userId = readInt("User id to decorate as researcher: ");
        ResearchDecorator decorator = SYSTEM.createResearchProfile(userId);
        System.out.println("Researcher role granted: " + decorator);
    }

    private static void assignTeacherToCourse() throws UniversitySystemException {
        printTeachers();
        int teacherId = readInt("Teacher id: ");
        printCourses(SYSTEM.getAllCourses());
        int courseId = readInt("Course id: ");
        LessonType lessonType = selectLessonType();
        SYSTEM.assignTeacherToCourse(teacherId, courseId, lessonType);
        System.out.println("Teacher assigned.");
    }

    private static LessonType selectLessonType() {
        System.out.println("1. LECTURE");
        System.out.println("2. PRACTICE");
        int choice = readInt("Lesson type: ");
        return choice == 2 ? LessonType.PRACTICE : LessonType.LECTURE;
    }

    private static void approveRegistration() throws UniversitySystemException {
        printRegistrations(SYSTEM.getPendingRegistrations());
        int registrationId = readInt("Registration id to approve: ");
        SYSTEM.approveRegistration(registrationId);
        System.out.println("Registration approved.");
    }

    private static void rejectRegistration() throws UniversitySystemException {
        printRegistrations(SYSTEM.getPendingRegistrations());
        int registrationId = readInt("Registration id to reject: ");
        SYSTEM.rejectRegistration(registrationId);
        System.out.println("Registration rejected.");
    }

    private static void generateCoursePerformanceReport() throws UniversitySystemException {
        printCourses(SYSTEM.getAllCourses());
        int courseId = readInt("Course id: ");
        System.out.println(SYSTEM.generateCoursePerformanceReport(courseId));
    }

    private static void publishGeneralNews(User author) {
        String title = readLine("News title: ");
        String content = readLine("News content: ");
        SYSTEM.publishNews(author, title, content, false, NewsCategory.GENERAL, "GENERAL");
        System.out.println("News published.");
    }

    private static void putMarks(Teacher teacher) throws UniversitySystemException {
        printCourses(new ArrayList<Course>(teacher.getAssignedCourses()));
        int courseId = readInt("Course id: ");
        Course course = SYSTEM.getCourseById(courseId);
        printStudents(new ArrayList<Student>(course.getStudents()));
        int studentId = readInt("Student id: ");
        double first = readDouble("1st attestation: ");
        double second = readDouble("2nd attestation: ");
        double finalExam = readDouble("Final exam: ");
        SYSTEM.assignMarks(teacher, studentId, courseId, first, second, finalExam);
        System.out.println("Marks saved.");
    }

    private static void addLesson(Teacher teacher) throws UniversitySystemException {
        printCourses(new ArrayList<Course>(teacher.getAssignedCourses()));
        int courseId = readInt("Course id: ");
        Course course = SYSTEM.getCourseById(courseId);
        if (!teacher.teachesCourse(course)) {
            System.out.println("You are not assigned to this course.");
            return;
        }
        String topic = readLine("Lesson topic: ");
        LessonType lessonType = selectLessonType();
        String room = readLine("Room: ");
        int daysOffset = readInt("Schedule after how many days from now: ");
        SYSTEM.addLessonToCourse(courseId, topic, lessonType, teacher, LocalDateTime.now().plusDays(daysOffset), room);
        System.out.println("Lesson added.");
    }

    private static void sendComplaint(Teacher teacher) throws UniversitySystemException {
        String content = readLine("Complaint text: ");
        SYSTEM.sendComplaint(teacher, content);
        System.out.println("Complaint sent.");
    }

    private static void registerForCourse(Student student) throws UniversitySystemException {
        printCourses(SYSTEM.getAllCourses());
        int courseId = readInt("Course id to request: ");
        CourseRegistration registration = SYSTEM.requestCourseRegistration(student, courseId);
        System.out.println("Registration request created: " + registration);
    }

    private static void rateTeacher(Student student) throws UniversitySystemException {
        printTeachers();
        int teacherId = readInt("Teacher id: ");
        int rating = readInt("Rating (1-5): ");
        SYSTEM.rateTeacher(student, teacherId, rating);
        System.out.println("Teacher rated.");
    }

    private static void setSupervisor(GraduateStudent graduateStudent) throws UniversitySystemException {
        List<ResearchDecorator> researchers = SYSTEM.getResearchProfiles();
        if (researchers.isEmpty()) {
            System.out.println("No researchers are available.");
            return;
        }
        for (ResearchDecorator researcher : researchers) {
            System.out.println(researcher.getId() + " - " + researcher.getResearcherName() + " (h-index="
                    + researcher.calculateHIndex() + ")");
        }
        int supervisorId = readInt("Supervisor user id: ");
        SYSTEM.assignSupervisor(graduateStudent.getId(), supervisorId);
        System.out.println("Supervisor assigned.");
    }

    private static void viewSupervisor(GraduateStudent graduateStudent) {
        if (graduateStudent.getSupervisor() == null) {
            System.out.println("Supervisor is not set.");
        } else {
            System.out.println("Supervisor: " + graduateStudent.getSupervisor().getResearcherName() + " (h-index="
                    + graduateStudent.getSupervisor().calculateHIndex() + ")");
        }
    }

    private static void printNews() {
        List<News> newsFeed = SYSTEM.getNewsFeed();
        if (newsFeed.isEmpty()) {
            System.out.println("No news published yet.");
            return;
        }
        for (News news : newsFeed) {
            System.out.println(news);
            System.out.println(news.getContent());
            if (!news.getComments().isEmpty()) {
                System.out.println("Comments:");
                for (communication.Comment comment : news.getComments()) {
                    System.out.println("  " + comment);
                }
            }
            System.out.println();
        }
    }

    private static void subscribeToTopic(User user) {
        System.out.println("Examples: GENERAL, RESEARCH, Journal of Educational Computing, ALL");
        String topic = readLine("Topic or journal name: ");
        SYSTEM.subscribeUser(user, topic);
        System.out.println("Subscription added.");
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
            message.markAsRead();
        }
    }

    private static void sendMessage(User sender) throws UniversitySystemException {
        printUsers(SYSTEM.getAllUsers());
        int receiverId = readInt("Receiver id: ");
        String content = readLine("Message text: ");
        SYSTEM.sendMessage(sender, receiverId, content);
        System.out.println("Message sent.");
    }

    private static void submitTechSupportRequest(User user) {
        String description = readLine("Describe the issue: ");
        TechSupportRequest request = SYSTEM.submitTechSupportRequest(user, description);
        System.out.println("Created request #" + request.getId());
    }

    private static void addCommentToNews(User user) throws UniversitySystemException {
        printNews();
        int newsId = readInt("News id: ");
        String comment = readLine("Comment: ");
        SYSTEM.addCommentToNews(newsId, user, comment);
        System.out.println("Comment added.");
    }

    private static void openResearchMenu(User user) throws UniversitySystemException {
        if (!SYSTEM.isResearcher(user)) {
            System.out.println("This user does not have the researcher role.");
            return;
        }

        ResearchDecorator researcher = SYSTEM.getResearchProfile(user);
        boolean active = true;
        while (active) {
            System.out.println();
            System.out.println("===== Research Menu =====");
            System.out.println("0. Back");
            System.out.println("1. View h-index");
            System.out.println("2. Print papers");
            System.out.println("3. Create research project");
            System.out.println("4. Join research project");
            System.out.println("5. View research projects");
            System.out.println("6. Publish research paper");

            int choice = readInt("Choose action: ");
            switch (choice) {
                case 0:
                    active = false;
                    break;
                case 1:
                    System.out.println("h-index: " + researcher.calculateHIndex());
                    break;
                case 2:
                    printResearchPapers(researcher);
                    break;
                case 3:
                    createResearchProject(user);
                    break;
                case 4:
                    joinResearchProject(user);
                    break;
                case 5:
                    printResearchProjects();
                    break;
                case 6:
                    publishResearchPaper(user);
                    break;
                default:
                    System.out.println("Unknown research action.");
            }
        }
    }

    private static void printResearchPapers(ResearchDecorator researcher) {
        if (researcher.getPapers().isEmpty()) {
            System.out.println("No papers found.");
            return;
        }

        System.out.println("1. Sort by title");
        System.out.println("2. Sort by date");
        System.out.println("3. Sort by citations");
        int choice = readInt("Sort option: ");

        Comparator<ResearchPaper> comparator;
        switch (choice) {
            case 2:
                comparator = new Comparator<ResearchPaper>() {
                    @Override
                    public int compare(ResearchPaper first, ResearchPaper second) {
                        return second.getDate().compareTo(first.getDate());
                    }
                };
                break;
            case 3:
                comparator = new Comparator<ResearchPaper>() {
                    @Override
                    public int compare(ResearchPaper first, ResearchPaper second) {
                        return Integer.compare(second.getCitations(), first.getCitations());
                    }
                };
                break;
            case 1:
            default:
                comparator = new Comparator<ResearchPaper>() {
                    @Override
                    public int compare(ResearchPaper first, ResearchPaper second) {
                        return first.getTitle().compareToIgnoreCase(second.getTitle());
                    }
                };
        }

        researcher.printPapers(comparator);
    }

    private static void createResearchProject(User user) throws UniversitySystemException {
        String topic = readLine("Project topic: ");
        int startOffset = readInt("Start in how many days from now (negative for past): ");
        int endOffset = readInt("End in how many days from now: ");
        double budget = readDouble("Budget: ");
        ResearchProject project = SYSTEM.createResearchProject(user, topic, LocalDate.now().plusDays(startOffset),
                LocalDate.now().plusDays(endOffset), budget);
        System.out.println("Created project: " + project);
    }

    private static void joinResearchProject(User user) throws UniversitySystemException {
        printResearchProjects();
        int projectId = readInt("Project id: ");
        SYSTEM.joinResearchProject(user, projectId);
        System.out.println("Joined project.");
    }

    private static void printResearchProjects() {
        List<ResearchProject> projects = SYSTEM.getResearchProjects();
        if (projects.isEmpty()) {
            System.out.println("No research projects.");
            return;
        }
        for (ResearchProject project : projects) {
            System.out.println(project);
        }
    }

    private static void publishResearchPaper(User user) throws UniversitySystemException {
        String title = readLine("Paper title: ");
        int citations = readInt("Initial citations: ");
        String journal = readLine("Journal: ");
        String pages = readLine("Pages: ");
        int year = readInt("Publication year: ");
        int month = readInt("Publication month (1-12): ");
        int day = readInt("Publication day: ");
        String doi = readLine("DOI: ");
        String rawAuthorIds = readLine("Author user ids separated by commas (leave blank to use current user): ");
        List<Integer> authorIds = parseIdList(rawAuthorIds);

        Integer projectId = null;
        if (!SYSTEM.getResearchProjects().isEmpty()) {
            printResearchProjects();
            String projectValue = readLine("Attach to project id (leave blank for none): ");
            if (!projectValue.trim().isEmpty()) {
                projectId = Integer.valueOf(Integer.parseInt(projectValue.trim()));
            }
        }

        ResearchPaper paper = SYSTEM.publishResearchPaper(user, authorIds, title, citations, journal, pages,
                LocalDate.of(year, month, day), doi, projectId);
        System.out.println("Research paper published: " + paper);
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
        List<TechSupportRequest> requests = SYSTEM.getTechSupportRequests();
        if (requests.isEmpty()) {
            System.out.println("No tech support requests.");
            return;
        }
        for (TechSupportRequest request : requests) {
            System.out.println(request);
        }
    }

    private static void processTechSupportRequest(TechSupportSpecialist specialist) throws UniversitySystemException {
        printTechSupportRequests();
        int requestId = readInt("Request id: ");
        System.out.println("1. VIEWED");
        System.out.println("2. ACCEPTED");
        System.out.println("3. REJECTED");
        System.out.println("4. DONE");
        int choice = readInt("New status: ");

        TechSupportRequestStatus status;
        switch (choice) {
            case 1:
                status = TechSupportRequestStatus.VIEWED;
                break;
            case 2:
                status = TechSupportRequestStatus.ACCEPTED;
                break;
            case 3:
                status = TechSupportRequestStatus.REJECTED;
                break;
            case 4:
                status = TechSupportRequestStatus.DONE;
                break;
            default:
                System.out.println("Invalid status choice.");
                return;
        }

        SYSTEM.processTechSupportRequest(specialist, requestId, status);
        System.out.println("Request updated.");
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
        List<Teacher> teachers = SYSTEM.getTeachers();
        if (teachers.isEmpty()) {
            System.out.println("No teachers available.");
            return;
        }
        for (Teacher teacher : teachers) {
            System.out.println(teacher + ", rating=" + String.format("%.2f", teacher.getRating()));
        }
    }

    private static void printStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("No students available.");
            return;
        }
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
        if (registrations.isEmpty()) {
            System.out.println("No registrations found.");
            return;
        }
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
