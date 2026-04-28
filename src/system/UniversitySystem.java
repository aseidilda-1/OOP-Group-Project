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
import java.util.Collections;
import java.util.Comparator;
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
import exceptions.CourseCapacityException;
import exceptions.CreditLimitExceededException;
import exceptions.EntityNotFoundException;
import exceptions.LowHIndexException;
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
            } catch (IOException e) {
                System.err.println("Could not load saved system state. Starting with demo data.");
            } catch (ClassNotFoundException e) {
                System.err.println("Saved system state is incompatible. Starting with demo data.");
            }
        }

        UniversitySystem system = new UniversitySystem();
        system.seedDemoData();
        system.saveQuietly();
        return system;
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
            System.err.println("Failed to save system state: " + e.getMessage());
        }
    }

    public User authenticate(String username, String password) throws AuthenticationException {
        for (User user : users.values()) {
            if (matchesLogin(user, username) && user.checkPassword(password)) {
                logAction("User logged in: " + user.getEmail());
                saveQuietly();
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
        for (News item : newsFeed) {
            if (item.getId() == newsId) {
                return item;
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
        logAction("Lesson added to course " + course.getCode() + ": " + lesson);
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
        logAction("Teacher " + teacher.getName() + " assigned to " + course.getCode() + " as " + lessonType);
        saveQuietly();
    }

    public CourseRegistration requestCourseRegistration(Student student, int courseId)
            throws UniversitySystemException {
        Course course = getCourseById(courseId);
        if (student.getEnrolledCourses().contains(course)) {
            throw new UniversitySystemException("Student is already enrolled in this course.");
        }
        if (student.getRequestedCourses().contains(course)) {
            throw new UniversitySystemException("Student has already requested this course.");
        }

        student.requestCourse(course);
        CourseRegistration registration = new CourseRegistration(nextRegistrationId++, student, course);
        registrations.put(Integer.valueOf(registration.getId()), registration);
        logAction("Course registration requested: " + registration);
        saveQuietly();
        return registration;
    }

    public void approveRegistration(int registrationId) throws UniversitySystemException {
        CourseRegistration registration = getRegistrationById(registrationId);
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new UniversitySystemException("Registration is not pending anymore.");
        }

        try {
            registration.getCourse().addStudent(registration.getStudent());
        } catch (CourseCapacityException e) {
            registration.getStudent().removeRequestedCourse(registration.getCourse());
            throw e;
        }

        registration.getStudent().enrollCourse(registration.getCourse());
        registration.approve();
        logAction("Registration approved: " + registration);
        saveQuietly();
    }

    public void rejectRegistration(int registrationId) throws UniversitySystemException {
        CourseRegistration registration = getRegistrationById(registrationId);
        if (registration.getStatus() != RegistrationStatus.PENDING) {
            throw new UniversitySystemException("Registration is not pending anymore.");
        }
        registration.getStudent().removeRequestedCourse(registration.getCourse());
        registration.reject();
        logAction("Registration rejected: " + registration);
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
        Course course = getCourseById(courseId);
        if (!teacher.teachesCourse(course)) {
            throw new UniversitySystemException("Teacher is not assigned to this course.");
        }

        User user = getUserById(studentId);
        if (!(user instanceof Student)) {
            throw new UniversitySystemException("Selected user is not a student.");
        }
        Student student = (Student) user;
        if (!student.getEnrolledCourses().contains(course)) {
            throw new UniversitySystemException("Student is not enrolled in the selected course.");
        }
        if (firstAttestation < 0 || secondAttestation < 0 || finalExam < 0
                || firstAttestation + secondAttestation + finalExam > 100) {
            throw new UniversitySystemException("Marks must be non-negative and sum up to at most 100.");
        }

        Mark mark = teacher.createMarkSnapshot(firstAttestation, secondAttestation, finalExam, student, course);
        student.getTranscript().addOrUpdateMark(course, mark);
        logAction("Mark assigned by " + teacher.getName() + " to " + student.getName() + " for " + course.getCode()
                + ": " + mark.calculateTotal());
        saveQuietly();
    }

    public void rateTeacher(Student student, int teacherId, int rating) throws UniversitySystemException {
        if (rating < 1 || rating > 5) {
            throw new UniversitySystemException("Teacher rating must be between 1 and 5.");
        }

        User user = getUserById(teacherId);
        if (!(user instanceof Teacher)) {
            throw new UniversitySystemException("Selected user is not a teacher.");
        }

        Teacher teacher = (Teacher) user;
        if (student.hasRatedTeacher(teacher)) {
            throw new UniversitySystemException("This student has already rated the selected teacher.");
        }

        student.storeTeacherRating(teacher, rating);
        teacher.addRating(rating);
        logAction("Teacher rated: " + teacher.getName() + " received " + rating + " from " + student.getName());
        saveQuietly();
    }

    public String generateGpaReport() {
        List<Student> students = getStudents();
        Collections.sort(students, new Comparator<Student>() {
            @Override
            public int compare(Student first, Student second) {
                return Double.compare(second.getTranscript().calculateGpa(), first.getTranscript().calculateGpa());
            }
        });

        StringBuilder builder = new StringBuilder();
        builder.append("GPA Report").append(System.lineSeparator());
        for (Student student : students) {
            builder.append(student.getId()).append(" - ").append(student.getName()).append(": GPA ")
                    .append(String.format("%.2f", student.getTranscript().calculateGpa())).append(System.lineSeparator());
        }
        return builder.toString();
    }

    public String generateCoursePerformanceReport(int courseId) throws UniversitySystemException {
        Course course = getCourseById(courseId);
        double totalScore = 0.0;
        int gradedStudents = 0;
        int passedStudents = 0;

        for (Student student : course.getStudents()) {
            Mark mark = student.getTranscript().getMarkForCourse(course);
            if (mark != null) {
                gradedStudents++;
                totalScore += mark.calculateTotal();
                if (mark.isPassing()) {
                    passedStudents++;
                }
            }
        }

        double average = gradedStudents == 0 ? 0.0 : totalScore / gradedStudents;
        double passRate = gradedStudents == 0 ? 0.0 : (passedStudents * 100.0) / gradedStudents;

        return "Course Performance Report for " + course.getCode() + " - " + course.getName()
                + System.lineSeparator() + "Graded students: " + gradedStudents + System.lineSeparator()
                + "Average score: " + String.format("%.2f", average) + System.lineSeparator() + "Pass rate: "
                + String.format("%.2f", passRate) + "%";
    }

    public void subscribeUser(User user, String topic) {
        user.subscribeToTopic(topic);
        logAction(user.getName() + " subscribed to topic: " + topic);
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
        logAction("News published: " + news);
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
        logAction("Comment added to news #" + newsId + " by " + author.getName());
        saveQuietly();
    }

    public List<News> getNewsFeed() {
        List<News> orderedNews = new ArrayList<News>(newsFeed);
        Collections.sort(orderedNews, new Comparator<News>() {
            @Override
            public int compare(News first, News second) {
                if (first.isPinned() != second.isPinned()) {
                    return Boolean.compare(second.isPinned(), first.isPinned());
                }
                return second.getPublishedAt().compareTo(first.getPublishedAt());
            }
        });
        return orderedNews;
    }

    public Message sendMessage(User sender, int receiverId, String content) throws UniversitySystemException {
        User receiver = getUserById(receiverId);
        Message message = new Message(nextMessageId++, sender, receiver, content);
        receiver.receiveMessage(message);
        messages.add(message);
        logAction("Message sent from " + sender.getName() + " to " + receiver.getName());
        saveQuietly();
        return message;
    }

    public Message sendComplaint(Teacher teacher, String content) throws UniversitySystemException {
        List<Manager> managers = getManagers();
        if (!managers.isEmpty()) {
            return sendMessage(teacher, managers.get(0).getId(), "[Complaint] " + content);
        }
        List<Admin> admins = getAdmins();
        if (!admins.isEmpty()) {
            return sendMessage(teacher, admins.get(0).getId(), "[Complaint] " + content);
        }
        throw new UniversitySystemException("No manager or admin is available to receive the complaint.");
    }

    public List<Message> getMessages() {
        return messages;
    }

    public TechSupportRequest submitTechSupportRequest(User requester, String description) {
        TechSupportRequest request = new TechSupportRequest(nextRequestId++, requester, description);
        techSupportRequests.add(request);
        logAction("Tech support request submitted by " + requester.getName() + ": #" + request.getId());
        saveQuietly();
        return request;
    }

    public void processTechSupportRequest(TechSupportSpecialist specialist, int requestId,
            TechSupportRequestStatus status) throws UniversitySystemException {
        TechSupportRequest request = getRequestById(requestId);
        switch (status) {
            case VIEWED:
                request.markViewed();
                break;
            case ACCEPTED:
                request.accept(specialist);
                specialist.addHandledRequest(request);
                break;
            case REJECTED:
                request.reject(specialist);
                specialist.addHandledRequest(request);
                break;
            case DONE:
                request.markDone(specialist);
                specialist.addHandledRequest(request);
                break;
            case NEW:
            default:
                throw new UniversitySystemException("NEW is not a processing action.");
        }
        logAction("Tech support request #" + requestId + " moved to " + status + " by " + specialist.getName());
        saveQuietly();
    }

    public List<TechSupportRequest> getTechSupportRequests() {
        return techSupportRequests;
    }

    public ResearchDecorator createResearchProfile(int userId) throws EntityNotFoundException {
        ResearchDecorator profile = researcherProfiles.get(Integer.valueOf(userId));
        if (profile != null) {
            return profile;
        }
        User user = getUserById(userId);
        profile = new ResearchDecorator(user);
        researcherProfiles.put(Integer.valueOf(userId), profile);
        logAction("Researcher role granted to user: " + user.getName());
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
            throws UniversitySystemException, LowHIndexException {
        User graduateUser = getUserById(graduateStudentId);
        if (!(graduateUser instanceof GraduateStudent)) {
            throw new UniversitySystemException("Selected student is not a graduate student.");
        }
        GraduateStudent graduateStudent = (GraduateStudent) graduateUser;

        User supervisorUser = getUserById(supervisorUserId);
        ResearchDecorator supervisor = requireResearcher(supervisorUser);
        graduateStudent.setSupervisor(supervisor);
        logAction("Supervisor assigned: " + supervisor.getResearcherName() + " -> " + graduateStudent.getName());
        saveQuietly();
    }

    public ResearchProject createResearchProject(User creator, String topic, LocalDate startDate, LocalDate endDate,
            double budget) throws NotResearcherException {
        ResearchDecorator researcher = requireResearcher(creator);
        ResearchProject project = new ResearchProject(nextResearchProjectId++, topic, startDate, endDate, budget);
        project.addParticipant(researcher);
        researchProjects.put(Integer.valueOf(project.getId()), project);
        logAction("Research project created: " + project);
        saveQuietly();
        return project;
    }

    public void joinResearchProject(User user, int projectId) throws UniversitySystemException {
        ResearchDecorator researcher = requireResearcher(user);
        ResearchProject project = getResearchProjectById(projectId);
        project.addParticipant(researcher);
        logAction(user.getName() + " joined research project #" + projectId);
        saveQuietly();
    }

    public ResearchPaper publishResearchPaper(User submittingUser, List<Integer> authorIds, String title, int citations,
            String journal, String pages, LocalDate date, String doi, Integer projectId) throws UniversitySystemException {
        requireResearcher(submittingUser);
        if (authorIds == null || authorIds.isEmpty()) {
            authorIds = new ArrayList<Integer>();
            authorIds.add(Integer.valueOf(submittingUser.getId()));
        }

        ResearchPaper paper = new ResearchPaper(nextResearchPaperId++, title, citations, journal, pages, date, doi);
        for (Integer authorId : authorIds) {
            User author = getUserById(authorId.intValue());
            ResearchDecorator authorProfile = requireResearcher(author);
            paper.addAuthor(authorProfile);
            authorProfile.addPaper(paper);
        }

        researchPapers.put(Integer.valueOf(paper.getId()), paper);
        if (projectId != null) {
            ResearchProject project = getResearchProjectById(projectId.intValue());
            project.addPaper(paper);
            for (Integer authorId : authorIds) {
                User author = getUserById(authorId.intValue());
                ResearchDecorator authorProfile = requireResearcher(author);
                if (!project.getParticipants().contains(authorProfile)) {
                    project.addParticipant(authorProfile);
                }
            }
        }

        publishNews(submittingUser, "New Research Paper: " + title,
                "Journal: " + journal + System.lineSeparator() + "Citation: "
                        + paper.getCitation(CitationFormat.PLAIN_TEXT),
                true, NewsCategory.RESEARCH, journal);
        logAction("Research paper published: " + paper);
        saveQuietly();
        return paper;
    }

    public List<ResearchProject> getResearchProjects() {
        return new ArrayList<ResearchProject>(researchProjects.values());
    }

    public Collection<ResearchPaper> getResearchPapers() {
        return researchPapers.values();
    }

    public List<String> getLogs() {
        return logs;
    }

    private void logAction(String action) {
        logs.add(LocalDateTime.now() + " - " + action);
    }

    private void seedDemoData() {
        Admin admin = new Admin(nextUserId++, "Aruzhan Admin", "admin@research-university.kz", "admin123",
                "Administration", 5);
        Manager manager = new Manager(nextUserId++, "Marat Manager", "manager@research-university.kz", "manager123",
                "Academic Affairs", "Academic Operations");
        Teacher teacher = new Teacher(nextUserId++, "Dana Teacher", "teacher@research-university.kz", "teacher123",
                "Computer Science", TeacherPosition.PROFESSOR);
        Teacher practiceTeacher = new Teacher(nextUserId++, "Timur Instructor", "instructor@research-university.kz",
                "teacher123", "Computer Science", TeacherPosition.TUTOR);
        Student student = new Student(nextUserId++, "Aigerim Student", "student@research-university.kz",
                "student123", "Software Engineering");
        GraduateStudent graduateStudent = new GraduateStudent(nextUserId++, "Nursultan Grad",
                "grad@research-university.kz", "grad123", "Explainable AI in Education");
        TechSupportSpecialist specialist = new TechSupportSpecialist(nextUserId++, "Ainur Support",
                "support@research-university.kz", "support123", "IT Support");

        addUser(admin);
        addUser(manager);
        addUser(teacher);
        addUser(practiceTeacher);
        addUser(student);
        addUser(graduateStudent);
        addUser(specialist);

        Course oop = createCourse("CS201", "Object-Oriented Programming", 5,
                "Core OOP concepts, inheritance, polymorphism, and design patterns.", 30);
        Course researchMethods = createCourse("CS402", "Research Methods", 4,
                "Methods of academic research, citation, and project design.", 20);
        Course dataStructures = createCourse("CS202", "Data Structures", 5,
                "Linear and nonlinear data structures for software systems.", 25);

        try {
            assignTeacherToCourse(teacher.getId(), oop.getId(), LessonType.LECTURE);
            assignTeacherToCourse(practiceTeacher.getId(), oop.getId(), LessonType.PRACTICE);
            assignTeacherToCourse(teacher.getId(), researchMethods.getId(), LessonType.LECTURE);
            assignTeacherToCourse(practiceTeacher.getId(), dataStructures.getId(), LessonType.PRACTICE);

            addLessonToCourse(oop.getId(), "Inheritance and polymorphism", LessonType.LECTURE, teacher,
                    LocalDateTime.now().plusDays(1), "A-201");
            addLessonToCourse(oop.getId(), "Practice on class hierarchies", LessonType.PRACTICE, practiceTeacher,
                    LocalDateTime.now().plusDays(2), "B-104");
            addLessonToCourse(researchMethods.getId(), "Research design and hypothesis", LessonType.LECTURE, teacher,
                    LocalDateTime.now().plusDays(3), "A-305");

            student.requestCourse(oop);
            CourseRegistration studentRegistration = new CourseRegistration(nextRegistrationId++, student, oop);
            registrations.put(Integer.valueOf(studentRegistration.getId()), studentRegistration);
            approveRegistration(studentRegistration.getId());

            student.requestCourse(dataStructures);
            CourseRegistration secondRegistration = new CourseRegistration(nextRegistrationId++, student, dataStructures);
            registrations.put(Integer.valueOf(secondRegistration.getId()), secondRegistration);
            approveRegistration(secondRegistration.getId());

            graduateStudent.requestCourse(researchMethods);
            CourseRegistration gradRegistration = new CourseRegistration(nextRegistrationId++, graduateStudent,
                    researchMethods);
            registrations.put(Integer.valueOf(gradRegistration.getId()), gradRegistration);
            approveRegistration(gradRegistration.getId());
        } catch (UniversitySystemException e) {
            throw new IllegalStateException("Failed to seed academic demo data.", e);
        }

        try {
            assignMarks(teacher, student.getId(), oop.getId(), 28, 27, 36);
            assignMarks(practiceTeacher, student.getId(), dataStructures.getId(), 25, 24, 32);
            assignMarks(teacher, graduateStudent.getId(), researchMethods.getId(), 29, 28, 35);
        } catch (UniversitySystemException e) {
            throw new IllegalStateException("Failed to seed marks.", e);
        }

        try {
            ResearchDecorator teacherResearcher = createResearchProfile(teacher.getId());
            ResearchProject project = createResearchProject(teacher, "AI for Adaptive Learning", LocalDate.now().minusMonths(6),
                    LocalDate.now().plusMonths(12), 1500000.0);
            publishResearchPaper(teacher, singletonAuthorList(teacher.getId()), "Adaptive Feedback in CS Education", 8,
                    "Journal of Educational Computing", "11-24", LocalDate.now().minusMonths(8), "10.1234/jec.2025.001",
                    project.getId());
            publishResearchPaper(teacher, singletonAuthorList(teacher.getId()), "Measuring Student Engagement with OOP Labs",
                    5, "International Journal of Learning Analytics", "55-68", LocalDate.now().minusMonths(5),
                    "10.1234/ijla.2025.045", project.getId());
            publishResearchPaper(teacher, singletonAuthorList(teacher.getId()), "Automated Assessment for Programming Courses",
                    3, "Journal of Educational Computing", "91-104", LocalDate.now().minusMonths(2),
                    "10.1234/jec.2026.013", project.getId());
            assignSupervisor(graduateStudent.getId(), teacher.getId());
            teacherResearcher.addProject(project);
        } catch (LowHIndexException e) {
            throw new IllegalStateException("Invalid demo supervisor configuration.", e);
        } catch (UniversitySystemException e) {
            throw new IllegalStateException("Failed to seed research demo data.", e);
        }

        subscribeUser(student, "RESEARCH");
        subscribeUser(student, "Journal of Educational Computing");
        subscribeUser(graduateStudent, "RESEARCH");
        subscribeUser(manager, "GENERAL");
        publishNews(manager, "Registration Week Open",
                "Students may submit course registrations this week through the console system.", false,
                NewsCategory.GENERAL, "GENERAL");
        submitTechSupportRequest(student, "Cannot see detailed lesson room information in one of my courses.");
    }

    private List<Integer> singletonAuthorList(int userId) {
        List<Integer> authorIds = new ArrayList<Integer>();
        authorIds.add(Integer.valueOf(userId));
        return authorIds;
    }
}
