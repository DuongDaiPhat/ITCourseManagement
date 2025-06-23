package backend.repository.notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import backend.repository.DatabaseConnection;
import model.notification.Notification;
import model.notification.NotificationStatus;
import model.notification.UserNotification;

public class NotificationRepository {
	// Change getConnection to always return a new connection
	private Connection getConnection() throws SQLException {
		return DatabaseConnection.getNewConnection(); // Implement this in DatabaseConnection
	}

	public boolean createNotification(String title, String content, int adminId, String notificationType,
			int targetRole, Integer userId, String icon, String category) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet generatedKeys = null;
		boolean success = false;

		try {
			con = getConnection();
			String sql = "INSERT INTO Notification (NotificationName, Content, NotificationType, TargetRole, Icon, Category, NotifiedAt) "
					+ "VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
			ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, title);
			ps.setString(2, content);
			ps.setString(3, notificationType);
			ps.setInt(4, targetRole);
			ps.setString(5, icon);
			ps.setString(6, category);
			// NotifiedAt sẽ tự động được set bởi CURRENT_TIMESTAMP

			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				generatedKeys = ps.getGeneratedKeys();
				if (generatedKeys.next()) {
					int notificationId = generatedKeys.getInt(1);
					if (userId != null) {
						success = createNotificationForSingleUser(notificationId, userId, con);
					} else {
						success = createNotificationDetails(notificationId, targetRole, con);
					}
				}
			}
			return success;
		} finally {
			if (generatedKeys != null)
				generatedKeys.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		}
	}

	private boolean createNotificationForSingleUser(int notificationId, int userId, Connection con)
			throws SQLException {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO NotificationDetail (NotificationID, UserID) VALUES (?, ?)";
			ps = con.prepareStatement(sql);
			ps.setInt(1, notificationId);
			ps.setInt(2, userId);
			return ps.executeUpdate() > 0;
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	private boolean createNotificationDetails(int notificationId, int targetRole, Connection con) throws SQLException {
		PreparedStatement ps = null;
		try {
			String sql;
			if (targetRole == 0) {
				sql = "INSERT INTO NotificationDetail (NotificationID, UserID) SELECT ?, USERID FROM USERS";
				ps = con.prepareStatement(sql);
				ps.setInt(1, notificationId);
			} else {
				sql = "INSERT INTO NotificationDetail (NotificationID, UserID) SELECT ?, USERID FROM USERS WHERE ROLEID = ?";
				ps = con.prepareStatement(sql);
				ps.setInt(1, notificationId);
				ps.setInt(2, targetRole);
			}
			return ps.executeUpdate() > 0;
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	public List<Map<String, Object>> getNotificationHistory() throws SQLException {
		List<Map<String, Object>> history = new ArrayList<>();
		String sql = "SELECT n.NotificationName as title, n.Content as content, "
				+ "n.NotificationType as type, n.TargetRole as targetRole, "
				+ "n.NotifiedAt as date, COUNT(nd.UserID) as userCount " + "FROM Notification n "
				+ "LEFT JOIN NotificationDetail nd ON n.NotificationID = nd.NotificationID "
				+ "GROUP BY n.NotificationID ORDER BY n.NotifiedAt DESC LIMIT 20";

		try (Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Map<String, Object> entry = new HashMap<>();
				entry.put("title", rs.getString("title"));
				entry.put("content", rs.getString("content"));
				entry.put("type", rs.getString("type"));

				int targetRole = rs.getInt("targetRole");
				String target = "All Users";
				if (targetRole == 1)
					target = "Instructors Only";
				else if (targetRole == 2)
					target = "Students Only";
				entry.put("target", target + " (" + rs.getInt("userCount") + " users)");

				entry.put("date", rs.getTimestamp("date").toLocalDateTime()
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

				history.add(entry);
			}
		}
		return history;
	}

	public boolean markAllNotificationsAsRead(int userId) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getConnection();
			String sql = "UPDATE NotificationDetail SET Status = 'read' WHERE UserID = ? AND Status = 'unread'";
			ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			return ps.executeUpdate() > 0;
		} finally {
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		}
	}

	public Notification getNotificationById(int notificationId) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Notification notification = null;

		try {
			con = getConnection();
			String sql = "SELECT * FROM Notification WHERE NotificationID = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, notificationId);
			rs = ps.executeQuery();

			if (rs.next()) {
				notification = new Notification();
				notification.setNotificationID(rs.getInt("NotificationID"));
				notification.setNotificationName(rs.getString("NotificationName"));
				notification.setContent(rs.getString("Content"));
				notification.setNotifiedAt(rs.getTimestamp("NotifiedAt").toLocalDateTime());
				notification.setIcon(rs.getString("Icon"));
				notification.setCategory(rs.getString("Category"));
			}
			return notification;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		}
	}

	public List<UserNotification> getReadNotificationsForUser(int userId) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<UserNotification> notifications = new ArrayList<>();

		try {
			con = getConnection();
			ps = con.prepareStatement("SELECT n.*, nd.Status FROM Notification n "
					+ "JOIN NotificationDetail nd ON n.NotificationID = nd.NotificationID "
					+ "WHERE nd.UserID = ? AND nd.Status = 'read' ORDER BY n.NotifiedAt DESC");
			ps.setInt(1, userId);
			rs = ps.executeQuery();

			while (rs.next()) {
				UserNotification notification = new UserNotification();
				notification.setNotificationID(rs.getInt("NotificationID"));
				notification.setUserID(userId);
				notification.setNotificationName(rs.getString("NotificationName"));
				notification.setContent(rs.getString("Content"));
				notification.setIcon(rs.getString("Icon"));
				notification.setCategory(rs.getString("Category"));
				notification.setStatus(NotificationStatus.valueOf(rs.getString("Status").toUpperCase()));

				// Thêm dòng này để lấy thời gian
				Timestamp timestamp = rs.getTimestamp("NotifiedAt");
				notification.setNotifiedAt(timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now());

				notifications.add(notification);
			}
			return notifications;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		}
	}

	public List<UserNotification> getNotificationsForUser(int userId) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<UserNotification> notifications = new ArrayList<>();

		try {
			con = getConnection();
			ps = con.prepareStatement("SELECT n.*, nd.Status FROM Notification n "
					+ "JOIN NotificationDetail nd ON n.NotificationID = nd.NotificationID "
					+ "WHERE nd.UserID = ? ORDER BY n.NotifiedAt DESC");
			ps.setInt(1, userId);
			rs = ps.executeQuery();

			while (rs.next()) {
				UserNotification notification = new UserNotification();
				notification.setNotificationID(rs.getInt("NotificationID"));
				notification.setUserID(userId);
				notification.setNotificationName(rs.getString("NotificationName"));
				notification.setContent(rs.getString("Content"));
				notification.setIcon(rs.getString("Icon"));
				notification.setCategory(rs.getString("Category"));

				// Đảm bảo lấy thời gian
				Timestamp timestamp = rs.getTimestamp("NotifiedAt");
				notification.setNotifiedAt(timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now());

				notification.setStatus(NotificationStatus.valueOf(rs.getString("Status").toUpperCase()));
				notifications.add(notification);
			}
			return notifications;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		}
	}

	public List<UserNotification> getUnreadNotificationsForUser(int userId, Connection con) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<UserNotification> notifications = new ArrayList<>();

		try {
			String sql = "SELECT n.*, nd.Status FROM Notification n "
					+ "JOIN NotificationDetail nd ON n.NotificationID = nd.NotificationID "
					+ "WHERE nd.UserID = ? AND nd.Status = 'unread' ORDER BY n.NotifiedAt DESC LIMIT 5";
			ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			rs = ps.executeQuery();

			while (rs.next()) {
				UserNotification notification = new UserNotification();
				notification.setNotificationID(rs.getInt("NotificationID"));
				notification.setUserID(userId);
				notification.setNotificationName(rs.getString("NotificationName"));
				notification.setContent(rs.getString("Content"));
				notification.setIcon(rs.getString("Icon"));
				notification.setCategory(rs.getString("Category"));
				notification.setStatus(NotificationStatus.valueOf(rs.getString("Status").toUpperCase()));

				// Đảm bảo không bao giờ bị NULL
				Timestamp timestamp = rs.getTimestamp("NotifiedAt");
				notification.setNotifiedAt(timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now());

				notifications.add(notification);
			}
			return notifications;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}

	public boolean markNotificationAsRead(int notificationId, int userId) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = getConnection();
			String sql = "UPDATE NotificationDetail SET Status = 'read' WHERE NotificationID = ? AND UserID = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, notificationId);
			ps.setInt(2, userId);
			return ps.executeUpdate() > 0;
		} finally {
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		}
	}

	private int getUnreadNotificationCount(int userId, Connection con) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT COUNT(*) FROM NotificationDetail WHERE UserID = ? AND Status = 'unread'";
			ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
		}
	}

	public NotificationResult getNotificationsAndCount(int userId) throws SQLException {
		Connection con = null;
		try {
			con = getConnection(); // Make sure this gets a fresh connection
			List<UserNotification> notifications = getUnreadNotificationsForUser(userId, con);
			int unreadCount = getUnreadNotificationCount(userId, con);
			return new NotificationResult(notifications, unreadCount);
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}

	public static class NotificationResult {
		private final List<UserNotification> notifications;
		private final int unreadCount;

		public NotificationResult(List<UserNotification> notifications, int unreadCount) {
			this.notifications = notifications;
			this.unreadCount = unreadCount;
		}

		public List<UserNotification> getNotifications() {
			return notifications;
		}

		public int getUnreadCount() {
			return unreadCount;
		}
	}

	public List<UserNotification> getUnreadNotificationsForUser(int userId) throws SQLException {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<UserNotification> notifications = new ArrayList<>();

		try {
			con = getConnection();
			String sql = "SELECT n.*, nd.Status FROM Notification n "
					+ "JOIN NotificationDetail nd ON n.NotificationID = nd.NotificationID "
					+ "WHERE nd.UserID = ? AND nd.Status = 'unread' ORDER BY n.NotifiedAt DESC";
			ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			rs = ps.executeQuery();

			while (rs.next()) {
				UserNotification notification = new UserNotification();
				notification.setNotificationID(rs.getInt("NotificationID"));
				notification.setUserID(userId);
				notification.setNotificationName(rs.getString("NotificationName"));
				notification.setContent(rs.getString("Content"));
				notification.setIcon(rs.getString("Icon"));
				notification.setCategory(rs.getString("Category"));
				notification.setStatus(NotificationStatus.valueOf(rs.getString("Status").toUpperCase()));

				// Thêm dòng này để lấy thời gian
				Timestamp timestamp = rs.getTimestamp("NotifiedAt");
				notification.setNotifiedAt(timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now());

				notifications.add(notification);
			}
			return notifications;
		} finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (con != null)
				con.close();
		}
	}
}