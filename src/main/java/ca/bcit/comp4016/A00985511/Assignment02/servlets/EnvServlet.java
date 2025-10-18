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
 * Servlet to return the environment value mapped to the key that we have configured for this server.
 * Due to how ValuesMappings works, will likely pull in *all* environment variables, because I do not know how to tell if 
 * specific variables are different than the ones I actually want to get.
 * @author Alexander Ryan (#A00985511)
 *
 */
//@WebServlet(name="EnvironmentServlet", urlPatterns={"/envValue"})
public class EnvServlet extends HttpServlet{
	private static final long serialVersionUID;
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	private static final Logger LOG;
	
	
	
	static {
		serialVersionUID = 7122485076962591694L;
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		LOG = LogManager.getLogger(EnvServlet.class);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		LOG.info("Initialized Env Servlet");
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		final String requestedKey = req.getRequestURI().substring(ValueMappings.REQUEST_URI_SLICE_INDEX_OFFSET);
		LOG.info("Env Servlet Get receieved, proceeding to env value of env key: " + requestedKey);
		
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		if(ValueMappings.VALUE_MAPS.get(ValueMappings.ENV_MAP_KEY).containsKey(requestedKey)) {
			
			res.setStatus(HttpServletResponse.SC_OK);
			
			// Returns the specific env map key, for the given url mapping.
			res.getWriter().println(ValueMappings.VALUE_MAPS.get(ValueMappings.ENV_MAP_KEY).get(requestedKey));
		}else {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			res.getWriter().println("Env not found for env: " + requestedKey);
		}
		
		
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	} 
}
