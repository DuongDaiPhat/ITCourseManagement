package utils;

public class UserSession {
    private static UserSession instance;
    private int currentUserId;

    private UserSession() {
        this.currentUserId = 1; // Thay bằng logic lấy từ hệ thống đăng nhập
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
}