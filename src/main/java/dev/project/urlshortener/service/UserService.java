package dev.project.urlshortener.service;

import dev.project.urlshortener.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.project.urlshortener.dto.LoginRequest;
import dev.project.urlshortener.dto.RegisterRequest;
import dev.project.urlshortener.entity.Role;
import dev.project.urlshortener.entity.UserEntity;
import dev.project.urlshortener.exception.UserAlreadyExistsException;
import dev.project.urlshortener.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()))
            throw new UserAlreadyExistsException("Username '" + request.username() + "' is already taken");
        if (userRepository.existsByEmail(request.email()))
            throw new UserAlreadyExistsException("Email '" + request.email() + "' is already taken");

        String encodedPassword = passwordEncoder.encode(request.password());
        UserEntity user = new UserEntity(
                request.username(),
                request.email(),
                encodedPassword,
                Role.ROLE_USER);
        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String token = jwtUtil.generateToken(request.username());
        return token;
    }
}
