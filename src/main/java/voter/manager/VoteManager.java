package voter.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import voter.bean.VoteResult;
import voter.bean.Restaurant;
import voter.clock.TimeService;
import voter.exception.TooLateException;
import voter.repo.RestaurantRepository;
import voter.bean.Vote;
import voter.repo.VotesRepository;

import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class VoteManager {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private VotesRepository votesRepository;
    @Autowired
    TimeService timeService;

    private LocalTime VOTE_END_TIME = LocalTime.of(11,00);


    public List<VoteResult> voteResults() {
        List<Restaurant> all = restaurantRepository.findAll();
        return all.stream().sorted((r1, r2) -> r2.getVotes().size() - r1.getVotes().size())
                .map(i -> new VoteResult(i.getRestaurantName(), i.getVotes().size()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void vote(String restaurantName, String userName) {
        Restaurant byRestaurantName = restaurantRepository.findByRestaurantName(restaurantName);
        vote(byRestaurantName.getId(), userName);
    }

    @Transactional
    public void vote(Long restId, String userName) {
        if(timeService.getLocalTime().isBefore(VOTE_END_TIME)) {
            votesRepository.saveAndFlush(new Vote(userName, restId));
        }else{
            throw new TooLateException("It is too late to vote");
        }
    }

}
