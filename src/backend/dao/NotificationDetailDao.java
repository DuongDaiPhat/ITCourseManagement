package backend.dao;

import java.sql.*;
import java.util.ArrayList;
import model.notification.NotificationDetail;
import model.notification.NotificationStatus;
import backend.dao.DatabaseConnection;

public class NotificationDetailDao implements DAOInterface<NotificationDetail> {
    
    private Connection connection;

    public NotificationDetailDao() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public int Insert(NotificationDetail notificationDetail) throws SQLException {
        String sql = "INSERT INTO notificationdetail (notificationID, userID, status) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, notificationDetail.getNotificationID());
        statement.setInt(2, notificationDetail.getUserID());
        statement.setString(3, notificationDetail.getStatus().name());

        int rowsInserted = statement.executeUpdate();

        // Lấy ID vừa được tạo ra (nếu có)
        if (rowsInserted > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public int Update(NotificationDetail notificationDetail) throws SQLException {
        String sql = "UPDATE notificationdetail SET status = ? WHERE notificationID = ? AND userID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, notificationDetail.getStatus().name());
        statement.setInt(2, notificationDetail.getNotificationID());
        statement.setInt(3, notificationDetail.getUserID());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(NotificationDetail notificationDetail) throws SQLException {
        String sql = "DELETE FROM notificationdetail WHERE notificationID = ? AND userID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, notificationDetail.getNotificationID());
        statement.setInt(2, notificationDetail.getUserID());

        return statement.executeUpdate();
    }

    @Override
    public ArrayList<NotificationDetail> SelectAll() throws SQLException {
        ArrayList<NotificationDetail> notificationDetails = new ArrayList<>();
        String sql = "SELECT * FROM notificationdetail";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            NotificationDetail notificationDetail = new NotificationDetail(
                resultSet.getInt("notificationID"),
                resultSet.getInt("userID"),
                NotificationStatus.valueOf(resultSet.getString("status").toUpperCase())
            );
            notificationDetails.add(notificationDetail);
        }
        return notificationDetails;
    }

    @Override
    public NotificationDetail SelectByID(int id) throws SQLException {
        String sql = "SELECT * FROM notificationdetail WHERE notificationID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new NotificationDetail(
                resultSet.getInt("notificationID"),
                resultSet.getInt("userID"),
                NotificationStatus.valueOf(resultSet.getString("status"))
            );
        }
        return null;
    }

    @Override
    public ArrayList<NotificationDetail> SelectByCondition(String condition) throws SQLException {
        ArrayList<NotificationDetail> notificationDetails = new ArrayList<>();
        String sql = "SELECT * FROM notificationdetail WHERE " + condition;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            NotificationDetail notificationDetail = new NotificationDetail(
                resultSet.getInt("notificationID"),
                resultSet.getInt("userID"),
                NotificationStatus.valueOf(resultSet.getString("status"))
            );
            notificationDetails.add(notificationDetail);
        }
        return notificationDetails;
    }
}
