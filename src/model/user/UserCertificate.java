package model.user;

import java.util.Date;

public class UserCertificate {
	private int certificateId;
	private int userId;
	private Date issueDate;

	public UserCertificate() {
	}

	public UserCertificate(int certificateId, int userId, Date issueDate) {
		this.certificateId = certificateId;
		this.userId = userId;
		this.issueDate = issueDate;
	}

	public int getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(int certificateId) {
		this.certificateId = certificateId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
}