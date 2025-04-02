package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.user.Permissions;

public class PermissionRepository implements RepositoryInterface<Permissions>{
	@Override
	public int Insert(Permissions t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO PERMISSIONS VALUE(?);";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
			ps.setString(1,t.getPermissionName());
			result = ps.executeUpdate();
			System.out.println(result + " row(s) affected");
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
	public int Update(Permissions t) throws SQLException {
		int result = 0;
		String sql = "UPDATE PERMISSIONS SET"
				+ "PERMISSIONNAME = ?"
				+ "WHERE PERMISSIONID = ?;";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setString(1,t.getPermissionName());
			ps.setInt(2, t.getPermissionID());
			result = ps.executeUpdate();
			System.out.println(result + " row(s) affected");
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
	public int Delete(Permissions t) throws SQLException {
		int rs = 0;
		String sql = "DELETE FROM PERMISSIONS WHERE PERMISSIONID = ?;";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, t.getPermissionID());
			rs = ps.executeUpdate();
			System.out.println(rs + " row(s) affected");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return rs;
	}

	@Override
	public ArrayList<Permissions> SelectAll() throws SQLException {
		ArrayList<Permissions> PermissionList = new ArrayList<Permissions>();
		String sql = "SELECT * FROM PERMISSIONS";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Permissions permission = new Permissions();
				permission.setPermissionID(rs.getInt("PERMISSIONID"));
				permission.setPermissionName(rs.getString("PERMISSIONNAME"));
				PermissionList.add(permission);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return PermissionList;
	}

	@Override
	public Permissions SelectByID(int id) throws SQLException {
		Permissions permission = new Permissions();
		String sql = "SELECT * FROM PERMISSIONS WHERE PERMISSIONID = " + String.valueOf(id) + ";";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				permission.setPermissionID(rs.getInt("PERMISSIONID"));
				permission.setPermissionName(rs.getString("PERMISSIONNAME"));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return permission;
	}

	@Override
	public ArrayList<Permissions> SelectByCondition(String condition) throws SQLException {
		ArrayList<Permissions> PermissionList = new ArrayList<Permissions>();
		String sql = "SELECT * FROM PERMISSIONS WHERE " + condition;
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Permissions permission = new Permissions();
				permission.setPermissionID(rs.getInt("PERMISSIONID"));
				permission.setPermissionName(rs.getString("PERMISSIONNAME"));
				PermissionList.add(permission);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return PermissionList;
	}

}
