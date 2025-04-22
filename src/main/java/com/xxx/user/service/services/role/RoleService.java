package com.xxx.user.service.services.role;

import com.xxx.user.service.database.entity.RoleEntity;

import java.util.List;

public interface RoleService {
    RoleEntity create(RoleEntity role);
    RoleEntity findById(Long id);
    List<RoleEntity> findAll();
    List<RoleEntity> findAllByUserId(Long userId);
    void initRole();
}
