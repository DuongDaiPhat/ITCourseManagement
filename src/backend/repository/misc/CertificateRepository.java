package backend.repository.misc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.misc.Certificate;

public class CertificateRepository implements RepositoryInterface<Certificate> {

	public static CertificateRepository getInstance() {
		return new CertificateRepository();
	}

	@Override
	public int Insert(Certificate t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO CERTIFICATE(CERTIFICATENAME, COURSEID) VALUES (?, ?)";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

			ps.setString(1, t.getCertificateName());
			ps.setInt(2, t.getCourseID());

			result = ps.executeUpdate();

			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					t.setCertificateID(generatedKeys.getInt(1));
				}
			}

			System.out.println("Insert executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public int Update(Certificate t) throws SQLException {
		int result = 0;
		String sql = "UPDATE CERTIFICATE SET CERTIFICATENAME = ?, COURSEID = ? WHERE CERTIFICATEID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, t.getCertificateName());
			ps.setInt(2, t.getCourseID());
			ps.setInt(3, t.getCertificateID());

			result = ps.executeUpdate();
			System.out.println("Update executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public int Delete(Certificate t) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM CERTIFICATE WHERE CERTIFICATEID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, t.getCertificateID());
			result = ps.executeUpdate();
			System.out.println("Delete executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public ArrayList<Certificate> SelectAll() throws SQLException {
		ArrayList<Certificate> certificateList = new ArrayList<>();
		String sql = "SELECT * FROM CERTIFICATE";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Certificate certificate = new Certificate();
				certificate.setCertificateID(rs.getInt("CERTIFICATEID"));
				certificate.setCertificateName(rs.getString("CERTIFICATENAME"));
				certificate.setCourseID(rs.getInt("COURSEID"));
				certificateList.add(certificate);
			}
			System.out.println(certificateList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return certificateList;
	}

	@Override
	public Certificate SelectByID(int id) throws SQLException {
		Certificate certificate = new Certificate();
		String sql = "SELECT * FROM CERTIFICATE WHERE CERTIFICATEID = " + String.valueOf(id);
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				certificate.setCertificateID(rs.getInt("CERTIFICATEID"));
				certificate.setCertificateName(rs.getString("CERTIFICATENAME"));
				certificate.setCourseID(rs.getInt("COURSEID"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return certificate;
	}

	@Override
	public ArrayList<Certificate> SelectByCondition(String condition) throws SQLException {
		ArrayList<Certificate> certificateList = new ArrayList<>();
		String sql = "SELECT * FROM CERTIFICATE WHERE " + condition;
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Certificate certificate = new Certificate();
				certificate.setCertificateID(rs.getInt("CERTIFICATEID"));
				certificate.setCertificateName(rs.getString("CERTIFICATENAME"));
				certificate.setCourseID(rs.getInt("COURSEID"));
				certificateList.add(certificate);
			}
			System.out.println(certificateList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return certificateList;
	}

}