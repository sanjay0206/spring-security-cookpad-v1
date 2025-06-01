package com.cookpad.controllers;

import com.cookpad.dto.UserDto;
import com.cookpad.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user login and registration")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user",
            description = "This endpoint registers a new user in the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User successfully registered"),
                    @ApiResponse(responseCode = "400", description = "Invalid user data")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.register(userDto));
    }

    @Operation(
            summary = "Logout the user",
            description = "Logs the user out of the system and invalidates the session.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged out"),
                    @ApiResponse(responseCode = "400", description = "Error during logout")
            }
    )
    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse) {
        String response = authService.logout(httpServletRequest, httpServletResponse);
        return ResponseEntity.ok(response);
    }
}
