package ca.bcit.comp4016.A00985511.Assignment02.servlets;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Servlet class for the Hello POST response.
 * If GET is attempted, will fail and if provide the same message as a malformed POST response is.
 * Expecting a JSON object as the Post body containing a PARAMTER_NAME key with a corresponding value.
 * @author Alexander Ryan (A00985511)
 * 
 */

@WebServlet(name="HelloServlet", urlPatterns={"/hello"})
public class HelloServlet extends HttpServlet {
	
	private static final long serialVersionUID;
	
	public static final String POST_DATA_PARAMETER_NAME = "jsondata";
	
	public static final String PARAMETER_NAME;
	
	private static final String BAD_REQUEST_MESSAGE;
	
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	private static final Logger LOG;
	
	
	static {
		serialVersionUID = 3843089809063569346L;
		
		
		PARAMETER_NAME = "name";
		
		BAD_REQUEST_MESSAGE = String.format("Hello Servlet could not complete request " + 
											"- must provide POST with JSON parameter %s!", PARAMETER_NAME);
		
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		LOG = LogManager.getLogger(HelloServlet.class);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		LOG.info("Initialized Hello Servlet");
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		
		LOG.info(BAD_REQUEST_MESSAGE);
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		res.getWriter().println(BAD_REQUEST_MESSAGE);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		
		String name = null;
		
		final String jsonObject = req.getReader().readLine(); // TODO: Fix this so that it doesn't break on a single newline. For now, was just trying to get it to work.
		
		if(jsonObject != null) {
			try {
				final JSONObject parsedObject = new JSONObject(jsonObject);
				name = parsedObject.getString(PARAMETER_NAME);
			}catch(JSONException e) {
				LOG.warn("JSON Object not found for POST object");
			}
		}else {
			LOG.warn(String.format("Could not found data at %s", POST_DATA_PARAMETER_NAME));
		}
		
		if(name != null) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
			resp.getWriter().println(String.format("Hello %s!", name));
		}else {
			doGet(req, resp);
		}
	}

}
