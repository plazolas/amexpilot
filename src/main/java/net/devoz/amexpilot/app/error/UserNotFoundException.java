package net.devoz.amexpilot.app.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class UserNotFoundException extends Exception {

  private static final Logger logger = Logger.getLogger(UserNotFoundException.class.getName());

  public UserNotFoundException() {
    super();
  }

  // Constructor with a message
  public UserNotFoundException(String message) {
    super(message);
    logger.warning(message);
    logger.warning(getStackTraceString());
  }

  // Constructor with a message and a cause
  public UserNotFoundException(String message, Throwable cause) {
    super(message, cause);
    logger.warning(message);
    logger.warning(cause.getMessage());
    logger.warning(getStackTraceString());
  }

  // Constructor with a cause
  public UserNotFoundException(Throwable cause) {
    super(cause);
    logger.warning(cause.getMessage());
  }

  public String getStackTraceString() {
    StringWriter sw = new StringWriter();
    // Create a PrintWriter that writes to the StringWriter
    PrintWriter pw = new PrintWriter(sw);
    // Print the stack trace to the PrintWriter
    super.printStackTrace(pw);
    // Get the captured stack trace as a String
    return sw.toString();
  }
}
