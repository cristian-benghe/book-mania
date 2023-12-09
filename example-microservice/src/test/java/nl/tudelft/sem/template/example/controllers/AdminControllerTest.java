package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.services.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpgradeToAuthor_AdminUser_Success() {
        Mockito.when(adminService.isAdmin(anyLong())).thenReturn(true);
        Mockito.when(adminService.getUserById(anyLong())).thenReturn(new User());

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Author privileges granted successfully", response.getBody());

        Mockito.verify(adminService, Mockito.times(1)).grantAuthorPrivileges(Mockito.any());
    }

    @Test
    void testUpgradeToAuthorNonAdminUserForbidden() {
        Mockito.when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("NOT_AN_ADMIN", response.getBody());

        Mockito.verify(adminService, Mockito.never()).grantAuthorPrivileges(Mockito.any());
    }

    @Test
    void testUpgradeToAuthorUserNotFoundNotFound() {
        Mockito.when(adminService.isAdmin(anyLong())).thenReturn(true);
        Mockito.when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());

        Mockito.verify(adminService, Mockito.never()).grantAuthorPrivileges(Mockito.any());
    }

    @Test
    void testUpgradeToAuthorBannedUserForbidden() {
        Mockito.when(adminService.isAdmin(anyLong())).thenReturn(true);
        Mockito.when(adminService.getUserById(anyLong())).thenReturn(new User());
        Mockito.when(adminService.isBanned(anyLong())).thenReturn(true);

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("REQUESTED_USER_BANNED", response.getBody());

        Mockito.verify(adminService, Mockito.never()).grantAuthorPrivileges(Mockito.any());
    }

    @Test
    void testBanUser_UserBannedSuccessfully() {
        User wantedUser = new User();

        Mockito.when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        Mockito.when(adminService.isAdmin(anyLong())).thenReturn(true);

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Banned Successfully", response.getBody());

        Mockito.verify(adminService, Mockito.times(1)).banUser(Mockito.eq(wantedUser));
    }

    @Test
    void testBanUser_UserNotFound() {
        Mockito.when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User Not Found", response.getBody());

        Mockito.verify(adminService, Mockito.never()).banUser(Mockito.any());
    }

    @Test
    void testBanUser_UnauthorizedUser() {
        User wantedUser = new User();

        Mockito.when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        Mockito.when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Unauthorized User - NOT_AN_ADMIN", response.getBody());

        Mockito.verify(adminService, Mockito.never()).banUser(Mockito.any());
    }

    @Test
    void testBanUser_InternalServerError() {
        User wantedUser = new User();

        Mockito.when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        Mockito.when(adminService.isAdmin(anyLong())).thenReturn(true);
        Mockito.doThrow(new RuntimeException("Simulated internal server error"))
                .when(adminService).banUser(Mockito.any());

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());

        Mockito.verify(adminService, Mockito.times(1)).banUser(Mockito.eq(wantedUser));
    }

}
