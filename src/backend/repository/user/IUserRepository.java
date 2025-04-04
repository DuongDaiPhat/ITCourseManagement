package backend.repository.user;

import java.sql.SQLException;

public interface IUserRepository {
	public String GetUserPasswordByName(String username)throws SQLException;
	
	public boolean isExistUser(String username) throws SQLException;
}
