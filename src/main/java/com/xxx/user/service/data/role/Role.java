package com.xxx.user.service.data.role;

import com.xxx.user.service.data.permission.Permission;

import java.util.Set;

public record Role(Long id,
                   String code,
                   String value,
                   Set<Permission> roles) {
}
