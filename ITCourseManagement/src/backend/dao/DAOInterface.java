package backend.dao;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DAOInterface <T>{
	
	public int Insert(T t) throws SQLException;
	
	public int Update(T t) throws SQLException;
	
	public int Delete(T t) throws SQLException;
	
	public ArrayList<T> SelectAll() throws SQLException;
	
	public T SelectByID(T t) throws SQLException;
	
	public ArrayList<T> SelectByCondition(String condition) throws SQLException;
}
