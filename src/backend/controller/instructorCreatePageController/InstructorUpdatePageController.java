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
import backend.controller.scene.SceneManager;
import backend.service.course.CourseService;
import backend.service.user.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
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
import model.course.Technology;
import model.user.Session;

public class InstructorUpdatePageController implements IInstructorUpdatePageController{
	@FXML
	private TextField courseName;
	@FXML
	private ComboBox<String> technology;
	@FXML
	private ComboBox<String> language;
	@FXML
	private ComboBox<String> category;
	@FXML
	private ComboBox<String> level;
	@FXML
	private TextField price;
	@FXML
	private TextArea description;
	@FXML
	private ImageView thumbnail;
	@FXML
	private Label myCourse;
	@FXML
	private Label pageTitle;

	private UserService userService;
	private CourseService courseService;
	private int userID;
	private File selectedImageFile;
	private Courses currentCourse;
	private String originalImagePath;

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
		TextFormatter<Double> formatter = new TextFormatter<>(new DoubleStringConverter(), 0.0, filter);
		price.setTextFormatter(formatter);

		if (category.getItems().isEmpty()) {
			category.getItems().addAll(getCategoryDisplayNames());
		}

		if (level.getItems().isEmpty()) {
			level.getItems().addAll("Beginner", "Intermediate", "Advanced", "All Level");
		}

		myCourse.setOnMouseClicked(event -> {
			try {
				ReturnToInstructorMainPage();
			} catch (IOException | SQLException e) {
				e.printStackTrace();
			}
		});

		technology.setVisibleRowCount(5);
		technology.setEditable(false);

		language.setVisibleRowCount(5);
		language.setEditable(false);

		category.setVisibleRowCount(5);
		category.setEditable(false);

		level.setEditable(false);
		userService = new UserService();
		courseService = new CourseService();
		userID = Session.getCurrentUser().getUserID();
	}

	public void setCourseData(Courses course) {
		if (course == null) {
			throw new IllegalArgumentException("Course cannot be null");
		}
		this.currentCourse = course;
		this.originalImagePath = course.getThumbnailURL();

		courseName.setText(course.getCourseName());
		technology.setValue(course.getTechnology().toString());
		language.setValue(course.getLanguage().toString());
		category.setValue(course.getCategory().toString().replace('_', ' '));

		String levelDisplay = course.getLevel().toString();
		level.setValue(convertLevelForDisplay(levelDisplay));

		price.setText(String.valueOf(course.getPrice()));
		description.setText(course.getCourseDescription());

		if (course.getThumbnailURL() != null && !course.getThumbnailURL().isEmpty()) {
			File imageFile = new File(course.getThumbnailURL());
			if (imageFile.exists()) {
				Image image = new Image(imageFile.toURI().toString());
				thumbnail.setImage(image);
			}
		}
	}

	private String convertLevelForDisplay(String level) {
		if ("ALLLEVEL".equals(level)) {
			return "All Level";
		}
		return level;
	}

	private String convertDisplayToLevel(String displayLevel) {
		if ("All Level".equals(displayLevel)) {
			return "ALLLEVEL";
		}
		return displayLevel.toUpperCase();
	}

	public void SelectImage(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Chọn ảnh");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			selectedImageFile = file;
			Image image = new Image(file.toURI().toString());
			thumbnail.setImage(image);
		}
	}

	public void UpdateCourse(ActionEvent e) throws IOException, SQLException {
		String str_courseName = courseName.getText().trim();
		String str_price = price.getText().trim();
		String str_description = description.getText().trim();
		String str_technology;
		String str_language;
		String str_category;
		String str_level;

		if (str_courseName.isEmpty()) {
			System.out.println("Course name is empty");
			return;
		} else if (!IsValidCourseName(str_courseName)) {
			System.out.println("Invalid Course name");
			return;
		} else if (technology.getValue() == null) {
			System.out.println("You haven't chose programming language for your course yet");
			return;
		} else if (language.getValue() == null) {
			System.out.println("You haven't chose language for your course yet");
			return;
		} else if (category.getValue() == null) {
			System.out.println("You haven't chose category for your course yet");
			return;
		} else if (level.getValue() == null) {
			System.out.println("You haven't chose level for your course yet");
			return;
		} else if (str_price.isEmpty()) {
			System.out.println("Please enter the course's price");
			return;
		} else if (str_description.isEmpty()) {
			System.out.println("Please enter the course's description");
			return;
		}

		str_technology = technology.getValue();
		str_language = language.getValue();
		str_level = convertDisplayToLevel(level.getValue()); // Sử dụng hàm chuyển đổi
		str_category = category.getValue();

		Float f_price = Float.parseFloat(str_price);
		String imagePath = originalImagePath;

		if (selectedImageFile != null) {

			if (originalImagePath != null && !originalImagePath.isEmpty()) {
				try {
					File oldImage = new File(originalImagePath);
					if (oldImage.exists()) {
						oldImage.delete();
					}
				} catch (Exception ex) {
					System.err.println("Error deleting old image: " + ex.getMessage());
				}
			}

			imagePath = saveImageToLocalDir(selectedImageFile);
		}

		currentCourse.setCourseName(str_courseName);
		currentCourse.setCourseDescription(str_description);
		currentCourse.setTechnology(Technology.valueOf(str_technology));
		currentCourse.setLanguage(Language.valueOf(str_language));
		currentCourse.setLevel(Level.valueOf(str_level));
		currentCourse.setCategory(Category.valueOf(str_category.replace(' ', '_')));
		currentCourse.setPrice(f_price);
		currentCourse.setThumbnailURL(imagePath);
		currentCourse.setUpdatedAt(LocalDateTime.now());

		courseService.updateCourse(currentCourse);
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
		File destDir = new File("user_data/images");
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

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
		SceneManager.switchScene("My Course", "/frontend/view/instructorMainPage/instructorMainPage.fxml");
	}
}