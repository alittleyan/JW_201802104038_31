package service;
/**
 * 1. 设计UserService和UseDao，有以下功能1.1 修改密码changePassword
 * 1.2 按id查询find，按用户名查询findByUsername
 * 1.3 登录User login(String username, String password) 如果有同时满足用户名和密码的记录，返回该记录对应的User对象，否则返回null.
 * 2.修改TeacherDao，用事务实现：
 * 2.1增加教师后，立即增加User记录，用户名和初始密码均为教师的no字段。
 * 2.2 删除教师后，立即删除对应的User记录
 * 注：User中有id，username，password和Teacher关联属性
 * 提示：
 * 增加教师：
 *  先为Teacher表增加一条记录，获得新增记录的id，然后为本方法的teacher参数赋id。然后创建一个User对象，关联已经拥有id的Teacher对象，然后将User对象保存到表中。
 * 删除教师： 在user表中先删teacherId为当前teacher的id的user记录，然后再删teacher记录
 * 如果使用UserDao中的方法增加/删除User时，需要保证TeacherDao和UserDao使用同一个Connection对象来完成事务。
 */
import dao.UserDao;
import domain.User;

import java.sql.SQLException;
import java.util.Collection;

public final class UserService {
	private UserDao userDao = UserDao.getInstance();
	private static UserService userService = new UserService();
	
	public UserService() {
	}
	
	public static UserService getInstance(){
		return UserService.userService;
	}

	public Collection<User> getUsers() throws SQLException {
		return userDao.findAll();
	}
	
	public User getUser(Integer id) throws SQLException {
		return userDao.find(id);
	}
	public User findByUserName(String username) throws SQLException {
		return userDao.findByUsername(username);
	}
	
	public boolean changePassword(User user) throws SQLException {
		return userDao.changePassword(user);
	}
	
	public boolean addUser(User user) throws SQLException {
		return userDao.add(user);
	}

	public boolean deleteUser(Integer id) throws SQLException {
		return userDao.delete(id);
	}

	public User login(String username,String password) throws SQLException {
		return userDao.login(username,password);
	}
}
