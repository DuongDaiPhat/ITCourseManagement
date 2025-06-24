package backend.repository.notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import backend.repository.DatabaseConnection;
import model.notification.NotificationTemplate;

public class NotificationTemplateRepository {
	private Connection getConnection() throws SQLException {
		return DatabaseConnection.getNewConnection();
	}

	public List<NotificationTemplate> getAllTemplates() throws SQLException {
		List<NotificationTemplate> templates = new ArrayList<>();
		String sql = "SELECT * FROM NotificationTemplate ORDER BY NotificationType, Category";

		try (Connection con = getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				NotificationTemplate template = new NotificationTemplate();
				template.setId(rs.getInt("TemplateID"));
				template.setTitle(rs.getString("Title"));
				template.setContent(rs.getString("Content"));
				template.setNotificationType(rs.getString("NotificationType"));
				template.setCategory(rs.getString("Category"));
				templates.add(template);
			}
		}
		return templates;
	}

	public List<NotificationTemplate> getTemplatesByType(String type) throws SQLException {
		List<NotificationTemplate> templates = new ArrayList<>();
		String sql = "SELECT * FROM NotificationTemplate WHERE NotificationType = ? ORDER BY Category";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, type);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					NotificationTemplate template = new NotificationTemplate();
					template.setId(rs.getInt("TemplateID"));
					template.setTitle(rs.getString("Title"));
					template.setContent(rs.getString("Content"));
					template.setNotificationType(rs.getString("NotificationType"));
					template.setCategory(rs.getString("Category"));
					templates.add(template);
				}
			}
		}
		return templates;
	}

	public boolean saveTemplate(NotificationTemplate template) throws SQLException {
		String sql;
		if (template.getId() == 0) {
			sql = "INSERT INTO NotificationTemplate (Title, Content, NotificationType, Category) VALUES (?, ?, ?, ?)";
		} else {
			sql = "UPDATE NotificationTemplate SET Title = ?, Content = ?, NotificationType = ?, Category = ? WHERE TemplateID = ?";
		}

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, template.getTitle());
			ps.setString(2, template.getContent());
			ps.setString(3, template.getNotificationType());
			ps.setString(4, template.getCategory());

			if (template.getId() != 0) {
				ps.setInt(5, template.getId());
			}

			return ps.executeUpdate() > 0;
		}
	}

	public boolean deleteTemplate(int templateId) throws SQLException {
		String sql = "DELETE FROM NotificationTemplate WHERE TemplateID = ?";

		try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, templateId);
			return ps.executeUpdate() > 0;
		}
	}
}