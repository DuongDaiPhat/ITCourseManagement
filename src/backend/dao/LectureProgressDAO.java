package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.lecture.LectureProgress;
import model.lecture.LectureStatus;

public class LectureProgressDAO implements DAOInterface<LectureProgress> {

	public static LectureProgressDAO getInstance() {
		return new LectureProgressDAO();
	}

	@Override
	public int Insert(LectureProgress t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO LECTUREPROGRESS(USERID, LECTUREID, STATUS) VALUES (?, ?, ?)";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, t.getUserID());
			ps.setInt(2, t.getLectureID());
			ps.setString(3, t.getStatus().getValue());

			result = ps.executeUpdate();
			System.out.println("Insert executed. " + result + " row(s) affected");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return result;
	}

	@Override
	public int Update(LectureProgress t) throws SQLException {
		int result = 0;
		String sql = "UPDATE LECTUREPROGRESS SET STATUS = ? WHERE USERID = ? AND LECTUREID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, t.getStatus().getValue());
			ps.setInt(2, t.getUserID());
			ps.setInt(3, t.getLectureID());

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
	public int Delete(LectureProgress t) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM LECTUREPROGRESS WHERE USERID = ? AND LECTUREID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, t.getUserID());
			ps.setInt(2, t.getLectureID());

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
	public ArrayList<LectureProgress> SelectAll() throws SQLException {
		ArrayList<LectureProgress> progressList = new ArrayList<>();
		String sql = "SELECT * FROM LECTUREPROGRESS";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				LectureProgress progress = new LectureProgress();
				progress.setUserID(rs.getInt("USERID"));
				progress.setLectureID(rs.getInt("LECTUREID"));
				progress.setStatus(LectureStatus.fromString(rs.getString("STATUS")));
				progressList.add(progress);
			}
			System.out.println(progressList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return progressList;
	}

	@Override
	public LectureProgress SelectByID(int id) throws SQLException {
		throw new UnsupportedOperationException(
				"LectureProgress requires both userID and lectureID. Use SelectByCondition instead.");
	}

	public LectureProgress SelectByIDs(int userID, int lectureID) throws SQLException {
		LectureProgress progress = null;
		String sql = "SELECT * FROM LECTUREPROGRESS WHERE USERID = ? AND LECTUREID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userID);
			ps.setInt(2, lectureID);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				progress = new LectureProgress();
				progress.setUserID(rs.getInt("USERID"));
				progress.setLectureID(rs.getInt("LECTUREID"));
				progress.setStatus(LectureStatus.fromString(rs.getString("STATUS")));
			}
			System.out.println(progress != null ? "Record found" : "No record found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return progress;
	}

	@Override
	public ArrayList<LectureProgress> SelectByCondition(String condition) throws SQLException {
		ArrayList<LectureProgress> progressList = new ArrayList<>();
		String sql = "SELECT * FROM LECTUREPROGRESS WHERE " + condition;
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				LectureProgress progress = new LectureProgress();
				progress.setUserID(rs.getInt("USERID"));
				progress.setLectureID(rs.getInt("LECTUREID"));
				progress.setStatus(LectureStatus.fromString(rs.getString("STATUS")));
				progressList.add(progress);
			}
			System.out.println(progressList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return progressList;
	}

}