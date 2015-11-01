package voter.rest;

import org.springframework.stereotype.Component;
import voter.clock.TimeService;

import java.time.LocalTime;


@Component
public class MockTimeService implements TimeService {

    private LocalTime localTime = LocalTime.of(10, 55);

    @Override
    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setTime(int hour, int minute) {
        localTime = LocalTime.of(hour, minute);
    }
}
