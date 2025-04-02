package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.user.CourseStatus;
import model.user.MyLearning;

public class MyLearningRepository implements RepositoryInterface<MyLearning> {

    public static MyLearningRepository getInstance() {
        return new MyLearningRepository();
    }

    @Override
    public int Insert(MyLearning t) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO MYLEARNING (USERID, COURSEID, COURSESTATUS, LASTACCESSEDAT) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getUserID());
            ps.setInt(2, t.getCourseID());
            ps.setString(3, t.getCourseStatus().getValue());
            ps.setString(4, t.getLastAccessedAt().toString());

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
    public int Update(MyLearning t) throws SQLException {
        int result = 0;
        result += Delete(t);
        result += Insert(t);
        return result;
    }

    @Override
    public int Delete(MyLearning t) throws SQLException {
        int result = 0;
        String sql = "DELETE FROM MYLEARNING WHERE USERID = ? AND COURSEID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getUserID());
            ps.setInt(2, t.getCourseID());

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
    public ArrayList<MyLearning> SelectAll() throws SQLException {
        ArrayList<MyLearning> myLearningList = new ArrayList<>();
        String sql = "SELECT * FROM MYLEARNING";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MyLearning myLearning = new MyLearning();
                myLearning.setUserID(rs.getInt("USERID"));
                myLearning.setCourseID(rs.getInt("COURSEID"));
                myLearning.setCourseStatus(CourseStatus.fromString(rs.getString("COURSESTATUS")));
                myLearning.setLastAccessedAt(rs.getTimestamp("LASTACCESSEDAT").toLocalDateTime());
                myLearningList.add(myLearning);
            }
            System.out.println(myLearningList.size() + " row(s) found");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return myLearningList;
    }

    @Override
    public MyLearning SelectByID(int id) throws SQLException {
        MyLearning myLearning = null;
        String sql = "SELECT * FROM MYLEARNING WHERE USERID = ? OR COURSEID = ? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                myLearning = new MyLearning();
                myLearning.setUserID(rs.getInt("USERID"));
                myLearning.setCourseID(rs.getInt("COURSEID"));
                myLearning.setCourseStatus(CourseStatus.fromString(rs.getString("COURSESTATUS")));
                myLearning.setLastAccessedAt(rs.getTimestamp("LASTACCESSEDAT").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return myLearning;
    }

    @Override
    public ArrayList<MyLearning> SelectByCondition(String condition) throws SQLException {
        ArrayList<MyLearning> myLearningList = new ArrayList<>();
        String sql = "SELECT * FROM MYLEARNING WHERE " + condition;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MyLearning myLearning = new MyLearning();
                myLearning.setUserID(rs.getInt("USERID"));
                myLearning.setCourseID(rs.getInt("COURSEID"));
                myLearning.setCourseStatus(CourseStatus.fromString(rs.getString("COURSESTATUS")));
                myLearning.setLastAccessedAt(rs.getTimestamp("LASTACCESSEDAT").toLocalDateTime());
                myLearningList.add(myLearning);
            }
            System.out.println(myLearningList.size() + " row(s) found");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return myLearningList;
    }

    public ArrayList<MyLearning> SelectByUserID(int userID) throws SQLException {
        ArrayList<MyLearning> myLearningList = new ArrayList<>();
        String sql = "SELECT * FROM MYLEARNING WHERE USERID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MyLearning myLearning = new MyLearning();
                myLearning.setUserID(rs.getInt("USERID"));
                myLearning.setCourseID(rs.getInt("COURSEID"));
                myLearning.setCourseStatus(CourseStatus.fromString(rs.getString("COURSESTATUS")));
                myLearning.setLastAccessedAt(rs.getTimestamp("LASTACCESSEDAT").toLocalDateTime());
                myLearningList.add(myLearning);
            }
            System.out.println(myLearningList.size() + " row(s) found for userID: " + userID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return myLearningList;
    }

    public ArrayList<MyLearning> SelectByCourseID(int courseID) throws SQLException {
        ArrayList<MyLearning> myLearningList = new ArrayList<>();
        String sql = "SELECT * FROM MYLEARNING WHERE COURSEID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, courseID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MyLearning myLearning = new MyLearning();
                myLearning.setUserID(rs.getInt("USERID"));
                myLearning.setCourseID(rs.getInt("COURSEID"));
                myLearning.setCourseStatus(CourseStatus.fromString(rs.getString("COURSESTATUS")));
                myLearning.setLastAccessedAt(rs.getTimestamp("LASTACCESSEDAT").toLocalDateTime());
                myLearningList.add(myLearning);
            }
            System.out.println(myLearningList.size() + " row(s) found for courseID: " + courseID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return myLearningList;
    }

    public MyLearning SelectByCompositeID(int userID, int courseID) throws SQLException {
        MyLearning myLearning = null;
        String sql = "SELECT * FROM MYLEARNING WHERE USERID = ? AND COURSEID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ps.setInt(2, courseID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                myLearning = new MyLearning();
                myLearning.setUserID(rs.getInt("USERID"));
                myLearning.setCourseID(rs.getInt("COURSEID"));
                myLearning.setCourseStatus(CourseStatus.fromString(rs.getString("COURSESTATUS")));
                myLearning.setLastAccessedAt(rs.getTimestamp("LASTACCESSEDAT").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return myLearning;
    }
}