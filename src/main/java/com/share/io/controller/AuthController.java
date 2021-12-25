package com.share.io.controller;


import com.share.io.dto.request.LoginRequest;
import com.share.io.dto.request.SignUpRequest;
import com.share.io.dto.request.TokenRefreshRequest;
import com.share.io.dto.response.JwtResponse;
import com.share.io.dto.response.MessageResponse;
import com.share.io.dto.response.TokenRefreshResponse;
import com.share.io.exception.TokenRefreshException;
import com.share.io.model.role.Role;
import com.share.io.model.role.RoleName;
import com.share.io.model.token.RefreshToken;
import com.share.io.model.user.User;
import com.share.io.security.JwtUtils;
import com.share.io.security.UserDetailsImpl;
import com.share.io.service.role.RoleService;
import com.share.io.service.token.RefreshTokenService;
import com.share.io.service.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService,
                          RoleService roleService, PasswordEncoder encoder, JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleService = roleService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (Exception ex) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Incorrect username or password!"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is already taken!"));
        }

        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);
        user.setRoles(roles);

        userService.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
//        refreshTokenService::verifyExpiration(requestRefreshToken)
//        RefreshToken byToken = refreshTokenService.findByToken(requestRefreshToken).orElse(null);
//        String token = jwtUtils.generateJwtTokenFromUsername(byToken.getUser().getUsername());
//
//        return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));


        return  refreshTokenService.findByToken(requestRefreshToken)
               .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateJwtTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                }).orElse(null);

    }
}
