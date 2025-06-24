package backend.service.course;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import backend.repository.course.CourseRepository;
import backend.repository.lecture.LectureRepository;
import model.course.Courses;
import model.lecture.Lecture;

public class CourseService implements ICourseService {
	@Override
	public Courses GetCourseByID(int id) throws SQLException {
		return CourseRepository.getInstance().SelectByID(id);
	}

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
		LectureRepository.getInstance().Update(lecture);

	}

	@Override
	public void DeleteLecture(Lecture lecture) throws SQLException {
		LectureRepository.getInstance().Delete(lecture);
	}

	public List<Courses> getPendingCourses() throws SQLException {
		ArrayList<Courses> allCourses = CourseRepository.getInstance().SelectAll();
		return allCourses.stream().filter(course -> !course.isApproved()).collect(Collectors.toList());
	}

	public ArrayList<Courses> getAllCourses() throws SQLException {
		return CourseRepository.getInstance().SelectAll();
	}

}