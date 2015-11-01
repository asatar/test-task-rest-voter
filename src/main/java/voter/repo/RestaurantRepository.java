package voter.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import voter.bean.Restaurant;

import java.util.Date;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    Restaurant findByRestaurantName(String name);

    @Modifying
    void deleteByCreatedBefore(Date before);
}
