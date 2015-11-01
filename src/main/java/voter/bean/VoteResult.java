package voter.bean;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class VoteResult {
    private String restaurantName;
    private int votes;

    public VoteResult() {
    }

    public VoteResult(String restaurantName, int votes) {
        this.restaurantName = restaurantName;
        this.votes = votes;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
