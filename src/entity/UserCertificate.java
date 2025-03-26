package entity;

import java.time.LocalDate;
import java.util.Objects;

public class UserCertificate {
	private int certificateID;
	private int userID;
	private LocalDate issueDate;

	public UserCertificate() {
	}

	public UserCertificate(int certificateID, int userID, LocalDate issueDate) {
		this.certificateID = certificateID;
		this.userID = userID;
		this.issueDate = issueDate;
	}

	public int getCertificateID() {
		return certificateID;
	}

	public void setCertificateID(int certificateID) {
		this.certificateID = certificateID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public LocalDate getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(LocalDate issueDate) {
		this.issueDate = issueDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserCertificate that = (UserCertificate) o;
		return certificateID == that.certificateID && userID == that.userID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(certificateID, userID);
	}

	@Override
	public String toString() {
		return "UserCertificate{" + "certificateID=" + certificateID + ", userID=" + userID + ", issueDate=" + issueDate
				+ '}';
	}

	public void print() {
		System.out.println(this.toString());
	}
}