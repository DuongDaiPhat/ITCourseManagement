package Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import backend.repository.invoice.InvoiceRepository;
import backend.repository.notification.NotificationRepository;
import backend.repository.payment.PaymentRepository;
import backend.repository.user.MyCartRepository;
import backend.repository.user.UserNotificationRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.invoice.Invoice;
import model.notification.Notification;
import model.notification.UserNotification;
import model.notification.NotificationStatus;
import model.payment.Payment;
/*
public class Main {
    public static void main(String[] args) throws SQLException {
        NotificationDAO notificationDAO = new NotificationDAO();

        // Thêm một thông báo mới
        Notification notification = new Notification(0, "System Update", "The system will be updated at midnight.", LocalDateTime.now(), new ArrayList<>());
        int newID = notificationDAO.Insert(notification);
        System.out.println("Inserted notification with ID: " + newID);

        // Hiển thị danh sách thông báo
        ArrayList<Notification> notifications = notificationDAO.SelectAll();
        for (Notification n : notifications) {
            System.out.println("ID: " + n.getNotificationID() + " - Name: " + n.getNotificationName() + " - Content: " + n.getContent());
        }
    }
}*/
import model.user.MyCart;
public class Main extends Application{
	@Override
	public void start(Stage stage) throws Exception {
	}
    public static void main(String[] args) throws SQLException {
    	launch(args);
    }
}






