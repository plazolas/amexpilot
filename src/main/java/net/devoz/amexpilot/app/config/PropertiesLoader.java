package net.devoz.amexpilot.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class PropertiesLoader {

  private static final Logger logger = Logger.getLogger(PropertiesLoader.class.getName());

  public static Properties properties = new Properties();
  private static final String fileName = "application.properties";

  public static Properties loadProperties() {
    try {
      InputStream input = PropertiesLoader.class.getClassLoader().getResourceAsStream(fileName);
      if (input == null) {
        throw new IOException("Sorry, unable to find " + fileName);
      }
      // Load the properties from the InputStream
      properties.load(input);

    } catch (IOException e) {
      logger.severe(e.getMessage());
    }
    return properties;
  }
}
