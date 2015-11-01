package voter.clock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class RealTimeService implements TimeService {
    public LocalTime getLocalTime(){
        return LocalTime.now();
    }
}
