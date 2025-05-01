package backend.controller.instructorCreatePageController;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public interface IInstructorCreatePageController {
	@FXML
	public void initialize();
	
	public void SelectImage(ActionEvent e);
	
	public void CreateCourse(ActionEvent e) throws IOException, SQLException;
	
	public void ReturnToMyCourse(ActionEvent e) throws IOException, SQLException;
}
