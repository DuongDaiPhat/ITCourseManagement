package entity;

import java.util.Objects;

public class Certificate {
	private int certificateID;
	private String certificateName;
	private int courseID;

	public Certificate() {
	}

	public Certificate(int certificateID, String certificateName, int courseID) {
		this.certificateID = certificateID;
		this.certificateName = certificateName;
		this.courseID = courseID;
	}

	public int getCertificateID() {
		return certificateID;
	}

	public void setCertificateID(int certificateID) {
		this.certificateID = certificateID;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public int getCourseID() {
		return courseID;
	}

	public void setCourseID(int courseID) {
		this.courseID = courseID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Certificate that = (Certificate) o;
		return certificateID == that.certificateID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(certificateID);
	}

	@Override
	public String toString() {
		return "Certificate{" + "certificateID=" + certificateID + ", certificateName='" + certificateName + '\''
				+ ", courseID=" + courseID + '}';
	}

	public void print() {
		System.out.println(this.toString());
	}
}