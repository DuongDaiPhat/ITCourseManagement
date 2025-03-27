package backend.dao;

import java.sql.*;
import java.util.ArrayList;
import model.payment.Payment;

public class PaymentDAO implements DAOInterface<Payment> {
    
    private Connection connection;

    public PaymentDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public int Insert(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (paymentName) VALUES (?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, payment.getPaymentName());

        return statement.executeUpdate();
    }

    @Override
    public int Update(Payment payment) throws SQLException {
        String sql = "UPDATE payment SET paymentName = ? WHERE paymentID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, payment.getPaymentName());
        statement.setInt(2, payment.getPaymentID());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(Payment payment) throws SQLException {
        String sql = "DELETE FROM payment WHERE paymentID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, payment.getPaymentID());

        return statement.executeUpdate();
    }

    @Override
    public ArrayList<Payment> SelectAll() throws SQLException {
        ArrayList<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            Payment payment = new Payment(
                resultSet.getInt("paymentID"),
                resultSet.getString("paymentName")
            );
            payments.add(payment);
        }
        return payments;
    }

    @Override
    public Payment SelectByID(int id) throws SQLException {
        String sql = "SELECT * FROM payment WHERE paymentID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Payment(
                resultSet.getInt("paymentID"),
                resultSet.getString("paymentName")
            );
        }
        return null;
    }

    @Override
    public ArrayList<Payment> SelectByCondition(String condition) throws SQLException {
        ArrayList<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM payment WHERE " + condition;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            Payment payment = new Payment(
                resultSet.getInt("paymentID"),
                resultSet.getString("paymentName")
            );
            payments.add(payment);
        }
        return payments;
    }
}
