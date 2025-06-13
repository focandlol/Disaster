package focandlol.disaster.exception;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

  private final ErrorCode errorCode;
  private final String description;
  private final HttpStatus httpStatus;

  public ErrorResponse(ErrorCode errorCode){
    this.errorCode = errorCode;
    this.description = errorCode.getDescription();
    this.httpStatus = errorCode.getStatus();
  }

}
