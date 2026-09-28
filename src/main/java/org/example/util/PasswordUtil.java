package org.example.util;

import org.example.dao.UserDAO;
import org.example.model.User;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class PasswordUtil {
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    /**
     * Hashes a password with a random dynamic salt using PBKDF2WithHmacSHA256.
     */
    public static String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Verifies a raw input password against a stored hashed password.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null || !storedHash.contains(":")) {
            return false;
        }

        String[] parts = storedHash.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        byte[] actualHash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        // Constant-time array comparison to prevent timing attacks
        int diff = expectedHash.length ^ actualHash.length;
        for (int i = 0; i < expectedHash.length && i < actualHash.length; i++) {
            diff |= expectedHash[i] ^ actualHash[i];
        }
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error occurred while hashing password", e);
        }
    }

    public static void main(String[] args) {
        String hashedPassword = PasswordUtil.hashPassword("Admin@123");
        User adminUser = new User("System Admin", "admin@mail.com", hashedPassword);

        UserDAO userDAO = new UserDAO();
        userDAO.save(adminUser);
        System.out.println("Admin user created with hashed password: " + hashedPassword);
    }
}
