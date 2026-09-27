package org.example.service;

import org.example.dto.UserDto;

import java.util.List;

public class AuthService {

    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Verifies user credentials.
     * Note: In production, passwords should be hashed (e.g., using BCrypt).
     */
    public boolean authenticate(String usernameOrEmail, String password) {
        if (usernameOrEmail == null || usernameOrEmail.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        String inputUser = usernameOrEmail.trim();

        // 1. Check Hardcoded Admin
        if ("admin".equalsIgnoreCase(inputUser) && "admin123".equals(password)) {
            UserSession.startSession(new UserDto("0", "Administrator", "admin@example.com"));
            return true;
        }

        // 2. Check Database Users
        List<UserDto> users = userService.getAllUsers();
        for (UserDto user : users) {
            if (user.getEmail().equalsIgnoreCase(inputUser) && "123456".equals(password)) {
                UserSession.startSession(user);
                return true;
            }
        }

        return false;
    }

    public void logout() {
        UserSession.cleanSession();
    }
}