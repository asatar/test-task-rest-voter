package voter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Too late for voting")
public class TooLateException extends RuntimeException {
    public TooLateException(String s) {
        super(s);
    }
}
