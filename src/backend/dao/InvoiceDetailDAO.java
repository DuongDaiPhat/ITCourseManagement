package backend.dao;

import java.sql.*;
import java.util.ArrayList;
import model.invoice.InvoiceDetail;

public class InvoiceDetailDAO implements DAOInterface<InvoiceDetail> {
    
    private Connection connection;

    public InvoiceDetailDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public int Insert(InvoiceDetail invoiceDetail) throws SQLException {
        String sql = "INSERT INTO invoicedetail (invoiceID, courseID) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, invoiceDetail.getInvoiceID());
        statement.setInt(2, invoiceDetail.getCourseID());

        return statement.executeUpdate();
    }

    @Override
    public int Update(InvoiceDetail invoiceDetail) throws SQLException {
        String sql = "UPDATE invoicedetail SET courseID = ? WHERE invoiceID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, invoiceDetail.getCourseID());
        statement.setInt(2, invoiceDetail.getInvoiceID());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(InvoiceDetail invoiceDetail) throws SQLException {
        String sql = "DELETE FROM invoicedetail WHERE invoiceID = ? AND courseID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, invoiceDetail.getInvoiceID());
        statement.setInt(2, invoiceDetail.getCourseID());

        return statement.executeUpdate();
    }

    @Override
    public ArrayList<InvoiceDetail> SelectAll() throws SQLException {
        ArrayList<InvoiceDetail> invoiceDetails = new ArrayList<>();
        String sql = "SELECT * FROM invoicedetail";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            InvoiceDetail invoiceDetail = new InvoiceDetail(
                resultSet.getInt("invoiceID"),
                resultSet.getInt("courseID")
            );
            invoiceDetails.add(invoiceDetail);
        }
        return invoiceDetails;
    }

    @Override
    public InvoiceDetail SelectByID(int id) throws SQLException {
        String sql = "SELECT * FROM invoicedetail WHERE invoiceID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new InvoiceDetail(
                resultSet.getInt("invoiceID"),
                resultSet.getInt("courseID")
            );
        }
        return null;
    }

    @Override
    public ArrayList<InvoiceDetail> SelectByCondition(String condition) throws SQLException {
        ArrayList<InvoiceDetail> invoiceDetails = new ArrayList<>();
        String sql = "SELECT * FROM invoicedetail WHERE " + condition;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            InvoiceDetail invoiceDetail = new InvoiceDetail(
                resultSet.getInt("invoiceID"),
                resultSet.getInt("courseID")
            );
            invoiceDetails.add(invoiceDetail);
        }
        return invoiceDetails;
    }
}
