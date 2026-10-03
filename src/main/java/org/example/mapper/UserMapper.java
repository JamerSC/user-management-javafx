package org.example.mapper;

import org.example.dto.UserDto;
import org.example.model.Role;
import org.example.model.User;
import org.example.security.CryptoUtil;

import java.time.format.DateTimeFormatter;

public class UserMapper {
    // This class can be used to map between User and UserDto if needed in the future.

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static UserDto toDto(User user) {

        String encryptedId = CryptoUtil.encrypt(String.valueOf(user.getId()));

//        return new UserDto(encryptedId, user.getName(), user.getEmail(), user.getPassword());
        UserDto dto = new UserDto(encryptedId, user.getName(), user.getEmail(), user.getPassword());

        if (user.getCreatedByName() != null) {
            dto.setCreatedByName(user.getCreatedByName());
        }
        if (user.getCreatedDate() != null) {
            dto.setCreatedDate(user.getCreatedDate().format(FORMATTER));
        }

        if (user.getUpdatedByName() != null) {
            dto.setUpdatedByName(user.getUpdatedByName());
        }
        if (user.getUpdatedDate() != null) {
            dto.setUpdatedDate(user.getUpdatedDate().format(FORMATTER));
        }

        // Map Permissions & Roles
        if (user.getPermissions() != null) {
            dto.setPermissions(user.getPermissions());
        }
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                dto.getRoles().add(role.getName());
            }
        }

        return dto;
    }

    public static User toEntity(UserDto userDto) {

        int id = Integer.parseInt(CryptoUtil.decrypt(userDto.getId()));

        return new User(id, userDto.getName(), userDto.getEmail());
    }
}
