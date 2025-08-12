package com.xxx.user.service.services.role;

import com.xxx.user.service.data.role.Role;
import com.xxx.user.service.database.entity.RoleEntity;

import java.util.List;

public interface RoleService {
    RoleEntity create(Role role);
    RoleEntity findById(Long id);
    List<RoleEntity> findAll();
    void initRole();
}
