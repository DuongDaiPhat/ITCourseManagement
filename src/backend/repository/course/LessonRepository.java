package backend.repository.course;

import backend.repository.DatabaseConnection;
import backend.repository.RepositoryInterface;
import model.course.Category;
import model.course.Lesson;

import java.sql.*;
import java.util.ArrayList;

public class LessonRepository implements RepositoryInterface<Lesson> {

    @Override
    public int Insert(Lesson lesson) throws SQLException {
        String sql = "INSERT INTO lesson (title, author, rating, total_ratings, price, image_path, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, lesson.getTitle());
            stmt.setString(2, lesson.getAuthor());
            stmt.setDouble(3, lesson.getRating());
            stmt.setInt(4, lesson.getTotalRatings());
            stmt.setDouble(5, lesson.getPrice());
            stmt.setString(6, lesson.getImagePath());
            stmt.setString(7, lesson.getCategory().name());
            return stmt.executeUpdate();
        }
    }

    @Override
    public int Update(Lesson lesson) throws SQLException {
        String sql = "UPDATE lesson SET title=?, author=?, rating=?, total_ratings=?, price=?, image_path=?, category=? WHERE id=?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, lesson.getTitle());
            stmt.setString(2, lesson.getAuthor());
            stmt.setDouble(3, lesson.getRating());
            stmt.setInt(4, lesson.getTotalRatings());
            stmt.setDouble(5, lesson.getPrice());
            stmt.setString(6, lesson.getImagePath());
            stmt.setString(7, lesson.getCategory().name());
            stmt.setInt(8, lesson.getId());
            return stmt.executeUpdate();
        }
    }

    @Override
    public int Delete(Lesson lesson) throws SQLException {
        String sql = "DELETE FROM lesson WHERE id=?";
        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, lesson.getId());
            return stmt.executeUpdate();
        }
    }

    @Override
    public ArrayList<Lesson> SelectAll() throws SQLException {
        return SelectByCondition("1=1");
    }

    @Override
    public Lesson SelectByID(int id) throws SQLException {
        ArrayList<Lesson> list = SelectByCondition("id = " + id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public ArrayList<Lesson> SelectByCondition(String condition) throws SQLException {
        ArrayList<Lesson> list = new ArrayList<>();
        String sql = "SELECT * FROM lesson WHERE " + condition;
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Lesson lesson = new Lesson(
                        rs.getInt("id"),
                        rs.getString("title"),
rs.getString("author"),
                        rs.getDouble("rating"),
                        rs.getInt("total_ratings"),
                        rs.getDouble("price"),
                        rs.getString("image_path"),
                        Category.valueOf(rs.getString("category"))
                );
                list.add(lesson);
            }
        }
        return list;
    }

    public ArrayList<Lesson> getLessonsByCategory(Category category) {
        try {
            return SelectByCondition("category = '" + category.name() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}