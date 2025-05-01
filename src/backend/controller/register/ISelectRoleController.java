package backend.controller.register;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import model.user.Users;

public interface ISelectRoleController {
	public void SelectRoleForUser(Users user);
	
	public void Next(ActionEvent e) throws SQLException;
}
