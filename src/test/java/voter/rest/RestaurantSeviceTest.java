package voter.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;
import voter.bean.Restaurant;
import voter.repo.RestaurantRepository;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Starter.class)
@SpringApplicationConfiguration(classes = TestConfig.class)
@WebAppConfiguration
public class RestaurantSeviceTest {

    private String testRestaurantJson = "{\n" +
            "  \"Restaurant\" : {\n" +
            "    \"restaurantName\" : \"BBBB\",\n" +
            "    \"menuList\" : [ {\n" +
            "      \"name\" : \"crabs\",\n" +
            "      \"price\" : 1.0\n" +
            "    }, {\n" +
            "      \"name\" : \"prawns\",\n" +
            "      \"price\" : 2.0\n" +
            "    }, {\n" +
            "      \"name\" : \"carrot\",\n" +
            "      \"price\" : 3.0\n" +
            "    }, {\n" +
            "      \"name\" : \"lemon\",\n" +
            "      \"price\" : 4.0\n" +
            "    } ]\n" +
            "  }\n" +
            "}";


    private MockMvc mockMvc;


    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
                hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        restaurantRepository.deleteAll();
    }


    @Test
    public void testAddRestaurant() throws Exception {
        mockMvc.perform(
                put("/restaurant/add").
                content(testRestaurantJson).
                contentType(MediaType.APPLICATION_JSON).
                with(httpBasic("admin", ""))
        ).andExpect(status().isOk());
        List<Restaurant> all = restaurantRepository.findAll();
        assertEquals("Expect 1 item", 1, all.size());
    }

    @Test
    public void testRemoveRestaurant() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Restaurant restaurant = mapper.readValue(new StringReader(testRestaurantJson), Restaurant.class);
        restaurantRepository.saveAndFlush(restaurant);
        mockMvc.perform(
                delete("/restaurant/delete/BBBB").
                contentType(MediaType.APPLICATION_JSON).
                with(httpBasic("admin", ""))
        ).andExpect(status().isOk());
        int count = restaurantRepository.findAll().size();
        assertEquals("Expect 0 item", 0, count);
    }

    @Test
    public void testList() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Restaurant restaurant = mapper.readValue(new StringReader(testRestaurantJson), Restaurant.class);
        restaurantRepository.saveAndFlush(restaurant);
        MvcResult result = mockMvc.perform(
                get("/restaurant/list").
                contentType(MediaType.APPLICATION_JSON).
                with(httpBasic("admin", ""))
        ).andExpect(status().isOk()).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        Restaurant[] restaurantResult = mapper.readValue(new StringReader(contentAsString), Restaurant[].class);
        assertEquals(1, restaurantResult.length);
        assertEquals(restaurant.getRestaurantName(), restaurantResult[0].getRestaurantName());
        assertEquals(restaurant.getMenuList().size(), restaurantResult[0].getMenuList().size());
    }
}
