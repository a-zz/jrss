/* ****************************************************************************************************************** *
 * StackException.java                                                                                                *
 * github.com/a-zz, 2018                                                                                              *
 * ****************************************************************************************************************** */

package io.github.azz.jrss;

public class StackException extends Exception {

	private static final long serialVersionUID = -212124890027512331L;

	/**
	 * Error codes:<br/><ul>
	 * 	<strong>General errors:</strong><br/>
	 * 	<li>E00: No error (useless, won't ever be reported)</li>
	 * 	<li>E01: Unsupported operation</li>
	 * 	<li>E09: Unknown error</li>
	 * 	<strong>Data integrity errors:</strong><br/>
	 * 	<li>E11: Bad stackid (posibly null, zero-length or length above 1024 chars)</li>
	 * 	<li>E12: Stack doesn't exist</li>
	 * 	<li>E13: Stack is empty</li>
	 * 	<strong>DB storage related errors:</strong><br/>
	 * 	<li>E31: DB is unavailable</li>
	 * 	<li>E32: DB read error</li>
	 * 	<li>E33: DB write error</li>
	 * 	<li>E39: Unkown DB error</li>  
	 * </ul>
	 */
	public enum EnumErrorCodes {
		// E0x: General errors
		/** E00: No error (useless, won't ever be reported) */
		E00,
		/** E01: Unsupported operation */
		E01, 
		/** E09: Unkown error */
		E09,
		// E1x: Data integrity errors
		/** E11: Bad stackid */
		E11,
		/** E12: Stack doesn't exist */
		E12,
		/** E13: Stack is empty */
		E13,
		// E2x: DB storage related errors
		/** E31: DB unavailable */
		E31,
		/** E32: DB read error */
		E32,
		/** E33: DB write error */
		E33,
		/** E39: Unknown DB error */
		E39
	}	
	
	private EnumErrorCodes errorCode;
	
	/**
	 * Constructor
	 * @param errorCode (EnumErrorCodes) The error code.
	 */
	public StackException(EnumErrorCodes errorCode) {
		
		super("Stack handling error");
		this.errorCode = errorCode;
	}
	
	/**
	 * Gets the error code
	 * @return (EnumErrorCodes)
	 */
	public EnumErrorCodes getErrorCode() {
		
		return this.errorCode;
	}
}
/* ****************************************************************************************************************** */