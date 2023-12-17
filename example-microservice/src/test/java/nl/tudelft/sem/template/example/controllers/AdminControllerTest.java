package nl.tudelft.sem.template.example.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import nl.tudelft.sem.template.example.modules.user.BannedType;
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
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        when(adminService.getUserById(anyLong())).thenReturn(new User());

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Author privileges granted successfully", response.getBody());

        Mockito.verify(adminService, times(1)).grantAuthorPrivileges(any());
    }

    @Test
    void testUpgradeToAuthorNonAdminUserForbidden() {
        when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("NOT_AN_ADMIN", response.getBody());

        Mockito.verify(adminService, Mockito.never()).grantAuthorPrivileges(any());
    }

    @Test
    void testUpgradeToAuthorUserNotFoundNotFound() {
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());

        Mockito.verify(adminService, Mockito.never()).grantAuthorPrivileges(any());
    }

    @Test
    void testUpgradeToAuthorBannedUserForbidden() {
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        when(adminService.getUserById(anyLong())).thenReturn(new User());
        when(adminService.isBanned(anyLong())).thenReturn(true);

        ResponseEntity<String> response = adminController.upgradeToAuthor(2L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("REQUESTED_USER_BANNED", response.getBody());

        Mockito.verify(adminService, Mockito.never()).grantAuthorPrivileges(any());
    }

    @Test
    void testBanUser_UserBannedSuccessfully() {
        User wantedUser = new User();

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Banned Successfully", response.getBody());

        Mockito.verify(adminService, times(1)).banUser(Mockito.eq(wantedUser));
    }

    @Test
    void testBanUser_UserNotFound() {
        when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User Not Found", response.getBody());

        Mockito.verify(adminService, Mockito.never()).banUser(any());
    }

    @Test
    void testBanUser_UnauthorizedUser() {
        User wantedUser = new User();

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Unauthorized User - NOT_AN_ADMIN", response.getBody());

        Mockito.verify(adminService, Mockito.never()).banUser(any());
    }

    @Test
    void testBanUser_InternalServerError() {
        User wantedUser = new User();

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        doThrow(new RuntimeException("Simulated internal server error"))
                .when(adminService).banUser(any());

        ResponseEntity<String> response = adminController.banUser(1L, 2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());

        Mockito.verify(adminService, times(1)).banUser(Mockito.eq(wantedUser));
    }

    @Test
    void testUnbanUser_UserUnbannedSuccessfully() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(true));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);

        ResponseEntity<String> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User Unbanned Successfully", response.getBody());

        Mockito.verify(adminService, times(1)).unbanUser(Mockito.eq(wantedUser));
    }

    @Test
    void testUnbanUser_UserNotBanned() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(false));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);

        ResponseEntity<String> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User is not banned", response.getBody());

        Mockito.verify(adminService, Mockito.never()).unbanUser(any());
    }

    @Test
    void testUnbanUser_UnauthorizedUser() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(true));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(false);

        ResponseEntity<String> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Unauthorized User - NOT_AN_ADMIN", response.getBody());

        Mockito.verify(adminService, Mockito.never()).unbanUser(any());
    }

    @Test
    void testUnbanUser_UserNotFound() {
        when(adminService.getUserById(anyLong())).thenReturn(null);

        ResponseEntity<String> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User Not Found", response.getBody());

        Mockito.verify(adminService, Mockito.never()).unbanUser(any());
    }


    @Test
    void testUnbanUser_InternalServerError() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(true));

        when(adminService.getUserById(anyLong())).thenReturn(wantedUser);
        when(adminService.isAdmin(anyLong())).thenReturn(true);
        // Mocking adminService.unbanUser to throw an exception
        doThrow(new RuntimeException("Simulated internal server error"))
                .when(adminService).unbanUser(any());

        ResponseEntity<String> response = adminController.unbanUser(1L, 2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());

        verify(adminService, times(1)).unbanUser(eq(wantedUser));
    }
}
