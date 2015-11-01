package voter.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import voter.manager.RestaurantManager;

import javax.annotation.PostConstruct;
import java.util.Date;

@Component
public class CleanupTask {
    @Autowired
    private RestaurantManager restaurantManager;

    @PostConstruct
    @Scheduled(cron = "01 00 00 * * *")
    public void doCleanup(){
        restaurantManager.cleanAllOld(new Date());
    }

}
