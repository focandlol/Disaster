package focandlol.disaster.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{

  private final ErrorCode errorCode;

  private final Exception e;

}
