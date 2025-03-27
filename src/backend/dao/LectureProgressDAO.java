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
		result += Delete(t);
		result += Insert(t);
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
		LectureProgress progress = null;
		String sql = "SELECT * FROM LECTUREPROGRESS WHERE USERID = ? OR LECTUREID = ? LIMIT 1";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			ps.setInt(2, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				progress = new LectureProgress();
				progress.setUserID(rs.getInt("USERID"));
				progress.setLectureID(rs.getInt("LECTUREID"));
				progress.setStatus(LectureStatus.fromString(rs.getString("STATUS")));
			}
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

	public ArrayList<LectureProgress> SelectByUserID(int userID) throws SQLException {
		ArrayList<LectureProgress> progressList = new ArrayList<>();
		String sql = "SELECT * FROM LECTUREPROGRESS WHERE USERID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, userID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				LectureProgress progress = new LectureProgress();
				progress.setUserID(rs.getInt("USERID"));
				progress.setLectureID(rs.getInt("LECTUREID"));
				progress.setStatus(LectureStatus.fromString(rs.getString("STATUS")));
				progressList.add(progress);
			}
			System.out.println(progressList.size() + " row(s) found for userID: " + userID);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return progressList;
	}

	public ArrayList<LectureProgress> SelectByLectureID(int lectureID) throws SQLException {
		ArrayList<LectureProgress> progressList = new ArrayList<>();
		String sql = "SELECT * FROM LECTUREPROGRESS WHERE LECTUREID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, lectureID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				LectureProgress progress = new LectureProgress();
				progress.setUserID(rs.getInt("USERID"));
				progress.setLectureID(rs.getInt("LECTUREID"));
				progress.setStatus(LectureStatus.fromString(rs.getString("STATUS")));
				progressList.add(progress);
			}
			System.out.println(progressList.size() + " row(s) found for lectureID: " + lectureID);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return progressList;
	}

	public LectureProgress SelectByCompositeID(int userID, int lectureID) throws SQLException {
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
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return progress;
	}
}