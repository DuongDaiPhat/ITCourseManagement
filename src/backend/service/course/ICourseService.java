package backend.service.course;

import java.sql.SQLException;
import java.util.ArrayList;

import model.course.Courses;
import model.lecture.Lecture;

public interface ICourseService {
	public Courses GetCourseByID(int id) throws SQLException;
	public ArrayList<Courses> GetCourseByUserID(int id) throws SQLException;

	public void AddCourse(Courses course) throws SQLException;
	
	public int deleteCourse(int courseId) throws SQLException;
	
	public void updateCourse(Courses course) throws SQLException;
	
	public void addLecture(Lecture lecture) throws SQLException;
	
	public ArrayList<Lecture> getLectureByCourseID(int CourseID)throws SQLException;
	
	public void DeleteLectureByID(int id) throws SQLException;
	
	public void DeleteLecture(Lecture lecture) throws SQLException;
	
	public void ApproveByID(int id, boolean status) throws SQLException;
	
	public void PublishByID(int id, boolean status) throws SQLException;
	
	public void UpdateLecture(Lecture lecture) throws SQLException;
	
}
