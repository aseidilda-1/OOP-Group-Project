package academic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import enums.LessonType;
import exceptions.CourseCapacityException;
import users.Student;
import users.Teacher;

public class Course implements Serializable {
	private final int id;
	private final String code;
	private String name;
	private int credits;
	private String description;
	private final Set<Student> students;
	private final Map<LessonType, Set<Teacher>> teachersByType;
	private final List<Lesson> lessons;
	private int maxStudents;

	public Course(int id, String code, String name, int credits, String description, int maxStudents) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.credits = credits;
		this.description = description;
		this.maxStudents = maxStudents;
		this.students = new LinkedHashSet<Student>();
		this.teachersByType = new EnumMap<LessonType, Set<Teacher>>(LessonType.class);
		this.lessons = new ArrayList<Lesson>();
		for (LessonType lessonType : LessonType.values()) {
			teachersByType.put(lessonType, new LinkedHashSet<Teacher>());
		}
	}

	public int getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public int getCredits() {
		return credits;
	}

	public String getDescription() {
		return description;
	}

	public Set<Student> getStudents() {
		return students;
	}

	public List<Lesson> getLessons() {
		return lessons;
	}

	public Map<LessonType, Set<Teacher>> getTeachersByType() {
		return teachersByType;
	}

	public int getMaxStudents() {
		return maxStudents;
	}

	public void setMaxStudents(int maxStudents) {
		this.maxStudents = maxStudents;
	}

	public void addStudent(Student student) throws CourseCapacityException {
		// TODO: full capacity validation in final version
		// simplified for demonstration
		students.add(student);
	}

	public void removeStudent(Student student) {
		students.remove(student);
	}

	public void addTeacher(Teacher teacher, LessonType lessonType) {
		// TODO: full assignment checks in final version
		teachersByType.get(lessonType).add(teacher);
		teacher.assignCourse(this);
	}

	public void removeTeacher(Teacher teacher) {
		for (Set<Teacher> teachers : teachersByType.values()) {
			teachers.remove(teacher);
		}
		teacher.unassignCourse(this);
	}

	public void addLesson(Lesson lesson) {
		lessons.add(lesson);
	}

	public boolean isOpen() {
		// TODO: full availability logic in final version
		return students.size() < maxStudents;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Course)) {
			return false;
		}
		Course course = (Course) o;
		return id == course.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "Course{id=" + id + ", code='" + code + "', name='" + name + "', credits=" + credits
				+ ", students=" + students.size() + "/" + maxStudents + "}";
	}
}
