package backend.repository.payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import backend.repository.DatabaseConnection;
import model.payment.UserPayment;

public class UserPaymentRepository {

    public List<UserPayment> getUserPayments(int userId) {
        List<UserPayment> userPayments = new ArrayList<>();
        String query = "SELECT up.*, p.PaymentName FROM UserPayment up " +
                       "JOIN Payments p ON up.PaymentID = p.PaymentID " +
                       "WHERE up.UserID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserPayment userPayment = new UserPayment(
                        rs.getInt("PaymentID"),
                        rs.getInt("UserID"),
                        rs.getFloat("Balance"),
                        rs.getString("PaymentName")
                    );
                    userPayments.add(userPayment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userPayments;
    }

    public boolean addUserPayment(int userId, int paymentId) {
        String query = "INSERT INTO UserPayment (PaymentID, UserID, Balance) VALUES (?, ?, 0)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, paymentId);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasPaymentMethod(int userId, int paymentId) {
        String query = "SELECT COUNT(*) FROM UserPayment WHERE UserID = ? AND PaymentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, paymentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public float getBalance(int userId, int paymentId) {
        String query = "SELECT Balance FROM UserPayment WHERE UserID = ? AND PaymentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, paymentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getFloat("Balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean deposit(int userId, int paymentId, float amount) {
        if (amount <= 0) {
            return false;
        }

        String query = "UPDATE UserPayment SET Balance = Balance + ? WHERE UserID = ? AND PaymentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setFloat(1, amount);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, paymentId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean withdraw(int userId, int paymentId, float amount) {
        if (amount <= 0) {
            return false;
        }

        float currentBalance = getBalance(userId, paymentId);
        if (currentBalance < amount) {
            return false;
        }

        String query = "UPDATE UserPayment SET Balance = Balance - ? WHERE UserID = ? AND PaymentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setFloat(1, amount);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, paymentId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUserPayment(int userId, int paymentId) {
        String query = "DELETE FROM UserPayment WHERE UserID = ? AND PaymentID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, paymentId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
