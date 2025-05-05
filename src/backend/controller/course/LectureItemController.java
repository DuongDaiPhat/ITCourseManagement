package backend.controller.course;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import model.course.CourseSession;
import model.lecture.Lecture;
import java.io.File;
import java.sql.SQLException;
import java.util.Optional;
import backend.controller.instructorCreatePageController.IOnChildRemovedListener;
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
    @FXML private HBox lectureTitleContainer;
    @FXML private VBox descriptionSection;
    @FXML private VBox videoContainer;
    @FXML private Button fullscreenButton;
    
    
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isMuted = false;
    private boolean isDescriptionExpanded = true;
    private boolean isFullScreen = false;
    
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

        mediaView.setSmooth(true);
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
                mediaPlayer.setMute(volume == 0);
                updateVolumeButton(volume);
            }
        });
        // Thiết lập progressSlider
        progressSlider.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging && mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
            }
        });
        progressSlider.setOnMousePressed(event -> {
            if (mediaPlayer != null) {
                double value = progressSlider.getValue();
                mediaPlayer.seek(Duration.seconds(value));
            }
        });
    }
    public void setOnChildRemovedListener(IOnChildRemovedListener listener) {
        this.listener = listener;
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
    private String convertToValidMediaUri(String path) {
        try {
            // Handle URL format paths
            if (path.startsWith("http://") || path.startsWith("https://") || 
                path.startsWith("file:/")) {
                return path;
            }
            
            // Handle local file paths
            File file = new File(path);
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist: " + path);
            }
            return file.toURI().toString();
        } catch (Exception e) {
            System.err.println("Error converting path to URI: " + e.getMessage());
            return null;
        }
    }
    private void loadVideo(String videoUrl) {
        try {
        	if (mediaPlayer != null) {
        		mediaPlayer.stop();
        	    mediaPlayer.dispose();
        	    mediaPlayer = null;
        	}
        	String validUri = convertToValidMediaUri(videoUrl);
            if (validUri == null) {
                System.err.println("Invalid media URI");
                return;
            }
            
            final String mediaUri = validUri;
            
            Platform.runLater(() -> {
                try {
                	// Tạo Media object từ URL
                    Media media = new Media(mediaUri);
                    
                    // Tạo MediaPlayker
                    mediaPlayer = new MediaPlayer(media);
                    mediaView.setMediaPlayer(mediaPlayer);
                    changePlaybackRate();
                    
                    // Xử lý sự kiện khi media sẵn sàng
                    mediaPlayer.setOnReady(() -> {
                        Duration totalDuration = mediaPlayer.getTotalDuration();
                        totalDurationLabel.setText(formatDuration(totalDuration));
                        progressSlider.setMax(totalDuration.toSeconds());
                    });
                    mediaPlayer.setOnError(() -> {
                        System.out.println("MediaPlayer error: " + mediaPlayer.getError());
                        mediaPlayer.getError().printStackTrace();
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
                    System.err.println("Exception when creating media: " + e.getMessage());
                    e.printStackTrace();
                }
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
    @FXML
    private void toggleFullscreen() {
//        Stage stage = (Stage) mediaViewContainer.getScene().getWindow();
//        
//        if (!isFullScreen) {
//            // Save original dimensions and position
//            originalWidth = stage.getWidth();
//            originalHeight = stage.getHeight();
//            originalX = stage.getX();
//            originalY = stage.getY();
//            
//            // Get screen dimensions
//            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//            
//            // Enter fullscreen mode
//            stage.setX(screenBounds.getMinX());
//            stage.setY(screenBounds.getMinY());
//            stage.setWidth(screenBounds.getWidth());
//            stage.setHeight(screenBounds.getHeight());
//            
//            // Hide navigation elements for cleaner fullscreen experience
//            lectureTitleContainer.setVisible(false);
//            lectureTitleContainer.setManaged(false);
//            descriptionSection.setVisible(false);
//            descriptionSection.setManaged(false);
//            
//            // Focus on video content
//            videoContainer.setPrefWidth(Screen.getPrimary().getVisualBounds().getWidth());
//            mediaViewContainer.setPrefHeight(Screen.getPrimary().getVisualBounds().getHeight() - 100); // Leave space for controls
//        } else {
//            // Restore original dimensions and position
//            stage.setX(originalX);
//            stage.setY(originalY);
//            stage.setWidth(originalWidth);
//            stage.setHeight(originalHeight);
//            
//            // Restore navigation elements
//            lectureTitleContainer.setVisible(true);
//            lectureTitleContainer.setManaged(true);
//            descriptionSection.setVisible(true);
//            descriptionSection.setManaged(true);
//            
//            // Restore original video container dimensions
//            videoContainer.setPrefWidth(582.0);
//            mediaViewContainer.setPrefHeight(374.0);
//        }
//        
//        // Toggle state and update button icon
//        isFullScreen = !isFullScreen;
//        updateFullscreenButtonIcon();
    }
    
    // Update fullscreen button icon based on current state
//    private void updateFullscreenButtonIcon() {
//        if (isFullScreen) {
//            fullscreenButton.setText("◱"); // Unicode for exit fullscreen
//        } else {
//            fullscreenButton.setText("⛶"); // Unicode for enter fullscreen
//        }
//    }
  
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
        HBox.setHgrow(videoUrlField, Priority.ALWAYS);
        
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