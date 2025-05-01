package model.user;

import java.time.LocalDate;

public class Users {
	private int userID;
    private String userFirstName;
    private String userLastName;
    private String userName;
    private String password;
    private String salt; // Thêm trường salt
    private String phoneNumber;
    private String email;
    private String description;
    private UserStatus status;
    private LocalDate createdAt;
    private int roleID;

	
	public Users() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Users(int userID, String userFirstName, String userLastName, String userName, String password, String salt,
			String phoneNumber, String email, String description, UserStatus status, LocalDate createdAt, int roleID) {
		super();
		this.userID = userID;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.userName = userName;
		this.password = password;
		this.salt = salt;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.description = description;
		this.status = status;
		this.createdAt = createdAt;
		this.roleID = roleID;
	}


	public int getUserID() {
		return userID;
	}


	public void setUserID(int userID) {
		this.userID = userID;
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


	public String getSalt() {
		return salt;
	}


	public void setSalt(String salt) {
		this.salt = salt;
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


	public int getRoleID() {
		return roleID;
	}


	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}


	@Override
	public String toString() {
		return "Users [userID=" + userID + ", userFirstName=" + userFirstName + ", userLastName=" + userLastName
				+ ", userName=" + userName + ", password=" + password + ", salt=" + salt + ", phoneNumber="
				+ phoneNumber + ", email=" + email + ", description=" + description + ", status=" + status
				+ ", createdAt=" + createdAt + ", roleID=" + roleID + "]";
	}


	public void print() {
		System.out.println(this.toString());
	}
}