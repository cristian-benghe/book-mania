package nl.tudelft.sem.template.example.modules.user;

import java.util.List;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class FollowingType {
    @OneToMany(targetEntity = User.class)
    private List<Long> followedUsers;

    /**
     * Constructor of the FollowingType.
     *
     * @param followedUsers list of userIDs of the users this account follows
     */
    public FollowingType(List<Long> followedUsers) {
        this.followedUsers = followedUsers;
    }
}
