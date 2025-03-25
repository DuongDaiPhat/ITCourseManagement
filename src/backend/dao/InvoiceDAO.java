package backend.dao;

import java.sql.*;
import java.util.ArrayList;
import model.invoice.Invoice;
import backend.dao.DatabaseConnection;

public class InvoiceDAO implements DAOInterface<Invoice> {
    
    private Connection connection;

    public InvoiceDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public int Insert(Invoice invoice) throws SQLException {
    	String sql = "INSERT INTO invoice (userID, totalPrice, boughtAt) VALUES (?, ?, ?)";
    	PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    	statement.setInt(1, invoice.getUserID());
    	statement.setFloat(2, invoice.getTotalPrice());
    	statement.setTimestamp(3, Timestamp.valueOf(invoice.getBoughtAt()));
    	statement.executeUpdate();
        return statement.executeUpdate();
    }

    @Override
    public int Update(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoice SET totalPrice = ?, boughtAt = ? WHERE invoiceID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setFloat(1, invoice.getTotalPrice());
        statement.setTimestamp(2, Timestamp.valueOf(invoice.getBoughtAt()));
        statement.setInt(3, invoice.getInvoiceID());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(Invoice invoice) throws SQLException {
        String sql = "DELETE FROM invoice WHERE invoiceID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, invoice.getInvoiceID());

        return statement.executeUpdate();
    }

    @Override
    public ArrayList<Invoice> SelectAll() throws SQLException {
        ArrayList<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            Invoice invoice = new Invoice(
                resultSet.getInt("invoiceID"),
                resultSet.getInt("userID"),
                resultSet.getFloat("totalPrice"),
                resultSet.getTimestamp("boughtAt").toLocalDateTime(),
                new ArrayList<>() // Chưa xử lý InvoiceDetail
            );
            invoices.add(invoice);
        }
        return invoices;
    }

    @Override
    public Invoice SelectByID(int id) throws SQLException {
        String sql = "SELECT * FROM invoice WHERE invoiceID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Invoice(
                resultSet.getInt("invoiceID"),
                resultSet.getInt("userID"),
                resultSet.getFloat("totalPrice"),
                resultSet.getTimestamp("boughtAt").toLocalDateTime(),
                new ArrayList<>() // Chưa xử lý InvoiceDetail
            );
        }
        return null;
    }

    @Override
    public ArrayList<Invoice> SelectByCondition(String condition) throws SQLException {
        ArrayList<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoice WHERE " + condition;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            Invoice invoice = new Invoice(
                resultSet.getInt("invoiceID"),
                resultSet.getInt("userID"),
                resultSet.getFloat("totalPrice"),
                resultSet.getTimestamp("boughtAt").toLocalDateTime(),
                new ArrayList<>() // Chưa xử lý InvoiceDetail
            );
            invoices.add(invoice);
        }
        return invoices;
    }
}
