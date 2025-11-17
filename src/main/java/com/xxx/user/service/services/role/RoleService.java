package com.xxx.user.service.services.role;

import com.htv.proto.user.RoleGrpc;
import com.htv.proto.user.RolesByUserGrpc;

import java.util.List;

public interface RoleService {
    void initRole();
    void addUserRoleDefault(Long userid);
    List<RoleGrpc> getRoleByUsername(String username);
    List<RolesByUserGrpc> getRolesByUsername(List<String> usernames);
}
