package com.sunrise.sunriseapp.controller;

import com.sunrise.sunriseapp.dto.AuthRequest;
import com.sunrise.sunriseapp.dto.AuthResponse;
import com.sunrise.sunriseapp.security.JwtUtil;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserDetailsService uds;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserDetailsService uds, PasswordEncoder encoder, JwtUtil jwt) {
        this.uds = uds; this.encoder = encoder; this.jwt = jwt;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        var user = uds.loadUserByUsername(req.username());
        if (user == null || !encoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Credenciais inválidas");
        }
        String token = jwt.generate(user.getUsername());
        return new AuthResponse(token);
    }
}
