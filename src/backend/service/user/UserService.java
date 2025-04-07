package backend.service.user;

import backend.repository.user.UsersRepository;
import model.user.Users;

public class UserService {
	public Users GetUserByID(int id) {
		return UsersRepository.getInstance().SelectByID(id);
	}
	public Users GetNameByID(int id) {
		return null;
	}
}
