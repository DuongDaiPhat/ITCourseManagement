package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.user.Users;

public class UsersRepository implements RepositoryInterface<Users>, IUserRepository {
	private Connection con = DatabaseConnection.getConnection();
	public static UsersRepository getInstance() {
		return new UsersRepository();
	}

	@Override
	public int Insert(Users t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO USERS(ROLEID, USERFIRSTNAME, USERLASTNAME, USERNAME, PASSWORD, PHONENUMBER, EMAIL, DESCRIPTION, STATUS, CREATEDAT)"
				+ "VALUES (?,?,?,?,?,?,?,?,?,?);";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, t.getRoleID() + "");
			ps.setString(2, t.getUserFirstName());
			ps.setString(3, t.getUserLastName());
			ps.setString(4, t.getUserName());
			ps.setString(5, t.getPassword());
			ps.setString(6, t.getPhoneNumber());
			ps.setString(7, t.getEmail());
			ps.setString(8, t.getDescription());
			ps.setString(9, t.getStatus() + "");
			ps.setString(10, t.getCreatedAt() + "");
			result = ps.executeUpdate();
			System.out.println(result + " row(s) afffected");
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
	public Users SelectByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Users> SelectByCondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String GetUserPasswordByName(String username) throws SQLException {
		String password = "";
		String sql = "SELECT PASSWORD FROM USERS WHERE USERNAME = ?";
		try(PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				password = rs.getString("PASSWORD");
			}
			if(!password.isEmpty()) {
				return password;
			}
			else {
				return "";
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return password;
	}

	@Override
	public boolean isExistUser(String username) throws SQLException {
		String sql = "SELECT USERNAME FROM USERS WHERE USERNAME = ?";
		try(PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String str_username = rs.getString("USERNAME");
				if(!str_username.isEmpty()) {
					return true;
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}