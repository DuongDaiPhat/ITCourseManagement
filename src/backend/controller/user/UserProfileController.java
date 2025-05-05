package backend.controller.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;
import model.user.Session;
import model.user.UserStatus;
import model.user.Users;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import backend.controller.scene.SceneManager;
import backend.service.user.UserService;

public class UserProfileController implements Initializable {
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label userIdLabel;
    
    @FXML
    private Label roleLabel;
    
    @FXML
    private Label firstNameLabel;
    
    @FXML
    private Label lastNameLabel;
    
    @FXML
    private Label usernameLabel;
    
    @FXML
    private Label phoneLabel;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private Circle statusIndicator;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Label createdAtLabel;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Button editDescriptionButton;
    
    @FXML
    private Button saveDescriptionButton;
    
    @FXML
    private Button cancelDescriptionButton;
    
    private String originalDescription;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadUserData();
        editDescriptionButton.setOnAction(event -> handleEditDescription());
        saveDescriptionButton.setOnAction(event -> handleSaveDescription());
        cancelDescriptionButton.setOnAction(event -> handleCancelEdit());
        
    }
    public void GoBack(ActionEvent e) {
    	SceneManager.goBack();
    }
    private void loadUserData() {
        // Sử dụng Session.getUser() để lấy thông tin người dùng
        Users user = Session.getCurrentUser();
        
        if (user != null) {
            userIdLabel.setText("#" + String.valueOf(user.getUserID()));
            
            // Hiển thị vai trò dựa vào roleId
            switch (user.getRoleID()) {
                case 1:
                    roleLabel.setText("Instructor");
                    break;
                case 2:
                    roleLabel.setText("Student");
                    break;
                case 3:
                    roleLabel.setText("Admin");
                    break;
                default:
                    roleLabel.setText("Unknown");
                    break;
            }
            
            firstNameLabel.setText(user.getUserFirstName());
            lastNameLabel.setText(user.getUserLastName());
            usernameLabel.setText(user.getUserName());
            phoneLabel.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A");
            emailLabel.setText(user.getEmail());
            
            // Hiển thị trạng thái và cập nhật màu sắc
            UserStatus status = user.getStatus();
            statusLabel.setText(String.valueOf(status));

            
            // Định dạng ngày tạo
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            createdAtLabel.setText(user.getCreatedAt().format(formatter));
            
            // Hiển thị mô tả
            descriptionArea.setText(user.getDescription() != null ? user.getDescription() : "");
        }
    }
    private void handleEditDescription() {
        // Kích hoạt chế độ chỉnh sửa
        descriptionArea.setEditable(true);
        descriptionArea.requestFocus();
        
        // Hiển thị nút Lưu và Hủy, ẩn nút Sửa
        editDescriptionButton.setVisible(false);
        saveDescriptionButton.setVisible(true);
        cancelDescriptionButton.setVisible(true);
    }
 
    private void handleSaveDescription() {
        try {
            // Lấy thông tin người dùng hiện tại
            Users user = Session.getCurrentUser();
            if (user != null) {
                // Cập nhật mô tả mới
                String newDescription = descriptionArea.getText();
                user.setDescription(newDescription);
                
                // Cập nhật mô tả trong database - mẫu code
                boolean updateSuccess = updateUserDescription(user.getUserID(), newDescription);
                
                if (updateSuccess) {
                    // Cập nhật lại giá trị gốc nếu thành công
                    originalDescription = newDescription;
                    
                    // Hiển thị thông báo thành công
                    showAlert(AlertType.INFORMATION, "Complete", "Update description completed");
                    
                    // Trở về chế độ xem
                    exitEditMode();
                } else {
                    // Hiển thị thông báo lỗi
                    showAlert(AlertType.ERROR, "Error", "Can't update description, Try again later");
                    
                    // Khôi phục lại mô tả ban đầu
                    descriptionArea.setText(originalDescription);
                }
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "Errors occured: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleCancelEdit() {
        // Khôi phục mô tả ban đầu
        descriptionArea.setText(originalDescription);
        
        // Trở về chế độ xem
        exitEditMode();
    }
    
    private void exitEditMode() {
        // Vô hiệu hóa chỉnh sửa
        descriptionArea.setEditable(false);
        
        // Hiển thị nút Sửa, ẩn nút Lưu và Hủy
        editDescriptionButton.setVisible(true);
        saveDescriptionButton.setVisible(false);
        cancelDescriptionButton.setVisible(false);
    }
    private boolean updateUserDescription(int userId, String newDescription) {
        Users temp = Session.getCurrentUser();
        temp.setDescription(newDescription);
        UserService sv = new UserService();
        try {
			sv.UpdateUser(temp);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
    }
    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}