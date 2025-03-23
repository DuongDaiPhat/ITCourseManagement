package entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
	 private int invoiceID;
     private int userID;
     private float totalPrice;
     private LocalDateTime boughtAt = LocalDateTime.now();
     private List<InvoiceDetail> invoiceDetails = new ArrayList<>();
	public int getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public LocalDateTime getBoughtAt() {
		return boughtAt;
	}
	public void setBoughtAt(LocalDateTime boughtAt) {
		this.boughtAt = boughtAt;
	}
	public List<InvoiceDetail> getInvoiceDetails() {
		return invoiceDetails;
	}
	public void setInvoiceDetails(List<InvoiceDetail> invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
	}
     
     


}
