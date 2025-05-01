package backend.controller.course;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.course.CourseSession;
import model.lecture.Lecture;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

import backend.controller.instructorCreatePageController.IOnChildRemovedListener;
import backend.controller.instructorCreatePageController.InstructorAddLectureController;
import backend.service.course.CourseService;

public class LectureItemController implements ILectureItemController{

    @FXML private VBox lectureItemContainer;
    @FXML private Label lectureTitleLabel;
    @FXML private MediaView mediaView;
    @FXML private StackPane mediaViewContainer;
    @FXML private Button playButton;
    @FXML private Slider progressSlider;
    @FXML private Label currentTimeLabel;
    @FXML private Label totalDurationLabel;
    @FXML private Button muteButton;
    @FXML private Slider volumeSlider;
    @FXML private ComboBox<String> playbackRateComboBox;
    @FXML private Label descriptionArrow;
    @FXML private VBox descriptionContent;
    @FXML private TextFlow lectureDescriptionText;
    @FXML private Text descriptionText;
    
    private InstructorAddLectureController parentController;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isMuted = false;
    private boolean isDescriptionExpanded = true;
    private CourseService courseService = new CourseService();
    private IOnChildRemovedListener listener;
    
    // Mô hình dữ liệu cho bài giảng
    private Lecture lecture;
    // Callback interface for lecture operations
    private LectureOperationsCallback lectureCallback;
    
    // Interface for lecture operations callback
    public interface LectureOperationsCallback {
        void onLectureUpdated(Lecture updatedLecture);
        void onLectureDeleted(Lecture lecture);
    }
    
    @FXML
    public void initialize() {
    	// Gắn MediaView vào container
        mediaView.fitWidthProperty().bind(mediaViewContainer.widthProperty());
        mediaView.fitHeightProperty().bind(mediaViewContainer.heightProperty());
        mediaView.setPreserveRatio(true);
        
        // Khởi tạo ComboBox tốc độ phát
        playbackRateComboBox.setItems(FXCollections.observableArrayList(
            "0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "2.0x"
        ));
        playbackRateComboBox.setValue("1.0x");
        playbackRateComboBox.setOnAction(e -> changePlaybackRate());
        
        // Mặc định hiển thị mô tả
        descriptionContent.setVisible(true);
        descriptionContent.setManaged(true);
        descriptionArrow.setText("▼");
        	
        // Thiết lập volumeSlider
        volumeSlider.setValue(100);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                double volume = newValue.doubleValue() / 100.0;
                mediaPlayer.setVolume(volume);
                updateVolumeButton(volume);
            }
        });
        
        // Thiết lập progressSlider
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            }
        });
    }
    public void setOnChildRemovedListener(IOnChildRemovedListener listener) {
        this.listener = listener;
    }
    public void setParentController(InstructorAddLectureController parentController) {
        this.parentController = parentController;
    }
    
    // Phương thức để thiết lập dữ liệu bài giảng
    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
        this.UpdateUI();
    }
    private void UpdateUI() {
    	// Cập nhật UI với dữ liệu bài giảng
        lectureTitleLabel.setText(lecture.getLectureName());
        descriptionText.setText(lecture.getLectureDescription());
        
        // Tải video
        loadVideo(lecture.getVideoURL());
    }
    private void loadVideo(String videoUrl) {
        try {
        	if (mediaPlayer != null) {
        	    mediaPlayer.dispose();
        	}
        	String source;
        	if (videoUrl.startsWith("http") || videoUrl.startsWith("file:/")) {
		       source = videoUrl;
        	} else {
		       // Convert local file to URI
		       source = new File(videoUrl).toURI().toString();
        	}
            // Tạo Media object từ URL
            Media media = new Media(source);
            
            // Tạo MediaPlayer
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            
            // Xử lý sự kiện khi media sẵn sàng
            mediaPlayer.setOnReady(() -> {
                Duration totalDuration = mediaPlayer.getTotalDuration();
                totalDurationLabel.setText(formatDuration(totalDuration));
                progressSlider.setMax(totalDuration.toSeconds());
            });
            
            // Cập nhật current time và progress slider
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!progressSlider.isValueChanging()) {
                    progressSlider.setValue(newValue.toSeconds());
                }
                currentTimeLabel.setText(formatDuration(newValue));
            });
            
            // Xử lý khi video kết thúc
            mediaPlayer.setOnEndOfMedia(() -> {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.pause();
                isPlaying = false;
                playButton.setText("▶");
            });
            mediaPlayer.setOnError(() -> {
                System.out.println("MediaPlayer error: " + mediaPlayer.getError());
            });
            media.setOnError(() -> {
                System.out.println("Media error: " + media.getError());
            });
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tải video: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void togglePlayPause() {
        if (mediaPlayer == null) return;
        
        if (isPlaying) {
            mediaPlayer.pause();
            playButton.setText("▶");
        } else {
            mediaPlayer.play();
            playButton.setText("⏸");
        }
        
        isPlaying = !isPlaying;
    }
    
    @FXML
    private void toggleMute() {
        if (mediaPlayer == null) return;
        
        isMuted = !isMuted;
        mediaPlayer.setMute(isMuted);
        muteButton.setText(isMuted ? "🔇" : "🔊");
    }
    
    private void updateVolumeButton(double volume) {
        if (volume == 0) {
            muteButton.setText("🔇");
        } else if (volume < 0.5) {
            muteButton.setText("🔉");
        } else {
            muteButton.setText("🔊");
        }
    }
    
    private void changePlaybackRate() {
        if (mediaPlayer == null) return;
        
        String rateString = playbackRateComboBox.getValue();
        double rate = Double.parseDouble(rateString.replace("x", ""));
        mediaPlayer.setRate(rate);
    }
    
    @FXML
    private void toggleDescription() {
        isDescriptionExpanded = !isDescriptionExpanded;
        descriptionContent.setVisible(isDescriptionExpanded);
        descriptionContent.setManaged(isDescriptionExpanded);
        descriptionArrow.setText(isDescriptionExpanded ? "▼" : "▲");
    }
    
    private String formatDuration(Duration duration) {
        int seconds = (int) Math.floor(duration.toSeconds() % 60);
        int minutes = (int) Math.floor(duration.toMinutes() % 60);
        int hours = (int) Math.floor(duration.toHours());
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
    @FXML
    private void handleEditLecture() {
        // Create a dialog for editing lecture
        Dialog<Lecture> dialog = new Dialog<>();
        dialog.setTitle("Edit Lecture");
        dialog.setHeaderText("Edit Lecture Details");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField(lecture.getLectureName());
        titleField.setPromptText("Lecture Title");
        
        TextField videoUrlField = new TextField(lecture.getVideoURL());
        videoUrlField.setPromptText("Video URL");  
        
        Button browseButton = new Button("Browse...");
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Video File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mov", "*.wmv", "*.mkv")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                videoUrlField.setText(selectedFile.getAbsolutePath());
            }
        });
        String updatedVideoURL = videoUrlField.getText();
        String source;
        if (updatedVideoURL.startsWith("http") || updatedVideoURL.startsWith("file:/")) {
            source = updatedVideoURL;
        } else {
            // Convert local file to URI
            source = new File(updatedVideoURL).toURI().toString();
        }
        Media media = new Media(source);
        MediaPlayer updatedMediaPlayer = new MediaPlayer(media);
        MediaView updatedMediaView = new MediaView();
        updatedMediaView.setMediaPlayer(updatedMediaPlayer);
 
        HBox videoUrlBox = new HBox(10, videoUrlField, browseButton);
        videoUrlBox.setHgrow(videoUrlField, Priority.ALWAYS);
        
        TextArea descriptionArea = new TextArea(lecture.getLectureDescription());
        descriptionArea.setPromptText("Lecture Description");
        descriptionArea.setPrefRowCount(5);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Video URL:"), 0, 1);
        grid.add(videoUrlBox, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descriptionArea, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        Platform.runLater(() -> titleField.requestFocus());
        
        // Convert the result to lecture object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
            	Lecture updatedLecture = new Lecture();
                updatedLecture.setLectureID(lecture.getLectureID());
                updatedLecture.setLectureName(titleField.getText());
                updatedLecture.setCourseID(CourseSession.getCurrentCourse().getCourseID());
                updatedLecture.setVideoURL(videoUrlField.getText());
                updatedLecture.setLectureDescription(descriptionArea.getText());
                Duration d = media.getDuration();
                int minutes = (int) Math.ceil(d.toMinutes());
                short durationVal = (short) minutes;
                updatedLecture.setDuration(durationVal);
                return updatedLecture;
            }
            return null;
        });
        
        Optional<Lecture> result = dialog.showAndWait();
        
        result.ifPresent(updatedLecture -> {
            try {
				courseService.UpdateLecture(updatedLecture);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
            
            // Update the model
            this.lecture = updatedLecture;
            
            // Update the UI
            UpdateUI();
            
            // Notify listener about the update
            if (lectureCallback != null) {
                lectureCallback.onLectureUpdated(updatedLecture);
            }
        });
    }
    
    @FXML
    private void handleDeleteLecture() {
        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Lecture");
        confirmDialog.setHeaderText("Are you sure you want to delete this lecture?");
        confirmDialog.setContentText("Lecture: " + lecture.getLectureName());
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
				courseService.DeleteLecture(lecture);
				this.dispose();
				if (listener != null) {
		            listener.onChildRemoved();
		        }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // Notify listener about the deletion
            if (lectureCallback != null) {
                lectureCallback.onLectureDeleted(lecture);
            }
        }
    }
    // Giải phóng tài nguyên khi controller không được sử dụng nữa
    public void dispose() {
    	if (mediaPlayer != null) {
    	    mediaPlayer.stop();
    	    mediaPlayer.dispose();
    	    mediaView.setMediaPlayer(null);
    	}
    }
}