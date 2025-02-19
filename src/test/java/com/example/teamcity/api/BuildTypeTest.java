package com.example.teamcity.api;

import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.models.User;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
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
        var user = generate(User.class);

        superUserCheckRequests.getRequest(USERS).create(user);
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(user));

        var project = generate(Project.class);

        project = userCheckRequests.<Project>getRequest(PROJECT).create(project);

        var buildType = generate(Arrays.asList(project), BuildType.class);

        userCheckRequests.getRequest(BUILD_TYPES).create(buildType);

        var createdBuildType = userCheckRequests.<BuildType>getRequest(BUILD_TYPES).read(buildType.getId());

        softy.assertEquals(buildType.getName(), createdBuildType.getName(), "Created build type name should be equal to expected");
    }

    @Test(
            description = "User should not be able to build types with the same id",
            groups = {"Negative", "CRUD"}
    )
    public void userCreatesTwoBuildTypesWithTheSameIdTest() {
        step("Create user");
        step("Create project by user");
        step("Create buildType1 for project by user");
        step("Create buildType2 with buildType1 id for project by user");
        step("Check buildType2 was not created with bad request code");
    }

    @Test(
            description = "Project admin should be able to create build type for their project",
            groups = {"Positive", "Roles"}
    )
    public void projectAdminCreatesBuildTypeTest() {
        step("Create user");
        step("Create project");
        step("Grant user PROJECT_ADMIN role in project");

        step("Create buildType for project by user with role");
        step("Check buildType was created successfully");
    }

    @Test(
            description = "Project admin should not be able to create build type for not their project",
            groups = {"Negative", "Roles"}
    )
    public void projectAdminCreatesBuildTypeForAnotherProjectTest() {
        step("Create user1");
        step("Create project1");
        step("Grant user1 PROJECT_ADMIN role in project1");

        step("Create user2");
        step("Create project2");
        step("Grant user2 PROJECT_ADMIN role in project2");


        step("Create buildType for project1 by user2");
        step("Create buildType was not created with forbidden code");
    }
}
