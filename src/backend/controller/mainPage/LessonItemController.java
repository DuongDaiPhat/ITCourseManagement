package backend.controller.mainPage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.course.Lesson;

public class LessonItemController {

    @FXML
    private ImageView courseImage;

    @FXML
    private Label courseTitle;

    @FXML
    private Label courseAuthor;

    @FXML
    private Label courseRating;

    @FXML
    private Label coursePrice;

    public void setLesson(Lesson lesson) {
        courseTitle.setText(lesson.getTitle());
        courseAuthor.setText(lesson.getAuthor());
        courseRating.setText(String.format("%.1f/5.0 (%d)", lesson.getRating(), lesson.getTotalRatings()));
        coursePrice.setText("$" + String.format("%.2f", lesson.getPrice()));

        try {
            String imagePath = "/" + lesson.getImagePath(); // thêm dấu /
            Image img = new Image(getClass().getResourceAsStream(imagePath));
            courseImage.setImage(img);
        } catch (Exception e) {
            System.err.println("Image not found: " + lesson.getImagePath());
        }
    }
}
