package backend.controller.course;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import model.course.Courses;

public interface ICourseItemController {
	//Thêm dữ liệu course vào controller
	public void setCourseData(Courses course);
	
	//Xử lý cập nhật 
	@FXML
	public void handleUpdateCourse(ActionEvent event);
	
	//Thêm bài giảng
	@FXML
	public void AddLecture(ActionEvent event) throws IOException;
	
	//Công khai bài giảng
	public void PublishCourse(ActionEvent event) throws SQLException;
}
