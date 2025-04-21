package com.xxx.user.service.services.permission;

import com.xxx.user.service.data.permission.Permission;

public interface PermissionService {
    Permission findById(Long id);
    Permission save(Permission permission);
    Permission update(Permission permission);
    Permission delete(Long id);

    void initPermission();
}
