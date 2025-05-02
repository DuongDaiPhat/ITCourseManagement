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
import backend.controller.scene.SceneManager;
import backend.repository.DatabaseConnection;
import backend.service.course.CourseService;

public class InstructorAddLectureController implements IInstructorAddLectureController, IOnChildRemovedListener {

    @FXML private ScrollPane mainScrollPane;
    @FXML private VBox mainContainer;
    @FXML private HBox addLectureBar;
    @FXML private Label addLectureArrow;
    @FXML private VBox createLectureForm;
    @FXML private VBox lecturesContainer;
    @FXML private Label goBack;
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
    List<LectureItemController> lectureControllers = new ArrayList<>();
    
    private Stage stage;
    private Scene scene;
    
    
    @FXML
    public void initialize() throws SQLException {
    	goBack.setOnMouseClicked(event->{
    		SceneManager.goBack();
    	});
    	courseLabel.setText(CourseSession.getCurrentCourse().getCourseName());
        loadExistingLectures();
        videoUrl.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.trim().isEmpty()) {
                Platform.runLater(() -> loadVideo(newText));
            }
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
					LectureItemController controller = loader.getController();
					controller.setLecture(lecture);
		            controller.setOnChildRemovedListener(() -> {
		                // Khi con bị xóa, reload lại danh sách
		            	try {
							loadExistingLectures();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            });
					Separator sp = new Separator();

					lectureControllers.add(controller);
					lecturesContainer.getChildren().add(sp);
					lecturesContainer.getChildren().add(lectureItem);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
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
                mediaPlayer.stop();      
                mediaPlayer.dispose();  
                mediaPlayer = null;
            }

            String source;
            if (url.startsWith("http") || url.startsWith("file:/")) {
                source = url;
            } else {
                // Convert local file to URI
                source = new File(url).toURI().toString();
            }
            Platform.runLater(() -> {
                try {
                    Media media = new Media(source);
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaPlayer.setOnReady(() -> {
                        Duration d = media.getDuration();
                        int minutes = (int) Math.ceil(d.toMinutes());
                        duration.setText(String.valueOf(minutes)); // set duration
                    });
                } catch (Exception e) {
                    System.err.println("Lỗi khi tạo MediaPlayer: " + e.getMessage());
                    e.printStackTrace();
                }
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
        short durationVal = (short) Math.ceil(Short.parseShort(duration.getText().trim()));
        String description = lectureDescription.getText().trim();
        
        Lecture lecture = new Lecture();
        lecture.setCourseID(courseId);
        lecture.setDuration(durationVal);
        lecture.setLectureDescription(description);
        lecture.setVideoURL(url);
        lecture.setLectureName(name);
        
        courseService.addLecture(lecture);
        this.ToAddLecturePage();;
    }
    private void ToAddLecturePage() throws IOException {
    	this.dispose();
    	SceneManager.switchScene("Add Lecture", "/frontend/view/instructorCreatePage/instructorAddLecturePage.fxml");
	}
    
    private void ToCreateCoursePage() throws IOException {
    	this.dispose();
    	SceneManager.switchScene("Create Course", "/frontend/view/instructorCreatePage/instructorCreatePage.fxml");
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
    	this.dispose();
		SceneManager.switchScene("Instructor Main Page", "/frontend/view/instructorMainPage/instructorMainPage.fxml");
	}
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void dispose() {
    	for(LectureItemController l : lectureControllers) {
    		l.dispose();
    	}
    }

	@Override
	public void onChildRemoved() {
		// TODO Auto-generated method stub
		
	}
    
    
}