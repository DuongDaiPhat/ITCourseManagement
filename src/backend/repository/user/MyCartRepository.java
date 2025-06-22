package backend.repository.user;

import backend.repository.DatabaseConnection;
import backend.repository.RepositoryInterface;
import model.user.MyCart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class MyCartRepository implements RepositoryInterface<MyCart> {
    private Connection connection;

    public MyCartRepository() {
        initializeConnection();
    }

    private void initializeConnection() {
        try {
            this.connection = DatabaseConnection.getConnection();
            if (connection == null || connection.isClosed()) {
                System.err.println("Connection is null or closed, reinitializing...");
                this.connection = DatabaseConnection.getConnection();
            }
        } catch (SQLException e) {
            System.err.println("Error initializing connection: " + e.getMessage());
        }
    }

    private synchronized void ensureConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("Connection is closed or null, reinitializing...");
            initializeConnection();
            if (connection == null || connection.isClosed()) {
                throw new SQLException("Failed to re-establish database connection");
            }
        }
    }

    @Override
    public int Insert(MyCart cart) throws SQLException {
        ensureConnection();
        String sql = "INSERT INTO mycart (userId, courseId, isBuy, addedAt) VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE isBuy = VALUES(isBuy), addedAt = VALUES(addedAt)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, cart.getUserId());
            statement.setInt(2, cart.getCourseId());
            statement.setBoolean(3, cart.isBuy());
            statement.setTimestamp(4, Timestamp.valueOf(cart.getAddedAt()));
            int rowsAffected = statement.executeUpdate();
            System.out.println("Inserted/Updated cart item: userId=" + cart.getUserId() + ", courseId=" + cart.getCourseId() + ", rowsAffected=" + rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Error inserting/updating cart item: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int Update(MyCart cart) throws SQLException {
        ensureConnection();
        String sql = "UPDATE mycart SET isBuy = ?, addedAt = ? WHERE userId = ? AND courseId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, cart.isBuy());
            statement.setTimestamp(2, Timestamp.valueOf(cart.getAddedAt()));
            statement.setInt(3, cart.getUserId());
            statement.setInt(4, cart.getCourseId());
            int rowsAffected = statement.executeUpdate();
            System.out.println("Updated cart item: userId=" + cart.getUserId() + ", courseId=" + cart.getCourseId() + ", rowsAffected=" + rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Error updating cart item: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int Delete(MyCart cart) throws SQLException {
        ensureConnection();
        String sql = "DELETE FROM mycart WHERE userId = ? AND courseId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, cart.getUserId());
            statement.setInt(2, cart.getCourseId());
            int rowsAffected = statement.executeUpdate();
            System.out.println("Deleted cart item: userId=" + cart.getUserId() + ", courseId=" + cart.getCourseId() + ", rowsAffected=" + rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            System.err.println("Error deleting cart item: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public ArrayList<MyCart> SelectAll() throws SQLException {
        ensureConnection();
        ArrayList<MyCart> carts = new ArrayList<>();
        String sql = "SELECT * FROM mycart";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                MyCart cart = new MyCart(
                    resultSet.getInt("userId"),
                    resultSet.getInt("courseId"),
                    resultSet.getBoolean("isBuy"),
                    resultSet.getTimestamp("addedAt").toLocalDateTime()
                );
                carts.add(cart);
            }
            System.out.println("Selected all cart items: " + carts.size() + " items");
            return carts;
        } catch (SQLException e) {
            System.err.println("Error selecting all cart items: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public MyCart SelectByID(int id) throws SQLException {
        ensureConnection();
        String sql = "SELECT * FROM mycart WHERE userId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    MyCart cart = new MyCart(
                        resultSet.getInt("userId"),
                        resultSet.getInt("courseId"),
                        resultSet.getBoolean("isBuy"),
                        resultSet.getTimestamp("addedAt").toLocalDateTime()
                    );
                    System.out.println("Selected cart item: userId=" + id);
                    return cart;
                }
                System.out.println("No cart item found for userId=" + id);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Error selecting cart item by ID: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public ArrayList<MyCart> SelectByCondition(String condition) throws SQLException {
        ensureConnection();
        ArrayList<MyCart> carts = new ArrayList<>();
        String sql = "SELECT * FROM mycart WHERE " + condition;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                MyCart cart = new MyCart(
                    resultSet.getInt("userId"),
                    resultSet.getInt("courseId"),
                    resultSet.getBoolean("isBuy"),
                    resultSet.getTimestamp("addedAt").toLocalDateTime()
                );
                carts.add(cart);
            }
            System.out.println("Selected cart items with condition '" + condition + "': " + carts.size() + " items");
            return carts;
        } catch (SQLException e) {
            System.err.println("Error selecting cart items by condition: " + e.getMessage());
            throw e;
        }
    }

    public ArrayList<Integer> getCourseIdsByUserId(int userId) throws SQLException {
        ensureConnection();
        ArrayList<Integer> courseIds = new ArrayList<>();
        String sql = "SELECT courseId FROM mycart WHERE userId = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    courseIds.add(resultSet.getInt("courseId"));
                }
                System.out.println("Retrieved " + courseIds.size() + " course IDs for userId=" + userId);
                return courseIds;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving course IDs for userId=" + userId + ": " + e.getMessage());
            throw e;
        }
    }
}