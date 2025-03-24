package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import model.user.Roles;

public class RolesDAO implements DAOInterface<Roles>{
	
	public static RolesDAO getInstance() {
		return new RolesDAO();
	}
	@Override
	public int Insert(Roles t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO ROLE(ROLENAME) VALUES(?);";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			result = ps.executeUpdate();
			System.out.println(result + " row(s) affected");
			DatabaseConnection.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
	    	DatabaseConnection.closeConnection();
	    }
		return result;
	}

	@Override
	public int Update(Roles t) throws SQLException {
		int result = 0;
		String sql = "UPDATE ROLES SET ROLENAME = ? WHERE ROLEID = ?;";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1,t.getRoleName());
			ps.setInt(2, t.getRoleID());
			result = ps.executeUpdate();
			System.out.println(result + " row(s) affected");
			DatabaseConnection.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
	    	DatabaseConnection.closeConnection();
	    }
		return result;
	}

	@Override
	public int Delete(Roles t) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM ROLES WHERE ROLEID = ?;";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, t.getRoleID());
			result = ps.executeUpdate();
			System.out.println(result + " row(s) affected");
			DatabaseConnection.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
	    	DatabaseConnection.closeConnection();
	    }
		return result;
	}

	@Override
	public ArrayList<Roles> SelectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Roles SelectByID(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Roles> SelectByCondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}

}
