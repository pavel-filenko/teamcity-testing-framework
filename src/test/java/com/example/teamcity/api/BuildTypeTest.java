package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.requests.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;
import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class BuildTypeTest extends BaseApiTest {
    @Test(
            description = "User should be able to create build type",
            groups = {"Positive", "CRUD"}
    )
    public void userCreatesBuildTypeTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.<Project>getRequest(PROJECT).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());

        softy.assertEquals(testData.getBuildType().getName(), createdBuildType.getName(), "Created build type name should be equal to expected");
    }

    @Test(
            description = "User should not be able to build types with the same id",
            groups = {"Negative", "CRUD"}
    )
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        var buildTypeWithSameId = generate(Arrays.asList(testData.getProject()), BuildType.class, testData.getBuildType().getId());

        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        userCheckRequests.getRequest(PROJECT).create(testData.getProject());

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        new UncheckedRequests(Specifications.authSpec(testData.getUser())).getRequest(BUILD_TYPES)
                .create(buildTypeWithSameId)
                .then()
                .assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(Matchers.containsString(
                        "The build configuration / template ID \"%s\" is already used by another configuration or template"
                                .formatted(testData.getBuildType().getId())
                ));
    }

    @Test(
            description = "Project admin should be able to create build type for their project",
            groups = {"Positive", "Roles"}
    )
    public void projectAdminCreatesBuildTypeTest() {
        var projectAdminRole = Role.builder()
                .roleId("PROJECT_ADMIN")
                .scope("p:%s".formatted(testData.getProject().getId()))
                .build();
        testData.getUser().getRoles().setRole(Arrays.asList());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var createdUserId = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser()).getId();
        superUserCheckRequests.getRequest(PROJECT).create(testData.getProject());
        superUserCheckRequests.getRequest(USERS).addUserRole(createdUserId, projectAdminRole);

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());
    }

    @Test(
            description = "Project admin should not be able to create build type for not their project",
            groups = {"Negative", "Roles"}
    )
    public void projectAdminCreatesBuildTypeForAnotherProjectTest() {
        testData.getUser().getRoles().setRole(Arrays.asList());
        var projectAdminRole = Role.builder()
                .roleId("PROJECT_ADMIN")
                .scope("p:%s".formatted(testData.getProject().getId()))
                .build();

        var createdUserId = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser()).getId();
        superUserCheckRequests.getRequest(PROJECT).create(testData.getProject());
        superUserCheckRequests.getRequest(USERS).addUserRole(createdUserId, projectAdminRole);

        var testData2 = generate();
        testData2.getUser().getRoles().setRole(Arrays.asList());
        projectAdminRole = Role.builder()
                .roleId("PROJECT_ADMIN")
                .scope("p:%s".formatted(testData2.getProject().getId()))
                .build();
        var createdUserId2 = superUserCheckRequests.<User>getRequest(USERS).create(testData2.getUser()).getId();
        superUserCheckRequests.getRequest(PROJECT).create(testData2.getProject());
        superUserCheckRequests.getRequest(USERS).addUserRole(createdUserId2, projectAdminRole);

        var userUncheckRequests = new UncheckedRequests(Specifications.authSpec(testData2.getUser()));

        userUncheckRequests.getRequest(BUILD_TYPES)
                .create(testData.getBuildType())
                .then()
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN)
                .body(Matchers.containsString(
                        "You do not have enough permissions to edit project with id: %s"
                                .formatted(testData.getProject().getId())
                ));
    }
}
