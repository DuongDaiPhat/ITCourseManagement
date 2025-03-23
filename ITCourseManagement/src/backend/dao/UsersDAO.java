package backend.dao;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import entity.Users;

public class UsersDAO implements DAOInterface<Users>{
	public static UsersDAO getInstance() {
		return new UsersDAO();
	}
	@Override
	public int Insert(Users t) throws SQLException {
		int result = 0;
		try {
			Connection con = DatabaseConnection.getConnection();
			Statement st = con.createStatement();
			String sql = "INSERT INTO USERS(USERID, ROLEID, USERFULLNAME, USERNAME, PASSWORD, PHONENUMBER, EMAIL, DESCRIPTION, STATUS, CREATEDAT) VALUES"
					+ "('"+t.getUserID()
					+"','"+t.getRoleID()
					+"',N'"+t.getUserFullName()
					+"',N'"+t.getUserName()
					+"','"+t.getPassword()
					+"','"+t.getPhoneNumber()
					+"','"+t.getEmail()
					+"','"+t.getDescription()
					+"','"+t.getStatus()
					+"','"+t.getCreatedAt()
					+"');";
			System.out.println(sql);
			result = st.executeUpdate(sql);
			System.out.println(sql + " executed");
			System.out.println(result + " row(s) affected");
			DatabaseConnection.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
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
	public Users SelectByID(Users t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Users> SelectByCondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
