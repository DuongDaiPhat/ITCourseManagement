package backend.service.course;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import backend.repository.course.CourseRepository;
import backend.repository.lecture.LectureRepository;
import model.course.Courses;
import model.lecture.Lecture;

public class CourseService {
	public ArrayList<Courses> GetCourseByUserID(int id) throws SQLException {
		return CourseRepository.getInstance().GetCoursesByUserID(id);
	}

	public void AddCourse(Courses course) throws SQLException {
		CourseRepository.getInstance().Insert(course);
	}

	public int deleteCourse(int courseId) throws SQLException {
		return CourseRepository.getInstance().DeleteById(courseId);
	}

	public void updateCourse(Courses course) throws SQLException {
		CourseRepository.getInstance().Update(course);
	}
	public void addLecture(Lecture lecture) throws SQLException {
		LectureRepository.getInstance().Insert(lecture);
	}
	public ArrayList<Lecture> getLectureByCourseID(int CourseID) throws SQLException {
		return LectureRepository.getInstance().SelectByCondition("COURSEID = " + String.valueOf(CourseID));
	}
	public void DeleteLectureByID(int id) throws SQLException {
		LectureRepository.getInstance().DeleteByCondition("LECTUREID = " + String.valueOf(id));
	}
}