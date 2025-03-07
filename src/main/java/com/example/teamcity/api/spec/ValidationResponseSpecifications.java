package com.example.teamcity.api.spec;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public class ValidationResponseSpecifications {
    public static ResponseSpecification checkUserDontHavePermissionsToEditProject(String projectId) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .expectBody(Matchers.containsString(
                        "You do not have enough permissions to edit project with id: %s".formatted(projectId)
                ))
                .build();
    }

    public static ResponseSpecification checkBuildTypeIdAlreadyExists(String BuildTypeId) {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(Matchers.containsString(
                        "The build configuration / template ID \"%s\" is already used by another configuration or template"
                                .formatted(BuildTypeId)
                ))
                .build();
    }
}
