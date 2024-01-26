package it.rate.webapp.exceptions.badrequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

  private final Logger logger = LoggerFactory.getLogger(BadRequestException.class);

  public BadRequestException(String message) {
    super(message);
    logger.error(message);
  }

  public BadRequestException(String message, Throwable throwable) {
    super(message, throwable);
    logger.error(message, throwable);
  }
}
