package voter.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import voter.bean.Restaurant;
import voter.repo.RestaurantRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Component
public class RestaurantManager {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional
    public void addRestaurant(Restaurant restaurant){
        Restaurant byRestaurantName = restaurantRepository.findByRestaurantName(restaurant.getRestaurantName());
        if(byRestaurantName == null){
            saveRestaurant(restaurant);
        }else{
            delete(byRestaurantName);
            restaurantRepository.flush();
            saveRestaurant(restaurant);
        }
    }

    private void saveRestaurant(Restaurant restaurant) {
        restaurant.setCreated(new Date());
        restaurantRepository.save(restaurant);
    }

    @Transactional
    public void deleteRestaurant(String name){
        Restaurant byRestaurantName = restaurantRepository.findByRestaurantName(name);
        if(byRestaurantName!=null){
            delete(byRestaurantName);
        }
    }

    @Transactional
    public List<Restaurant> list() {
        return restaurantRepository.findAll();
    }

    @Transactional(Transactional.TxType.MANDATORY)
    private void delete(Restaurant byRestaurantName) {
        restaurantRepository.delete(byRestaurantName);
    }

    @Transactional
    public void cleanAllOld(Date now) {
        restaurantRepository.deleteByCreatedBefore(now);
    }
}
