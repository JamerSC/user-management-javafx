package org.example.mapper;

import org.example.dto.UserDto;
import org.example.model.User;
import org.example.security.CryptoUtil;

public class UserMapper {
    // This class can be used to map between User and UserDto if needed in the future.

    public static UserDto toDto(User user) {

        String encryptedId =
                CryptoUtil.encrypt(
                        String.valueOf(user.getId())
                );

        return new UserDto(encryptedId, user.getName(), user.getEmail(), user.getPassword());
    }

    public static User toEntity(UserDto userDto) {

        int id =
                Integer.parseInt(
                        CryptoUtil.decrypt(
                                userDto.getId()
                        )
                );

        return new User(id, userDto.getName(), userDto.getEmail());
    }
}
