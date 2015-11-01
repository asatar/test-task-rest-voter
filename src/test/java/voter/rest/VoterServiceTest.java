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
import voter.bean.Vote;
import voter.bean.VoteResult;
import voter.clock.TimeService;
import voter.repo.RestaurantRepository;
import voter.repo.VotesRepository;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfig.class)
@WebAppConfiguration
public class VoterServiceTest {
    public static final int MAX_TOTAL_VOTES = 5;
    public static final String RESTAURANT_B_NAME = "BBBB";
    public static final String RESTAURANT_A_NAME = "AAAA";
    private String testRestaurantJson1 = "{\n" +
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

    private String testRestaurantJson2 = "{\n" +
            "  \"Restaurant\" : {\n" +
            "    \"restaurantName\" : \"AAAA\",\n" +
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

    @Autowired
    private VotesRepository votesRepository;

    @Autowired
    TimeService timeService;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        restaurantRepository.deleteAll();
        ((MockTimeService) timeService).setTime(10, 00);
    }


    @Test
    public void vote() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Restaurant restaurantB = mapper.readValue(new StringReader(testRestaurantJson1), Restaurant.class);
        restaurantRepository.saveAndFlush(restaurantB);
        Restaurant restaurantA = mapper.readValue(new StringReader(testRestaurantJson2), Restaurant.class);
        restaurantRepository.saveAndFlush(restaurantA);

        doVote(restaurantB.getRestaurantName(), "qqq");
        doVote(restaurantB.getRestaurantName(), "www");
        doVote(restaurantB.getRestaurantName(), "ddd");

        doVote(restaurantA.getRestaurantName(), "kkk");
        doVote(restaurantA.getRestaurantName(), "lll");


        List<Vote> votes = votesRepository.findAll();
        //check all votes are here
        assertEquals(MAX_TOTAL_VOTES, votes.size());
        Map<Long, Long> res = votes.stream().collect(Collectors.groupingBy(Vote::getRestaurantId, Collectors.counting()));
        //check all votes counter correctly
        assertEquals(3l, res.get(restaurantB.getId()).longValue());
        assertEquals(2l, res.get(restaurantA.getId()).longValue());
    }

    @Test
    public void voteAndGetResults() throws Exception {
        //do 5 votes
        vote();

        MvcResult result = mockMvc.perform(
                get("/vote/showWinner").
                        contentType(MediaType.APPLICATION_JSON).
                        with(httpBasic("user", ""))
        ).andExpect(status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

        VoteResult[] voteResult = mapper.readValue(new StringReader(contentAsString), VoteResult[].class);
        //check we have results for all restaurants
        assertEquals(2, voteResult.length);

        //check it was sorted properly
        assertEquals(RESTAURANT_B_NAME, voteResult[0].getRestaurantName());
        //check votes counted properly
        assertEquals(3, voteResult[0].getVotes());
        assertEquals(2, voteResult[1].getVotes());
    }

    @Test
    public void voteChangeAndTimeout() throws Exception {
        //do 5 votes
        vote();
        //change mind, vote for another restaurant
        doVote(RESTAURANT_A_NAME, "qqq");
        Restaurant restA = restaurantRepository.findByRestaurantName(RESTAURANT_A_NAME);
        Restaurant restB = restaurantRepository.findByRestaurantName(RESTAURANT_B_NAME);

        checkResults(restA, restB);

        //time-up
        ((MockTimeService) timeService).setTime(15, 00);

        mockMvc.perform(
                put("/vote/vote-by-name/" + RESTAURANT_B_NAME).
                contentType(MediaType.APPLICATION_JSON).
                with(httpBasic("zzz", ""))
        ).andExpect(status().isNotFound());

        //check nothing was changed
        checkResults(restA, restB);
    }

    private void checkResults(Restaurant restA, Restaurant restB) {
        List<Vote> votes = votesRepository.findAll();
        //check changing vote doesn't create new entries
        assertEquals(MAX_TOTAL_VOTES, votes.size());
        Map<Long, Long> res = votes.stream().collect(Collectors.groupingBy(Vote::getRestaurantId, Collectors.counting()));
        //check that vote actually changed
        assertEquals(2l, res.get(restB.getId()).longValue());
        assertEquals(3l, res.get(restA.getId()).longValue());
    }

    private void doVote(String name, String user) throws Exception {
        mockMvc.perform(
                put("/vote/vote-by-name/" + name).
                contentType(MediaType.APPLICATION_JSON).
                with(httpBasic(user, ""))
        ).andExpect(status().isOk());
    }

}
