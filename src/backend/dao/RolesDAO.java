package backend.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import entity.Roles;

public class RolesDAO implements DAOInterface<Roles>{
	
	public static RolesDAO getInstance() {
		return new RolesDAO();
	}
	@Override
	public int Insert(Roles t) throws SQLException {
		int result = 0;
		try {
			Connection con = DatabaseConnection.getConnection();
			Statement st = con.createStatement();
			String sql = "INSERT INTO ROLES(ROLEID, ROLENAME)" + 
						  "VALUES('"+t.getRoleID()+ "',N'" +t.getRoleName() + "');";
			result = st.executeUpdate(sql);
			System.out.println(sql + " executed");
			System.out.println(result + " row(s) affected");
			DatabaseConnection.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int Update(Roles t) throws SQLException {
		int result = 0;
		try {
			Connection con = DatabaseConnection.getConnection();
			Statement st = con.createStatement();
			String sql = "UPDATE ROLES "
						+ "SET "
						+ "ROLENAME = N'" + t.getRoleName() + "' "
						+ "WHERE ROLEID = '" + t.getRoleID() + "';";
			result = st.executeUpdate(sql);
			System.out.println(sql + " executed");
			System.out.println(result + " row(s) affected");
			DatabaseConnection.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int Delete(Roles t) throws SQLException {
		int result = 0;
		try {
			Connection con = DatabaseConnection.getConnection();
			Statement st = con.createStatement();
			String sql = "DELETE FROM ROLES "
					+ "WHERE ROLEID = '" + t.getRoleID()+  "';";
			result = st.executeUpdate(sql);
			System.out.println(sql + " executed");
			System.out.println(result + " row(s) affected");
			DatabaseConnection.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public ArrayList<Roles> SelectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Roles SelectByID(Roles t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Roles> SelectByCondition(String condition) {
		// TODO Auto-generated method stub
		return null;
	}

}
