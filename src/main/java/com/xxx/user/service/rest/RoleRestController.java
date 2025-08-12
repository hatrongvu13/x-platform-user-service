package com.xxx.user.service.rest;

import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.role.Role;
import com.xxx.user.service.services.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    @PostMapping(value = "/new")
    public ResponseEntity<?> createRole(@RequestBody Role role) throws JOSEException {
        return ResponseEntity.ok(roleService.create(role));
    }
}
