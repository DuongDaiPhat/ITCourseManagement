package backend.controller.login;

import java.io.IOException;
import java.sql.SQLException;

import javafx.event.ActionEvent;

public interface ILoginController {
	public void Login(ActionEvent e) throws SQLException, IOException;
	
	public void Register(ActionEvent e) throws SQLException, IOException;
}
