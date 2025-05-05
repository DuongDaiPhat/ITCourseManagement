package model.payment;

public class UserPayment {
    private int paymentID;
    private int userID;
    private float balance;
    private String paymentName;
    
    public UserPayment(int paymentID, int userID, float balance) {
        this.paymentID = paymentID;
        this.userID = userID;
        this.balance = balance;
    }
    
    public UserPayment(int paymentID, int userID, float balance, String paymentName) {
        this.paymentID = paymentID;
        this.userID = userID;
        this.balance = balance;
        this.paymentName = paymentName;
    }
    
    public int getPaymentID() {
        return paymentID;
    }
    
    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }
    
    public int getUserID() {
        return userID;
    }
    
    public void setUserID(int userID) {
        this.userID = userID;
    }
    
    public float getBalance() {
        return balance;
    }
    
    public void setBalance(float balance) {
        this.balance = balance;
    }
    
    public String getPaymentName() {
        return paymentName;
    }
    
    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}