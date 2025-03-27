package backend.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import model.user.Users;

public class UsersDAO implements DAOInterface<Users>{
	public static UsersDAO getInstance() {
		return new UsersDAO();
	}
	@Override
	public int Insert(Users t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO USERS(ROLEID, USERFULLNAME,USERNAME,PASSWORD, PHONENUMBER, EMAIL, DESCRIPTION, STATUS, CREATEDAT)"
				+ "VALUES (?,?,?,?,?,?,?,?,?);";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, t.getRoleID() + "");
			ps.setString(2, t.getUserFullName());
			ps.setString(3, t.getUserName());
			ps.setString(4, t.getPassword());
			ps.setString(5, t.getPhoneNumber());
			ps.setString(6, t.getEmail());
			ps.setString(7, t.getDescription());
			ps.setString(8, t.getStatus() + "");
			ps.setString(9,t.getCreatedAt() + "");
			result = ps.executeUpdate();
			System.out.println(result + " row(s) afffected");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
	    	DatabaseConnection.closeConnection();
	    }
		return result;
	}

	@Override
	public int Update(Users t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int Delete(Users t) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Users> SelectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Users SelectByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Users> SelectByCondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
