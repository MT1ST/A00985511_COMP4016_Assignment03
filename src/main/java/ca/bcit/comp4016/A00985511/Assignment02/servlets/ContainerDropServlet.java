package ca.bcit.comp4016.A00985511.Assignment02.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This servlet attempts to drop the current container.
 * @author Alexander Ryan (A00985511)
 *
 */
@WebServlet(name="ContainerDropServlet", urlPatterns={"/kill"})
public class ContainerDropServlet extends HttpServlet {
	

	private static final long serialVersionUID;
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	private static final String OUTPUT_MESSAGE;
	private static final Logger LOG;
	
	private static final String DOCKER_COMMAND;
	
	
	static {
		serialVersionUID = 7122485076962591694L;
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		DOCKER_COMMAND = "kill -SIGKILL -1";
		OUTPUT_MESSAGE = "Killing Container";
		LOG = LogManager.getLogger(ContainerDropServlet.class);
	}
	
	@Override
	public void init() throws ServletException {
		super.init();
		LOG.info("Initialized Container Drop Servlet");
	}
	
	@Override
	public void destroy() {
		super.destroy();
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{
		
		LOG.info("Container Drop Get receieved, proceeding to shut Container!");
		
		LOG.debug("Executing command " + DOCKER_COMMAND);
		
		final Runtime runtime = Runtime.getRuntime();
		
		final Process process = runtime.exec(DOCKER_COMMAND);
		
		LOG.info("Command Response: " + process.getInputStream().read());
		
		// Shouldn't get here, but if we do, that essentially means the above process hit an issue.
		// In the meantime, at least should let the endpoint know that it recognized that it *should've* do the above.
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
//		
		res.setStatus(HttpServletResponse.SC_OK);
//		
		res.getWriter().println(OUTPUT_MESSAGE);
		
		
		
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	}
}
