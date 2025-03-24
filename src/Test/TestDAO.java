package Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import backend.dao.CourseDAO;
import backend.dao.UsersDAO;
import model.course.*;
import model.user.UserStatus;
import model.user.Users;

//private int courseID;
//private String courseName;
//private Language language;
//private ProgrammingLanguage programmingLanguage;
//private Level level;
//private int userID;
//private String thumbnailURL;
//private float price;
//private String CourseDescription;
//private LocalDateTime createdAt;
//private LocalDateTime updatedAt;

public class TestDAO {
	public static void main(String[] args) throws SQLException {
		//EXAMPLE:
		List<Courses> coursesList = new ArrayList<Courses>();
		coursesList = CourseDAO.getInstance().SelectByCondition("PRICE > 400000");
		for(Courses i : coursesList) {
			i.print();
		}
	}
}
