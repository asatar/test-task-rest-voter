package voter.bean;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Vote {

    @Column(name = "RESTAURANT_ID")
    private long restaurantId;

    @Id
    @Column(name = "USER_NAME")
    private String userName;

    @Column
    private Date created;

    public Vote() {
    }

    public Vote(String userName, long restId) {
        this.userName = userName;
        this.restaurantId = restId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vote votes = (Vote) o;

        return userName.equals(votes.userName);

    }

    @Override
    public int hashCode() {
        return userName.hashCode();
    }
}
