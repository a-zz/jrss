/* ****************************************************************************************************************** *
 * Stack.java                                                                                                         *
 * github.com/a-zz, 2018                                                                                              *
 * ****************************************************************************************************************** */

package io.github.azz.jrss;

import io.github.azz.jrss.StackException.EnumErrorCodes;
import io.github.azz.jrss.da.StackDaInterface;
import io.github.azz.logging.AppLogger;
import io.github.azz.sql.DaInterface;

/**
 * Stack handling
 * @author a-zz
 */
public class Stack {
	
	private static AppLogger logger = new AppLogger(Stack.class);
	
	private final static StackDaInterface dao = init();
	private static StackDaInterface init() {	
		try {
			return (StackDaInterface)DaInterface.
				getImplClassFor(Stack.class).newInstance();
		}
		catch(Exception e) {
			logger.error("Unable to instantiate data access implementation " +
				"class for " + Stack.class);
			return null;
		}
	}
	
	private String stackid;

	/**
	 * Constructor
	 * @param stackid (String) The stack id
	 * @throws StackException
	 */
	public Stack(String stackid) throws StackException {
	
		if(stackid==null ||
				stackid.length()==0 ||
				stackid.length()>2014)
			throw new StackException(EnumErrorCodes.E11);
		
		this.stackid = stackid;
	}
	
	/**
	 * Pushes data into the stack. The stack is created if it doesn't exist.
	 * @param data (String) The data to push. If null, an empty string is pushed.
	 * @return (String) The operation timestamp. 
	 * @throws StackException
	 */
	public String psh(String data) throws StackException {
		
		data = data!=null?data:"";
		logger.debug("Pushing " + data.getBytes().length + " bytes of data into stack " + stackid);
		return dao.psh(stackid, data, true);
	}
	
	/**
	 * Pops data from the stack (get latest value and delete it)
	 * @return (String[]) A String vector with values: [0] the data timestamp; [1] the actual data 
	 * @throws StackException
	 */
	public String[] pop() throws StackException {

		logger.debug("Popping data from stack " + stackid);
		return dao.get(stackid, true);
	}
	
	/**
	 * Gets data from the stack (get latest value but don't delete it)
	 * @return (String[]) A String vector with values: [0] the data timestamp; [1] the actual data 
	 * @throws StackException
	 */
	public String[] get() throws StackException {

		logger.debug("Getting data from stack " + stackid);
		return dao.get(stackid, false);
	}
	
	/**
	 * Resets (i.e. destroys) the stack
	 * @return (String) The operation timestamp
	 * @throws StackException
	 */
	public String rst() throws StackException {

		logger.debug("Resetting stack " + stackid);
		return dao.rst(stackid);
	}
}
/* ****************************************************************************************************************** */