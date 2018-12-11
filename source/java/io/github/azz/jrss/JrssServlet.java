/* ****************************************************************************************************************** *
 * JrssServlet.java                                                                                                  *
 * github.com/a-zz, 2018                                                                                              *
 * ****************************************************************************************************************** */

package io.github.azz.jrss;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import io.github.azz.jrss.StackException.EnumErrorCodes;
import io.github.azz.logging.AppLogger;

/**
 * Stack operation servlet
 * @author a-zz
 * TODO Production-grade testing needed
 */
public class JrssServlet extends HttpServlet {

	private static final long serialVersionUID = 8833843983513571537L;
	private static AppLogger logger = new AppLogger(Stack.class); 
	
	/**
	 * Supported operations
	 */
	public enum EnumOperations { PSH, POP, GET, RST }

	/**
	 * URL to operation map
	 */
	private static HashMap<String,EnumOperations> urlOperationMap = new HashMap<>();
	static {
		urlOperationMap.put("psh", EnumOperations.PSH);
		urlOperationMap.put("pop", EnumOperations.POP);
		urlOperationMap.put("get", EnumOperations.GET);
		urlOperationMap.put("rst", EnumOperations.RST);
	}
	
	public enum EnumOperationStatus { OK, ERROR };
	
	/**
	 * JSON response values for operation statuses.
	 */
	private static HashMap<EnumOperationStatus,String> operationStatusJsonValues = new HashMap<>();
	static {
		operationStatusJsonValues.put(EnumOperationStatus.OK, "OK");
		operationStatusJsonValues.put(EnumOperationStatus.ERROR, "ER");
	}
		
	/**
	 * Error strings in JSON responses
	 */
	private static HashMap<StackException.EnumErrorCodes,String> jsonErrorMessages = new HashMap<>();
	static {
		jsonErrorMessages.put(StackException.EnumErrorCodes.E00, "ER00 - No error");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E01, "ER01 - Unsupported operation");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E09, "ER09 - Unknown error");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E10, "ER10 - Bad stackid");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E11, "ER11 - Stack does not exist");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E12, "ER12 - Stack is empty");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E30, "ER30 - DB is unavailable");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E31, "ER31 - DB read error");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E32, "ER32 - DB write error");
		jsonErrorMessages.put(StackException.EnumErrorCodes.E39, "ER39 - Unknown DB error");
	}
	
	public void init() throws ServletException {
		
		logger.debug("jrss servlet initialized");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		logger.info("Servlet received GET call");
		doGetOrPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
	    
		logger.info("Servlet received POST call");
		doGetOrPost(request, response);
	}

	private void doGetOrPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		JrssResponse jrssResponse = null;
		
		String opUrl = request.getRequestURI().substring(request.getContextPath().length()+1).trim();
		EnumOperations op = urlOperationMap.get(opUrl);
		if(op==null) {
			logger.warn("Unkown stack operation '" + opUrl + "', returning error");
			jrssResponse = new JrssResponse(EnumOperationStatus.ERROR, 
					null, null, StackException.EnumErrorCodes.E01);
		}
		else {			
			String stackid = URLEncoder.encode(request.getParameter("stackid"), "UTF-8");
			String data = request.getParameter("data");
			if(data!=null)
				data = URLEncoder.encode(data, "UTF-8");
			jrssResponse = stackOperation(op, stackid, data);
		}
		
		out.print(jrssResponse.toJson());
	}
	
	private JrssResponse stackOperation(EnumOperations operation, String stackid, String data) {
		
		try {
			Stack stack = new Stack(stackid);
			String opRespScalar;
			String[] opRespVector;	
			
			switch(operation) {
			case PSH:
				opRespScalar = stack.psh(data);
				return new JrssResponse(EnumOperationStatus.OK, opRespScalar, null, null);
			case POP:
				opRespVector = stack.pop();
				return new JrssResponse(EnumOperationStatus.OK, 
						opRespVector[0], opRespVector[1], null);
			case GET:
				opRespVector = stack.get();
				return new JrssResponse(EnumOperationStatus.OK, 
						opRespVector[0], opRespVector[1], null);
			case RST:
				opRespScalar = stack.rst();
				return new JrssResponse(EnumOperationStatus.OK, opRespScalar, null, null);
			}
			
			// Can't happen...
			return null;
		} catch(StackException e) {
			return new JrssResponse(EnumOperationStatus.ERROR, null, null, e.getErrorCode());
		}
	}
	
	public void destroy() {
	
		logger.debug("jrss servlet destroyed");
	} 
	
	/**
	 * A simple class for conveying servlet responses
	 * @author a-zz
	 */
	public class JrssResponse {
		
		private String status;
		private String ts;
		private String data;
		
		/**
		 * Constructor: populates the object 
		 * @param status (EnumOperationStatus) The status code.
		 * @param ts (String) The operation timestamp. If null (i.e. in error), the current date & time is used.
		 * @param data (String) The operation data. Ignored if errorCode is not null (data is filled with the text
		 * 	representation of the error). 
		 * @param errorCode (StackException.EnumErrorCodes) The operation error code.
		 */
		public JrssResponse(EnumOperationStatus status, String ts, String data, EnumErrorCodes errorCode) {
			
			this.status = operationStatusJsonValues.get(status);
			this.ts = ts!=null?ts:Long.toString(new Date().getTime());
			this.data = data!=null?data:
				errorCode!=null?jsonErrorMessages.get(errorCode):"";
		}

		/**
		 * Serializes the object into JSON
		 * @return (String)
		 */
		public String toJson() {
			
			return new Gson().toJson(this);
		}
		
		public String getStatus() {
			
			return status;
		}

		public String getTs() {
			
			return ts;
		}

		public String getData() {
			
			return data;
		}
	}
}
/* ****************************************************************************************************************** */