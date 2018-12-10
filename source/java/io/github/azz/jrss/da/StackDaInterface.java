/* ****************************************************************************************************************** *
 * StackDaInterface.java                                                                                              *
 * github.com/a-zz, 2018                                                                                              *
 * ****************************************************************************************************************** */

package io.github.azz.jrss.da;

import io.github.azz.jrss.StackException;
import io.github.azz.sql.DaInterface;

/**
 * Data access interface for Stack class
 * @author a-zz
 */
public interface StackDaInterface extends DaInterface {

	/**
	 * Push data into a stack
	 * @param stackid (String) The stack id
	 * @param data (String) The data to be pushed
	 * @param create (boolean) Sets whether the stack should be created if it doesn't exist
	 * @return (String) THe pushed data timestamp
	 * @throws SQLException
	 */
	public String psh(String stackid, String data, boolean create) throws StackException;
	
	/**
	 * Gets the latest data from a stack
	 * @param stackid (String) The stack id
	 * @param delete (boolean) Sets whether the data should be deleted (pop functionality)
	 * @return (String[]) A String vector with values: [0] the data timestamp; [1] the actual data
	 * @throws SQLException
	 */
	public String[] get(String stackid, boolean delete) throws StackException;
	
	/**
	 * Resets (i.e. destroys a stack)
	 * @param stackid (String) The stack id
	 * @return (String) The operation timestamp
	 * @throws StackException
	 */
	public String rst(String stackid) throws StackException;
}
/* ****************************************************************************************************************** */