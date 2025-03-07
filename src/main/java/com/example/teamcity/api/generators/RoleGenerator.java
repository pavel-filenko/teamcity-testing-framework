package com.example.teamcity.api.generators;

import com.example.teamcity.api.models.Role;

public class RoleGenerator {
    public static Role generateProjectAdmin(String projectId) {
        return Role
                .builder()
                .roleId("PROJECT_ADMIN")
                .scope("p:%s".formatted(projectId))
                .build();
    }
}
