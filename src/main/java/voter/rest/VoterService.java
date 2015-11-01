package voter.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import voter.bean.VoteResult;
import voter.exception.TooLateException;
import voter.manager.VoteManager;
import voter.bean.Restaurant;

import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN,ROLE_USER')")
@RequestMapping(value = "/vote/")
public class VoterService {
    @Autowired
    private VoteManager voteManager;

    @RequestMapping("/showWinner")
    public List<VoteResult> showWinner() {
        return voteManager.voteResults();
    }

    @RequestMapping(value = "/vote-by-name/{restaurantName}", method = RequestMethod.PUT)
    public void vote(@PathVariable String restaurantName, Principal user) {
        voteManager.vote(restaurantName, user.getName());
    }

    @RequestMapping(value = "/vote-by-id/{restaurantId}", method = RequestMethod.PUT)
    public void vote(@PathVariable Long restaurantId, Principal user) {
        voteManager.vote(restaurantId, user.getName());
    }
}
