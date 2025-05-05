package backend.repository.user;

import java.sql.SQLException;

import model.user.Users;

public interface IUserRepository {
	public String GetUserPasswordByName(String username)throws SQLException;
	
	public boolean isExistUser(String username) throws SQLException;
	
	public Users GetUserByUsername(String username) throws SQLException;
	
	public boolean isExistEmail(String email) throws SQLException;
	
	public boolean isExistPhonenumber(String phonenumber) throws SQLException;
}
