package site.metacoding.miniproject.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.metacoding.miniproject.domain.user.User;
import site.metacoding.miniproject.domain.user.UserDao;
import site.metacoding.miniproject.web.dto.request.LoginDto;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserDao userDao;

	public User 로그인(LoginDto loginDto) {
		User userPS = userDao.findByUsername(loginDto.getUsername());
		if (userPS == null) {
			return null;
		}

		if (userPS.getPassword().equals(loginDto.getPassword())) {
			return userPS;
		}
		return null;
	}

	public User 유저네임으로유저찾기(String Username) {
		User userPS = userDao.findByUsername(Username);
		return userPS;
	}

	public void 유저삭제하기(Integer userId) {
		userDao.deleteUserId(userId);
	}
}
