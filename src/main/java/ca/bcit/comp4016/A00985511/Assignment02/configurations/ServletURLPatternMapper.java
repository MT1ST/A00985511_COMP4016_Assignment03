package ca.bcit.comp4016.A00985511.Assignment02.configurations;

import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.bcit.comp4016.A00985511.Assignment02.servlets.ConfigMapServlet;
import ca.bcit.comp4016.A00985511.Assignment02.servlets.EnvServlet;
import ca.bcit.comp4016.A00985511.Assignment02.servlets.SecretServlet;

/**
 * Initializes the Serlvets that need custom URL pattern mappings, related to the ValueMappings.VALUE_MAPS maps, sorted by type,
 * so that they the Keys of the Maps of each type are mapped to the appropriate Servlet (ConfigMapServlet, EnvServlet, or SecretServlet).
 * 
 * @author Alexander Ryan (#A00985511)
 *
 */
public class ServletURLPatternMapper implements ServletContainerInitializer {
	
	private static final Logger LOG;
	
	static {
		LOG = LogManager.getLogger(ServletURLPatternMapper.class);
	}

	@Override
	public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
		LOG.info("Started ServletURLPatternMapper onStartup call!");
		try {
			Map<String, Map<String, String>> values = ValueMappings.VALUE_MAPS; // Mainly to make sure it initializizes before doing things here.
			
			Dynamic configRegistration = ctx.addServlet("ConfigMapServlet", new ConfigMapServlet());
			String[] configKeys = values.get(ValueMappings.CONFIG_MAP_KEY).keySet().toArray(new String[values.get(ValueMappings.CONFIG_MAP_KEY).keySet().size()]);
			configRegistration.addMapping(configKeys);
			
			LOG.info("Configured ConfigMapServlet.");
			
			Dynamic envRegistration = ctx.addServlet("EnvironmentServlet", new EnvServlet());
			String[] envKeys = values.get(ValueMappings.ENV_MAP_KEY).keySet().toArray(new String[values.get(ValueMappings.ENV_MAP_KEY).keySet().size()]);
			envRegistration.addMapping(envKeys);
			
			LOG.info("Configured EnvironmentServlet.");
			
			String[] secretKeys = values.get(ValueMappings.SECRET_MAP_KEY).keySet().toArray(new String[values.get(ValueMappings.SECRET_MAP_KEY).keySet().size()]);
			Dynamic secretRegistration = ctx.addServlet("SecretServlet", new SecretServlet());
			secretRegistration.addMapping(secretKeys);
			
			LOG.info("Configured SecretServlet.");
			
		}catch(Exception e) {
			LOG.error(e.getMessage());
		}finally {
			
		}
	}
}
