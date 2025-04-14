package com.xxx.user.service.data.user;

import com.xxx.user.service.data.role.Role;

import java.util.Set;

public record User(Long id,
                   String username,
                   String fullName,
                   String email,
                   String password,
                   Set<Role> roles) {
}
