package backend.controller.course;

import javafx.fxml.FXML;
import model.lecture.Lecture;

public interface ILectureItemController {
	//Khởi tạo bài giảng
	 @FXML
	 public void initialize();
	 
	 //Thêm dữ liệu bài giảng vào controller
	 public void setLecture(Lecture lecture);
	 
	 //Giải phóng dữ liệu dạng Media
	 public void dispose();
}
