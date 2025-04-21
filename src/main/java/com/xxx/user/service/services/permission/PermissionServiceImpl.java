package com.xxx.user.service.services.permission;

import com.xxx.user.service.data.permission.Permission;
import com.xxx.user.service.database.entity.PermissionEntity;
import com.xxx.user.service.database.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public Permission findById(Long id) {
        PermissionEntity permissionEntity = permissionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Permission not found"));

        return new Permission(permissionEntity.getId(),
                permissionEntity.getCode(),
                permissionEntity.getValue());
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity pe = new PermissionEntity();
        BeanUtils.copyProperties(permission, pe);
        permissionRepository.save(pe);
        return permission;
    }

    @Override
    public Permission update(Permission permission) {
        PermissionEntity pe = new PermissionEntity();
        BeanUtils.copyProperties(permission, pe);
        permissionRepository.save(pe);
        return permission;
    }

    @Override
    public Permission delete(Long id) {
        permissionRepository.deleteById(id);
        return null;
    }

    @Override
    public void initPermission() {

    }


}
