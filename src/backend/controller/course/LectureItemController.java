package backend.controller.course;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import model.lecture.Lecture;

import java.io.File;
import java.util.Arrays;

public class LectureItemController {

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
    
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isMuted = false;
    private boolean isDescriptionExpanded = true;
    
    // Mô hình dữ liệu cho bài giảng
    private Lecture lecture;
    
    @FXML
    public void initialize() {
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
        
        // Gắn MediaView vào container
        DoubleProperty mvw = mediaView.fitWidthProperty();
        DoubleProperty mvh = mediaView.fitHeightProperty();   
        mediaView.fitWidthProperty().bind(mediaViewContainer.widthProperty());
        mediaView.fitHeightProperty().bind(mediaViewContainer.heightProperty());
        mediaView.setPreserveRatio(true);
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
    
    // Phương thức để thiết lập dữ liệu bài giảng
    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
        
        // Cập nhật UI với dữ liệu bài giảng
        lectureTitleLabel.setText(lecture.getLectureName());
        descriptionText.setText(lecture.getLectureDescription());
        
        // Tải video
        loadVideo(lecture.getVideoURL());
    }
    
    private void loadVideo(String videoUrl) {
        try {
            // Giải phóng tài nguyên nếu đã có mediaPlayer trước đó
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
            
            // Tạo Media object từ URL
            File videoFile = new File(videoUrl);
            Media media = new Media(videoFile.toURI().toString());
            
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
    
    // Giải phóng tài nguyên khi controller không được sử dụng nữa
    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
    }
}