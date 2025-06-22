package backend.repository.course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import backend.repository.DatabaseConnection;
import backend.repository.RepositoryInterface;
import model.course.Category;
import model.course.Courses;
import model.course.Language;
import model.course.Level;
import model.course.Technology;

public class CourseRepository implements RepositoryInterface<Courses>, ICourseRepository {

    public static CourseRepository getInstance() {
        return new CourseRepository();
    }

    private synchronized void ensureConnection(Connection con) throws SQLException {
        if (con == null || con.isClosed()) {
            System.out.println("Connection is closed or null, reinitializing...");
            throw new SQLException("Invalid connection, please check DatabaseConnection");
        }
    }

    @Override
    public int Insert(Courses t) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO COURSES(COURSENAME, LANGUAGE, TECHNOLOGY, LEVEL, CATEGORY, USERID, THUMBNAILURL, PRICE, COURSEDESCRIPTION, CREATEDAT, UPDATEDAT, ISAPPROVED) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getCourseName());
            ps.setString(2, t.getLanguage().toString());
            ps.setString(3, t.getTechnology().toString());
            ps.setString(4, t.getLevel().toString());
            ps.setString(5, t.getCategory().toString());
            ps.setString(6, String.valueOf(t.getUserID()));
            ps.setString(7, t.getThumbnailURL());
            ps.setDouble(8, t.getPrice());
            ps.setString(9, t.getCourseDescription());
            ps.setString(10, t.getCreatedAt().toString());
            ps.setString(11, t.getUpdatedAt().toString());
            ps.setBoolean(12, t.isApproved());

            result = ps.executeUpdate();
            System.out.println("Insert executed. " + result + " row(s) affected");
        } catch (SQLException e) {
            System.err.println("Error inserting course: " + e.getMessage());
            throw e;
        }
        return result;
    }

    @Override
    public int Update(Courses t) throws SQLException {
        int result = 0;
        String sql = "UPDATE COURSES SET COURSENAME = ?, LANGUAGE = ?, TECHNOLOGY = ?, LEVEL = ?, CATEGORY = ?, USERID = ?, THUMBNAILURL = ?, PRICE = ?, COURSEDESCRIPTION = ?, UPDATEDAT = ?, ISAPPROVED = ?, is_rejected = ? WHERE COURSEID = ?";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t.getCourseName());
            ps.setString(2, t.getLanguage().toString());
            ps.setString(3, t.getTechnology().toString());
            ps.setString(4, t.getLevel().toString());
            ps.setString(5, t.getCategory().toString());
            ps.setInt(6, t.getUserID());
            ps.setString(7, t.getThumbnailURL());
            ps.setFloat(8, t.getPrice());
            ps.setString(9, t.getCourseDescription());
            ps.setString(10, t.getUpdatedAt().toString());
            ps.setBoolean(11, t.isApproved());
            ps.setBoolean(12, t.isRejected());
            ps.setInt(13, t.getCourseID());

            result = ps.executeUpdate();
            System.out.println("Update executed. " + result + " row(s) affected");
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
            throw e;
        }
        return result;
    }

    @Override
    public int Delete(Courses t) throws SQLException {
        int result = 0;
        String sql = "DELETE FROM COURSES WHERE COURSEID = ?";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, t.getCourseID());
            result = ps.executeUpdate();
            System.out.println("Delete executed. " + result + " row(s) affected");
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
            throw e;
        }
        return result;
    }

    public int DeleteById(int courseId) throws SQLException {
        int result = 0;
        String sql = "DELETE FROM COURSES WHERE COURSEID = ?";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            result = ps.executeUpdate();
            System.out.println("Delete executed. " + result + " row(s) affected");
        } catch (SQLException e) {
            System.err.println("Error deleting course by ID: " + e.getMessage());
            throw e;
        }
        return result;
    }

    @Override
    public ArrayList<Courses> SelectAll() throws SQLException {
        ArrayList<Courses> courseList = new ArrayList<>();
        String sql = "SELECT * FROM COURSES";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Courses course = new Courses();
                course.setCourseID(rs.getInt("COURSEID"));
                course.setCourseName(rs.getString("COURSENAME"));
                course.setLanguage(Language.valueOf(rs.getString("LANGUAGE")));
                course.setTechnology(Technology.valueOf(rs.getString("TECHNOLOGY")));
                course.setLevel(Level.valueOf(rs.getString("LEVEL")));
                course.setCategory(Category.valueOf(rs.getString("CATEGORY")));
                course.setUserID(rs.getInt("USERID"));
                course.setThumbnailURL(rs.getString("THUMBNAILURL"));
                course.setPrice(rs.getFloat("PRICE"));
                course.setCourseDescription(rs.getString("COURSEDESCRIPTION"));
                course.setCreatedAt(rs.getTimestamp("CREATEDAT").toLocalDateTime());
                course.setUpdatedAt(rs.getTimestamp("UPDATEDAT").toLocalDateTime());
                course.setApproved(rs.getBoolean("ISAPPROVED"));
                course.setPublish(rs.getBoolean("ISPUBLISHED"));
                courseList.add(course);
            }
            System.out.println(courseList.size() + " row(s) found");
        } catch (SQLException e) {
            System.err.println("Error selecting all courses: " + e.getMessage());
            throw e;
        }
        return courseList;
    }

    @Override
    public Courses SelectByID(int id) throws SQLException {
        Courses course = null;
        String sql = "SELECT * FROM COURSES WHERE COURSEID = ?";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    course = new Courses();
                    course.setCourseID(rs.getInt("COURSEID"));
                    course.setCourseName(rs.getString("COURSENAME"));
                    course.setLanguage(Language.valueOf(rs.getString("LANGUAGE")));
                    course.setTechnology(Technology.valueOf(rs.getString("TECHNOLOGY")));
                    course.setLevel(Level.valueOf(rs.getString("LEVEL")));
                    course.setCategory(Category.valueOf(rs.getString("CATEGORY")));
                    course.setUserID(rs.getInt("USERID"));
                    course.setThumbnailURL(rs.getString("THUMBNAILURL"));
                    course.setPrice(rs.getFloat("PRICE"));
                    course.setCourseDescription(rs.getString("COURSEDESCRIPTION"));
                    course.setCreatedAt(rs.getTimestamp("CREATEDAT").toLocalDateTime());
                    course.setUpdatedAt(rs.getTimestamp("UPDATEDAT").toLocalDateTime());
                    course.setApproved(rs.getBoolean("ISAPPROVED"));
                    course.setPublish(rs.getBoolean("ISPUBLISHED"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error selecting course by ID: " + e.getMessage());
            throw e;
        }
        return course;
    }

    @Override
    public ArrayList<Courses> SelectByCondition(String condition) throws SQLException {
        ArrayList<Courses> courseList = new ArrayList<>();
        String sql = "SELECT * FROM COURSES WHERE ISAPPROVED = TRUE AND " + condition;
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Courses course = new Courses();
                course.setCourseID(rs.getInt("COURSEID"));
                course.setCourseName(rs.getString("COURSENAME"));
                course.setLanguage(Language.valueOf(rs.getString("LANGUAGE")));
                course.setTechnology(Technology.valueOf(rs.getString("TECHNOLOGY")));
                course.setLevel(Level.valueOf(rs.getString("LEVEL")));
                course.setCategory(Category.valueOf(rs.getString("CATEGORY")));
                course.setUserID(rs.getInt("USERID"));
                course.setThumbnailURL(rs.getString("THUMBNAILURL"));
                course.setPrice(rs.getFloat("PRICE"));
                course.setCourseDescription(rs.getString("COURSEDESCRIPTION"));
                course.setCreatedAt(rs.getTimestamp("CREATEDAT").toLocalDateTime());
                course.setUpdatedAt(rs.getTimestamp("UPDATEDAT").toLocalDateTime());
                course.setApproved(rs.getBoolean("ISAPPROVED"));
                course.setPublish(rs.getBoolean("ISPUBLISHED"));
                courseList.add(course);
            }
            System.out.println(courseList.size() + " row(s) found");
        } catch (SQLException e) {
            System.err.println("Error selecting courses by condition: " + e.getMessage());
            throw e;
        }
        return courseList;
    }

    @Override
    public ArrayList<Courses> GetCoursesByUserID(int id) throws SQLException {
        ArrayList<Courses> courseList = new ArrayList<>();
        String sql = "SELECT * FROM COURSES WHERE USERID = ?";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Courses course = new Courses();
                    course.setCourseID(rs.getInt("COURSEID"));
                    course.setCourseName(rs.getString("COURSENAME"));
                    course.setLanguage(Language.valueOf(rs.getString("LANGUAGE")));
                    course.setTechnology(Technology.valueOf(rs.getString("TECHNOLOGY")));
                    course.setLevel(Level.valueOf(rs.getString("LEVEL")));
                    course.setCategory(Category.valueOf(rs.getString("CATEGORY")));
                    course.setUserID(rs.getInt("USERID"));
                    course.setThumbnailURL(rs.getString("THUMBNAILURL"));
                    course.setPrice(rs.getFloat("PRICE"));
                    course.setCourseDescription(rs.getString("COURSEDESCRIPTION"));
                    course.setCreatedAt(rs.getTimestamp("CREATEDAT").toLocalDateTime());
                    course.setUpdatedAt(rs.getTimestamp("UPDATEDAT").toLocalDateTime());
                    course.setApproved(rs.getBoolean("ISAPPROVED"));
                    course.setPublish(rs.getBoolean("ISPUBLISHED"));
                    courseList.add(course);
                }
                System.out.println(courseList.size() + " row(s) found");
            }
        } catch (SQLException e) {
            System.err.println("Error selecting courses by user ID: " + e.getMessage());
            throw e;
        }
        return courseList;
    }

    @Override
    public void ApproveByCourseId(int id, boolean status) throws SQLException {
        String sql = "UPDATE COURSES SET ISAPPROVED = ? WHERE COURSEID = ?";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            int result = ps.executeUpdate();
            System.out.println("Update executed. " + result + " row(s) affected");
        } catch (SQLException e) {
            System.err.println("Error approving course: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void PublishByCourseId(int id, boolean status) throws SQLException {
        String sql = "UPDATE COURSES SET ISPUBLISHED = ? WHERE COURSEID = ?";
        Connection con = DatabaseConnection.getConnection();
        ensureConnection(con);
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            int result = ps.executeUpdate();
            System.out.println("Update executed. " + result + " row(s) affected");
        } catch (SQLException e) {
            System.err.println("Error publishing course: " + e.getMessage());
            throw e;
        }
    }
}