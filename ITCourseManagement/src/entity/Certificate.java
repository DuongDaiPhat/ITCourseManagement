package entity;

public class Certificate {
	private int certificateId;
	private String certificateName;
	private int courseId;

	// Constructor
	public Certificate(int certificateId, String certificateName, int courseId) {
		this.certificateId = certificateId;
		this.certificateName = certificateName;
		this.courseId = courseId;
	}

	public int getCertificateId() {
		return certificateId;
	}

	public void setCertificateId(int certificateId) {
		this.certificateId = certificateId;
	}

	public String getCertificateName() {
		return certificateName;
	}

	public void setCertificateName(String certificateName) {
		this.certificateName = certificateName;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
}