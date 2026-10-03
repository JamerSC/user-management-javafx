package org.example.service;

import org.example.dto.UserDto;

public class UserSession {

    private static UserDto currentUser;

    public static void startSession(UserDto user) {
        currentUser = user;
    }

    public static UserDto getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if the active user possesses a given permission string (e.g. "USER_CREATE").
     */
    public static boolean hasPermission(String permissionName) {
        if (currentUser == null || currentUser.getPermissions() == null || permissionName == null) {
            return false;
        }
        return currentUser.getPermissions().contains(permissionName);
    }

    /**
     * Checks if the active user holds a specific role (e.g. "ADMIN").
     */
    public static boolean hasRole(String roleName) {
        if (currentUser == null || currentUser.getRoles() == null || roleName == null) {
            return false;
        }
        return currentUser.getRoles().contains(roleName);
    }

    public static void cleanSession() {
        currentUser = null;
    }

}
