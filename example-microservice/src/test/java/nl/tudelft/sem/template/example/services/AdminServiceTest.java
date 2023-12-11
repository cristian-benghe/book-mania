package nl.tudelft.sem.template.example.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void testIsAdminWithAdminUser() {
        User adminUser = new User();
        adminUser.setRole(new UserEnumType("ADMIN"));
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(adminUser));
        assertTrue(adminService.isAdmin(1L));
    }

    @Test
    public void testIsAdminWithNonAdminUser() {
        User nonAdminUser = new User();
        nonAdminUser.setRole(new UserEnumType("USER"));
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(nonAdminUser));
        assertFalse(adminService.isAdmin(2L));
    }

    @Test
    public void testIsBannedWithBannedUser() {
        User bannedUser = new User();
        bannedUser.setBanned(new BannedType(true));
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(bannedUser));
        assertTrue(adminService.isBanned(3L));
    }

    @Test
    public void testIsBannedWithNonBannedUser() {
        User nonBannedUser = new User();
        nonBannedUser.setBanned(new BannedType(false));
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(nonBannedUser));
        assertFalse(adminService.isBanned(4L));
    }

    @Test
    public void testGetUserById() {
        User expectedUser = new User();
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(expectedUser));
        assertEquals(expectedUser, adminService.getUserById(5L));
    }

    @Test
    @Transactional
    public void testGrantAuthorPrivileges() {
        User userToGrantPrivileges = new User();
        userToGrantPrivileges.setRole(new UserEnumType("USER"));
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(userToGrantPrivileges));
        when(userRepository.save(Mockito.any())).thenReturn(userToGrantPrivileges);
        assertEquals("AUTHOR", adminService.grantAuthorPrivileges(userToGrantPrivileges).getRole().getUserRole());
    }

    @Test
    @Transactional
    public void testGrantAuthorPrivilegesForAdminUser() {
        User adminUser = new User();
        adminUser.setRole(new UserEnumType("ADMIN"));
        assertEquals("ADMIN", adminService.grantAuthorPrivileges(adminUser).getRole().getUserRole());
    }

    @Test
    @Transactional
    public void testGrantAuthorPrivilegesForAuthorUser() {
        User authorUser = new User();
        authorUser.setRole(new UserEnumType("AUTHOR"));
        assertEquals("AUTHOR", adminService.grantAuthorPrivileges(authorUser).getRole().getUserRole());
    }

    @Test
    void testBanUser() {
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(false));

        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        User result = adminService.banUser(wantedUser);

        // Assert
        assertTrue(result.getBanned().isBanned());
        verify(userRepository, times(1)).save(eq(wantedUser));
    }

    @Test
    void testUnbanUser() {
        // Arrange
        User wantedUser = new User();
        wantedUser.setBanned(new BannedType(true));

        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User result = adminService.unbanUser(wantedUser);

        assertFalse(result.getBanned().isBanned());
        verify(userRepository, times(1)).save(eq(wantedUser));
    }
}
