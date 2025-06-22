package backend.controller.mainPage;

import backend.service.course.CourseService;
import backend.repository.user.MyCartRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.course.Courses;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryController {
	
	@FXML
    private ImageView Cartpage; // Thêm tham chiếu đến ImageView của giỏ hàng


    @FXML private CheckBox Artificial_Intelligence;
    @FXML private CheckBox Business_Analysis;
    @FXML private CheckBox Cloud_Computing;
    @FXML private CheckBox Computer_Architecture;
    @FXML private CheckBox Computer_Networks;
    @FXML private CheckBox Cryptography;
    @FXML private CheckBox Cybersecurity;
    @FXML private CheckBox Data_Science;
    @FXML private CheckBox Data_structures_and_Algorithms;
    @FXML private CheckBox Databases;
    @FXML private CheckBox Deep_Learning;
    @FXML private CheckBox Desktop_Applications;
    @FXML private CheckBox DevOps;
    @FXML private CheckBox Game_Development;
    @FXML private CheckBox Machine_Learning;
    @FXML private CheckBox Mobile_Development;
    @FXML private CheckBox Project_Management;
    @FXML private CheckBox Testing_and_QA;
    @FXML private CheckBox UI_UX;
    @FXML private CheckBox Web_Development;

    @FXML private VBox CourseContainer;
    @FXML private Label pageMyLearning;
    @FXML private Label pageStudent;
    @FXML private TextField searchField;
    @FXML private ImageView searchIcon;

    private List<Courses> allCourses;
    private int currentUserId;

    @FXML
    public void initialize() {
        this.currentUserId = utils.UserSession.getInstance().getCurrentUserId();
        loadAllApprovedCourses();
        setupCategoryFilterListeners();
    }

    private void loadAllApprovedCourses() {
        CourseService courseService = new CourseService();
        MyCartRepository cartRepository = new MyCartRepository();
        try {
            ArrayList<Integer> cartCourseIds = cartRepository.getCourseIdsByUserId(currentUserId);
            allCourses = courseService.getAllCourses().stream()
                    .filter(Courses::isApproved)
                    .filter(course -> !cartCourseIds.contains(course.getCourseID()))
                    .collect(Collectors.toList());
            System.out.println("Loaded " + allCourses.size() + " approved courses for userId=" + currentUserId);
            displayCourses(allCourses);
        } catch (SQLException e) {
            System.err.println("Error loading approved courses: " + e.getMessage());
        }
    }

    private void displayCourses(List<Courses> courses) {
        CourseContainer.getChildren().clear();
        for (Courses course : courses) {
            try {
                Parent courseNode = CourseCategoryController.loadWithData(course, (v) -> loadAllApprovedCourses());
                CourseContainer.getChildren().add(courseNode);
                System.out.println("Added course node: " + course.getCourseName());
            } catch (IOException e) {
                System.err.println("Error loading course node: " + e.getMessage());
            }
        }
    }

    private void setupCategoryFilterListeners() {
        List<CheckBox> checkBoxes = List.of(
                Artificial_Intelligence, Business_Analysis, Cloud_Computing, Computer_Architecture,
                Computer_Networks, Cryptography, Cybersecurity, Data_Science, Data_structures_and_Algorithms,
                Databases, Deep_Learning, Desktop_Applications, DevOps, Game_Development,
                Machine_Learning, Mobile_Development, Project_Management, Testing_and_QA,
                UI_UX, Web_Development
        );

        for (CheckBox cb : checkBoxes) {
            cb.setOnAction(e -> filterCoursesBySelectedCategories());
        }
    }

    private void filterCoursesBySelectedCategories() {
        List<String> selected = new ArrayList<>();
        if (Artificial_Intelligence.isSelected()) selected.add("Artificial_Intelligence");
        if (Business_Analysis.isSelected()) selected.add("Business_Analysis");
        if (Cloud_Computing.isSelected()) selected.add("Cloud_Computing");
        if (Computer_Architecture.isSelected()) selected.add("Computer_Architecture");
        if (Computer_Networks.isSelected()) selected.add("Computer_Networks");
        if (Cryptography.isSelected()) selected.add("Cryptography");
        if (Cybersecurity.isSelected()) selected.add("Cybersecurity");
        if (Data_Science.isSelected()) selected.add("Data_Science");
        if (Data_structures_and_Algorithms.isSelected()) selected.add("Data_structures_and_Algorithms");
        if (Databases.isSelected()) selected.add("Databases");
        if (Deep_Learning.isSelected()) selected.add("Deep_Learning");
        if (Desktop_Applications.isSelected()) selected.add("Desktop_Applications");
        if (DevOps.isSelected()) selected.add("DevOps");
        if (Game_Development.isSelected()) selected.add("Game_Development");
        if (Machine_Learning.isSelected()) selected.add("Machine_Learning");
        if (Mobile_Development.isSelected()) selected.add("Mobile_Development");
        if (Project_Management.isSelected()) selected.add("Project_Management");
        if (Testing_and_QA.isSelected()) selected.add("Testing_and_QA");
        if (UI_UX.isSelected()) selected.add("UI_UX");
        if (Web_Development.isSelected()) selected.add("Web_Development");

        if (selected.isEmpty()) {
            displayCourses(allCourses);
        } else {
            List<Courses> filtered = allCourses.stream()
                    .filter(c -> selected.contains(c.getCategory().toString()))
                    .collect(Collectors.toList());
            displayCourses(filtered);
        }
    }

    @FXML
    void switchToMainPage(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/mainPage.fxml"));
        Stage stage = (Stage) pageStudent.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void switchToMyLearning(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/mylearning.fxml"));
        Stage stage = (Stage) pageMyLearning.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    // Trong MainPageController.java
    @FXML
    void switchToCartPage(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/mainPage/CartPage.fxml"));
        Stage stage = (Stage) Cartpage.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void refreshCourses() {
        loadAllApprovedCourses();
    }
}