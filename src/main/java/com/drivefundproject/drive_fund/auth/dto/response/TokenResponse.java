package com.drivefundproject.drive_fund.auth.dto.response;

import com.drivefundproject.drive_fund.utilities.roles.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    private String token;
    private Role role;
    private String firstname;

}
