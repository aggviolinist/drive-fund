package com.drivefundproject.drive_fund.admin.fetchUsers.dto.response;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchUserResponse {
    private String firstname;

    private String lastname;

    private String profileImage;

    private String email;    
}
