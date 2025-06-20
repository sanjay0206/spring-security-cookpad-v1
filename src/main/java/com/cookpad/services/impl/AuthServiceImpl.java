package com.cookpad.services.impl;

import com.cookpad.dto.UserDto;
import com.cookpad.entities.User;
import com.cookpad.exceptions.RecipeAPIException;
import com.cookpad.repositories.UserRepository;
import com.cookpad.security.AppConfig;
import com.cookpad.security.UserRole;
import com.cookpad.services.AuthService;
import com.cookpad.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
//    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppConfig appConfig;


    @Autowired
    public AuthServiceImpl(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder, AppConfig appConfig) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appConfig = appConfig;
    }

    @Override
    public UserDto register (UserDto userDto) {
        log.info("userDto: {}", userDto);

        // add check for username exists in database
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RecipeAPIException(HttpStatus.BAD_REQUEST, "Username is already exists!.");
        }

        // add check for email exists in database
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RecipeAPIException(HttpStatus.BAD_REQUEST, "Email is already exists!.");
        }

        if (userDto.getUserRole().equals(UserRole.ADMIN)) {
            throw new RecipeAPIException(HttpStatus.BAD_REQUEST, "Can not register as ADMIN.");
        }

        // create user object
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setGender(userDto.getGender());
        user.setUserRole(userDto.getUserRole());
        user.setCreatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);

        return userService.mapToDTO(savedUser);
    }

    @Override
    public String logout (HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> clientCookie = Arrays.stream(request.getCookies())
                .filter(cookie -> Objects.equals(appConfig.getCookieName(), cookie.getName()))
                .findAny();

        clientCookie.ifPresent(cookie -> {
                    cookie.setValue(null);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    response.addHeader(HttpHeaders.COOKIE, cookie.getValue());
                    log.info("Cookie after logout: {}", cookie.getValue());
                }
        );

        SecurityContextHolder.clearContext();
        return "User logged out successfully";
    }
}
