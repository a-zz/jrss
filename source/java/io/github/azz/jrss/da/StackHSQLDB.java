/* ****************************************************************************************************************** *
 * StackHSQLDB.java                                                                                                   *
 * github.com/a-zz, 2018                                                                                              *
 * ****************************************************************************************************************** */

package io.github.azz.jrss.da;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import io.github.azz.jrss.StackException;
import io.github.azz.jrss.StackException.EnumErrorCodes;
import io.github.azz.sql.SqlTransaction;
import io.github.azz.sql.rdbms.HSQLDBInterface;

/**
 * HSQLDB data access implementation for Stack class
 * TODO Consider whether db-replicable UUIDs should be used. 
 * @author a-zz
 */
public class StackHSQLDB  implements StackDaInterface, HSQLDBInterface {

	@Override
	public String psh(String stackid, String data, boolean create) throws StackException {
		
		SqlTransaction t = null;
		String sql = null;
		EnumErrorCodes currentOperationErrorCode = null;
		
		try {
			// 1. Setups the transaction
			currentOperationErrorCode = EnumErrorCodes.E31;
			t = new SqlTransaction("Stack push", false);
			
			// 2. Check whether the stack exists
			long id = -1;
			currentOperationErrorCode = EnumErrorCodes.E32;
			sql = "select id from STACKS where STACKID='" + stackid + "'";
			ResultSet rs1 = t.query(sql);
			if(rs1.next())
				id = rs1.getLong("ID");
			
			// 2.1. Create stack if needed to
			if(id==-1) {
				currentOperationErrorCode = EnumErrorCodes.E33;
				sql = "insert into STACKS (STACKID) values ('" + stackid + "')";
				t.statement(sql);
				currentOperationErrorCode = EnumErrorCodes.E32;
				sql = "select id from STACKS where STACKID='" + stackid + "'";
				ResultSet rs2 = t.query(sql);
				rs2.next();
				id = rs2.getLong("ID");
			}

			// 3. Insert data into the stack
			currentOperationErrorCode = EnumErrorCodes.E33;
			sql = "insert into STACK_DATA (STACK_ID, DATA) values (" + id + ", ?)";
			ArrayList<Object> values = new ArrayList<>();
			values.add(data);
			t.preparedStatement(sql, values);

			// 4. Get the data timestamp
			currentOperationErrorCode = EnumErrorCodes.E32;
			sql = "select unix_millis(TS) as TS from STACK_DATA where STACK_ID=" + id + " order by ID desc";
			ResultSet rs3 = t.query(sql);
			rs3.next();
			String result =  Long.toString(rs3.getLong("TS"));
						
			t.commit();
			return result;
		}
		catch(SQLException e) {
			throw new StackException(currentOperationErrorCode!=null?
					currentOperationErrorCode:
						EnumErrorCodes.E09);
		}
		finally {
			t.close();
		}
	}

	@Override
	public String[] get(String stackid, boolean delete) throws StackException {
		
		SqlTransaction t = null;
		String sql = null;
		EnumErrorCodes currentOperationErrorCode = null;
		
		try {
			// 1. Setups the transaction
			currentOperationErrorCode = EnumErrorCodes.E31;
			t = new SqlTransaction("Stack pop/get", true);
			
			// 2. Get data from the stack
			currentOperationErrorCode = EnumErrorCodes.E32;
			long id = -1;
			String[] result = new String[2];
			sql = "select		D.ID, unix_millis(D.TS) as TS, D.DATA " + 
					"from		STACKS S left join STACK_DATA D on S.ID=D.STACK_ID " + 
					"where		S.STACKID='" + stackid + "' " +
					"order by 	D.TS desc";
			ResultSet rs = t.query(sql);
			if(!rs.next())
				throw new StackException(EnumErrorCodes.E12);
			else {
				String data = rs.getString("DATA");
				if(data==null)
					throw new StackException(EnumErrorCodes.E13);
				else {
					id = rs.getLong("ID");
					result[0] = Long.toString(rs.getLong("TS"));
					result[1] = data;
				}
			}
			
			// 3. Delete the last value, if instructed
			if(delete) {
				currentOperationErrorCode = EnumErrorCodes.E33;
				sql = "delete from STACK_DATA where ID=" + id;
				t.statement(sql);
			}
			
			return result;
		}
		catch(SQLException e) {
			throw new StackException(currentOperationErrorCode!=null?
					currentOperationErrorCode:
						EnumErrorCodes.E09);
		}
		finally {
			t.close();
		}
	}

	@Override
	public String rst(String stackid) throws StackException {

		SqlTransaction t = null;
		String sql = null;
		EnumErrorCodes currentOperationErrorCode = null;
		
		try {
			// 1. Setups the transaction
			currentOperationErrorCode = EnumErrorCodes.E31;
			t = new SqlTransaction("Stack reset", false);
			
			// 2. Check whether the stack exists
			long id = -1;
			long timestamp = -1;
			currentOperationErrorCode = EnumErrorCodes.E32;
			sql = "select unix_millis(current_timestamp) as TS, id from STACKS where STACKID='" + stackid + "'";
			ResultSet rs1 = t.query(sql);
			if(rs1.next()) {
				id = rs1.getLong("ID");
				timestamp = rs1.getLong("TS");
			}
			else
				throw new StackException(EnumErrorCodes.E12);
			
			// 3. Destroys stack data and the stack itself
			currentOperationErrorCode = EnumErrorCodes.E33;
			sql = "delete from STACK_DATA where STACK_ID=" + id;
			t.statement(sql);
			sql = "delete from STACKS where ID=" + id;
			t.statement(sql);
						
			t.commit();
			return Long.toString(timestamp);
		}
		catch(SQLException e) {
			throw new StackException(currentOperationErrorCode!=null?
					currentOperationErrorCode:
						EnumErrorCodes.E09);
		}
		finally {
			t.close();
		}	
	}
}
/* ****************************************************************************************************************** */