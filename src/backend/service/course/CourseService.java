package backend.service.course;

import backend.repository.course.CourseRepository;
import backend.repository.lecture.LectureRepository;
import model.course.Courses;
import model.lecture.Lecture;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseService implements ICourseService {
    @Override
    public Courses GetCourseByID(int id) throws SQLException {
        Courses course = CourseRepository.getInstance().SelectByID(id);
        if (course == null) {
            System.err.println("Course not found for ID: " + id);
        }
        return course;
    }

    public ArrayList<Courses> GetCourseByUserID(int id) throws SQLException {
        ArrayList<Courses> courses = CourseRepository.getInstance().GetCoursesByUserID(id);
        if (courses == null) {
            System.err.println("No courses found for user ID: " + id);
            return new ArrayList<>();
        }
        return courses;
    }

    public void AddCourse(Courses course) throws SQLException {
        if (course != null) {
            CourseRepository.getInstance().Insert(course);
        } else {
            System.err.println("Cannot add null course");
        }
    }

    public int deleteCourse(int courseId) throws SQLException {
        return CourseRepository.getInstance().DeleteById(courseId);
    }

    public void updateCourse(Courses course) throws SQLException {
        if (course != null) {
            CourseRepository.getInstance().Update(course);
        } else {
            System.err.println("Cannot update null course");
        }
    }

    public void addLecture(Lecture lecture) throws SQLException {
        if (lecture != null) {
            LectureRepository.getInstance().Insert(lecture);
        } else {
            System.err.println("Cannot add null lecture");
        }
    }

    public ArrayList<Lecture> getLectureByCourseID(int CourseID) throws SQLException {
        ArrayList<Lecture> lectures = LectureRepository.getInstance().SelectByCondition("COURSEID = " + String.valueOf(CourseID));
        if (lectures == null) {
            System.err.println("No lectures found for course ID: " + CourseID);
            return new ArrayList<>();
        }
        return lectures;
    }

    public void DeleteLectureByID(int id) throws SQLException {
        LectureRepository.getInstance().DeleteByCondition("LECTUREID = " + String.valueOf(id));
    }

    @Override
    public void ApproveByID(int id, boolean status) throws SQLException {
        CourseRepository.getInstance().ApproveByCourseId(id, status);
    }

    @Override
    public void PublishByID(int id, boolean status) throws SQLException {
        CourseRepository.getInstance().PublishByCourseId(id, status);
    }

    @Override
    public void UpdateLecture(Lecture lecture) throws SQLException {
        if (lecture != null) {
            LectureRepository.getInstance().Update(lecture);
        } else {
            System.err.println("Cannot update null lecture");
        }
    }

    @Override
    public void DeleteLecture(Lecture lecture) throws SQLException {
        if (lecture != null) {
            LectureRepository.getInstance().Delete(lecture);
        } else {
            System.err.println("Cannot delete null lecture");
        }
    }

    public List<Courses> getPendingCourses() throws SQLException {
        ArrayList<Courses> allCourses = CourseRepository.getInstance().SelectAll();
        if (allCourses == null) {
            System.err.println("No courses found for pending courses");
            return new ArrayList<>();
        }
        return allCourses.stream()
                .filter(course -> course != null && !course.isApproved())
                .collect(Collectors.toList());
    }

    public ArrayList<Courses> getAllCourses() throws SQLException {
        ArrayList<Courses> courses = CourseRepository.getInstance().SelectAll();
        if (courses == null) {
            System.err.println("No courses found in getAllCourses");
            return new ArrayList<>();
        }
        return courses;
    }
}