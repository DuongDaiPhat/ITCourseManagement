package backend.controller.register;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;

public interface IRegisterController {
	public void BackToLogin(ActionEvent e) throws IOException;
	
	public void SignIn(ActionEvent e) throws IOException, SQLException;
}
