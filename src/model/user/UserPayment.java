package model.user;

public class UserPayment {
	private int paymentID;
    private int userID;
    private float balance;
    
	public UserPayment(int paymentID, int userID, float balance) {
		super();
		this.paymentID = paymentID;
		this.userID = userID;
		this.balance = balance;
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

    

}
