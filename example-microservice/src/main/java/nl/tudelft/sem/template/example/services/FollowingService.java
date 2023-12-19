package nl.tudelft.sem.template.example.services;

import java.util.Arrays;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FollowingService {

    private final transient UserRepository userRepository;

    /**
     * Constructor of the FollowingService.
     *
     * @param userRepository the injected UserRepository
     */
    public FollowingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Service method for checking if the userIDs exist in the database, and follows the user with wantedID.
     *
     * @param userID ID of the user making the request
     * @param wantedID ID of the user to be followed
     * @return HTTP status code representing the outcome of the operation
     */
    public HttpStatus followUser(long userID, long wantedID) {
        try {
            // both users must exist
            if (!userRepository.existsById(userID) || !userRepository.existsById(wantedID)) {
                return HttpStatus.NOT_FOUND;
            }

            User userRetrieved = userRepository.findById(userID).get();

            // banned users cannot follow others
            if (userRetrieved.getBanned().isBanned()) {
                return HttpStatus.FORBIDDEN;
            }

            // users cannot start following someone they already follow, nor can they follow themselves
            if (userRetrieved.getFollowing().follows(userRepository.findById(wantedID).get()) || wantedID == userID) {
                return HttpStatus.BAD_REQUEST;
            }

            // add the wanted user to the list of users being followed
            userRetrieved.getFollowing().getFollowedUsers().add(userRepository.findById(wantedID).get());

            // persist the user in the database again
            userRepository.save(userRetrieved);

            return HttpStatus.OK;
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
