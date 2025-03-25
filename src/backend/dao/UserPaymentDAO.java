package backend.dao;

import java.sql.*;
import java.util.ArrayList;
import model.user.UserPayment;
import backend.dao.DatabaseConnection;

public class UserPaymentDAO implements DAOInterface<UserPayment> {
    
    private Connection connection;

    public UserPaymentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public int Insert(UserPayment userPayment) throws SQLException {
        String sql = "INSERT INTO userpayment (paymentID, userID, balance) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userPayment.getPaymentID());
        statement.setInt(2, userPayment.getUserID());
        statement.setFloat(3, userPayment.getBalance());

        return statement.executeUpdate();
    }

    @Override
    public int Update(UserPayment userPayment) throws SQLException {
        String sql = "UPDATE userpayment SET balance = ? WHERE paymentID = ? AND userID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setFloat(1, userPayment.getBalance());
        statement.setInt(2, userPayment.getPaymentID());
        statement.setInt(3, userPayment.getUserID());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(UserPayment userPayment) throws SQLException {
        String sql = "DELETE FROM userpayment WHERE paymentID = ? AND userID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, userPayment.getPaymentID());
        statement.setInt(2, userPayment.getUserID());

        return statement.executeUpdate();
    }

    @Override
    public ArrayList<UserPayment> SelectAll() throws SQLException {
        ArrayList<UserPayment> userPayments = new ArrayList<>();
        String sql = "SELECT * FROM userpayment";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            UserPayment userPayment = new UserPayment(
                resultSet.getInt("paymentID"),
                resultSet.getInt("userID"),
                resultSet.getFloat("balance")
            );
            userPayments.add(userPayment);
        }
        return userPayments;
    }

    @Override
    public UserPayment SelectByID(int id) throws SQLException {
        String sql = "SELECT * FROM userpayment WHERE userID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new UserPayment(
                resultSet.getInt("paymentID"),
                resultSet.getInt("userID"),
                resultSet.getFloat("balance")
            );
        }
        return null;
    }

    @Override
    public ArrayList<UserPayment> SelectByCondition(String condition) throws SQLException {
        ArrayList<UserPayment> userPayments = new ArrayList<>();
        String sql = "SELECT * FROM userpayment WHERE " + condition;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            UserPayment userPayment = new UserPayment(
                resultSet.getInt("paymentID"),
                resultSet.getInt("userID"),
                resultSet.getFloat("balance")
            );
            userPayments.add(userPayment);
        }
        return userPayments;
    }
}
