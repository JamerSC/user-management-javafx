package org.example.dao;

import org.example.database.DatabaseConnection;
import org.example.model.User;
import org.example.util.PasswordUtil;
import org.slf4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(UserDAO.class);

    // CREATE USER
// CREATE USER
    public void save(User user) {

        String sql = "INSERT INTO users (name, email, password, created_by, created_date, updated_by, updated_date) " +
                "VALUES (?, ?, ?, ?, NOW(), ?, NOW())";

        logger.debug("Creating user with name: {}", user.getName());

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword()); // Pass the hashed password

            if (user.getCreatedBy() != null) {
                statement.setInt(4, user.getCreatedBy());
                statement.setInt(5, user.getCreatedBy()); // Set updated_by same as creator on creation
            } else {
                statement.setNull(4, Types.INTEGER);
                statement.setNull(5, Types.INTEGER);
            }

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("User created successfully: {}", user.getName());
            } else {
                logger.warn("User was not created: {}", user.getName());
            }

        } catch (SQLException e) {
            logger.error("Failed to create user: {}", user.getName(), e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    // FIND ALL USERS
//    public List<User> findAll() {
//        List<User> users =
//                new ArrayList<>();
//
//        String sql =
//                "SELECT id, name, email FROM users";
//
//        logger.debug("Fetching all users");
//
//        try (
//                Connection connection = DatabaseConnection.getConnection();
//                PreparedStatement statement = connection.prepareStatement(sql);
//                ResultSet result = statement.executeQuery()
//        ) {
//
//            while (result.next()) {
//
//                User user = new User();
//                user.setId(result.getInt("id"));
//                user.setName(result.getString("name"));
//                user.setEmail(result.getString("email"));
//                users.add(user);
//            }
//
//            logger.info("Successfully fetched {} users", users.size());
//
//        } catch (SQLException e) {
//
//            logger.error("Failed to fetch users", e);
//            throw new RuntimeException("Failed to fetch users", e);
//        }
//
//        return users;
//    }

    // FIND ALL WITH JOIN
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.id, u.name, u.email, u.created_date, u.updated_date, " +
                "c.name AS creator_name, p.name AS updater_name " +
                "FROM users u " +
                "LEFT JOIN users c ON u.created_by = c.id " +
                "LEFT JOIN users p ON u.updated_by = p.id";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                User user = new User();
                user.setId(result.getInt("id"));
                user.setName(result.getString("name"));
                user.setEmail(result.getString("email"));
                user.setCreatedByName(result.getString("creator_name"));
                user.setUpdatedByName(result.getString("updater_name"));

                Timestamp cTimestamp = result.getTimestamp("created_date");
                if (cTimestamp != null) user.setCreatedDate(cTimestamp.toLocalDateTime());

                Timestamp uTimestamp = result.getTimestamp("updated_date");
                if (uTimestamp != null) user.setUpdatedDate(uTimestamp.toLocalDateTime());

                users.add(user);
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch users", e);
            throw new RuntimeException("Failed to fetch users", e);
        }

        return users;
    }

    // FIND USER BY ID
//    public User findById(int id) {
//
//        String sql =
//                "SELECT id, name, email " +
//                        "FROM users " +
//                        "WHERE id = ?";
//
//        logger.debug("Finding user by ID: {}", id);
//
//        try (
//                Connection connection = DatabaseConnection.getConnection();
//                PreparedStatement statement = connection.prepareStatement(sql)
//        ) {
//
//            statement.setInt(1, id);
//
//            try (ResultSet result =
//                         statement.executeQuery()) {
//
//                if (result.next()) {
//
//                    User user = new User(
//                            result.getInt("id"),
//                            result.getString("name"),
//                            result.getString("email")
//                    );
//
//                    logger.info("User found: ID={}", id);
//                    return user;
//                }
//            }
//
//            logger.debug("User not found: ID={}", id);
//
//        } catch (SQLException e) {
//            logger.error("Failed to find user: ID={}", id, e);
//            throw new RuntimeException("Failed to find user", e);
//        }
//
//        return null;
//    }

    public User findById(int id) {
        String sql = "SELECT u.id, u.name, u.email, u.created_date, u.updated_date, " +
                "c.name AS creator_name, p.name AS updater_name " +
                "FROM users u " +
                "LEFT JOIN users c ON u.created_by = c.id " +
                "LEFT JOIN users p ON u.updated_by = p.id " +
                "WHERE u.id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    User user = new User(result.getInt("id"), result.getString("name"), result.getString("email"));
                    user.setCreatedByName(result.getString("creator_name"));
                    user.setUpdatedByName(result.getString("updater_name"));

                    Timestamp cTimestamp = result.getTimestamp("created_date");
                    if (cTimestamp != null) user.setCreatedDate(cTimestamp.toLocalDateTime());

                    Timestamp uTimestamp = result.getTimestamp("updated_date");
                    if (uTimestamp != null) user.setUpdatedDate(uTimestamp.toLocalDateTime());

                    return user;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find user by ID", e);
            throw new RuntimeException("Failed to find user", e);
        }
        return null;
    }

    public User findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users WHERE LOWER(email) = LOWER(?)";

        logger.debug("Executing DB lookup for user login: {}", email);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new User(
                            result.getInt("id"),
                            result.getString("name"),
                            result.getString("email"),
                            result.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to query user by email: {}", email, e);
            throw new RuntimeException("Database error during authentication lookup", e);
        }

        return null;
    }

    // UPDATE USER
    public void update(User user) {
//        String sql =
//                "UPDATE users " +
//                        "SET name = ?, email = ?, password = ? " +
//                        "WHERE id = ?";
        boolean hasPassword = user.getPassword() != null && !user.getPassword().isBlank();

        String sql = hasPassword
                ? "UPDATE users SET name = ?, email = ?, password = ?, updated_by = ?, updated_date = NOW() WHERE id = ?"
                : "UPDATE users SET name = ?, email = ?, updated_by = ?, updated_date = NOW() WHERE id = ?";

        logger.debug("Updating user: ID={}", user.getId());

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());

//            statement.setInt(3, user.getId());

            int index = 3;
            if (hasPassword) {
                statement.setString(index++, user.getPassword());
            }

            if (user.getUpdatedBy() != null) {
                statement.setInt(index++, user.getUpdatedBy());
            } else {
                statement.setNull(index++, Types.INTEGER);
            }

            statement.setInt(index, user.getId());

//            int rowsAffected = statement.executeUpdate();
//
//            if (rowsAffected > 0) {
//
//                logger.info(
//                        "User updated successfully: ID={}",
//                        user.getId()
//                );
//
//            } else {
//
//                logger.warn(
//                        "No user was updated: ID={}",
//                        user.getId()
//                );
//            }

            statement.executeUpdate();
            logger.info("User updated successfully: ID={}", user.getId());

        } catch (SQLException e) {
            logger.error("Failed to update user: ID={}", user.getId(), e);
            throw new RuntimeException("Failed to update user", e);
        }
    }

    // DELETE USER
    public void delete(int id) {

        String sql =
                "DELETE FROM users WHERE id = ?";

        logger.debug("Deleting user: ID={}", id);

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("User deleted successfully: ID={}", id);
            } else {
                logger.warn("No user found to delete: ID={}", id);
            }

        } catch (SQLException e) {
            logger.error("Failed to delete user: ID={}", id, e);
            throw new RuntimeException("Failed to delete user", e);
        }
    }
}
