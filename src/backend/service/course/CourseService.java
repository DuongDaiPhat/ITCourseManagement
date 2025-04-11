package backend.service.course;

import java.sql.SQLException;
import java.util.ArrayList;

import backend.repository.course.CourseRepository;
import model.course.Courses;

public class CourseService {
	public ArrayList<Courses> GetCourseByUserID(int id) throws SQLException{
		return CourseRepository.getInstance().GetCoursesByUserID(id);
	}
	public void AddCourse(Courses course) throws SQLException {
		CourseRepository.getInstance().Insert(course);
	}
}
