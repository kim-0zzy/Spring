package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "error.bad") // <- 해당 Exception 발생 시 code로 지정한 Exception 으로 교체해줌. (reason은 에러메시지)
public class BadRequestException extends RuntimeException {
}
