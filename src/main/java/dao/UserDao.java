package dao;

import domain.Teacher;
import domain.User;
import util.JdbcHelper;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 1. 设计UserService和UseDao，有以下功能
 * 1.1 修改密码changePassword
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
public final class UserDao {
	private static UserDao userDao=new UserDao();
	private UserDao(){}
	public static UserDao getInstance(){
		return userDao;
	}
	private static Collection<User> users;

	public Collection<User> findAll() throws SQLException {
		Set<User> departments = new HashSet<User>();
		//获得连接对象
		Connection connection = JdbcHelper.getConn();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from user");
		//若结果集仍然有下一条记录，则执行循环体
		while (resultSet.next()){
			Teacher teacher = TeacherDao.getInstance().find(resultSet.getInt("teacher_id"));
			//以当前记录中的id,description,no,remarks值为参数，创建Degree对象
			User user = new User(
					resultSet.getInt("id"),
					resultSet.getString("username"),
					resultSet.getString("password"),
					new Date(),
					teacher);
			//向degrees集合中添加Degree对象
			departments.add(user);
		}
		//关闭资源
		JdbcHelper.close(resultSet,statement,connection);
		return departments;
	}

	public User find(Integer id) throws SQLException {
		User user = null;
		Connection connection = JdbcHelper.getConn();
		String user_sql = "SELECT * FROM user WHERE id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(user_sql);
		//为预编译参数赋值
		preparedStatement.setInt(1,id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()){
			Teacher teacher = TeacherDao.getInstance().find(resultSet.getInt("teacher_id"));
			user = new User(
					resultSet.getInt("id"),
					resultSet.getString("username"),
					resultSet.getString("password"),
					new Date(),
					teacher);
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return user;
	}

	public User findByUsername(String username) throws SQLException {
		User user = null;
		Connection connection = JdbcHelper.getConn();
		String user_sql = "SELECT * FROM user WHERE username=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(user_sql);
		//为预编译参数赋值
		preparedStatement.setString(1,username);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
		//若结果集中没有记录，则本方法返回null
		if (resultSet.next()){
			Teacher teacher = TeacherDao.getInstance().find(resultSet.getInt("teacher_id"));
			user = new User(
					resultSet.getInt("id"),
					resultSet.getString("username"),
					resultSet.getString("password"),
					new Date(),
					teacher);
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return user;
	}
	
	public boolean changePassword(User user) throws SQLException {
		Connection connection = JdbcHelper.getConn();
		//写sql语句
		String changePassword_sql = " update user set password=? where id=? ";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(changePassword_sql);
		//为预编译参数赋值
		preparedStatement.setString(1,user.getPassword());
		preparedStatement.setInt(2,user.getId());
		//执行预编译语句，获取改变记录行数并赋值给affectedRowNum
		int affectedRows = preparedStatement.executeUpdate();
		//关闭资源
		JdbcHelper.close(preparedStatement,connection);
		return affectedRows>0;
	}

	public boolean add(User user) throws SQLException {
		//获取连接对象
		Connection connection= JdbcHelper.getConn();
		//创建sql语句
		String addUser_sql="Insert into user (id,username,password,teacher_id) values (?,?,?,?)";
		//在该连接上创建预编译语句对象
		PreparedStatement pstmt =connection.prepareStatement(addUser_sql);
		//为预编译参数赋值
		pstmt.setInt(1,user.getId());
		pstmt.setString(2,user.getUsername());
		pstmt.setString(3,user.getPassword());
		pstmt.setInt(4,user.getTeacher().getId());
		//执行预编译对象的excuteUpdate方法
		int affectedRowNum=pstmt.executeUpdate();
		//显示添加的记录的行数
		System.out.println("添加了"+affectedRowNum+"条记录");
		JdbcHelper.close(pstmt,connection);
		return true;
	}

	public boolean delete(Integer id) throws SQLException {
		//获取连接对象
		Connection connection= JdbcHelper.getConn();
		//创建sql语句
		String addUser_sql="Delete from user where id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement pstmt =connection.prepareStatement(addUser_sql);
		//为预编译参数赋值
		pstmt.setInt(1,id);
		//执行预编译对象的excuteUpdate方法
		int affectedRowNum=pstmt.executeUpdate();
		//显示删除的记录的行数
		System.out.println("删除了"+affectedRowNum+"条记录");
		JdbcHelper.close(pstmt,connection);
		return true;
	}

	//登录
	public User login(String username,String password) throws SQLException {
		//根据用户名查找对应的user
		User user = userDao.findByUsername(username);
		//声明变量desiredUser
		User desiredUser = null;
		//判断前台传过来的user及密码是否与查找结果一致
		if(username.equals(user.getUsername()) && password.equals(user.getPassword())){
			desiredUser = user;
		}
		return desiredUser;
	}
}
