package entity;

import java.time.LocalDate;

public class Users {
	private int userID;
	private int roleID;
	private String userFullName;
	private String userName;
	private String password;
	private String phoneNumber;
	private String email;
	private String description;
	private UserStatus status;
	private LocalDate createdAt;
	
	public Users() {
		super();
	}
	
	public Users(int userID, int roleID, String userFullName, String userName, String password, String phoneNumber,
			String email, String description, UserStatus status, LocalDate createdAt) {
		super();
		this.userID = userID;
		this.roleID = roleID;
		this.userFullName = userFullName;
		this.userName = userName;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.description = description;
		this.status = status;
		this.createdAt = createdAt;
	}



	public int getUserID() {
		return userID;
	}
	
	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUserFullName() {
		return userFullName;
	}

	public void setUserFullName(String userFullName) {
		this.userFullName = userFullName;
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
}
