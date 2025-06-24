package model.user;

import java.time.LocalDate;

public class Users {
	private int userID;
	private int roleID;
	private String userFirstName;
	private String userLastName;
	private String userName;
	private String password;
	private String phoneNumber;
	private String email;
	private String salt;
	private String description;
	private UserStatus status;
	private LocalDate createdAt;
	private int courseCount;
	private int warningCount;

	public Users() {
		super();
	}

	public Users(int userID, int roleID, String userFirstName, String userLastName, String userName, String password,
			String phoneNumber, String email, String description, UserStatus status, LocalDate createdAt) {
		super();
		this.userID = userID;
		this.roleID = roleID;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.userName = userName;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.description = description;
		this.status = status;
		this.createdAt = createdAt;
	}

	// Getters and Setters
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

	public int getCourseCount() {
		return courseCount;
	}

	public void setCourseCount(int courseCount) {
		this.courseCount = courseCount;
	}

	public int getWarningCount() {
		return warningCount;
	}

	public void setWarningCount(int warningCount) {
		this.warningCount = warningCount;
	}

	@Override
	public String toString() {
		return "Users [userID=" + userID + ", roleID=" + roleID + ", userFirstName=" + userFirstName + ", userLastName="
				+ userLastName + ", userName=" + userName + ", password=" + password + ", phoneNumber=" + phoneNumber
				+ ", email=" + email + ", salt=" + salt + ", description=" + description + ", status=" + status
				+ ", createdAt=" + createdAt + ", courseCount=" + courseCount + ", warningCount=" + warningCount + "]";
	}

	public void print() {
		System.out.println(this.toString());
	}
}