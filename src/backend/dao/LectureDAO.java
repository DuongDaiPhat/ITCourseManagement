package backend.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import model.lecture.Lecture;

public class LectureDAO implements DAOInterface<Lecture> {

	public static LectureDAO getInstance() {
		return new LectureDAO();
	}

	@Override
	public int Insert(Lecture t) throws SQLException {
		int result = 0;
		String sql = "INSERT INTO LECTURE(LECTURENAME, COURSEID, VIDEOURL, DURATION, LECTUREDESCRIPTION) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, t.getLectureName());
			ps.setInt(2, t.getCourseID());
			ps.setString(3, t.getVideoURL());
			ps.setShort(4, t.getDuration());
			ps.setString(5, t.getLectureDescription());

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
	public int Update(Lecture t) throws SQLException {
		int result = 0;
		String sql = "UPDATE LECTURE SET LECTURENAME = ?, COURSEID = ?, VIDEOURL = ?, "
				+ "DURATION = ?, LECTUREDESCRIPTION = ? WHERE LECTUREID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, t.getLectureName());
			ps.setInt(2, t.getCourseID());
			ps.setString(3, t.getVideoURL());
			ps.setShort(4, t.getDuration());
			ps.setString(5, t.getLectureDescription());
			ps.setInt(6, t.getLectureID());

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
	public int Delete(Lecture t) throws SQLException {
		int result = 0;
		String sql = "DELETE FROM LECTURE WHERE LECTUREID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, t.getLectureID());
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
	public ArrayList<Lecture> SelectAll() throws SQLException {
		ArrayList<Lecture> lectureList = new ArrayList<>();
		String sql = "SELECT * FROM LECTURE";
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Lecture lecture = new Lecture();
				lecture.setLectureID(rs.getInt("LECTUREID"));
				lecture.setLectureName(rs.getString("LECTURENAME"));
				lecture.setCourseID(rs.getInt("COURSEID"));
				lecture.setVideoURL(rs.getString("VIDEOURL"));
				lecture.setDuration(rs.getShort("DURATION"));
				lecture.setLectureDescription(rs.getString("LECTUREDESCRIPTION"));
				lectureList.add(lecture);
			}
			System.out.println(lectureList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return lectureList;
	}

	@Override
	public Lecture SelectByID(int id) throws SQLException {
		Lecture lecture = new Lecture();
		String sql = "SELECT * FROM LECTURE WHERE LECTUREID = ?";
		try (Connection con = DatabaseConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					lecture.setLectureID(rs.getInt("LECTUREID"));
					lecture.setLectureName(rs.getString("LECTURENAME"));
					lecture.setCourseID(rs.getInt("COURSEID"));
					lecture.setVideoURL(rs.getString("VIDEOURL"));
					lecture.setDuration(rs.getShort("DURATION"));
					lecture.setLectureDescription(rs.getString("LECTUREDESCRIPTION"));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return lecture;
	}

	@Override
	public ArrayList<Lecture> SelectByCondition(String condition) throws SQLException {
		ArrayList<Lecture> lectureList = new ArrayList<>();
		String sql = "SELECT * FROM LECTURE WHERE " + condition;
		try (Connection con = DatabaseConnection.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Lecture lecture = new Lecture();
				lecture.setLectureID(rs.getInt("LECTUREID"));
				lecture.setLectureName(rs.getString("LECTURENAME"));
				lecture.setCourseID(rs.getInt("COURSEID"));
				lecture.setVideoURL(rs.getString("VIDEOURL"));
				lecture.setDuration(rs.getShort("DURATION"));
				lecture.setLectureDescription(rs.getString("LECTUREDESCRIPTION"));
				lectureList.add(lecture);
			}
			System.out.println(lectureList.size() + " row(s) found");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseConnection.closeConnection();
		}
		return lectureList;
	}

}