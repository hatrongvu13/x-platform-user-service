package com.xxx.user.service.rest;

import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.permission.Permission;
import com.xxx.user.service.services.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/permission")
@RequiredArgsConstructor
public class PermissionRestController {
    private final PermissionService permissionService;

    @PostMapping("/new")
    public ResponseEntity<?> createPermission(@RequestBody Permission permission) throws JOSEException {
        return ResponseEntity.ok(permissionService.save(permission));
    }
}
