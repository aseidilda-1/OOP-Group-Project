package system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import academic.Course;
import academic.CourseRegistration;
import academic.Lesson;
import academic.Mark;
import communication.Comment;
import communication.Message;
import communication.News;
import communication.TechSupportRequest;
import enums.CitationFormat;
import enums.LessonType;
import enums.NewsCategory;
import enums.RegistrationStatus;
import enums.TeacherPosition;
import enums.TechSupportRequestStatus;
import enums.UserType;
import exceptions.AuthenticationException;
import exceptions.EntityNotFoundException;
import exceptions.NotResearcherException;
import exceptions.UniversitySystemException;
import patterns.UserFactory;
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

public class UniversitySystem implements Serializable {
    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE = DATA_DIRECTORY + File.separator + "university-system.ser";

    private static UniversitySystem instance;

    private final Map<Integer, User> users;
    private final Map<Integer, Course> courses;
    private final Map<Integer, CourseRegistration> registrations;
    private final Map<Integer, ResearchDecorator> researcherProfiles;
    private final Map<Integer, ResearchProject> researchProjects;
    private final Map<Integer, ResearchPaper> researchPapers;
    private final List<News> newsFeed;
    private final List<Message> messages;
    private final List<TechSupportRequest> techSupportRequests;
    private final List<String> logs;

    private int nextUserId;
    private int nextCourseId;
    private int nextLessonId;
    private int nextRegistrationId;
    private int nextNewsId;
    private int nextCommentId;
    private int nextMessageId;
    private int nextRequestId;
    private int nextResearchPaperId;
    private int nextResearchProjectId;

    private UniversitySystem() {
        this.users = new LinkedHashMap<Integer, User>();
        this.courses = new LinkedHashMap<Integer, Course>();
        this.registrations = new LinkedHashMap<Integer, CourseRegistration>();
        this.researcherProfiles = new LinkedHashMap<Integer, ResearchDecorator>();
        this.researchProjects = new LinkedHashMap<Integer, ResearchProject>();
        this.researchPapers = new LinkedHashMap<Integer, ResearchPaper>();
        this.newsFeed = new ArrayList<News>();
        this.messages = new ArrayList<Message>();
        this.techSupportRequests = new ArrayList<TechSupportRequest>();
        this.logs = new ArrayList<String>();

        this.nextUserId = 1;
        this.nextCourseId = 1;
        this.nextLessonId = 1;
        this.nextRegistrationId = 1;
        this.nextNewsId = 1;
        this.nextCommentId = 1;
        this.nextMessageId = 1;
        this.nextRequestId = 1;
        this.nextResearchPaperId = 1;
        this.nextResearchProjectId = 1;

        seedDemoData();
    }

    public static synchronized UniversitySystem getInstance() {
        if (instance == null) {
            instance = loadState();
        }
        return instance;
    }

    private static UniversitySystem loadState() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
                UniversitySystem loadedSystem = (UniversitySystem) inputStream.readObject();
                inputStream.close();
                return loadedSystem;
            } catch (Exception e) {
                // TODO: full implementation in final version
                // simplified for demonstration
            }
        }

        return new UniversitySystem();
    }

    private Object readResolve() {
        instance = this;
        return this;
    }

    public void save() throws IOException {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(DATA_FILE));
        outputStream.writeObject(this);
        outputStream.close();
    }

    private void saveQuietly() {
        try {
            save();
        } catch (IOException e) {
            System.out.println("Simplified version: state was not saved.");
        }
    }

    public User authenticate(String username, String password) throws AuthenticationException {
        for (User user : users.values()) {
            if (matchesLogin(user, username) && user.checkPassword(password)) {
                logAction("User logged in: " + user.getEmail());
                return user;
            }
        }
        throw new AuthenticationException("Invalid username or password.");
    }

    private boolean matchesLogin(User user, String username) {
        String email = user.getEmail();
        String localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        return email.equalsIgnoreCase(username) || localPart.equalsIgnoreCase(username);
    }

    public User createUser(UserType userType, String name, String email, String password) {
        User user = UserFactory.createUser(userType, nextUserId++, name, email, password);
        addUser(user);
        return user;
    }

    public void addUser(User user) {
        if (user == null) {
            return;
        }

        users.put(Integer.valueOf(user.getId()), user);
        logAction("User added: " + user);
        saveQuietly();
    }

    public void removeUser(int userId) throws EntityNotFoundException {
        User removedUser = users.remove(Integer.valueOf(userId));
        if (removedUser == null) {
            throw new EntityNotFoundException("User with id " + userId + " was not found.");
        }

        researcherProfiles.remove(Integer.valueOf(userId));
        logAction("User removed: " + removedUser);
        saveQuietly();
    }

    public void updateUser(int userId, String name, String email, String password) throws EntityNotFoundException {
        User user = getUserById(userId);

        if (name != null && !name.trim().isEmpty()) {
            user.setName(name);
        }
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        if (password != null && !password.trim().isEmpty()) {
            user.setPassword(password);
        }

        logAction("User updated: " + user);
        saveQuietly();
    }

    public User getUserById(int userId) throws EntityNotFoundException {
        User user = users.get(Integer.valueOf(userId));
        if (user == null) {
            throw new EntityNotFoundException("User with id " + userId + " was not found.");
        }
        return user;
    }

    public Course getCourseById(int courseId) throws EntityNotFoundException {
        Course course = courses.get(Integer.valueOf(courseId));
        if (course == null) {
            throw new EntityNotFoundException("Course with id " + courseId + " was not found.");
        }
        return course;
    }

    public ResearchProject getResearchProjectById(int projectId) throws EntityNotFoundException {
        ResearchProject project = researchProjects.get(Integer.valueOf(projectId));
        if (project == null) {
            throw new EntityNotFoundException("Research project with id " + projectId + " was not found.");
        }
        return project;
    }

    public News getNewsById(int newsId) throws EntityNotFoundException {
        for (News news : newsFeed) {
            if (news.getId() == newsId) {
                return news;
            }
        }
        throw new EntityNotFoundException("News with id " + newsId + " was not found.");
    }

    public TechSupportRequest getRequestById(int requestId) throws EntityNotFoundException {
        for (TechSupportRequest request : techSupportRequests) {
            if (request.getId() == requestId) {
                return request;
            }
        }
        throw new EntityNotFoundException("Tech support request with id " + requestId + " was not found.");
    }

    public List<User> getAllUsers() {
        return new ArrayList<User>(users.values());
    }

    public List<Course> getAllCourses() {
        return new ArrayList<Course>(courses.values());
    }

    public List<Student> getStudents() {
        List<Student> students = new ArrayList<Student>();
        for (User user : users.values()) {
            if (user instanceof Student) {
                students.add((Student) user);
            }
        }
        return students;
    }

    public List<Teacher> getTeachers() {
        List<Teacher> teachers = new ArrayList<Teacher>();
        for (User user : users.values()) {
            if (user instanceof Teacher) {
                teachers.add((Teacher) user);
            }
        }
        return teachers;
    }

    public List<Manager> getManagers() {
        List<Manager> managers = new ArrayList<Manager>();
        for (User user : users.values()) {
            if (user instanceof Manager) {
                managers.add((Manager) user);
            }
        }
        return managers;
    }

    public List<Admin> getAdmins() {
        List<Admin> admins = new ArrayList<Admin>();
        for (User user : users.values()) {
            if (user instanceof Admin) {
                admins.add((Admin) user);
            }
        }
        return admins;
    }

    public List<TechSupportSpecialist> getTechSupportSpecialists() {
        List<TechSupportSpecialist> specialists = new ArrayList<TechSupportSpecialist>();
        for (User user : users.values()) {
            if (user instanceof TechSupportSpecialist) {
                specialists.add((TechSupportSpecialist) user);
            }
        }
        return specialists;
    }

    public Course createCourse(String code, String name, int credits, String description, int maxStudents) {
        Course course = new Course(nextCourseId++, code, name, credits, description, maxStudents);
        courses.put(Integer.valueOf(course.getId()), course);
        logAction("Course created: " + course);
        saveQuietly();
        return course;
    }

    public Lesson addLessonToCourse(int courseId, String topic, LessonType lessonType, Teacher teacher,
            LocalDateTime schedule, String room) throws EntityNotFoundException {
        Course course = getCourseById(courseId);
        Lesson lesson = new Lesson(nextLessonId++, topic, lessonType, teacher, schedule, room);
        course.addLesson(lesson);
        logAction("Lesson added to course: " + course.getCode());
        saveQuietly();
        return lesson;
    }

    public void assignTeacherToCourse(int teacherId, int courseId, LessonType lessonType)
            throws EntityNotFoundException {
        User user = getUserById(teacherId);
        if (!(user instanceof Teacher)) {
            throw new EntityNotFoundException("User with id " + teacherId + " is not a teacher.");
        }

        Teacher teacher = (Teacher) user;
        Course course = getCourseById(courseId);
        course.addTeacher(teacher, lessonType);
        logAction("Teacher assigned to course.");
        saveQuietly();
    }

    public CourseRegistration requestCourseRegistration(Student student, int courseId)
            throws UniversitySystemException {
        Course course = getCourseById(courseId);
        student.requestCourse(course);

        CourseRegistration registration = new CourseRegistration(nextRegistrationId++, student, course);
        registrations.put(Integer.valueOf(registration.getId()), registration);
        logAction("Registration requested.");
        saveQuietly();
        return registration;
    }

    public void approveRegistration(int registrationId) throws UniversitySystemException {
        CourseRegistration registration = getRegistrationById(registrationId);
        registration.approve();
        registration.getStudent().enrollCourse(registration.getCourse());
        registration.getCourse().addStudent(registration.getStudent());
        logAction("Registration approved.");
        saveQuietly();
    }

    public void rejectRegistration(int registrationId) throws UniversitySystemException {
        CourseRegistration registration = getRegistrationById(registrationId);
        registration.reject();
        registration.getStudent().removeRequestedCourse(registration.getCourse());
        logAction("Registration rejected.");
        saveQuietly();
    }

    private CourseRegistration getRegistrationById(int registrationId) throws EntityNotFoundException {
        CourseRegistration registration = registrations.get(Integer.valueOf(registrationId));
        if (registration == null) {
            throw new EntityNotFoundException("Registration with id " + registrationId + " was not found.");
        }
        return registration;
    }

    public List<CourseRegistration> getRegistrations() {
        return new ArrayList<CourseRegistration>(registrations.values());
    }

    public List<CourseRegistration> getPendingRegistrations() {
        List<CourseRegistration> pendingRegistrations = new ArrayList<CourseRegistration>();
        for (CourseRegistration registration : registrations.values()) {
            if (registration.getStatus() == RegistrationStatus.PENDING) {
                pendingRegistrations.add(registration);
            }
        }
        return pendingRegistrations;
    }

    public void assignMarks(Teacher teacher, int studentId, int courseId, double firstAttestation,
            double secondAttestation, double finalExam) throws UniversitySystemException {
        User user = getUserById(studentId);
        if (!(user instanceof Student)) {
            throw new UniversitySystemException("Selected user is not a student.");
        }

        Student student = (Student) user;
        Course course = getCourseById(courseId);
        Mark mark = teacher.createMarkSnapshot(firstAttestation, secondAttestation, finalExam, student, course);
        student.getTranscript().addOrUpdateMark(course, mark);
        logAction("Mark assigned.");
        saveQuietly();
    }

    public void rateTeacher(Student student, int teacherId, int rating) throws UniversitySystemException {
        User user = getUserById(teacherId);
        if (!(user instanceof Teacher)) {
            throw new UniversitySystemException("Selected user is not a teacher.");
        }

        Teacher teacher = (Teacher) user;
        teacher.addRating(rating);
        student.storeTeacherRating(teacher, rating);
        logAction("Teacher rated.");
        saveQuietly();
    }

    public String generateGpaReport() {
        // TODO: full implementation in final version
        StringBuilder builder = new StringBuilder();
        builder.append("Simplified GPA report").append(System.lineSeparator());
        for (Student student : getStudents()) {
            builder.append(student.getName()).append(" -> ")
                    .append(String.format("%.2f", student.getTranscript().calculateGpa()))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    public String generateCoursePerformanceReport(int courseId) throws UniversitySystemException {
        Course course = getCourseById(courseId);
        // TODO: full implementation in final version
        return "Simplified course performance report for " + course.getCode();
    }

    public void subscribeUser(User user, String topic) {
        user.subscribeToTopic(topic);
        logAction("Subscription added.");
        saveQuietly();
    }

    public News publishNews(User author, String title, String content, boolean pinned, NewsCategory category,
            String topic) {
        News news = new News(nextNewsId++, title, content, author, pinned, category, topic);
        for (User user : users.values()) {
            if (isSubscribed(user, category, topic)) {
                news.addObserver(user);
            }
        }
        news.publish();
        newsFeed.add(news);
        logAction("News published.");
        saveQuietly();
        return news;
    }

    private boolean isSubscribed(User user, NewsCategory category, String topic) {
        for (String subscription : user.getSubscriptions()) {
            if ("ALL".equalsIgnoreCase(subscription) || category.name().equalsIgnoreCase(subscription)) {
                return true;
            }
            if (topic != null && topic.equalsIgnoreCase(subscription)) {
                return true;
            }
        }
        return false;
    }

    public void addCommentToNews(int newsId, User author, String content) throws UniversitySystemException {
        News news = getNewsById(newsId);
        Comment comment = new Comment(nextCommentId++, author, content);
        news.addComment(comment);
        logAction("Comment added to news.");
        saveQuietly();
    }

    public List<News> getNewsFeed() {
        return new ArrayList<News>(newsFeed);
    }

    public Message sendMessage(User sender, int receiverId, String content) throws UniversitySystemException {
        User receiver = getUserById(receiverId);
        Message message = new Message(nextMessageId++, sender, receiver, content);
        receiver.receiveMessage(message);
        messages.add(message);
        logAction("Message sent.");
        saveQuietly();
        return message;
    }

    public Message sendComplaint(Teacher teacher, String content) throws UniversitySystemException {
        List<Manager> managers = getManagers();
        if (!managers.isEmpty()) {
            return sendMessage(teacher, managers.get(0).getId(), content);
        }

        List<Admin> admins = getAdmins();
        if (!admins.isEmpty()) {
            return sendMessage(teacher, admins.get(0).getId(), content);
        }

        throw new UniversitySystemException("No manager or admin is available.");
    }

    public List<Message> getMessages() {
        return new ArrayList<Message>(messages);
    }

    public TechSupportRequest submitTechSupportRequest(User requester, String description) {
        TechSupportRequest request = new TechSupportRequest(nextRequestId++, requester, description);
        techSupportRequests.add(request);
        logAction("Tech support request created.");
        saveQuietly();
        return request;
    }

    public void processTechSupportRequest(TechSupportSpecialist specialist, int requestId,
            TechSupportRequestStatus status) throws UniversitySystemException {
        TechSupportRequest request = getRequestById(requestId);

        if (status == TechSupportRequestStatus.VIEWED) {
            request.markViewed();
        } else if (status == TechSupportRequestStatus.ACCEPTED) {
            request.accept(specialist);
        } else if (status == TechSupportRequestStatus.REJECTED) {
            request.reject(specialist);
        } else if (status == TechSupportRequestStatus.DONE) {
            request.markDone(specialist);
        }

        specialist.addHandledRequest(request);
        logAction("Tech support request processed.");
        saveQuietly();
    }

    public List<TechSupportRequest> getTechSupportRequests() {
        return new ArrayList<TechSupportRequest>(techSupportRequests);
    }

    public ResearchDecorator createResearchProfile(int userId) throws EntityNotFoundException {
        ResearchDecorator existing = researcherProfiles.get(Integer.valueOf(userId));
        if (existing != null) {
            return existing;
        }

        ResearchDecorator profile = new ResearchDecorator(getUserById(userId));
        researcherProfiles.put(Integer.valueOf(userId), profile);
        logAction("Research profile created.");
        saveQuietly();
        return profile;
    }

    public ResearchDecorator getResearchProfile(User user) {
        return researcherProfiles.get(Integer.valueOf(user.getId()));
    }

    public ResearchDecorator getResearchProfile(int userId) {
        return researcherProfiles.get(Integer.valueOf(userId));
    }

    public boolean isResearcher(User user) {
        return researcherProfiles.containsKey(Integer.valueOf(user.getId()));
    }

    public List<ResearchDecorator> getResearchProfiles() {
        return new ArrayList<ResearchDecorator>(researcherProfiles.values());
    }

    private ResearchDecorator requireResearcher(User user) throws NotResearcherException {
        ResearchDecorator profile = getResearchProfile(user);
        if (profile == null) {
            throw new NotResearcherException(user.getName());
        }
        return profile;
    }

    public void assignSupervisor(int graduateStudentId, int supervisorUserId)
            throws UniversitySystemException {
        User graduateUser = getUserById(graduateStudentId);
        if (!(graduateUser instanceof GraduateStudent)) {
            throw new UniversitySystemException("Selected user is not a graduate student.");
        }

        ResearchDecorator supervisor = requireResearcher(getUserById(supervisorUserId));
        ((GraduateStudent) graduateUser).setSupervisor(supervisor);
        logAction("Supervisor assigned.");
        saveQuietly();
    }

    public ResearchProject createResearchProject(User creator, String topic, LocalDate startDate, LocalDate endDate,
            double budget) throws UniversitySystemException {
        ResearchDecorator researcher = requireResearcher(creator);
        ResearchProject project = new ResearchProject(nextResearchProjectId++, topic, startDate, endDate, budget);
        project.addParticipant(researcher);
        researchProjects.put(Integer.valueOf(project.getId()), project);
        logAction("Research project created.");
        saveQuietly();
        return project;
    }

    public void joinResearchProject(User user, int projectId) throws UniversitySystemException {
        ResearchDecorator researcher = requireResearcher(user);
        ResearchProject project = getResearchProjectById(projectId);
        project.addParticipant(researcher);
        logAction("Research project joined.");
        saveQuietly();
    }

    public ResearchPaper publishResearchPaper(User submittingUser, List<Integer> authorIds, String title, int citations,
            String journal, String pages, LocalDate date, String doi, Integer projectId) throws UniversitySystemException {
        requireResearcher(submittingUser);

        if (authorIds == null || authorIds.isEmpty()) {
            authorIds = singletonAuthorList(submittingUser.getId());
        }

        ResearchPaper paper = new ResearchPaper(nextResearchPaperId++, title, citations, journal, pages, date, doi);
        for (Integer authorId : authorIds) {
            ResearchDecorator researcher = requireResearcher(getUserById(authorId.intValue()));
            paper.addAuthor(researcher);
            researcher.addPaper(paper);
        }

        researchPapers.put(Integer.valueOf(paper.getId()), paper);

        if (projectId != null && researchProjects.containsKey(projectId)) {
            researchProjects.get(projectId).addPaper(paper);
        }

        publishNews(submittingUser, "New Research Paper", paper.getCitation(CitationFormat.PLAIN_TEXT), true,
                NewsCategory.RESEARCH, journal);
        logAction("Research paper published.");
        saveQuietly();
        return paper;
    }

    public List<ResearchProject> getResearchProjects() {
        return new ArrayList<ResearchProject>(researchProjects.values());
    }

    public Collection<ResearchPaper> getResearchPapers() {
        return new ArrayList<ResearchPaper>(researchPapers.values());
    }

    public List<String> getLogs() {
        return new ArrayList<String>(logs);
    }

    private void logAction(String action) {
        logs.add(LocalDateTime.now() + " - " + action);
    }

    private void seedDemoData() {
        if (!users.isEmpty()) {
            return;
        }

        Admin admin = new Admin(nextUserId++, "Admin User", "admin@university.kz", "admin123", "Administration", 1);
        Manager manager = new Manager(nextUserId++, "Manager User", "manager@university.kz", "manager123",
                "Academic Affairs", "Academic Office");
        Teacher teacher = new Teacher(nextUserId++, "Teacher User", "teacher@university.kz", "teacher123",
                "Computer Science", TeacherPosition.LECTOR);
        Student student = new Student(nextUserId++, "Student User", "student@university.kz", "student123",
                "Software Engineering");
        GraduateStudent graduateStudent = new GraduateStudent(nextUserId++, "Graduate User", "grad@university.kz",
                "grad123", "Sample Thesis");
        TechSupportSpecialist support = new TechSupportSpecialist(nextUserId++, "Support User",
                "support@university.kz", "support123", "IT Support");

        users.put(Integer.valueOf(admin.getId()), admin);
        users.put(Integer.valueOf(manager.getId()), manager);
        users.put(Integer.valueOf(teacher.getId()), teacher);
        users.put(Integer.valueOf(student.getId()), student);
        users.put(Integer.valueOf(graduateStudent.getId()), graduateStudent);
        users.put(Integer.valueOf(support.getId()), support);

        Course course = new Course(nextCourseId++, "CS201", "Object-Oriented Programming", 5,
                "Simplified course for demonstration.", 30);
        courses.put(Integer.valueOf(course.getId()), course);

        course.addTeacher(teacher, LessonType.LECTURE);
        course.addLesson(new Lesson(nextLessonId++, "Introduction to OOP", LessonType.LECTURE, teacher,
                LocalDateTime.now().plusDays(1), "A-101"));

        try {
            ResearchDecorator teacherResearcher = createResearchProfile(teacher.getId());
            ResearchProject project = new ResearchProject(nextResearchProjectId++, "Demo Research Project",
                    LocalDate.now(), LocalDate.now().plusMonths(6), 100000.0);
            project.addParticipant(teacherResearcher);
            researchProjects.put(Integer.valueOf(project.getId()), project);
        } catch (EntityNotFoundException e) {
            System.out.println("Simplified version: demo research profile was not created.");
        }

        publishNews(admin, "Welcome", "Simplified stub version of the project.", true, NewsCategory.GENERAL, "GENERAL");
    }

    private List<Integer> singletonAuthorList(int userId) {
        List<Integer> authorIds = new ArrayList<Integer>();
        authorIds.add(Integer.valueOf(userId));
        return authorIds;
    }
}
