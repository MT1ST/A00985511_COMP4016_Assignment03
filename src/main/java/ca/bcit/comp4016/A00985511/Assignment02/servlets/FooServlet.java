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
 * Servlet class for the Foo GET response
 * @author Alexander Ryan (A00985511)
 * 
 */
@WebServlet(name="FooServlet", urlPatterns={"/foo"})
public class FooServlet extends HttpServlet {
	

	private static final long serialVersionUID;
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	private static final Logger LOG;	
	
	static {
		serialVersionUID = 7122485076962591694L;
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		LOG = LogManager.getLogger(FooServlet.class);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		LOG.info("Initialized Foo Servlet");
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{		
		LOG.info("Foo Servlet Get!");
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		res.setStatus(HttpServletResponse.SC_OK);
		
		res.getWriter().println("Foo");
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	}

}
