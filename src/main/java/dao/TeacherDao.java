package dao;

import domain.*;
import service.UserService;
import util.JdbcHelper;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
/**
 * 2.修改TeacherDao，用事务实现：
 * 2.1增加教师后，立即增加User记录，用户名和初始密码均为教师的no字段。
 * 2.2 删除教师后，立即删除对应的User记录
 * 注：User中有id，username，password和Teacher关联属性
 * 提示：
 * 增加教师：
 *  先为Teacher表增加一条记录，获得新增记录的id，然后为本方法的teacher参数赋id。
 *  然后创建一个User对象，关联已经拥有id的Teacher对象，然后将User对象保存到表中。
 * 删除教师： 在user表中先删teacherId为当前teacher的id的user记录，然后再删teacher记录
 * 如果使用UserDao中的方法增加/删除User时，
 * 需要保证TeacherDao和UserDao使用同一个Connection对象来完成事务。
 */
public final class TeacherDao {
	private static TeacherDao teacherDao=new TeacherDao();
	private TeacherDao(){}
	public static TeacherDao getInstance(){
		return teacherDao;
	}
	public Collection<Teacher> findAll()throws SQLException {
		Set<Teacher> teachers = new HashSet<Teacher>();
		//获得连接对象
		Connection connection = JdbcHelper.getConn();
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from teacher");
		//若结果集仍然有下一条记录，则执行循环体
		while (resultSet.next()){
			//以当前记录中的id,description,no,remarks值为参数，创建Degree对象
			ProfTitle profTitle = ProfTitleDao.getInstance().find(resultSet.getInt("profTitle_id"));
			Degree degree = DegreeDao.getInstance().find(resultSet. getInt("degree_id"));
			Department department = DepartmentDao.getInstance().find(resultSet.getInt("department_id"));
			Teacher teacher = new Teacher(
					resultSet.getInt("id"),
					resultSet.getString("name"),
					profTitle,degree,department
			);
			//向degrees集合中添加Degree对象
			System.out.println(teacher);
			teachers.add(teacher);
		}
		//关闭资源
		JdbcHelper.close(resultSet,statement,connection);
		return teachers;
	}

	public Teacher find(Integer id) throws SQLException{
		Teacher teacher = null;
		Connection connection = JdbcHelper.getConn();
		String deleteTeacher_sql = "SELECT * FROM teacher WHERE id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(deleteTeacher_sql);
		//为预编译参数赋值
		preparedStatement.setInt(1,id);
		ResultSet resultSet = preparedStatement.executeQuery();
		//由于id不能取重复值，故结果集中最多有一条记录
		//若结果集有一条记录，则以当前记录中的id,description,no,remarks值为参数，创建Degree对象
		//若结果集中没有记录，则本方法返回null

		if (resultSet.next()){
			ProfTitle profTitle = ProfTitleDao.getInstance().find(resultSet.getInt("profTitle_id"));
			Degree degree = DegreeDao.getInstance().find(resultSet.getInt("degree_id"));
			Department department = DepartmentDao.getInstance().find(resultSet.getInt("department_id"));
			teacher = new Teacher(
					resultSet.getInt("id"),
					resultSet.getString("name"),
					profTitle,degree,department);
		}
		//关闭资源
		JdbcHelper.close(resultSet,preparedStatement,connection);
		return teacher;
	}

	public boolean add(Teacher teacher) throws SQLException{
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		int affectedRowNum = 0;
		try {
			connection = JdbcHelper.getConn();
			//关闭自动提交(事件开始）
			connection.setAutoCommit(false);
			String addTeacher_sql = "INSERT INTO teacher(name,profTitle_id,degree_id,department_id) VALUES"+" (?,?,?,?)";
			//在该连接上创建预编译语句对象
			preparedStatement = connection.prepareStatement(addTeacher_sql);
			//为预编译参数赋值
			preparedStatement.setString(1,teacher.getName());
			preparedStatement.setInt(2,teacher.getTitle().getId());
			preparedStatement.setInt(3,teacher.getDegree().getId());
			preparedStatement.setInt(4,teacher.getDepartment().getId());
			//执行预编译语句，获取添加记录行数并赋值给affectedRowNum
			affectedRowNum = preparedStatement.executeUpdate();
			String addUser_sql = "INSERT INTO User (username,password,teacher_id) VALUES" + " (?,?,?)";
			//在该连接上创建预编译语句对象
			preparedStatement = connection.prepareStatement(addUser_sql);
			//为预编译参数赋值
			preparedStatement.setString(1, teacher.getName());
			preparedStatement.setString(2, teacher.getName());
			preparedStatement.setInt(3, teacher.getId());
			preparedStatement.executeUpdate();
			//提交当前连接所做的操作（事件以提交结束）
			connection.commit();
		}catch (SQLException e){
			e.printStackTrace();
			try{
				//回滚当前连接所作的操作
				if (connection != null){
					//事件以回滚结束
					connection.rollback();
				}
			}catch (SQLException e1){
				e1.printStackTrace();
			}
		}catch (Exception e){
			e.printStackTrace();
			try{
				//回滚当前连接所作的操作
				if (connection != null){
					//事件以回滚结束
					connection.rollback();
				}
			}catch (SQLException e1){
				e1.printStackTrace();
			}
		} finally {
			try{
				//恢复自动提交
				if (connection!=null){
					connection.setAutoCommit(true);
				}
			}catch (SQLException e){
				e.printStackTrace();
			}
			//关闭资源
			JdbcHelper.close(preparedStatement,connection);
		}
		return affectedRowNum>0;
	}
	//delete方法，根据degree的id值，删除数据库中对应的degree对象
	public boolean delete(int id) throws SQLException{
		Connection connection = null;
		PreparedStatement pstmt=null;
		int affectedRowNum = 0;
		try {
			connection = JdbcHelper.getConn();
			//关闭自动提交(事件开始）
			connection.setAutoCommit(false);
			//创建PreparedStatement接口对象，包装编译后的目标代码（可以设置参数，安全性高）
			pstmt = connection.prepareStatement("DELETE FROM user WHERE teacher_id = ?");
			//为预编译的语句参数赋值
			pstmt.setInt(1, id);
			//执行预编译对象的executeUpdate()方法，获取删除记录的行数
			pstmt.executeUpdate();
			System.out.println("delete user");
			//创建PreparedStatement接口对象，包装编译后的目标代码（可以设置参数，安全性高）
			pstmt = connection.prepareStatement("DELETE FROM Teacher WHERE ID = ?");
			//为预编译的语句参数赋值
			pstmt.setInt(1, id);
			//执行预编译对象的executeUpdate()方法，获取删除记录的行数
			affectedRowNum = pstmt.executeUpdate();
			System.out.println("删除了老师的 " + affectedRowNum + " 条");
			//提交当前连接所做的操作（事件以提交结束）
			connection.commit();
		}catch (SQLException e){
			System.out.println(e.getMessage());
			try{
				//回滚当前连接所作的操作
				if (connection != null){
					//事件以回滚结束
					connection.rollback();
				}
			}catch (SQLException e1){
				e1.printStackTrace();
			}
		}catch (Exception e){
			e.printStackTrace();
			try{
				//回滚当前连接所作的操作
				if (connection != null){
					//事件以回滚结束
					connection.rollback();
				}
			}catch (SQLException e1){
				e1.printStackTrace();
			}
		}finally {
			try{
				//恢复自动提交
				if (connection!=null){
					connection.setAutoCommit(true);
				}
			}catch (SQLException e){
				e.printStackTrace();
			}
			//关闭资源
			JdbcHelper.close(pstmt,connection);
		}
		return affectedRowNum > 0;
	}

	public boolean update(Teacher teacher) throws SQLException{
		Connection connection = JdbcHelper.getConn();
		//写sql语句
		String updateDegree_sql = " update teacher set name =? where id=?";
		//在该连接上创建预编译语句对象
		PreparedStatement preparedStatement = connection.prepareStatement(updateDegree_sql);
		//为预编译参数赋值
		preparedStatement.setString(1,teacher.getName());
		preparedStatement.setInt(2,teacher.getId());
		//执行预编译语句，获取改变记录行数并赋值给affectedRowNum
		int affectedRows = preparedStatement.executeUpdate();
		//关闭资源
		JdbcHelper.close(preparedStatement,connection);
		return affectedRows>0;
	}

	//创建main方法，查询数据库中的对象，并输出
	public static void main(String[] args) throws ClassNotFoundException,SQLException{
		TeacherDao.getInstance().findAll();
		System.out.println();
		//增
		ProfTitle profTitle = ProfTitleDao.getInstance().find(36);
		Degree degree = DegreeDao.getInstance().find(36);
		Department department = DepartmentDao.getInstance().find(1);
		Teacher teacher = new Teacher(3,"WTeacher",profTitle,degree,department);
		System.out.println(teacher);
		TeacherDao.getInstance().findAll();
		System.out.println();
		//删
		TeacherDao.getInstance().delete(3);
		TeacherDao.getInstance().findAll();
		System.out.println();
		//改
		Teacher teacher1 = TeacherDao.getInstance().find(1);
		System.out.println(teacher1);
		teacher1.setName("WTeacher");
		TeacherDao.getInstance().update(teacher1);
		Teacher teacher2 = TeacherDao.getInstance().find(1);
		System.out.println(teacher2.getName());
	}
}
