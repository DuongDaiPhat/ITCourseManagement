package backend.controller.instructorCreatePageController;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import model.course.CourseSession;
import model.course.Courses;
import model.lecture.Lecture;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Text;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import backend.controller.InstructorMainPage.InstructorMainPageController;
import backend.controller.course.CourseItemController;
import backend.controller.course.LectureItemController;
import backend.repository.DatabaseConnection;
import backend.service.course.CourseService;

public class InstructorAddLectureController {

    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox mainContainer;
    @FXML private HBox addLectureBar;
    @FXML private Label addLectureArrow;
    @FXML private VBox createLectureForm;
    @FXML private VBox lecturesContainer;
    @FXML private TextField lectureName;
    @FXML private TextField videoUrl;
    @FXML private TextField duration;
    @FXML private TextArea lectureDescription;
    @FXML private MediaView mediaView;
    @FXML private Label courseLabel;
    @FXML private Label myCourse;
    @FXML private Label createCourse;

    private MediaPlayer mediaPlayer;
    private boolean isAddLectureFormVisible = false;
    
    private CourseService courseService = new CourseService();
    private List<Lecture> lectures = new ArrayList<>();
    
    private Stage stage;
    private Scene scene;
    
    @FXML
    public void initialize() throws SQLException {
    	courseLabel.setText(CourseSession.getCurrentCourse().getCourseName());
        loadExistingLectures();
        videoUrl.textProperty().addListener((obs, oldText, newText) -> {
            loadVideo(newText);
        });
        myCourse.setOnMouseClicked(event->{
        	try {
        		this.ReturnToInstructorMainPage();
        	} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        });
        createCourse.setOnMouseClicked(event->{
        	try {
        		this.ToCreateCoursePage();
        	}	catch (IOException e) {
				e.printStackTrace();
			}
        });
    }
    
    private void loadExistingLectures() throws SQLException {
        lecturesContainer.getChildren().clear();
        int courseId = CourseSession.getCurrentCourse().getCourseID();
        lectures = courseService.getLectureByCourseID(courseId);
        if (lectures != null && !lectures.isEmpty()) {
			for (Lecture lecture : lectures) {
				try {
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/frontend/view/instructorCreatePage/LectureItem.fxml"));
					VBox lectureItem = loader.load();
					Separator sp = new Separator();

					LectureItemController controller = loader.getController();
					controller.setLecture(lecture);
					lecturesContainer.getChildren().add(sp);
					lecturesContainer.getChildren().add(lectureItem);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
//    private Node findNodeById(Node parent, String id) {
//		if (parent.getId() != null && parent.getId().equals(id)) {
//			return parent;
//		}
//
//		if (parent instanceof javafx.scene.Parent) {
//			for (Node child : ((javafx.scene.Parent) parent).getChildrenUnmodifiable()) {
//				Node result = findNodeById(child, id);
//				if (result != null) {
//					return result;
//				}
//			}
//		}
//		return null;
//	}
    @FXML
    private void toggleAddLectureForm() {
        isAddLectureFormVisible = !isAddLectureFormVisible;
        createLectureForm.setVisible(isAddLectureFormVisible);
        createLectureForm.setManaged(isAddLectureFormVisible);
        addLectureArrow.setText(isAddLectureFormVisible ? "▼" : "▲");
        
        if (isAddLectureFormVisible) {
            clearLectureForm();
        }
    }
    
    @FXML
    private void browseVideo() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn tệp video");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov", "*.wmv")
        );
        
        File selectedFile = fileChooser.showOpenDialog(mainScrollPane.getScene().getWindow());
        if (selectedFile != null) {
            videoUrl.setText(selectedFile.getAbsolutePath());
        }
    }
    private void loadVideo(String url) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.dispose(); // Clear cái cũ tránh leak
            }

            String source;
            if (url.startsWith("http") || url.startsWith("file:/")) {
                source = url;
            } else {
                // Convert local file to URI
                source = new File(url).toURI().toString();
            }

            Media media = new Media(source);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            mediaPlayer.setOnReady(() -> {
                Duration d = media.getDuration();
                int minutes = (int)d.toMinutes();
                duration.setText(String.valueOf(minutes)); // set duration
                mediaPlayer.play(); 
            });

        } catch (Exception e) {
            System.out.println("Error loading video: " + e.getMessage());
        }
    }
    
    @FXML
    private void addLecture() throws SQLException, IOException {
        if (!validateForm()) {
            return;
        }
        
        int courseId = CourseSession.getCurrentCourse().getCourseID();
        String name = lectureName.getText().trim();
        String url = videoUrl.getText().trim();
        short durationVal = Short.parseShort(duration.getText().trim());
        String description = lectureDescription.getText().trim();
        
        Lecture lecture = new Lecture();
        lecture.setCourseID(courseId);
        lecture.setDuration(durationVal);
        lecture.setLectureDescription(description);
        lecture.setVideoURL(url);
        lecture.setLectureName(name);
        
        courseService.addLecture(lecture);
        this.ToCreateCoursePage();
    }
    
    private void ToCreateCoursePage() throws IOException {
		Parent root = FXMLLoader
				.load(getClass().getResource("/frontend/view/instructorCreatePage/instructorCreatePage.fxml"));
		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		stage = (Stage) mainScrollPane.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth()) / 2);
		stage.setY((rec.getHeight() - stage.getHeight()) / 2);
		stage.show();
	}

    private void deleteLecture(int lectureId, VBox lectureCard) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Lecture");
        confirmAlert.setContentText("Are you sure you want to delete this lecture? This action cannot be undone.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
					courseService.DeleteLectureByID(lectureId);
				} catch (SQLException e) {
					e.printStackTrace();
				}
            }
        });
    }
    
    @FXML
    private void cancelAddLecture() {
        clearLectureForm();
        toggleAddLectureForm();
    }
    
    private void clearLectureForm() {
        lectureName.clear();
        videoUrl.clear();
        duration.clear();
        lectureDescription.clear();
    }
    
    private boolean validateForm() {
        if (lectureName.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Lecture name is required");
            return false;
        }
        
        if (videoUrl.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Video URL is required");
            return false;
        }
        
        try {
            if (duration.getText().trim().isEmpty() || Integer.parseInt(duration.getText().trim()) <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Valid duration is required");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Duration must be a number");
            return false;
        }      
        return true;
    }
    
    private void ReturnToInstructorMainPage() throws IOException, SQLException {
		FXMLLoader Loader = new FXMLLoader(
				getClass().getResource("/frontend/view/instructorMainPage/instructorMainPage.fxml"));
		Parent root = Loader.load();
		InstructorMainPageController controller = Loader.getController();
		controller.initialize();

		Rectangle2D rec = Screen.getPrimary().getVisualBounds();
		stage = (Stage) mainContainer.getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.setX((rec.getWidth() - stage.getWidth()) / 2);
		stage.setY((rec.getHeight() - stage.getHeight()) / 2);
		stage.show();
	}
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    
}