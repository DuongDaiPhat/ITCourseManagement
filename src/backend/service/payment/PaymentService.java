package backend.service.payment;

import backend.service.user.MyLearningService;
import model.course.Courses;
import model.user.Users;
import java.sql.SQLException;
import java.util.Random;

public class PaymentService {
    private static PaymentService instance;
    private Random random = new Random();
    
    private PaymentService() {}
    
    public static PaymentService getInstance() {
        if (instance == null) {
            instance = new PaymentService();
        }
        return instance;
    }
    
    // Mock payment methods
    public PaymentResult initiatePayment(Users user, Courses course, String paymentMethod, String cardNumber, String cvv, String expiryDate) {
        try {
            // Simulate API delay
            Thread.sleep(1000);
            
            // Mock validation (always successful for demo)
            String transactionId = generateTransactionId();
            
            return new PaymentResult(
                true, 
                "Payment successful", 
                transactionId,
                course.getPrice(),
                paymentMethod
            );
            
        } catch (InterruptedException e) {
            return new PaymentResult(false, "Payment interrupted", null, 0.0f, paymentMethod);
        }
    }
      public boolean processCourseUnlock(Users user, Courses course, String transactionId) throws SQLException {
        // In a real system, this would:
        // 1. Record the purchase in the database
        // 2. Add the course to user's enrolled courses
        // 3. Remove from cart if present
        // 4. Update user's course access permissions
        
        try {
            // Add course to MyLearning
            MyLearningService learningService = new MyLearningService();
            boolean added = learningService.addToMyLearning(user.getUserID(), course.getCourseID());
            
            if (added) {
                System.out.println("Course unlocked and added to MyLearning for user: " + user.getUserName() + 
                              ", Course: " + course.getCourseName() + 
                              ", Transaction: " + transactionId);
                return true;
            } else {
                System.err.println("Failed to add course to MyLearning for user: " + user.getUserName());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error processing course unlock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + random.nextInt(1000);
    }
    
    // Inner class for payment result
    public static class PaymentResult {
        private boolean success;
        private String message;
        private String transactionId;
        private float amount;
        private String paymentMethod;
        
        public PaymentResult(boolean success, String message, String transactionId, float amount, String paymentMethod) {
            this.success = success;
            this.message = message;
            this.transactionId = transactionId;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getTransactionId() { return transactionId; }
        public float getAmount() { return amount; }
        public String getPaymentMethod() { return paymentMethod; }
    }
}
