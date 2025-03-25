package backend.dao;

import java.sql.*;
import java.util.ArrayList;
import model.notification.Notification;
import backend.dao.DatabaseConnection;

public class NotificationDAO implements DAOInterface<Notification> {
    
    private Connection connection;

    public NotificationDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public int Insert(Notification notification) throws SQLException {
        String sql = "INSERT INTO notification (notificationName, content, notifiedAt) VALUES (?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, notification.getNotificationName());
        statement.setString(2, notification.getContent());
        statement.setTimestamp(3, Timestamp.valueOf(notification.getNotifiedAt()));

        int rowsInserted = statement.executeUpdate();

        // Lấy ID vừa được tạo ra
        if (rowsInserted > 0) {
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        }
        return -1;
    }

    @Override
    public int Update(Notification notification) throws SQLException {
        String sql = "UPDATE notification SET notificationName = ?, content = ?, notifiedAt = ? WHERE notificationID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, notification.getNotificationName());
        statement.setString(2, notification.getContent());
        statement.setTimestamp(3, Timestamp.valueOf(notification.getNotifiedAt()));
        statement.setInt(4, notification.getNotificationID());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(Notification notification) throws SQLException {
        String sql = "DELETE FROM notification WHERE notificationID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, notification.getNotificationID());

        return statement.executeUpdate();
    }

    @Override
    public ArrayList<Notification> SelectAll() throws SQLException {
        ArrayList<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            Notification notification = new Notification(
                resultSet.getInt("notificationID"),
                resultSet.getString("notificationName"),
                resultSet.getString("content"),
                resultSet.getTimestamp("notifiedAt").toLocalDateTime(),
                new ArrayList<>()
            );
            notifications.add(notification);
        }
        return notifications;
    }

    @Override
    public Notification SelectByID(int id) throws SQLException {
        String sql = "SELECT * FROM notification WHERE notificationID = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new Notification(
                resultSet.getInt("notificationID"),
                resultSet.getString("notificationName"),
                resultSet.getString("content"),
                resultSet.getTimestamp("notifiedAt").toLocalDateTime(),
                new ArrayList<>()
            );
        }
        return null;
    }

    @Override
    public ArrayList<Notification> SelectByCondition(String condition) throws SQLException {
        ArrayList<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE " + condition;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            Notification notification = new Notification(
                resultSet.getInt("notificationID"),
                resultSet.getString("notificationName"),
                resultSet.getString("content"),
                resultSet.getTimestamp("notifiedAt").toLocalDateTime(),
                new ArrayList<>()
            );
            notifications.add(notification);
        }
        return notifications;
    }
}
