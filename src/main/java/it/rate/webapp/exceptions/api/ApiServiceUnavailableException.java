package it.rate.webapp.exceptions.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ApiServiceUnavailableException extends RuntimeException {

  private final Logger logger = LoggerFactory.getLogger(ApiServiceUnavailableException.class);

  public ApiServiceUnavailableException(String message) {
    super(message);
    logger.error(message);
  }

  public ApiServiceUnavailableException() {
    super("Service Unavailable : Third Party API is unavailable");
    logger.error("Service Unavailable : Third Party API is unavailable");
  }

  public ApiServiceUnavailableException(String message, Throwable throwable) {
    super(message, throwable);
    logger.error(message, throwable);
  }
}
