package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.user.Permissions;
import model.user.RolePermissions;
import model.user.Roles;

public class RolePermissionsDAO implements DAOInterface<RolePermissions>{
	public static RolePermissionsDAO getInstance() {
		return new RolePermissionsDAO();
	}
	@Override
	public int Insert(RolePermissions t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO ROLEPERMISSION(ROLEID, PERMISSIONID) VALUES(?,?);";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ps.setInt(1, t.getRoleID());
			ps.setInt(2, t.getPermissionID());
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
	public int Update(RolePermissions t) throws SQLException {
		int result = 0;
		String sql = "UPDATE ROLEPERMISSION SET PERMISSIONID = ? WHERE ROLEID = ?;";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1,t.getPermissionID());
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
	public int Delete(RolePermissions t) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM ROLEPERMISSION WHERE ROLEID = ?;";
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
	public ArrayList<RolePermissions> SelectAll() throws SQLException {
		ArrayList<RolePermissions> RolePermissionList = new ArrayList<RolePermissions>();
		String sql = "SELECT * FROM ROLEPERMISSION";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				RolePermissions rolePermission = new RolePermissions();
				rolePermission.setPermissionID(rs.getInt("PERMISSIONID"));
				rolePermission.setRoleID(rs.getInt("ROLEID"));
				RolePermissionList.add(rolePermission);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return RolePermissionList;
	}

	@Override
	public RolePermissions SelectByID(int id) throws SQLException {
		RolePermissions rolePermission = new RolePermissions();
		String sql = "SELECT * FROM ROLEPERMISSION WHERE ROLEID = " + String.valueOf(id) + ";";
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				rolePermission.setPermissionID(rs.getInt("PERMISSIONID"));
				rolePermission.setRoleID(rs.getInt("ROLEID"));
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return rolePermission;
	}

	@Override
	public ArrayList<RolePermissions> SelectByCondition(String condition) throws SQLException {
		ArrayList<RolePermissions> rolePermissionList = new ArrayList<RolePermissions>();
		String sql = "SELECT * FROM ROLEPERMISSION WHERE " + condition;
		try(Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql)){
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				RolePermissions rolePermission = new RolePermissions();
				rolePermission.setPermissionID(rs.getInt("PERMISSIONID"));
				rolePermission.setRoleID(rs.getInt("ROLEID"));
				rolePermissionList.add(rolePermission);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			DatabaseConnection.closeConnection();
		}
		return rolePermissionList;
	}
}
