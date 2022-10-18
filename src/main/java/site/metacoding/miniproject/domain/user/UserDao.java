package site.metacoding.miniproject.domain.user;

import java.util.List;

public interface UserDao {
	public void insert(User user);

	public User findById(Integer userId);

	public List<User> findAll();

	public void update(User user); // dto생각

	public void deleteById(Integer userId);

	public User findByUsername(String username);

	public void deleteUserId(Integer userId);
}
