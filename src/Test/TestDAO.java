package Test;

import entity.Courses;
import entity.Language;
import entity.Level;
import entity.ProgrammingLanguage;

import java.sql.SQLException;
import java.time.LocalDateTime;

import backend.dao.CourseDAO;

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
		Courses c1 = new Courses(1, "Lập trình Java", Language.Vietnamese, ProgrammingLanguage.Java, Level.BEGINNER, 1, "thumbnailURL", 399000f,"Khóa học cơ bản dành cho lập trình Java", LocalDateTime.now(), LocalDateTime.now());
		CourseDAO.getInstance().Insert(c1);
	}
}
