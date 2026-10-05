package org.example.service;

import org.example.dao.UserDAO;
import org.example.dto.UserDto;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.security.CryptoUtil;
import org.example.util.PasswordUtil;

import java.util.List;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // CREATE USER
    public void createUser(String name, String email, String rawPassword) {
        // Enforce RBAC Permission Check
        if (!UserSession.hasPermission("USER_CREATE")) {
            throw new SecurityException("Access Denied: Missing 'USER_CREATE' permission.");
        }

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or blank");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null or blank");
        if (rawPassword == null || rawPassword.isBlank()) throw new IllegalArgumentException("Password cannot be null or blank");

        String hashedPassword = PasswordUtil.hashPassword(rawPassword);
        User user = new User(name, email, hashedPassword);

        Integer currentUserId = getCurrentLoggedInUserId();
        user.setCreatedBy(currentUserId);
        user.setUpdatedBy(currentUserId);

        userDAO.save(user);
    }

    // GET ALL USERS
    public List<UserDto> getAllUsers() {
        // Enforce RBAC Permission Check
        if (!UserSession.hasPermission("USER_READ")) {
            throw new SecurityException("Access Denied: Missing 'USER_READ' permission.");
        }

        return userDAO.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // GET USER BY ID
    public User getUserById(String encryptedId) {
        if (!UserSession.hasPermission("USER_READ")) {
            throw new SecurityException("Access Denied: Missing 'USER_READ' permission.");
        }

        int id = Integer.parseInt(CryptoUtil.decrypt(encryptedId));
        return userDAO.findById(id);
    }

    // UPDATE USER
    public void updateUser(String encryptedId, String name, String email, String rawPassword) {
        // Enforce RBAC Permission Check
        if (!UserSession.hasPermission("USER_UPDATE")) {
            throw new SecurityException("Access Denied: Missing 'USER_UPDATE' permission.");
        }

        int id = Integer.parseInt(CryptoUtil.decrypt(encryptedId));

        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or blank");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email cannot be null or blank");

        String hashedPassword = (rawPassword != null && !rawPassword.isBlank())
                ? PasswordUtil.hashPassword(rawPassword)
                : null;

        User user = new User(id, name, email, hashedPassword);
        user.setUpdatedBy(getCurrentLoggedInUserId());

        userDAO.update(user);
    }

    // DELETE USER BY ID
    public void deleteUserById(String encryptedId) {
        // Enforce RBAC Permission Check
        if (!UserSession.hasPermission("USER_DELETE")) {
            throw new SecurityException("Access Denied: Missing 'USER_DELETE' permission.");
        }

        int id = Integer.parseInt(CryptoUtil.decrypt(encryptedId));
        userDAO.delete(id);
    }

    private Integer getCurrentLoggedInUserId() {
        UserDto currentUser = UserSession.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null) {
            try {
                return Integer.parseInt(CryptoUtil.decrypt(currentUser.getId()));
            } catch (Exception e) {
                return Integer.parseInt(currentUser.getId());
            }
        }
        return null;
    }
}
