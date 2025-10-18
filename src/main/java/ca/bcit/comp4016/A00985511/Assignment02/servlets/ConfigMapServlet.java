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
 * Servlet to return the configValue map that we have configured for this server.
 * @author Alexander Ryan (#A00985511)
 *
 */
//@WebServlet(name="ConfigMapServlet", urlPatterns={"/configValue"})
public class ConfigMapServlet extends HttpServlet {
	
	private static final long serialVersionUID;
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	private static final Logger LOG;
	
	static {
		serialVersionUID = 7122485076962591694L;
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		LOG = LogManager.getLogger(ConfigMapServlet.class);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		LOG.info("Initialized Config Map Servlet");
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		final String requestedKey = req.getRequestURI().substring(ValueMappings.REQUEST_URI_SLICE_INDEX_OFFSET);
		LOG.info("Config Map Servlet Get receieved, proceeding to get Configuration for :" + requestedKey);
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
//		
		if(ValueMappings.VALUE_MAPS.get(ValueMappings.CONFIG_MAP_KEY).containsKey(requestedKey)) {
			
			res.setStatus(HttpServletResponse.SC_OK);
//			
			// Returns the specific config map key, for the given url mapping.
			res.getWriter().println(ValueMappings.VALUE_MAPS.get(ValueMappings.CONFIG_MAP_KEY).get(requestedKey));
		}else {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			res.getWriter().println("Config value not found for config: " + requestedKey);
		}
		
		
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	}
}
