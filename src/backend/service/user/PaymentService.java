package backend.service.user;

import model.payment.*;
import java.util.List;

import backend.repository.payment.PaymentRepository;
import backend.repository.payment.UserPaymentRepository;

public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserPaymentRepository userPaymentRepository;
    
    public PaymentService() {
        this.paymentRepository = new PaymentRepository();
        this.userPaymentRepository = new UserPaymentRepository();
    }
    
    /**
     * Get all available payment methods
     */
    public List<Payment> getAllPaymentMethods() {
        return paymentRepository.getAllPayments();
    }
    
    /**
     * Get all payment methods associated with a user
     */
    public List<UserPayment> getUserPaymentMethods(int userId) {
        return userPaymentRepository.getUserPayments(userId);
    }
    
    /**
     * Add a new payment method for a user
     */
    public boolean addPaymentMethod(int userId, int paymentId) {
        if (userPaymentRepository.hasPaymentMethod(userId, paymentId)) {
            return false;
        }
        
        return userPaymentRepository.addUserPayment(userId, paymentId);
    }
    
    /**
     * Remove a payment method from a user
     */
    public boolean removePaymentMethod(int userId, int paymentId) {
        return userPaymentRepository.deleteUserPayment(userId, paymentId);
    }
    
    /**
     * Get balance for a specific payment method
     */
    public float getBalance(int userId, int paymentId) {
        return userPaymentRepository.getBalance(userId, paymentId);
    }
    
    /**
     * Deposit money
     */
    public boolean deposit(int userId, int paymentId, float amount) {
        if (amount <= 0) {
            return false;
        }
        
        return userPaymentRepository.deposit(userId, paymentId, amount);
    }
    
    /**
     * Withdraw money
     */
    public boolean withdraw(int userId, int paymentId, float amount) {
        if (amount <= 0) {
            return false;
        }
        
        float currentBalance = getBalance(userId, paymentId);
        if (currentBalance < amount) {
            return false;
        }
        
        return userPaymentRepository.withdraw(userId, paymentId, amount);
    }
}