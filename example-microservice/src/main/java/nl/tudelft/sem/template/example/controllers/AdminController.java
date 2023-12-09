package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class AdminController {

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
            @RequestParam Long userId) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
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
            @RequestParam Long userId) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
}