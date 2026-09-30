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
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }

        // Hash password before saving
        String hashedPassword = PasswordUtil.hashPassword(rawPassword);
        User user = new User(name, email, hashedPassword);

        // Populate createdBy from current session
        Integer currentUserId = getCurrentLoggedInUserId();
        user.setCreatedBy(currentUserId);
        user.setUpdatedBy(currentUserId);

        userDAO.save(user);
    }

    private Integer getCurrentLoggedInUserId() {
        UserDto currentUser = UserSession.getCurrentUser();
        if (currentUser != null && currentUser.getId() != null) {
            try {
                return Integer.parseInt(CryptoUtil.decrypt(currentUser.getId()));
            } catch (Exception e) {
                // If ID was not encrypted in session, parse directly
                return Integer.parseInt(currentUser.getId());
            }
        }
        return null; // Null if created by system / initial seed
    }

    // GET ALL USERS
    public List<UserDto> getAllUsers() {
//        return userDAO.findAll();

        List<User> users = userDAO.findAll();

        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    // GET USER BY ID
    public User getUserById(String encryptedId) {

        int id = Integer.parseInt(
                CryptoUtil.decrypt(encryptedId)
        );

        return userDAO.findById(id);
    }

    // UPDATE USER
    public void updateUser(String encryptedId, String name, String email, String rawPassword) {
        int id = Integer.parseInt(CryptoUtil.decrypt(encryptedId));

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        // Only hash password if a new password was typed in
        String hashedPassword = (rawPassword != null && !rawPassword.isBlank())
                ? PasswordUtil.hashPassword(rawPassword)
                : null;

        User user = new User(id, name, email, hashedPassword);

        // Populate updatedBy from current session
        user.setUpdatedBy(getCurrentLoggedInUserId());

        userDAO.update(user);
    }

    // DELETE USER BY ID
    public void deleteUserById(String encryptedId) {

        int id = Integer.parseInt(
                CryptoUtil.decrypt(encryptedId)
        );

        userDAO.delete(id);
    }
}
