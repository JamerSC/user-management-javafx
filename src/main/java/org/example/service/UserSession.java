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

    public static void cleanSession() {
        currentUser = null;
    }

}
