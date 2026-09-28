package org.example.service;

import org.example.dao.UserDAO;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.util.PasswordUtil;

import java.util.List;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

//    private final UserService userService;
//
//    public AuthService(UserService userService) {
//        this.userService = userService;
//    }

    /**
     * Authenticates a user against real-time database credentials.
     */
    public boolean authenticate(String email, String rawPassword) {
        if (email == null || email.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return false;
        }

        // 1. Direct O(1) DB fetch by email
        User user = userDAO.findByEmail(email.trim());
        if (user == null || user.getPassword() == null) {
            return false; // User not found
        }

        // 2. Cryptographic password verification
        boolean isValid = PasswordUtil.verifyPassword(rawPassword, user.getPassword());

        if (isValid) {
            // 3. Initialize Thread-Safe User Session
            UserDto userDto = UserMapper.toDto(user);
            UserSession.startSession(userDto);
            return true;
        }

        return false;
    }

    /**
     * Verifies user credentials.
     * Note: In production, passwords should be hashed (e.g., using BCrypt).
     */
//    public boolean authenticate(String usernameOrEmail, String password) {
//        if (usernameOrEmail == null || usernameOrEmail.isBlank() || password == null || password.isBlank()) {
//            return false;
//        }
//
//        String inputUser = usernameOrEmail.trim();
//
//        // 1. Check Hardcoded Admin
//        if ("admin".equalsIgnoreCase(inputUser) && "admin123".equals(password)) {
//            UserSession.startSession(new UserDto("0", "Administrator", "admin@example.com"));
//            return true;
//        }
//
//        // 2. Check Database Users
//        List<UserDto> users = userService.getAllUsers();
//        for (UserDto user : users) {
//            if (user.getEmail().equalsIgnoreCase(inputUser) && "123456".equals(password)) {
//                UserSession.startSession(user);
//                return true;
//            }
//        }
//
//        return false;
//    }

    public void logout() {
        UserSession.cleanSession();
    }
}