package domain;

import java.io.Serializable;
import java.util.Date;

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
public class User implements Comparable<User>,Serializable{
	private Integer id;
	private String username;
	private String password;
	private Date loginTime;
	private Teacher teacher;
	public User(){}
	
	public User(Integer id, String username, String password, Date loginTime,
                Teacher teacher) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.loginTime = loginTime;
		this.teacher = teacher;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Teacher getTeacher() {
		return teacher;
	}

	public void setTeacher(Teacher teacher) {
		this.teacher = teacher;
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "Login ( "
	        + super.toString() + TAB
	        + "id = " + this.id + TAB
	        + "username = " + this.username + TAB
	        + "password = " + this.password + TAB
	        + "loginTime = " + this.loginTime + TAB
	        + "teacher = " + this.teacher + TAB
	        + " )";
	
	    return retValue;
	}

	@Override
	public int compareTo(User o) {
		// TODO Auto-generated method stub
		return this.id-o.id;
	}

}
