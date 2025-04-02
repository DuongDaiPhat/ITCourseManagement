package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.user.UserCertificate;

public class UserCertificateRepository implements RepositoryInterface<UserCertificate> {

    public static UserCertificateRepository getInstance() {
        return new UserCertificateRepository();
    }

    @Override
    public int Insert(UserCertificate t) throws SQLException {
        int result = 0;
        String sql = "INSERT INTO USERCERTIFICATE (CERTIFICATEID, USERID, ISSUEDATE) VALUES (?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getCertificateID());
            ps.setInt(2, t.getUserID());
            ps.setString(3, t.getIssueDate().toString());

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
    public int Update(UserCertificate t) throws SQLException {
        int result = 0;
        result += Delete(t);
        result += Insert(t);
        return result;
    }

    @Override
    public int Delete(UserCertificate t) throws SQLException {
        int result = 0;
        String sql = "DELETE FROM USERCERTIFICATE WHERE CERTIFICATEID = ? AND USERID = ?";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, t.getCertificateID());
            ps.setInt(2, t.getUserID());

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
    public ArrayList<UserCertificate> SelectAll() throws SQLException {
        ArrayList<UserCertificate> userCertificateList = new ArrayList<>();
        String sql = "SELECT * FROM USERCERTIFICATE";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserCertificate userCertificate = new UserCertificate();
                userCertificate.setCertificateID(rs.getInt("CERTIFICATEID"));
                userCertificate.setUserID(rs.getInt("USERID"));
                userCertificate.setIssueDate(LocalDate.parse(rs.getString("ISSUEDATE")));
                userCertificateList.add(userCertificate);
            }
            System.out.println(userCertificateList.size() + " row(s) found");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return userCertificateList;
    }

    @Override
    public UserCertificate SelectByID(int id) throws SQLException {
        UserCertificate userCertificate = null;
        String sql = "SELECT * FROM USERCERTIFICATE WHERE CERTIFICATEID = ? OR USERID = ? LIMIT 1";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userCertificate = new UserCertificate();
                userCertificate.setCertificateID(rs.getInt("CERTIFICATEID"));
                userCertificate.setUserID(rs.getInt("USERID"));
                userCertificate.setIssueDate(LocalDate.parse(rs.getString("ISSUEDATE")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return userCertificate;
    }

    @Override
    public ArrayList<UserCertificate> SelectByCondition(String condition) throws SQLException {
        ArrayList<UserCertificate> userCertificateList = new ArrayList<>();
        String sql = "SELECT * FROM USERCERTIFICATE WHERE " + condition;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserCertificate userCertificate = new UserCertificate();
                userCertificate.setCertificateID(rs.getInt("CERTIFICATEID"));
                userCertificate.setUserID(rs.getInt("USERID"));
                userCertificate.setIssueDate(LocalDate.parse(rs.getString("ISSUEDATE")));
                userCertificateList.add(userCertificate);
            }
            System.out.println(userCertificateList.size() + " row(s) found");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return userCertificateList;
    }

    public ArrayList<UserCertificate> SelectByCertificateID(int certificateID) throws SQLException {
        ArrayList<UserCertificate> userCertificateList = new ArrayList<>();
        String sql = "SELECT * FROM USERCERTIFICATE WHERE CERTIFICATEID = ?";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, certificateID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserCertificate userCertificate = new UserCertificate();
                userCertificate.setCertificateID(rs.getInt("CERTIFICATEID"));
                userCertificate.setUserID(rs.getInt("USERID"));
                userCertificate.setIssueDate(LocalDate.parse(rs.getString("ISSUEDATE")));
                userCertificateList.add(userCertificate);
            }
            System.out.println(userCertificateList.size() + " row(s) found for certificateID: " + certificateID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return userCertificateList;
    }

    public ArrayList<UserCertificate> SelectByUserID(int userID) throws SQLException {
        ArrayList<UserCertificate> userCertificateList = new ArrayList<>();
        String sql = "SELECT * FROM USERCERTIFICATE WHERE USERID = ?";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserCertificate userCertificate = new UserCertificate();
                userCertificate.setCertificateID(rs.getInt("CERTIFICATEID"));
                userCertificate.setUserID(rs.getInt("USERID"));
                userCertificate.setIssueDate(LocalDate.parse(rs.getString("ISSUEDATE")));
                userCertificateList.add(userCertificate);
            }
            System.out.println(userCertificateList.size() + " row(s) found for userID: " + userID);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return userCertificateList;
    }

    public UserCertificate SelectByCompositeID(int certificateID, int userID) throws SQLException {
        UserCertificate userCertificate = null;
        String sql = "SELECT * FROM USERCERTIFICATE WHERE CERTIFICATEID = ? AND USERID = ?";
        try (Connection con = DatabaseConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, certificateID);
            ps.setInt(2, userID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userCertificate = new UserCertificate();
                userCertificate.setCertificateID(rs.getInt("CERTIFICATEID"));
                userCertificate.setUserID(rs.getInt("USERID"));
                userCertificate.setIssueDate(LocalDate.parse(rs.getString("ISSUEDATE")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
        return userCertificate;
    }
}