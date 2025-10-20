package ca.bcit.comp4016.A00985511.Assignment02.servlets;

import java.io.File;
import java.io.FileWriter;
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

import ca.bcit.comp4016.A00985511.Assignment02.configurations.ValueMappings;

/**
 * Servlet class for saving a JSON POST response for a value to a Persistent Volume.
 * @author Alexander Ryan (A00985511)
 * 
 */
@WebServlet(name="VolumeSaveServlet", urlPatterns={"/saveString"})
public class VolumeSaveServlet extends HttpServlet{
	
	private static Logger LOG;
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	
	public static final String PARAMETER_NAME;
	private static final String BAD_REQUEST_MESSAGE;
	
	public static final String POST_DATA_PARAMETER_NAME;
	
	static {
		LOG = LogManager.getLogger(VolumeSaveServlet.class);
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		POST_DATA_PARAMETER_NAME = "jsondata";
		PARAMETER_NAME = "data";
		
		BAD_REQUEST_MESSAGE = String.format("Volume Save Servlet could not complete request " + 
				"- must provide POST with JSON parameter %s!", PARAMETER_NAME);
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
		
		LOG.info(BAD_REQUEST_MESSAGE);
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		res.getWriter().println(BAD_REQUEST_MESSAGE);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		
		String writeValue = null;
		
		final String jsonObject = req.getReader().readLine(); // TODO: Fix this so that it doesn't break on a single newline. For now, was just trying to get it to work.
		
		if(jsonObject != null) {
			File volumeFile = null;
			FileWriter writer = null;
			
			try {
				final JSONObject parsedObject = new JSONObject(jsonObject);
				writeValue = parsedObject.getString(PARAMETER_NAME);
				
				volumeFile = new File(ValueMappings.DEFAULT_VOLUME_FILE_NAME);
				writer = new FileWriter(volumeFile);
				
				writer.write(writeValue);
				
			}catch(JSONException e) {
				LOG.warn("JSON Object not found for POST object");
			}catch(Exception e) {
				LOG.error("Volume file could not be written to: " + e.getMessage());
			}finally {
				if(writer != null) {
					try {
						writer.close();
					}catch(Exception err) {
						LOG.error("Closing volume file hit issue: " + err.getMessage());
					}finally {
						
					}
				}
			}
		}else {
			LOG.warn(String.format("Could not found data at %s", POST_DATA_PARAMETER_NAME));
		}
		
		if(writeValue != null) {
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
			resp.getWriter().println(String.format("Stored %s!", writeValue));
		}else {
			doGet(req, resp);
		}
	}

}
