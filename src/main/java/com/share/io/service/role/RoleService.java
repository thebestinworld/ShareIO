package com.share.io.service.role;

import com.share.io.model.role.Role;
import com.share.io.model.role.RoleName;

import java.util.Optional;

public interface RoleService {

    Optional<Role> findByRoleName(RoleName roleName);
}
