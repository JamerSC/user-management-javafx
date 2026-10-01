import org.example.dao.UserDAO;
import org.example.model.User;
import org.example.service.AuthService;
import org.example.service.UserSession;
import org.example.util.PasswordUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private UserDAO userDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        authService = new AuthService(userDAO);
        UserSession.cleanSession(); // Reset active user session
    }

    @AfterEach
    void tearDown() {
        UserSession.cleanSession();
    }

    @Test
    void authenticate_ShouldReturnTrueAndStartSession_WhenCredentialsAreValid() {
        // Arrange
        String email = "admin@example.com";
        String rawPassword = "Password123!";
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);

        User mockUser = new User(1, "Admin User", email, hashedPassword);
        when(userDAO.findByEmail(email)).thenReturn(mockUser);

        // Act
        boolean result = authService.authenticate(email, rawPassword);

        // Assert
        assertTrue(result);
        assertNotNull(UserSession.getCurrentUser());
        assertEquals("Admin User", UserSession.getCurrentUser().getName());
        verify(userDAO, times(1)).findByEmail(email);
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenUserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userDAO.findByEmail(email)).thenReturn(null);

        // Act
        boolean result = authService.authenticate(email, "somePassword");

        // Assert
        assertFalse(result);
        assertNull(UserSession.getCurrentUser());
        verify(userDAO, times(1)).findByEmail(email);
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenPasswordIsIncorrect() {
        // Arrange
        String email = "user@example.com";
        String correctPassword = "CorrectPassword123";
        String wrongPassword = "WrongPassword456";
        String hashedPassword = PasswordUtil.hashPassword(correctPassword);

        User mockUser = new User(1, "Test User", email, hashedPassword);
        when(userDAO.findByEmail(email)).thenReturn(mockUser);

        // Act
        boolean result = authService.authenticate(email, wrongPassword);

        // Assert
        assertFalse(result);
        assertNull(UserSession.getCurrentUser());
        verify(userDAO, times(1)).findByEmail(email);
    }

    @Test
    void authenticate_ShouldReturnFalse_WhenEmailOrPasswordIsBlank() {
        // Act & Assert
        assertFalse(authService.authenticate("", "password"));
        assertFalse(authService.authenticate("user@mail.com", "  "));
        assertFalse(authService.authenticate(null, "password"));
        assertFalse(authService.authenticate("user@mail.com", null));

        verify(userDAO, never()).findByEmail(any());
        assertNull(UserSession.getCurrentUser());
    }

    @Test
    void logout_ShouldClearActiveSession() {
        // Arrange: Start session
        String hashedPassword = PasswordUtil.hashPassword("pass123");
        User mockUser = new User(1, "John", "john@mail.com", hashedPassword);
        when(userDAO.findByEmail("john@mail.com")).thenReturn(mockUser);

        authService.authenticate("john@mail.com", "pass123");
        assertNotNull(UserSession.getCurrentUser()); // Verify session active

        // Act
        authService.logout();

        // Assert
        assertNull(UserSession.getCurrentUser()); // Verify session cleared
    }
}
