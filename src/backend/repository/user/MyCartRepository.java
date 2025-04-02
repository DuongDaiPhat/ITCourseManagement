package backend.repository.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import backend.repository.RepositoryInterface;
import backend.repository.DatabaseConnection;
import model.user.MyCart;

public class MyCartRepository implements RepositoryInterface<MyCart> {
	
	private Connection connection;

    public MyCartRepository() {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public int Insert(MyCart cart) throws SQLException {
        String sql = "INSERT INTO mycart (userId, courseId, isBuy, addedAt) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, cart.getUserId());
        statement.setInt(2, cart.getCourseId());
        statement.setBoolean(3, cart.isBuy());
        statement.setTimestamp(4, Timestamp.valueOf(cart.getAddedAt()));

        return statement.executeUpdate();
    }

    @Override
    public int Update(MyCart cart) throws SQLException {
        String sql = "UPDATE mycart SET isBuy = ?, addedAt = ? WHERE userId = ? AND courseId = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setBoolean(1, cart.isBuy());
        statement.setTimestamp(2, Timestamp.valueOf(cart.getAddedAt()));
        statement.setInt(3, cart.getUserId());
        statement.setInt(4, cart.getCourseId());

        return statement.executeUpdate();
    }

    @Override
    public int Delete(MyCart cart) throws SQLException {
        String sql = "DELETE FROM mycart WHERE userId = ? AND courseId = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, cart.getUserId());
        statement.setInt(2, cart.getCourseId());

        return statement.executeUpdate();
    }

    @Override
    public ArrayList<MyCart> SelectAll() throws SQLException {
        ArrayList<MyCart> carts = new ArrayList<>();
        String sql = "SELECT * FROM mycart";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            MyCart cart = new MyCart(
                resultSet.getInt("userId"),
                resultSet.getInt("courseId"),
                resultSet.getBoolean("isBuy"),
                resultSet.getTimestamp("addedAt").toLocalDateTime()
            );
            carts.add(cart);
        }
        return carts;
    }

    @Override
    public MyCart SelectByID(int id) throws SQLException {
        String sql = "SELECT * FROM mycart WHERE userId = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new MyCart(
                resultSet.getInt("userId"),
                resultSet.getInt("courseId"),
                resultSet.getBoolean("isBuy"),
                resultSet.getTimestamp("addedAt").toLocalDateTime()
            );
        }
        return null;
    }

    @Override
    public ArrayList<MyCart> SelectByCondition(String condition) throws SQLException {
        ArrayList<MyCart> carts = new ArrayList<>();
        String sql = "SELECT * FROM mycart WHERE " + condition;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            MyCart cart = new MyCart(
                resultSet.getInt("userId"),
                resultSet.getInt("courseId"),
                resultSet.getBoolean("isBuy"),
                resultSet.getTimestamp("addedAt").toLocalDateTime()
            );
            carts.add(cart);
        }
        return carts;
    }

}
