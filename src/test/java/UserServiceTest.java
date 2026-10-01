import org.example.dao.UserDAO;
import org.example.dto.UserDto;
import org.example.model.User;
import org.example.security.CryptoUtil;
import org.example.service.UserService;
import org.example.service.UserSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserDAO userDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = mock(UserDAO.class);
        userService = new UserService(userDAO);
        UserSession.cleanSession(); // Ensure a clean session before each test
    }

    @AfterEach
    void tearDown() {
        UserSession.cleanSession();
    }

//    1. Use a Testing Framework
//    Use a popular testing framework like JUnit (e.g., JUnit 5) for writing and running tests.
//    Add the JUnit dependency to your pom.xml:
//    2. Follow the Arrange-Act-Assert Pattern
//    Structure your tests into three sections:
//            Arrange: Set up the test data and environment.
//            Act: Call the method under test.
//            Assert: Verify the results.
//    3. Write Tests for Each Public Method
//    Test all public methods in your classes, especially in UserService and UserDAO.
//    4. Mock Dependencies
//    Use mocking frameworks like Mockito to mock dependencies (e.g., UserDAO in UserService).
//    5. Use Meaningful Test Names
//    Use descriptive names for test methods to indicate what is being tested and the expected outcome.
//    6. Test Edge Cases
//    Write tests for edge cases, such as null inputs, empty strings, and invalid data.


    @Test
    void createUser_ShouldSaveUser_WhenValidInput() {
        // Arrange
        String name = "Wolfgang Amadeus Mozart";
        String email = "wolfgang@mail.com";
        String password = "Password123!";

        // Act
        userService.createUser(name, email, password);

        // Assert
        verify(userDAO, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenNameIsBlank() {
        // Arrange
        String name = " ";
        String email = "example@example.com";
        String password = "Password123!";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(name, email, password)
        );

        assertEquals("Name cannot be null or blank", exception.getMessage());
        verify(userDAO, never()).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailIsBlank() {
        // Arrange
        String name = "John Doe";
        String email = "";
        String password = "Password123!";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(name, email, password)
        );

        assertEquals("Email cannot be null or blank", exception.getMessage());
        verify(userDAO, never()).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenPasswordIsBlank() {
        // Arrange
        String name = "John Doe";
        String email = "john@example.com";
        String password = "   ";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(name, email, password)
        );

        assertEquals("Password cannot be null or blank", exception.getMessage());
        verify(userDAO, never()).save(any());
    }

    // --- GET USERS TESTS ---

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        // Arrange
        List<User> mockUsers = Arrays.asList(
                new User(1, "John Doe", "john.doe@example.com"),
                new User(2, "Jane Doe", "jane.doe@example.com")
        );
        when(userDAO.findAll()).thenReturn(mockUsers);

        // Act
        List<UserDto> users = userService.getAllUsers();

        // Assert
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        verify(userDAO, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        int rawId = 1;
        String encryptedId = CryptoUtil.encrypt(String.valueOf(rawId));
        User mockUser = new User(rawId, "John Doe", "john.doe@example.com");

        when(userDAO.findById(rawId)).thenReturn(mockUser);

        // Act
        User user = userService.getUserById(encryptedId);

        // Assert
        assertNotNull(user);
        assertEquals("John Doe", user.getName());
        verify(userDAO, times(1)).findById(rawId);
    }

    @Test
    void getUserById_ShouldReturnNull_WhenUserDoesNotExist() {
        // Arrange
        int rawId = 99;
        String encryptedId = CryptoUtil.encrypt(String.valueOf(rawId));

        when(userDAO.findById(rawId)).thenReturn(null);

        // Act
        User user = userService.getUserById(encryptedId);

        // Assert
        assertNull(user);
        verify(userDAO, times(1)).findById(rawId);
    }

    // --- UPDATE USER TESTS ---

    @Test
    void updateUser_ShouldUpdateUser_WhenValidInputWithNewPassword() {
        // Arrange
        int rawId = 1;
        String encryptedId = CryptoUtil.encrypt(String.valueOf(rawId));
        String name = "Updated Name";
        String email = "updated.email@example.com";
        String newPassword = "NewPassword123!";

        // Act
        userService.updateUser(encryptedId, name, email, newPassword);

        // Assert
        verify(userDAO, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenPasswordIsBlankOrNull() {
        // Arrange
        int rawId = 1;
        String encryptedId = CryptoUtil.encrypt(String.valueOf(rawId));
        String name = "Updated Name";
        String email = "updated.email@example.com";

        // Act (Pass null or empty string for password to keep existing password)
        userService.updateUser(encryptedId, name, email, "");

        // Assert
        verify(userDAO, times(1)).update(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowException_WhenNameIsBlank() {
        // Arrange
        int rawId = 1;
        String encryptedId = CryptoUtil.encrypt(String.valueOf(rawId));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(encryptedId, " ", "updated@mail.com", "pass")
        );

        assertEquals("Name cannot be null or blank", exception.getMessage());
        verify(userDAO, never()).update(any());
    }


    // --- DELETE USER TESTS ---

    @Test
    void deleteUserById_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        int rawId = 1;
        String encryptedId = CryptoUtil.encrypt(String.valueOf(rawId));

        // Act
        userService.deleteUserById(encryptedId);

        // Assert
        verify(userDAO, times(1)).delete(rawId);
    }
}
