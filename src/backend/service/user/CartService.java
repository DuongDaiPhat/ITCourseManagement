package backend.service.user;

import backend.repository.user.MyCartRepository;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.user.MyCart;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CartService {
    private static CartService instance;
    private final ObservableList<Integer> courseIds = FXCollections.observableArrayList();
    private final MyCartRepository repo = new MyCartRepository();
    private int currentUserId;

    private CartService() {}

    public static CartService getInstance(int userId) {
        if (instance == null) instance = new CartService();
        instance.currentUserId = userId;
        return instance;
    }

    public void init() throws SQLException {
        ArrayList<Integer> ids = repo.getCourseIdsByUserId(currentUserId);
        courseIds.setAll(ids);
    }

    public ReadOnlyListProperty<Integer> courseIdsProperty() {
        return new ReadOnlyListWrapper<>(courseIds).getReadOnlyProperty();
    }

    public boolean addCourse(int courseId) throws SQLException {
        if (courseIds.contains(courseId)) return false;
        MyCart cart = new MyCart(currentUserId, courseId, false, LocalDateTime.now());
        int result = repo.Insert(cart);
        if (result > 0) courseIds.add(courseId);
        return result > 0;
    }

    public boolean removeCourse(int courseId) throws SQLException {
        MyCart cart = new MyCart(currentUserId, courseId, false, null);
        int result = repo.Delete(cart);
        if (result > 0) courseIds.remove((Integer) courseId);
        return result > 0;
    }
}
