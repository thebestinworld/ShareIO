package com.share.io.service.user;

import com.share.io.model.user.User;

public interface UserService {

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void save(User user);
}
