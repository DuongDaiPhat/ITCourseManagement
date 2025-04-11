package backend.controller.instructorCreatePageController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

import backend.controller.InstructorMainPage.InstructorMainPageController;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import model.course.Category;
import model.course.Courses;
import model.course.Language;
import model.course.Level;
import model.course.ProgrammingLanguage;
import model.user.Session;

public class InstructorCreatePageController {
	@FXML
	private TextField courseName;
	@FXML
	private ChoiceBox<String> programmingLanguage;
	@FXML
	private ChoiceBox<String> language;
	@FXML
	private ChoiceBox<String> category;
	@FXML
	private ChoiceBox<String> level;
	@FXML
	private TextField price;
	@FXML
	private TextArea description;
	@FXML
	private ImageView thumbnail;
	@FXML
	private Label myCourse;
	
	private UserService userService;
	private CourseService courseService;
	private int userID;
	private File selectedImageFile;
	
	private Stage stage;
	private Scene scene;
	
	@FXML
	public void initialize() {
		UnaryOperator<TextFormatter.Change> filter = change -> {
		    String newText = change.getControlNewText();
		    if (newText.matches("\\d*(\\.\\d{0,2})?")) {
		        return change;
		    } else {
		        return null;
		    }
		};
		System.out.println("Formatter set for price!");
		TextFormatter<Double> formatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, filter);
		price.setTextFormatter(formatter);
		
		category.getItems().addAll(getCategoryDisplayNames());
		
		myCourse.setOnMouseClicked(event -> {
			try {
				ReturnToInstructorMainPage();
			} catch (IOException | SQLException e) {
				e.printStackTrace();
			}
		});
		
		userService = new UserService();
		courseService = new CourseService();
		userID = Session.getCurrentUser().getUserID();
	}
	
	public void SelectImage(ActionEvent e) {
		 FileChooser fileChooser = new FileChooser();
	        fileChooser.setTitle("Chọn ảnh");
	        fileChooser.getExtensionFilters().addAll(
	            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
	        );
	        
	        File file = fileChooser.showOpenDialog(null);
	        if (file != null) {
	            selectedImageFile = file;
	            Image image = new Image(file.toURI().toString());
	            thumbnail.setImage(image);
	        }
	}
	
	public void CreateCourse(ActionEvent e) throws IOException, SQLException {
		String str_courseName = courseName.getText().trim();
		String str_price = price.getText().trim();
		String str_description = description.getText().trim();
		String str_programmingLanguage;
		String str_language;
		String str_category;
		String str_level;
		
		if(str_courseName.isEmpty()) {
			System.out.println("Course name is empty");
			return;
		}
		else if(!IsValidCourseName(str_courseName)) {
			System.out.println("Invalid Course name"); 
			return;
		}
		else if(programmingLanguage.getValue() == null) {
			System.out.println("You haven't chose programming language for your course yet");
			return;
		}
		else if(language.getValue() == null) {
			System.out.println("You haven't chose language for your course yet");
			return;
		}
		else if(category.getValue() == null) {
			System.out.println("You haven't chose category for your course yet");
			return;
		}
		else if(level.getValue() == null) {
			System.out.println("You haven't chose level for your course yet");
			return;
		}
		else if(str_price.isEmpty()) {
			System.out.println("Please enter the course's price");
			return;
		}
		else if(str_description.isEmpty()) {
			System.out.println("Please enter the course's description");
			return;
		}
		else if(selectedImageFile == null) {
			System.out.println("You haven't chosen the thumbnail image yet");
			return;
		}
		
		str_programmingLanguage = programmingLanguage.getValue();
		str_language = language.getValue();
		str_level = level.getValue().toUpperCase();
		str_category = category.getValue();
		
		Float f_price = Float.parseFloat(str_price);
		String imagePath = saveImageToLocalDir(selectedImageFile);
		Courses course = new Courses();
		course.setCourseName(str_courseName);
		course.setCourseDescription(str_description);
		course.setProgrammingLanguage(ProgrammingLanguage.valueOf(str_programmingLanguage));
		course.setLanguage(Language.valueOf(str_language));
		course.setLevel(Level.valueOf(str_level));
		course.setCategory(Category.valueOf(str_category.replace(' ', '_')));
		course.setUserID(userID);
		course.setPrice(f_price);
		course.setThumbnailURL(imagePath);
		course.setCreatedAt(LocalDateTime.now());
		course.setUpdatedAt(LocalDateTime.now());
		
		courseService.AddCourse(course);
		this.ReturnToInstructorMainPage();
	}
	
	public void ReturnToMyCourse(ActionEvent e) throws IOException, SQLException {
		this.ReturnToInstructorMainPage();
	}
	private static boolean IsValidCourseName(String name) {
		return name.matches("[\\p{L}\\p{Zs}]+");
	}
	private String saveImageToLocalDir(File sourceFile) throws IOException {
	    String fileName = UUID.randomUUID().toString() + "_" + sourceFile.getName(); 
	    File destDir = new File("user_data/images"); // thư mục lưu ảnh trong project
	    if (!destDir.exists()) destDir.mkdirs(); 

	    Path destPath = Paths.get(destDir.getAbsolutePath(), fileName);
	    Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

	    return "user_data/images/" + fileName; 
	}
	private List<String> getCategoryDisplayNames() {
	    List<String> displayNames = new ArrayList<>();
	    for (Category cat : Category.values()) {
	        displayNames.add(cat.name().replace('_', ' '));
	    }
	    return displayNames;
	}
	
	private void ReturnToInstructorMainPage() throws IOException, SQLException {
		FXMLLoader Loader = new FXMLLoader(getClass().getResource("/frontend/view/instructorMainPage/instructorMainPage.fxml"));
		Parent root = Loader.load();
		InstructorMainPageController controller = Loader.getController();
		controller.initialize();
		controller.loadUser(Session.getCurrentUser().getUserID());
		
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		stage =  (Stage) courseName.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth())/2);
		stage.setY((rec.getHeight() - stage.getHeight())/2);
		stage.show();
	}

}
