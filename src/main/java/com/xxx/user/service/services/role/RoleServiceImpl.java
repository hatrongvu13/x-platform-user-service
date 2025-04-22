package com.xxx.user.service.services.role;

import com.xxx.user.service.database.entity.PermissionEntity;
import com.xxx.user.service.database.entity.RoleEntity;
import com.xxx.user.service.database.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public RoleEntity create(RoleEntity role) {
        return roleRepository.save(role);
    }

    @Override
    public RoleEntity findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<RoleEntity> findAllByUserId(Long userId) {
        return roleRepository.findAllByUserId(userId);
    }

    @Override
    public void initRole() {
        PermissionEntity permissionEntity = new PermissionEntity();
        permissionEntity.setValue("EDIT");
        permissionEntity.setCode("EDIT");
        PermissionEntity permissionEntity1 = new PermissionEntity();
        permissionEntity1.setValue("VIEW");
        permissionEntity1.setCode("VIEW");
        PermissionEntity permissionEntity2 = new PermissionEntity();
        permissionEntity2.setValue("MANAGE");
        permissionEntity2.setCode("MANAGE");
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setCode("ADMIN");
        roleEntity.setValue("ADMIN");
        Set<PermissionEntity> permissionEntities = new HashSet<>();
        permissionEntities.add(permissionEntity);
        permissionEntities.add(permissionEntity1);
        permissionEntities.add(permissionEntity2);
        roleEntity.setPermissions(permissionEntities);
        roleRepository.save(roleEntity);
        RoleEntity roleEntity1 = new RoleEntity();
        roleEntity1.setCode("USER");
        roleEntity1.setValue("USER");
        Set<PermissionEntity> permissionEntities1 = new HashSet<>();
        permissionEntities1.add(permissionEntity);
        permissionEntities1.add(permissionEntity1);
        roleEntity1.setPermissions(permissionEntities1);
        roleRepository.save(roleEntity1);
    }
}
