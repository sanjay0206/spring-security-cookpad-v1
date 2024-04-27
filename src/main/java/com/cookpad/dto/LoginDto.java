package com.cookpad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @NotEmpty(message = "Username or email should not be null or empty")
    private String usernameOrEmail;

    @NotEmpty(message = "Password should not be null or empty")
    private String password;
}
