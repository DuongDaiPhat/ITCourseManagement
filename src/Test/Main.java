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
import model.user.MyCart;
public class Main extends Application{
	@Override
	public void start(Stage stage) throws Exception {
	    Parent root = FXMLLoader.load(getClass().getResource("/frontend/view/login/Login.fxml"));
	    Scene scene = new Scene(root);
	    
	    
	    stage.setTitle("Main Page");
	    stage.setScene(scene);
	    stage.show();
	}
    public static void main(String[] args) throws SQLException {
    	launch(args);
    }
}






