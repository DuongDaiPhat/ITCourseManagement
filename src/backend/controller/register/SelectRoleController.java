package backend.controller.register;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import model.user.UserStatus;
import model.user.Users;
import backend.repository.user.UsersRepository;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;


public class SelectRoleController implements Initializable{
	private Users user;
	@FXML
	private Label userFullName;
	@FXML
    private CheckBox instructorCheckbox;
    
    @FXML
    private CheckBox studentCheckbox;
    
    @FXML
    private Button nextButton;
    
    // Nếu bạn sử dụng ToggleButton thay thế
    // @FXML
    // private ToggleGroup roleToggleGroup;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Thiết lập nút NEXT ở trạng thái vô hiệu hóa ban đầu
        nextButton.setDisable(true);
        
        // Với CheckBox
        instructorCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
            	studentCheckbox.setSelected(false);
                nextButton.setDisable(false);
            } else if (!studentCheckbox.isSelected()) {
                nextButton.setDisable(true);
            }
        });
        
        studentCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
            	instructorCheckbox.setSelected(false);
                nextButton.setDisable(false);
            } else if (!instructorCheckbox.isSelected()) {
                nextButton.setDisable(true);
            }
        });
    }
    public void SelectRoleForUser(Users user) {
    	this.user = user;
    	userFullName.setText(this.user.getUserFirstName() + " " + this.user.getUserLastName());
    }
    public void Next(ActionEvent e) throws SQLException {
    	if(instructorCheckbox.isSelected()) {
    		this.user.setRoleID(3);
    	}
    	else if(studentCheckbox.isSelected()) {
    		this.user.setRoleID(2);
    	}
    	this.user.setCreatedAt(LocalDate.now());
    	this.user.setStatus(UserStatus.online);
    	this.user.setDescription("No bio yet");
    	UsersRepository.getInstance().Insert(user);
    	System.out.println("Press Button");
    }
}
