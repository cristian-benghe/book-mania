package nl.tudelft.sem.template.example.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;

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
        Mockito.when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(adminUser));
        assertTrue(adminService.isAdmin(1L));
    }

    @Test
    public void testIsAdminWithNonAdminUser() {
        User nonAdminUser = new User();
        nonAdminUser.setRole(new UserEnumType("USER"));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(nonAdminUser));
        assertFalse(adminService.isAdmin(2L));
    }

    @Test
    public void testIsBannedWithBannedUser() {
        User bannedUser = new User();
        bannedUser.setBanned(new BannedType(true));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(bannedUser));
        assertTrue(adminService.isBanned(3L));
    }

    @Test
    public void testIsBannedWithNonBannedUser() {
        User nonBannedUser = new User();
        nonBannedUser.setBanned(new BannedType(false));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(nonBannedUser));
        assertFalse(adminService.isBanned(4L));
    }

    @Test
    public void testGetUserById() {
        User expectedUser = new User();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(expectedUser));
        assertEquals(expectedUser, adminService.getUserById(5L));
    }

    @Test
    @Transactional
    public void testGrantAuthorPrivileges() {
        User userToGrantPrivileges = new User();
        userToGrantPrivileges.setRole(new UserEnumType("USER"));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(userToGrantPrivileges));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(userToGrantPrivileges);
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
}
