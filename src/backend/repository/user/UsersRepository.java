package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.user.UserStatus;
import model.user.Users;

public class UsersRepository implements RepositoryInterface<Users>, IUserRepository {
	private Connection con = DatabaseConnection.getConnection();

	public static UsersRepository getInstance() {
		return new UsersRepository();
	}

	@Override
	public int Insert(Users t) throws SQLException {
		int result = 0;

		// Câu lệnh SQL bao gồm cột SALT
		String sql = "INSERT INTO USERS(ROLEID, USERFIRSTNAME, USERLASTNAME, USERNAME, PASSWORD, SALT, PHONENUMBER, EMAIL, DESCRIPTION, STATUS, CREATEDAT) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?);";

		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, String.valueOf(t.getRoleID()));
			ps.setString(2, t.getUserFirstName());
			ps.setString(3, t.getUserLastName());
			ps.setString(4, t.getUserName());
			ps.setString(5, t.getPassword()); // Mật khẩu đã mã hóa
			ps.setString(6, t.getSalt()); // Salt
			ps.setString(7, t.getPhoneNumber());
			ps.setString(8, t.getEmail());
			ps.setString(9, t.getDescription());
			ps.setString(10, t.getStatus().toString());
			ps.setString(11, t.getCreatedAt().toString());

			result = ps.executeUpdate();
			System.out.println(result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
		return result;
	}

	@Override
	public int Update(Users t) throws SQLException {
		String sql = "UPDATE USERS SET " + " ROLEID = ?," + " USERFIRSTNAME = ?," + " USERLASTNAME = ?,"
				+ " USERNAME = ?," + " PHONENUMBER = ?," + " EMAIL = ?," + " DESCRIPTION = ?," + " STATUS = ?,"
				+ " CREATEDAT = ?" + " WHERE USERID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, String.valueOf(t.getRoleID()));
			ps.setString(2, t.getUserFirstName());
			ps.setString(3, t.getUserLastName());
			ps.setString(4, t.getUserName());
			ps.setString(5, t.getPhoneNumber());
			ps.setString(6, t.getEmail());
			ps.setString(7, t.getDescription());
			ps.setString(8, t.getStatus().toString());
			ps.setString(9, t.getCreatedAt().toString());
			ps.setString(10, String.valueOf(t.getUserID()));
			int result = ps.executeUpdate();
			System.out.println("Update executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
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
		Users user = new Users();
		String sql = "SELECT * FROM USERS WHERE USERID = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				user.setUserFirstName(rs.getString("USERFIRSTNAME"));
				user.setUserLastName(rs.getString("USERLASTNAME"));
				user.setUserID(rs.getInt("USERID"));
				user.setUserName(rs.getString("USERNAME"));
				user.setEmail(rs.getString("EMAIL"));
				user.setPhoneNumber(rs.getString("PHONENUMBER"));
				user.setPassword(rs.getString("PASSWORD"));
				user.setSalt(rs.getString("SALT")); // Lấy salt
				user.setDescription(rs.getString("DESCRIPTION"));
				user.setCreatedAt(rs.getDate("CREATEDAT").toLocalDate());
				user.setStatus(UserStatus.valueOf(rs.getString("STATUS")));
				user.setRoleID(rs.getInt("ROLEID"));
			}
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				password = rs.getString("PASSWORD");
			}
			return password != null ? password : "";
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String GetUserSaltByName(String username) throws SQLException {
		String salt = "";
		String sql = "SELECT SALT FROM USERS WHERE USERNAME = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				salt = rs.getString("SALT");
			}
			return salt != null ? salt : "";
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean isExistUser(String username) throws SQLException {
		String sql = "SELECT USERNAME FROM USERS WHERE USERNAME = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String str_username = rs.getString("USERNAME");
				if (!str_username.isEmpty()) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Users GetUserByUsername(String username) throws SQLException {
		Users user = new Users();
		String sql = "SELECT * FROM USERS WHERE USERNAME = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				user.setUserFirstName(rs.getString("USERFIRSTNAME"));
				user.setUserLastName(rs.getString("USERLASTNAME"));
				user.setUserID(rs.getInt("USERID"));
				user.setUserName(rs.getString("USERNAME"));
				user.setEmail(rs.getString("EMAIL"));
				user.setPhoneNumber(rs.getString("PHONENUMBER"));
				user.setPassword(rs.getString("PASSWORD"));
				user.setSalt(rs.getString("SALT")); // Lấy salt
				user.setDescription(rs.getString("DESCRIPTION"));
				user.setCreatedAt(rs.getDate("CREATEDAT").toLocalDate());
				user.setStatus(UserStatus.valueOf(rs.getString("STATUS")));
				user.setRoleID(rs.getInt("ROLEID"));
			}
			return user;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public boolean isExistEmail(String email) throws SQLException {
		String sql = "SELECT EMAIL FROM USERS WHERE EMAIL = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String str_email = rs.getString("EMAIL");
				if (!str_email.isEmpty()) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean isExistPhonenumber(String phonenumber) throws SQLException {
		String sql = "SELECT PHONENUMBER FROM USERS WHERE PHONENUMBER = ?";
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, phonenumber);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String str_phonenumber = rs.getString("PHONENUMBER");
				if (!str_phonenumber.isEmpty()) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}