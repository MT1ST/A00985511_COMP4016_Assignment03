package ca.bcit.comp4016.A00985511.Assignment02.servlets;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import ca.bcit.comp4016.A00985511.Assignment02.configurations.ValueMappings;

/**
 * Servlet class for reading the value saved to a Persistent Volume.
 * Returns HttpServletResponse.SC_NOT_FOUND if not already stored.
 *
 * @author Alexander Ryan (A00985511)
 * @see VolumeSaveServlet for details on how to store the value.
 * 
 */
@WebServlet(name="VolumeGetServlet", urlPatterns={"/getString"})
public class VolumeReadServlet extends HttpServlet{

	private static Logger LOG;
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	
//	public static final String PARAMETER_NAME;
	private static final String BAD_REQUEST_MESSAGE;
	
//	public static final String POST_DATA_PARAMETER_NAME;
	
	static {
		LOG = LogManager.getLogger(VolumeSaveServlet.class);
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
//		POST_DATA_PARAMETER_NAME = "jsondata";
//		PARAMETER_NAME = "data";
		BAD_REQUEST_MESSAGE = "Could not find string value of volume - please save value via saveString.";
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
		
		String readValue = null;
		File volumeFile = null;
		Scanner scanner = null;
		
		try {
//			final JSONObject parsedObject = new JSONObject(jsonObject);
//			readValue = parsedObject.getString(PARAMETER_NAME);
				
			volumeFile = new File(ValueMappings.DEFAULT_VOLUME_FILE_NAME);
			if(volumeFile.exists()) {
				scanner = new Scanner(volumeFile);
				final StringBuffer readBuffer = new StringBuffer();
				while(scanner.hasNextLine()) {
					readBuffer.append(scanner.nextLine());
				}
				readValue = readBuffer.toString();
			}				
		}catch(JSONException e) {
			LOG.warn("JSON Object not found for POST object");
		}catch(Exception e) {
			LOG.error("Volume file could not be read to: " + e.getMessage());
		}finally {
			if(scanner != null) {
				try {
					scanner.close();
				}catch(Exception err) {
					LOG.error("Closing volume file hit issue: " + err.getMessage());
				}finally {
						
				}
			}
		}
		
		if(readValue == null) {
			LOG.info(BAD_REQUEST_MESSAGE);
			
			res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
			
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			
			res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
			
			res.getWriter().println(BAD_REQUEST_MESSAGE);
			
		}else {
			LOG.info("Found value: " + readValue);
			res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
			res.setStatus(HttpServletResponse.SC_OK);
			res.getWriter().println(readValue);
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	}

}
