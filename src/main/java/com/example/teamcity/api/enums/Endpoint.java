package com.example.teamcity.api.enums;

import com.example.teamcity.api.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Endpoint {
    BUILD_TYPES("/app/rest/buildTypes", BuildType.class),
    BUILD_QUEUE("/app/rest/buildQueue", Build.class),
    PROJECT("/app/rest/projects", Project.class),
    USERS("/app/rest/users", User.class),

    PROJECT_BUILD_TYPES("/app/rest/projects/%s/buildTypes", BuildTypes.class),
    USER_ROLES("/app/rest/users/%s/roles", Roles.class);

    private final String url;
    private final Class<? extends BaseModel> modelClass;
}
