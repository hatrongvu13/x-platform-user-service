package com.xxx.user.service.services.role;

import com.xxx.user.service.database.entity.RoleEntity;
import com.xxx.user.service.database.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public RoleEntity create(RoleEntity role) {
        return null;
    }
}
