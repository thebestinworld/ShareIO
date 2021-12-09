package com.share.io.service.role;

import com.share.io.model.role.Role;
import com.share.io.model.role.RoleName;
import com.share.io.repository.role.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService{

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findByRoleName(RoleName roleName) {
        return this.roleRepository.findByRoleName(roleName);
    }
}
