package Test;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import backend.dao.InvoiceDAO;
import backend.dao.MyCartDAO;
import backend.dao.NotificationDAO;
import backend.dao.UserNotificationDAO;
import backend.dao.PaymentDAO;
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

/*public class Main {
    public static void main(String[] args) throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();

        // Thêm một hóa đơn
        Invoice invoice = new Invoice(0,1001, 500.0f, LocalDateTime.now(), new ArrayList<>());
        invoiceDAO.Insert(invoice);

        // Hiển thị danh sách hóa đơn
        ArrayList<Invoice> invoices = invoiceDAO.SelectAll();
        for (Invoice inv : invoices) {
            System.out.println("InvoiceID: " + inv.getInvoiceID() + " - UserID: " + inv.getUserID() + " - TotalPrice: " + inv.getTotalPrice());
        }
    }
}*/
/*
public class Main {
    public static void main(String[] args) throws SQLException {
        PaymentDAO paymentDAO = new PaymentDAO();

        // Thêm phương thức thanh toán mới
        Payment newPayment = new Payment(0, "Credit Card");
        paymentDAO.Insert(newPayment);

        // Hiển thị danh sách phương thức thanh toán
        ArrayList<Payment> payments = paymentDAO.SelectAll();
        for (Payment payment : payments) {
            System.out.println(payment.getPaymentID() + " - " + payment.getPaymentName());
        }
    }
}*/

public class Main {
    public static void main(String[] args) throws SQLException {
        MyCartDAO cartDAO = new MyCartDAO();

        // Thêm sản phẩm vào giỏ
        MyCart newCart = new MyCart(1, 101, false, LocalDateTime.now());
        cartDAO.Insert(newCart);

        // Hiển thị danh sách giỏ hàng
        ArrayList<MyCart> carts = cartDAO.SelectAll();
        for (MyCart cart : carts) {
            System.out.println(cart.getUserId() + " - " + cart.getCourseId());
        }
    }
}






