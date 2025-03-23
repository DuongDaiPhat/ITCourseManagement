package entity;

import java.util.ArrayList;
import java.util.List;

public class MyCart {
	private int myCartID;
    private int userID;
    private List<MyCartDetails> cartDetails = new ArrayList<>();
	public int getMyCartID() {
		return myCartID;
	}
	public void setMyCartID(int myCartID) {
		this.myCartID = myCartID;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public List<MyCartDetails> getCartDetails() {
		return cartDetails;
	}
	public void setCartDetails(List<MyCartDetails> cartDetails) {
		this.cartDetails = cartDetails;
	}
    
    
}
