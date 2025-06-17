package focandlol.disaster.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

  private final String description;
  private final HttpStatus httpStatus;

  public ErrorResponse(ErrorCode errorCode){
    this.description = errorCode.getDescription();
    this.httpStatus = errorCode.getStatus();
  }

}
