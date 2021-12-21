package com.share.io.controller;


import com.share.io.dto.query.file.FileQuery;
import com.share.io.repository.user.UserRepository;
import com.share.io.service.file.FileStorageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public TestController(UserRepository userRepository, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/all")
    public String allAccess() {
       // Optional<User> byId = userRepository.findById(1L);
        FileQuery fileQuery = new FileQuery();
        //fileQuery.setUserId(1L);
        fileQuery.setOriginalName("Capt");
//        fileQuery.setName("test");
        fileQuery.setDescription("cool");
        fileStorageService.findAllFilesBySpecification(fileQuery);
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
