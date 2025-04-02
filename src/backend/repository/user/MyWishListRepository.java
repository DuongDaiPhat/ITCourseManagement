package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.user.MyWishList;

public class MyWishListRepository implements RepositoryInterface<MyWishList> {

    public static MyWishListRepository getInstance() {
        return new MyWishListRepository();
    }

    @Override
    public int Insert(MyWishList t) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO MYWISHLIST(USERID, COURSEID, ADDEDAT) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getUserID());
            ps.setInt(2, t.getCourseID());
            ps.setTimestamp(3, Timestamp.valueOf(t.getAddedAt()));

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
    public int Update(MyWishList t) throws SQLException {
        int result = 0;
        result += Delete(t);
        result += Insert(t);
        return result;
    }

    @Override
    public int Delete(MyWishList t) throws SQLException {
        int result = 0;
        String sql = "DELETE FROM MYWISHLIST WHERE USERID = ? AND COURSEID = ?";
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
    public ArrayList<MyWishList> SelectAll() throws SQLException {
        ArrayList<MyWishList> wishList = new ArrayList<>();
        String sql = "SELECT * FROM MYWISHLIST";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MyWishList item = new MyWishList();
                item.setUserID(rs.getInt("USERID"));
                item.setCourseID(rs.getInt("COURSEID"));
                item.setAddedAt(rs.getTimestamp("ADDEDAT").toLocalDateTime());
                wishList.add(item);
            }
            System.out.println(wishList.size() + " row(s) found");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return wishList;
    }

    @Override
    public MyWishList SelectByID(int id) throws SQLException {
        MyWishList item = null;
        String sql = "SELECT * FROM MYWISHLIST WHERE USERID = ? OR COURSEID = ? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                item = new MyWishList();
                item.setUserID(rs.getInt("USERID"));
                item.setCourseID(rs.getInt("COURSEID"));
                item.setAddedAt(rs.getTimestamp("ADDEDAT").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return item;
    }

    @Override
    public ArrayList<MyWishList> SelectByCondition(String condition) throws SQLException {
        ArrayList<MyWishList> wishList = new ArrayList<>();
        String sql = "SELECT * FROM MYWISHLIST WHERE " + condition;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MyWishList item = new MyWishList();
                item.setUserID(rs.getInt("USERID"));
                item.setCourseID(rs.getInt("COURSEID"));
                item.setAddedAt(rs.getTimestamp("ADDEDAT").toLocalDateTime());
                wishList.add(item);
            }
            System.out.println(wishList.size() + " row(s) found");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return wishList;
    }

    public ArrayList<MyWishList> SelectByUserID(int userID) throws SQLException {
        ArrayList<MyWishList> wishList = new ArrayList<>();
        String sql = "SELECT * FROM MYWISHLIST WHERE USERID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MyWishList item = new MyWishList();
                item.setUserID(rs.getInt("USERID"));
                item.setCourseID(rs.getInt("COURSEID"));
                item.setAddedAt(rs.getTimestamp("ADDEDAT").toLocalDateTime());
                wishList.add(item);
            }
            System.out.println(wishList.size() + " row(s) found for userID: " + userID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return wishList;
    }

    public ArrayList<MyWishList> SelectByCourseID(int courseID) throws SQLException {
        ArrayList<MyWishList> wishList = new ArrayList<>();
        String sql = "SELECT * FROM MYWISHLIST WHERE COURSEID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, courseID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MyWishList item = new MyWishList();
                item.setUserID(rs.getInt("USERID"));
                item.setCourseID(rs.getInt("COURSEID"));
                item.setAddedAt(rs.getTimestamp("ADDEDAT").toLocalDateTime());
                wishList.add(item);
            }
            System.out.println(wishList.size() + " row(s) found for courseID: " + courseID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return wishList;
    }

    public MyWishList SelectByCompositeID(int userID, int courseID) throws SQLException {
        MyWishList item = null;
        String sql = "SELECT * FROM MYWISHLIST WHERE USERID = ? AND COURSEID = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ps.setInt(2, courseID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                item = new MyWishList();
                item.setUserID(rs.getInt("USERID"));
                item.setCourseID(rs.getInt("COURSEID"));
                item.setAddedAt(rs.getTimestamp("ADDEDAT").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return item;
    }
}