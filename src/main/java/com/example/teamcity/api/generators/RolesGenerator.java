package com.example.teamcity.api.generators;

import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.Roles;

import java.util.Arrays;

public class RolesGenerator {
    public static Roles generateProjectAdminRoles(String projectId) {
        return
                Roles.builder().role(Arrays.asList(Role
                .builder()
                .roleId("PROJECT_ADMIN")
                .scope("p:%s".formatted(projectId))
                .build())).build();
    }
}
