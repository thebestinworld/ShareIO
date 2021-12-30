package com.share.io.controller;

import com.share.io.model.user.User;
import com.share.io.repository.user.UserRepository;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> findUser(@CurrentUser UserCurrent userCurrent) {
        List<User> allByIdNotIn = userRepository.findAllByIdNot(userCurrent.getId());
        User user = new User();
        user.setId(3L);
        user.setUsername("Test Username");
        allByIdNotIn.add(user);
        return ResponseEntity.status(HttpStatus.OK).body(allByIdNotIn);
    }
}
