package backend.controller.InstructorMainPage;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public interface IInstructorMainPageController {
	@FXML
	public void initialize() throws SQLException;
	
	public void loadUser() throws SQLException;
	
	public void CreateCoursePage(ActionEvent e) throws IOException;
}
