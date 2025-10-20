package ca.bcit.comp4016.A00985511.Assignment02.configurations;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Static utility class to pull in the maps of Environment Variables (ENV_MAP_KEY), Secrets (SECRET_MAP_KEY), and ConfigMap (CONFIG_MAP_KEY) values.
 * combining them into VALUE_MAPS with the aforementioned key names referring to the key-value pairs for each of the above.
 * @author Alexander Ryan (#A00985511)
 *
 */
public class ValueMappings {
	private static final Logger LOG;
	
	public static final String ENV_MAP_KEY;
	public static final String SECRET_MAP_KEY;
	public static final String CONFIG_MAP_KEY;
	
	public static final String VOLUME_FILE_LOCATION;
	
	public static final String DEFAULT_VOLUME_FILE_NAME;
	
	private static final String REMOVABLE_PREFIX;
	private static final String REPLACED_PREFIX;
	
	private static final String SECRET_FILE_LOCATION;
	private static final String CONFIG_FILES_PATH;
	
	public static final Map<String, Map<String, String>> VALUE_MAPS;
	
	public static final int REQUEST_URI_SLICE_INDEX_OFFSET = 1;
	
	
	static {
		LOG = LogManager.getLogger(ValueMappings.class);
		SECRET_FILE_LOCATION = "/etc/secret-volume";
		CONFIG_FILES_PATH = "/config";
		
		VOLUME_FILE_LOCATION = "/usr/share/volume";
		DEFAULT_VOLUME_FILE_NAME = VOLUME_FILE_LOCATION + "/" + "testString";
		
		REMOVABLE_PREFIX = "."; // If this is the start of a secret key, we want to remove this, so...we define it here.
		REPLACED_PREFIX = "";
		ENV_MAP_KEY = "env";
		SECRET_MAP_KEY = "secret";
		CONFIG_MAP_KEY = "config";
		
		VALUE_MAPS = new HashMap<String, Map<String, String>>();
		
		
		try {
			
//			Map<String, String> configValues = new HashMap<String, String>();
//			for(final File configKey: configDirectory.listFiles()) {
//				if((configKey != null) && (!configKey.isDirectory()) && (!(configKey.getName().isEmpty()))) {
//					final String mappedValue = getMappingValue(configKey);
//					configValues.put(configKey.getName(), mappedValue);
//				}
//				
//			}
			File configDirectory = new File(CONFIG_FILES_PATH);
			VALUE_MAPS.put(CONFIG_MAP_KEY, getMappedValues(configDirectory));
			
//			Map<String, String> secretValues = new HashMap<String, String>();
//			for(final File secretKey: secretDirectory.listFiles()) {
//				if((secretKey != null) && (!secretKey.isDirectory()) && (!(secretKey.getName().isEmpty()))) {
//					final String mappedValue = getMappingValue(secretKey);
//					secretValues.put(secretKey.getName(), mappedValue);
//				}
//			}
			File secretDirectory = new File(SECRET_FILE_LOCATION);
			
			Map<String, String> secretKeyValues = getMappedValues(secretDirectory);
			
			
			// Okay, a little bit of cleanup - the "SecretKey" values often have a "." at the start, so we should...clean those up, as that makes for a difficult endpoint to capture.
			for(String key: secretKeyValues.keySet()) {
				if(key.startsWith(REMOVABLE_PREFIX)) {
					secretKeyValues.put(key.replaceFirst(REMOVABLE_PREFIX, REPLACED_PREFIX), secretKeyValues.get(key)); // I guess we could try popping the bad key, but...for now, we can keep it in.
					// It seems to not work normally that way anyways. Was tempted to call "REMOVABLE_PREFIX" "ANNOYING_PREFIX" because of that.
				}
			}
			
			VALUE_MAPS.put(SECRET_MAP_KEY, secretKeyValues);
		}catch(Exception e) {
			LOG.error(e.getMessage());
		}
		VALUE_MAPS.put(ENV_MAP_KEY, new HashMap<String, String>(System.getenv()));
		
		LOG.info("Hash Maps of Strings created!");
		LOG.info(VALUE_MAPS.keySet());
//		LOG.info(VALUE_MAPS.get(CONFIG_MAP_KEY).keySet());
//		LOG.info(VALUE_MAPS.get(CONFIG_MAP_KEY).values());
//		LOG.info(VALUE_MAPS.get(SECRET_MAP_KEY).keySet());
//		LOG.info(VALUE_MAPS.get(SECRET_MAP_KEY).values());
//		LOG.info(VALUE_MAPS.get(ENV_MAP_KEY).keySet());
//		LOG.info(VALUE_MAPS.get(ENV_MAP_KEY).values());
	}
	
	private static final Map<String, String> getMappedValues(final File directory){
		final Map<String, String> secretValues = new HashMap<String, String>();
		for(final File secretKey: directory.listFiles()) {
			if((secretKey != null) && (!secretKey.isDirectory()) && (!(secretKey.getName().isEmpty()))) {
				final String mappedValue = getMappingValue(secretKey);
				if((!mappedValue.isEmpty()) && ((secretValues.get(secretKey.getName()) == null) || 
						                        (secretValues.get(secretKey.getName()).isEmpty()) || 
						                        (secretValues.get(secretKey.getName()).contentEquals(System.lineSeparator())))) {
					// A Weird edge case - we may come across a value that is only a line separator, due to how getMappingValue gets values.
					
					secretValues.put(secretKey.getName(), mappedValue);
				}
			}else if((secretKey != null) && (secretKey.isDirectory())) {
				// Okay, in some weird situations, the mount has doubled values...
				// Let's go "Down a level" in the Directory, in case we can get a better, cleaner value.
				// This presumes that the value is just duplicated at times.
				Map<String, String> secondaryDirValues = getMappedValues(secretKey);
				for(String key: secondaryDirValues.keySet()) {
					if(!secretValues.containsKey(key)) {
						secretValues.put(key, secondaryDirValues.get(key));
					}else if((secretValues.get(key) == null) || 
							 (secretValues.get(key).isEmpty()) || 
							 (secretValues.get(key).contentEquals(System.lineSeparator())) ) {
						// Same as above edge case - essentially letting us know we should try and overwrite a value.
						secretValues.put(key, secondaryDirValues.get(key));
					}
				}
			}
		}
		return secretValues;
	}
	
	private static final String getMappingValue(final File configFile){
		String mappedValue = "";
		if((configFile != null) && (!configFile.isDirectory()) && (!(configFile.getName().isEmpty()))) {
			Scanner scanner = null;
			try {
				scanner = new Scanner(configFile);
				StringBuilder mappedValueBuilder = new StringBuilder();
				while(scanner != null && scanner.hasNextLine()) {
					mappedValueBuilder.append(scanner.nextLine() + System.lineSeparator()); // We want to separate lines, in case they are there, when outputting the value.
				}
				mappedValue = mappedValueBuilder.toString();
				LOG.info(configFile.getName());
				LOG.info(mappedValue);
			}catch(Exception e) {
				LOG.error(e.getMessage());
			}finally {
				// If we opened a file, we'll need to close it, for safety reasons, because we might actually be opening another file.
				if(scanner != null) {
					try {
						scanner.close();
					}catch(Exception err) {
						LOG.error(err.getMessage());
					}finally {
						// Well, we currently have not much else to do at this point, as I understand it.
					}
					
				}
			}
			
			
		}
		
		return mappedValue;
	}

}
