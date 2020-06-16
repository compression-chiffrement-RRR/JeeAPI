package com.cyphernet.api.auth.web;

import com.cyphernet.api.account.service.AccountService;
import com.cyphernet.api.auth.model.AuthRequest;
import com.cyphernet.api.auth.model.AuthResponse;
import com.cyphernet.api.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@RestController
@RequestMapping("/login")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @Autowired
    AuthController(AuthenticationManager authenticationManager, AccountService accountService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> createAuth(@Valid @RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }
        final UserDetails userDetails = accountService.loadUserByUsername(authRequest.getUsername());

        final String token = jwtUtil.generateToken(userDetails);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, "Bearer " + token);

        return new ResponseEntity<>(httpHeaders, HttpStatus.OK);

    }
}
