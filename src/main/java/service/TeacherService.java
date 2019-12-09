package service;

import dao.TeacherDao;
import domain.Teacher;

import java.sql.SQLException;
import java.util.Collection;
		/**
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
public final class TeacherService {
	private static TeacherDao teacherDao= TeacherDao.getInstance();
	private static TeacherService teacherService=new TeacherService();
	private TeacherService(){}
	
	public static TeacherService getInstance(){
		return teacherService;
	}
	
	public Collection<Teacher> findAll()throws SQLException {
		return teacherDao.findAll();
	}
	
	public Teacher find(Integer id)throws SQLException{
		return teacherDao.find(id);
	}
	
	public boolean update(Teacher teacher)throws SQLException{
		return teacherDao.update(teacher);
	}

	public boolean delete(Integer id)throws SQLException{
		return teacherDao.delete(id);
	}
	public boolean add(Teacher teacher) throws SQLException,ClassNotFoundException {
		return teacherDao.add(teacher);
	}
}
