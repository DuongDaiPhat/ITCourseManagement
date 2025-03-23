package entity;

public class InvoiceDetail {
	private int invoiceID;
    private int courseID;
    
	public InvoiceDetail(int invoiceID, int courseID) {
		super();
		this.invoiceID = invoiceID;
		this.courseID = courseID;
	}
	
	public int getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(int invoiceID) {
		this.invoiceID = invoiceID;
	}
	public int getCourseID() {
		return courseID;
	}
	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

}
