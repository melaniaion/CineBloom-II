package com.cinebloom.mainservice.dtos;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String username;
    private String email;
    private String bio;
    private MultipartFile profilePicture;
    private String currentPassword;
    private String newPassword;
}
