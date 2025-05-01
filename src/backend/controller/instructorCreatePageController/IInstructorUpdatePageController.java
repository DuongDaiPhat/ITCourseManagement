package backend.controller.instructorCreatePageController;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.course.Courses;

public interface IInstructorUpdatePageController {
	@FXML
	public void initialize();
	
	public void setCourseData(Courses course) ;
	
	public void SelectImage(ActionEvent e);
	
	public void UpdateCourse(ActionEvent e) throws IOException, SQLException;
	
	public void ReturnToMyCourse(ActionEvent e) throws IOException, SQLException;
}
