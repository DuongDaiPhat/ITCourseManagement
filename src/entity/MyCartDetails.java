package entity;

import java.time.LocalDateTime;

public class MyCartDetails {
	private int myCartID;
    private int courseID;
    private PurchaseStatus isBuy = PurchaseStatus.NOT_BOUGHT;
    private LocalDateTime addedAt = LocalDateTime.now();
    
	public int getMyCartID() {
		return myCartID;
	}
	public void setMyCartID(int myCartID) {
		this.myCartID = myCartID;
	}
	public int getCourseID() {
		return courseID;
	}
	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}
	public PurchaseStatus getIsBuy() {
		return isBuy;
	}
	public void setIsBuy(PurchaseStatus isBuy) {
		this.isBuy = isBuy;
	}
	public LocalDateTime getAddedAt() {
		return addedAt;
	}
	public void setAddedAt(LocalDateTime addedAt) {
		this.addedAt = addedAt;
	}
    
}
