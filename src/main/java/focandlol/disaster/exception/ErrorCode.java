package focandlol.disaster.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  API_CALL_FAILED("API 호출 실패", HttpStatus.INTERNAL_SERVER_ERROR),
  JSON_PARSE_FAILED("JSON 파싱 실패", HttpStatus.INTERNAL_SERVER_ERROR),
  CORRUPTED_TOKEN("토큰이 손상되었습니다.", BAD_REQUEST),
  EXPIRED_TOKEN("만료된 토큰입니다.", UNAUTHORIZED),
  INVALID_SIGNATURE("잘못된 시그니처입니다.", UNAUTHORIZED),
  UNSUPPORTED_TOKEN("지원하지 않는 토큰 형식입니다.", BAD_REQUEST),
  NO_TOKEN("토큰이 없습니다",BAD_REQUEST),
  RECENT_DATE_NOT_FOUND("최근 날짜가 존재하지 않습니다",HttpStatus.INTERNAL_SERVER_ERROR);


  private final String description;
  private final HttpStatus status;
}
