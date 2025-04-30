package backend.repository.course;

import java.sql.SQLException;
import java.util.ArrayList;

import model.course.Courses;

public interface ICourseRepository {
	public ArrayList<Courses> GetCoursesByUserID(int id) throws SQLException;
	
	public void ApproveByCourseId(int id, boolean status) throws SQLException;
	
	public void PublishByCourseId(int id, boolean status) throws SQLException;
}
