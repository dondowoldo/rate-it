package it.rate.webapp.exceptions.internalerror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends RuntimeException {

  private final Logger logger = LoggerFactory.getLogger(InternalErrorException.class);

  public InternalErrorException() {
    super("Internal server error");
    logger.error("Internal server error");
  }

  public InternalErrorException(String message) {
    super(message);
    logger.error(message);
  }

  public InternalErrorException(String message, Throwable throwable) {
    super(message, throwable);
    logger.error(message, throwable);
  }
}
