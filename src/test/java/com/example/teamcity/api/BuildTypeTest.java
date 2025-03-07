package com.example.teamcity.api;

import com.example.teamcity.api.generators.RoleGenerator;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.base.CheckedRequests;
import com.example.teamcity.api.requests.base.UncheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.api.spec.ValidationResponseSpecifications;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.generators.TestDataGenerator.generate;

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

        softy.assertThat(testData.getBuildType().getName()).isEqualTo(createdBuildType.getName());
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
                .spec(ValidationResponseSpecifications.checkBuildTypeIdAlreadyExists(testData.getBuildType().getId()));
    }

    @Test(
            description = "Project admin should be able to create build type for their project",
            groups = {"Positive", "Roles"}
    )
    public void projectAdminCreatesBuildTypeTest() {
        var projectAdminRole = RoleGenerator.generateProjectAdmin(testData.getProject().getId());
        testData.getUser().getRoles().setRole(Arrays.asList());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));

        var createdUserId = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser()).getId();
        superUserCheckRequests.getRequest(PROJECT).create(testData.getProject());
        superUserCheckRequests.getRequest(USERS).addUserRole(createdUserId, projectAdminRole);

        userCheckRequests.getRequest(BUILD_TYPES).create(testData.getBuildType());

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(testData.getBuildType().getId());
        softy.assertThat(createdBuildType)
                .usingRecursiveComparison()
                .ignoringFields("steps.count", "steps.step.id")
                .isEqualTo(testData.getBuildType());
    }

    @Test(
            description = "Project admin should not be able to create build type for not their project",
            groups = {"Negative", "Roles"}
    )
    public void projectAdminCreatesBuildTypeForAnotherProjectTest() {
        var projectAdminRole = RoleGenerator.generateProjectAdmin(testData.getProject().getId());
        testData.getUser().getRoles().setRole(Arrays.asList());

        var createdUserId = superUserCheckRequests.<User>getRequest(USERS).create(testData.getUser()).getId();
        superUserCheckRequests.getRequest(PROJECT).create(testData.getProject());
        superUserCheckRequests.getRequest(USERS).addUserRole(createdUserId, projectAdminRole);

        var testData2 = generate();
        projectAdminRole = RoleGenerator.generateProjectAdmin(testData2.getProject().getId());
        testData2.getUser().getRoles().setRole(Arrays.asList());

        var createdUserId2 = superUserCheckRequests.<User>getRequest(USERS).create(testData2.getUser()).getId();
        superUserCheckRequests.getRequest(PROJECT).create(testData2.getProject());
        superUserCheckRequests.getRequest(USERS).addUserRole(createdUserId2, projectAdminRole);

        var userUncheckRequests = new UncheckedRequests(Specifications.authSpec(testData2.getUser()));

        userUncheckRequests.getRequest(BUILD_TYPES)
                .create(testData.getBuildType())
                .then()
                .spec(ValidationResponseSpecifications
                        .checkUserDontHavePermissionsToEditProject(testData.getProject().getId()));
    }
}
