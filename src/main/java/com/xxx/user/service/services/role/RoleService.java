package com.xxx.user.service.services.role;

import com.google.protobuf.ProtocolStringList;
import com.htv.proto.user.RoleGrpc;

import java.util.List;

public interface RoleService {
    void initRole();
    void addUserRoleDefault(Long userid);
    List<RoleGrpc> getRoleByUsername(ProtocolStringList usernameList);
}
