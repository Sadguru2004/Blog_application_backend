package com.sadguru.blogapplication.controllers;

import com.sadguru.blogapplication.entities.Role;
import com.sadguru.blogapplication.entities.User;
import com.sadguru.blogapplication.repositories.RoleRepo;
import com.sadguru.blogapplication.repositories.UserRepo;
import com.sadguru.blogapplication.security.JwtHelper;
import com.sadguru.blogapplication.payloads.JwtRequest;
import com.sadguru.blogapplication.payloads.JwtResponse;

import com.sadguru.blogapplication.security.TokenBlacklist;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private TokenBlacklist tokenBlacklist;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public JwtResponse login(@Valid  @RequestBody JwtRequest request) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                );

        authenticationManager.authenticate(authToken);

        String token = jwtHelper.generateToken(request.getEmail());


        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        JwtResponse response = new JwtResponse();
        response.setToken(token);
        response.setUserId(user.getId());

        return response;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role role = roleRepo.findById(2)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.getRoles().add(role);

        return userRepo.save(user);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            tokenBlacklist.addToken(token);
        }

        return "Logged out successfully";
    }
}