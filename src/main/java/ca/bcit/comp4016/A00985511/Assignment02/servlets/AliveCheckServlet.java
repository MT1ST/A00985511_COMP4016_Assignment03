package ca.bcit.comp4016.A00985511.Assignment02.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet class checking if this Server is alive.
 * Really should always easily return true - if it does not, then...it's probably dead.
 * Used for Kubernetes to determine if readiness probe should intervene.
 * @author Alexander Ryan (A00985511)
 * 
 */
@WebServlet(name="AliveCheckServlet", urlPatterns={"/isAlive"})
public class AliveCheckServlet extends HttpServlet{
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	private static final Logger LOG;	
	public static final boolean DEFAULT_ALIVE_STATUS;
	
	static {
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		LOG = LogManager.getLogger(FooServlet.class);
		DEFAULT_ALIVE_STATUS = true;
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		LOG.info("Initialized AliveCheck Servlet");
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{		
		LOG.info("AliveCheck Servlet Get!");
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		res.setStatus(HttpServletResponse.SC_OK);
		
		res.getWriter().println(DEFAULT_ALIVE_STATUS);
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	}
}
