package com.cookpad.controllers;

import com.cookpad.dto.UserDto;
import com.cookpad.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "Endpoints for managing users in the system")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get all users",
            description = "This endpoint retrieves a list of all users in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved all users"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAuthority('SCOPE_user:read')")
    @Operation(
            summary = "Get user by ID",
            description = "This endpoint retrieves details of a user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PreAuthorize("hasAuthority('SCOPE_user:create')")
    @Operation(
            summary = "Create a new user",
            description = "This endpoint allows the creation of a new user.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created the user"),
                    @ApiResponse(responseCode = "400", description = "Invalid user data provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping("/add-user")
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        return userService.createUser(userDto);
    }

    @PreAuthorize("hasAuthority('SCOPE_user:update')")
    @Operation(
            summary = "Update an existing user",
            description = "This endpoint allows updating an existing user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid user data provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userId, userDto));
    }

    @PreAuthorize("hasAuthority('SCOPE_user:delete')")
    @Operation(
            summary = "Delete a user",
            description = "This endpoint allows deleting a user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the user"),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
