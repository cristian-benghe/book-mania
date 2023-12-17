package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.exceptions.UserBannedException;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class AdminController {

    private final transient String internalServerError = "Internal Server Error";

    @Autowired
    private transient AdminService adminService;

    /**
     * PUT request to upgrade a normal user to author privileges
     * When a user is an author, they can add books and access endpoints related to this.
     *
     * @param wantedId the user that will be granted author privileges
     * @param userId the user that does the request and wantedId
     * @return the new user
     */
    @PutMapping("/addAuthor/{wantedId}")
    public ResponseEntity<String> upgradeToAuthor(
            @PathVariable Long wantedId,
            @RequestParam("userID") Long userId) {
        try {

            // Step 1: Check if userId has admin privileges
            if (!adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NOT_AN_ADMIN");
            }

            // Step 2: Check if wantedId exists
            User wantedUser = adminService.getUserById(wantedId);
            if (wantedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Step 3: Check if wantedId is banned
            if (adminService.isBanned(wantedId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("REQUESTED_USER_BANNED");
            }

            adminService.grantAuthorPrivileges(wantedUser);

            return ResponseEntity.ok("Author privileges granted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(internalServerError);
        }
    }

    /**
     * PUT request to set the ban flag for a certain user as banned.
     *
     * @param wantedId the user that will be banned
     * @param userId   the user that does the request
     * @return ResponseEntity indicating the result of the ban operation
     */
    @PutMapping("/banUser/{wantedId}")
    public ResponseEntity<String> banUser(
            @PathVariable Long wantedId,
            @RequestParam("userID") Long userId) {
        try {
            // Step 1: Check if the wantedId exists
            User wantedUser = adminService.getUserById(wantedId);
            if (wantedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
            }

            // Step 2: Check if the userId is an admin
            if (!adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized User - NOT_AN_ADMIN");
            }

            // Step 3: Set the ban flag for the wantedId user
            adminService.banUser(wantedUser);

            return ResponseEntity.ok("User Banned Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            // Step 4: Handle internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(internalServerError);
        }
    }

    /**
     * PUT request to set the ban flag for a certain user as unbanned.
     *
     * @param wantedId the user to unban
     * @param userId   the user making the request
     * @return ResponseEntity indicating the result of the unban operation
     */
    @PutMapping("/unbanUser/{wantedID}")
    public ResponseEntity<String> unbanUser(
            @PathVariable Long wantedId,
            @RequestParam("userID") Long userId) {
        try {
            // Step 1: Check if the wantedId exists
            User wantedUser = adminService.getUserById(wantedId);
            if (wantedUser == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
            }

            // Step 2: Check if the userId is an admin
            if (!adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized User - NOT_AN_ADMIN");
            }

            // Step 3: Check if the user is banned
            if (!wantedUser.getBanned().isBanned()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not banned");
            }

            // Step 4: Unban the user
            adminService.unbanUser(wantedUser);

            return ResponseEntity.ok("User Unbanned Successfully");
        } catch (Exception e) {
            e.printStackTrace();
            // Step 5: Handle internal server error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(internalServerError);
        }
    }

    /**
     * addAdmin endpoint is handled.
     * Knowing the secret password a user may add himself admin
     * privilege by calling this endpoint
     *
     * @param userId the user that will update its rights
     * @param passwordRequest the password sent
     * @return a response entity according to how the processs went
     */
    @PostMapping("/addAdmin")
    public ResponseEntity<String> addAdmin(
            @RequestParam Long userId,
            @RequestBody String passwordRequest) {
        try {
            if (adminService.isAdmin(userId)) {
                return ResponseEntity.status(HttpStatus.OK).body("Already an Admin");
            }

            if (adminService.isBanned(userId)) {
                throw new UserBannedException();
            }

            if (!adminService.authenticateAdmin(passwordRequest)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request (incorrect password)");
            }
            adminService.addAdmin(userId);

            return ResponseEntity.ok("User is now an Admin");
        } catch (UserBannedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized User - USER_BANNED");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(internalServerError);
        }
    }
}