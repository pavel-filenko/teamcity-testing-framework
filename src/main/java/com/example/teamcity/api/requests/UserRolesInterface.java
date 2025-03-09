package com.example.teamcity.api.requests;

import com.example.teamcity.api.models.Role;

public interface UserRolesInterface {
    Object getUserRoles(String id);
    Object addUserRole(String id, Role role);
    Object deleteUserRole(String id, Role role);
}
