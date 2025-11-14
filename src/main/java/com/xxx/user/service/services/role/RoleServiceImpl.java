package com.xxx.user.service.services.role;

import com.xxx.user.service.database.entity.PermissionEntity;
import com.xxx.user.service.database.entity.RoleEntity;
import com.xxx.user.service.database.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void initRole() {
        if (!roleRepository.findAll().isEmpty()) {
            log.info("Ignore init role");
        }
        log.info("Init role");
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
        log.info("End init role");
    }

    @Override
    public void addUserRoleDefault(Long userid) {

    }
}
