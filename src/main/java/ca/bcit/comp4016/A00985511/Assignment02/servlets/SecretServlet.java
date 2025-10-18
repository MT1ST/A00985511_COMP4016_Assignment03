package ca.bcit.comp4016.A00985511.Assignment02.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.bcit.comp4016.A00985511.Assignment02.configurations.ValueMappings;

/**
 * Servlet to return the secretValue mapped value to the keys that we have configured for this server as secrets.
 * @author Alexander Ryan (#A00985511)
 *
 */
//@WebServlet(name="SecretServlet", urlPatterns={"/secretValue"})
public class SecretServlet extends HttpServlet{
	private static final long serialVersionUID;
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
//	private static final String OUTPUT_MESSAGE;
	private static final Logger LOG;
	
	static {
		serialVersionUID = 7122485076962591694L;
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
//		OUTPUT_MESSAGE = "secretSnake";
		LOG = LogManager.getLogger(SecretServlet.class);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		LOG.info("Initialized Secret Servlet");
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		
		LOG.info("Secret Servlet Get receieved, proceeding to retrieve Secret!");
		
		final String requestedKey = req.getRequestURI().substring(ValueMappings.REQUEST_URI_SLICE_INDEX_OFFSET);
		LOG.info("Secret key requested: " + requestedKey);
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		if(ValueMappings.VALUE_MAPS.get(ValueMappings.SECRET_MAP_KEY).containsKey(requestedKey)) {
			LOG.info("Secret Key found - returning value.");
			res.setStatus(HttpServletResponse.SC_OK);
//			
			// Returns the specific secret map key, for the given url mapping.
			res.getWriter().println(ValueMappings.VALUE_MAPS.get(ValueMappings.SECRET_MAP_KEY).get(requestedKey));
		}else {
			LOG.info("Secret Key not found.");
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			res.getWriter().println("Secret not found for secret: " + requestedKey);
		}
		
		
		
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	} 
}
