package model.user;

import java.time.LocalDate;

public class Admin extends Users {
	public Admin() {
		super();
	}

	public Admin(int userID, int roleID, String userFirstName, String userLastName, String userName, String password,
			String phoneNumber, String email, String description, UserStatus status, LocalDate createdAt) {
		super(userID, roleID, userFirstName, userLastName, userName, password, phoneNumber, email, description, status,
				createdAt);
	}
}