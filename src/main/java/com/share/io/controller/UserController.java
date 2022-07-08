package com.share.io.controller;

import com.share.io.dto.user.UserDTO;
import com.share.io.model.user.User;
import com.share.io.repository.user.UserRepository;
import com.share.io.security.CurrentUser;
import com.share.io.security.UserCurrent;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<UserDTO>> findUser(@CurrentUser UserCurrent userCurrent) {
        List<User> allByIdNotIn = userRepository.findAllByIdNot(userCurrent.getId());
        ModelMapper modelMapper = new ModelMapper();
        List<UserDTO> userDTOS = allByIdNotIn.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(userDTOS);
    }
}
