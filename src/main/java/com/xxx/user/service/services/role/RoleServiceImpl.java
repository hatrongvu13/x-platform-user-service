package com.xxx.user.service.services.role;

import com.htv.proto.user.RoleGrpc;
import com.htv.proto.user.RolesByUserGrpc;
import com.xxx.user.service.database.entity.PermissionEntity;
import com.xxx.user.service.database.entity.RoleEntity;
import com.xxx.user.service.database.entity.UserEntity;
import com.xxx.user.service.database.repository.RoleRepository;
import com.xxx.user.service.database.repository.UserRepository;
import io.grpc.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void initRole() {
        if (!roleRepository.findAll().isEmpty()) {
            log.info("Ignore init role");
            return;
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
    @Transactional
    public void addUserRoleDefault(Long userid) {
        try {
            UserEntity userEntity = userRepository.findById(userid).orElse(null);
            if (userEntity == null) {
                return;
            }
            RoleEntity roleEntity = roleRepository.findByCode("USER").orElse(null);
            if (roleEntity == null) {
                log.info("Add new user role default");
                roleEntity = new RoleEntity();
                roleEntity.setCode("USER");
                roleEntity.setValue("USER");
                PermissionEntity permissionEntity = new PermissionEntity();
                permissionEntity.setValue("EDIT");
                permissionEntity.setCode("EDIT");
                PermissionEntity permissionEntity1 = new PermissionEntity();
                permissionEntity1.setValue("VIEW");
                permissionEntity1.setCode("VIEW");
                roleEntity.setPermissions(Set.of(permissionEntity, permissionEntity1));
            }

            userEntity.setRoles(Set.of(roleEntity));

            userRepository.save(userEntity);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<RoleGrpc> getRoleByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElse(null);
        if (userEntity == null) {
            throw Status.NOT_FOUND.withDescription("User not found").asRuntimeException();
        }
        return userEntity.getRoles().stream().map(item -> RoleGrpc.newBuilder()
                .setId(item.getId())
                .setCode(item.getCode())
                .setValue(item.getValue())
                .build()).toList();
    }

    @Override
    public List<RolesByUserGrpc> getRolesByUsername(List<String> usernames) {
        List<UserEntity> userEntities = userRepository.findAllByUsernameIn(usernames).orElse(null);
        if (CollectionUtils.isEmpty(userEntities)) {
            throw Status.NOT_FOUND.withDescription("User not found").asRuntimeException();
        }

        List<RolesByUserGrpc> result = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            RolesByUserGrpc.Builder builder = RolesByUserGrpc.newBuilder();
            builder.setUsername(userEntity.getUsername());
            if (CollectionUtils.isNotEmpty(userEntity.getRoles())) {
                builder.addAllRole(userEntity.getRoles().stream().map(item -> RoleGrpc.newBuilder()
                        .setId(item.getId())
                        .setCode(item.getCode())
                        .setValue(item.getValue())
                        .build()).collect(Collectors.toList()));
            }
            result.add(builder.build());
        }

        return result;
    }
}
