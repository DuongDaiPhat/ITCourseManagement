package entity;

public class Payment {
	 private int paymentID;
     private String paymentName;
     
	public Payment(int paymentID, String paymentName) {
		super();
		this.paymentID = paymentID;
		this.paymentName = paymentName;
	}
	
	public int getPaymentID() {
		return paymentID;
	}
	public void setPaymentID(int paymentID) {
		this.paymentID = paymentID;
	}
	public String getPaymentName() {
		return paymentName;
	}
	public void setPaymentName(String paymentName) {
		this.paymentName = paymentName;
	}
     
     

}
