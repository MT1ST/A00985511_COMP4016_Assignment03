package ca.bcit.comp4016.A00985511.Assignment02.servlets;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.bcit.comp4016.A00985511.Assignment02.configurations.ValueMappings;

/**
 * Servlet that, when accessed, will begin to attempt to maximum CPU usage with threads, for the next MINUTES_BUSY minutes.
 * Will also ramp up CPU usage, if possible.
 * @author Alexander Ryan (A00985511)
 * 
 */
@WebServlet(name="BusyWaitServlet", urlPatterns={"/busywait"})
public class BusyServlet extends HttpServlet{
	
	private static final String SERVLET_CONTENT_TYPE_DEFAULT;
	private static final Logger LOG;
	
	public static final float MINUTES_BUSY;
	
	private static final int MINUTES_TO_SECONDS_CONVERSION_VALUE;
	
	private static final int MILLISECONDS_TO_MINUTES_CONVERSION_VALUE;
	private static final int SECONDS_FROM_MILLISECONDS_CONVERSION_VALUE;
	
	private static final String BUSY_SERVLET_URL;
	
	private static final String BUSYWAIT_FILE_LOCATION;
	
	
	
	private static Long BUSYWAIT_START_TIMESTAMP;
	
	
	
	static {
		LOG = LogManager.getLogger(BusyServlet.class);
		MINUTES_BUSY = 3;
		SERVLET_CONTENT_TYPE_DEFAULT = "text/plain";
		BUSYWAIT_FILE_LOCATION = ValueMappings.VOLUME_FILE_LOCATION + "/busyWaitValue";
		SECONDS_FROM_MILLISECONDS_CONVERSION_VALUE = 1000;
		MINUTES_TO_SECONDS_CONVERSION_VALUE = 60;
		MILLISECONDS_TO_MINUTES_CONVERSION_VALUE = SECONDS_FROM_MILLISECONDS_CONVERSION_VALUE * MINUTES_TO_SECONDS_CONVERSION_VALUE;
		
		
		BUSYWAIT_START_TIMESTAMP = null; // Until read or updated, treat as null. Makes the previous code sort of work better.
		
		BUSY_SERVLET_URL = "http://jboss-service.aryan20:30000/busywait";
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
	
	private static boolean getBusyWaitCount(File busyWaitFile) {
		FileWriter fileWriter = null;
		Scanner scanner = null;
		boolean fileFound = false;
		try {
			if(busyWaitFile.exists()) {
				LOG.info("busyFile appears to exist.");
				scanner = new Scanner(busyWaitFile);
				if(scanner.hasNextLong()) {
					// If this fails, it's probably due to something off with the file.
					BUSYWAIT_START_TIMESTAMP = scanner.nextLong();
					fileFound = true;
				}
			}else {
				LOG.info("busyFile doesn't exist yet - creating.");
				BUSYWAIT_START_TIMESTAMP = new Date().getTime();
				fileWriter = new FileWriter(busyWaitFile);
//				longWriter = new BufferedWriter(fileWriter);
				fileWriter.write(BUSYWAIT_START_TIMESTAMP + "\n");
				
			}
		}catch(Exception e) {
			LOG.error("Could not handle busyWait file: " + e.getMessage());
		}finally {
			try {
				if(fileWriter != null) {
					fileWriter.close();
				}
				// Okay, if it wasn't the fileReader, then it was probably the scanner that needs to close.
				if(scanner != null) {
					scanner.close();
				}
			}catch(Exception err) {
				LOG.error(err.getMessage());
			}finally {
				
			}
		}
		return fileFound;
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException{		
		LOG.info("BusyWait Servlet Get!");
		Long currentCountdown = null;
		File busyWaitFile = null;
		boolean fileRead = false;
		try{
			busyWaitFile = new File(BUSYWAIT_FILE_LOCATION);
			// We need to do the File construction here, just so that we can delete the file later if the timestamp has exceeded the time expected.
			fileRead = getBusyWaitCount(busyWaitFile);
			if(fileRead) {
				LOG.info("Started busy process");
			}else {
				LOG.info("Continuing busy process");
			}
		}catch(Exception err) {
			LOG.error("Could not create Busy Wait File reference: " + err.getMessage());
		}finally {
			
		}
		
		// So BUSYWAIT_START_TIMESTAMP could be null, if the File reading part of getting the value is not working.
		// If that is the case, then currentCountdown will *also* be null, since it couldn't be calculated.
		if(BUSYWAIT_START_TIMESTAMP != null) {
			currentCountdown = new Date().getTime() - BUSYWAIT_START_TIMESTAMP;			
		}
		
		if((currentCountdown != null ) && ((currentCountdown / MILLISECONDS_TO_MINUTES_CONVERSION_VALUE) >= MINUTES_BUSY)) {
			BUSYWAIT_START_TIMESTAMP = null;
			if(busyWaitFile != null) {
				try {
					busyWaitFile.delete(); // We don't want the existing file to still be around here.
				}catch(Exception err) {
					LOG.info("Busy wait file could not be deleted: " + err.getMessage());
				}
				
			}
		}else if(currentCountdown != null) {
			try {
				LOG.info("Attempting to call the server again, to force load balancing to balance.");
				HttpURLConnection con = (HttpURLConnection) (new URL(BUSY_SERVLET_URL)).openConnection(); // Just to make sure we do this across LoadBalanced machines.
				// Busy function in here, or a threaded version that can kick off while the writer finishes.
				LOG.info("Making busy thread.");
				new Thread(new Runnable() {
						public void run() {
							LOG.info("Okay, actually running threadable.");
							busyFunction();
						}
				}).start();
				LOG.info("Busy work started");
			}catch(Exception error) {
				LOG.error("Ran into issue when trying to run busy stuff: " + error.getMessage());
			}
		}
		res.setContentType(SERVLET_CONTENT_TYPE_DEFAULT);
		
		res.setStatus(HttpServletResponse.SC_OK);
		
		res.getWriter().println("BusyWait kicked in - seconds running BusyWait process until the maximum where it should be lowered: ");
		res.getWriter().println((currentCountdown != null ? (currentCountdown /SECONDS_FROM_MILLISECONDS_CONVERSION_VALUE) : "0" + "/" + (MINUTES_BUSY*MINUTES_TO_SECONDS_CONVERSION_VALUE)));
		
	}
	
	private void busyFunction() {
		File busyWaitFile = null;
		boolean fileRead = false;
		try{
			busyWaitFile = new File(BUSYWAIT_FILE_LOCATION);
			
			while(busyWaitFile != null && busyWaitFile.exists()) {
				// Here, we want to do something that could take a busy amount of time, but *not* too much memory.
				try {
					int randomValue = (int) (Math.random() * (MILLISECONDS_TO_MINUTES_CONVERSION_VALUE)); // We want to prevent this function from being cachable, so we need some randomness.
					int[] arrayToSort = new int[randomValue];
					for(int i = 0; i < arrayToSort.length; i++) {
						arrayToSort[i] = i% arrayToSort.length;
					}
					LOG.info("Busy writing the following to Log: " + Arrays.toString(arrayToSort));
					Arrays.sort(arrayToSort); // We don't need to be *that* inefficient, I hope. Maybe we be more inefficient with the number generation?
					LOG.info("Busy writing the sorted list to Log: " + Arrays.toString(arrayToSort));
				}catch(Exception e) {
					LOG.error("Busy attempt ran into issue: " + e.getMessage());
				}finally {
					
				}
			}
		}catch(Exception err) {
			LOG.error("Issue reading Busy Wait File while being busy: " + err.getMessage());
		}finally {
			
		}
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doGet(req, resp);
	}

}
