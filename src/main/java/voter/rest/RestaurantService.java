package voter.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import voter.bean.Restaurant;
import voter.manager.RestaurantManager;

import java.util.List;

@RestController
@RequestMapping(value = "/restaurant/")
public class RestaurantService {

    @Autowired
    private RestaurantManager restaurantManager;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/add", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void uploadRestaurantForToday(@RequestBody Restaurant restaurant){
        restaurantManager.addRestaurant(restaurant);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/delete/{restaurantName}", method = RequestMethod.DELETE)
    public void deleteRestaurant(@PathVariable String restaurantName){
        restaurantManager.deleteRestaurant(restaurantName);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN, ROLE_USER')")
    @RequestMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Restaurant> list(){
        return restaurantManager.list();
    }


}
