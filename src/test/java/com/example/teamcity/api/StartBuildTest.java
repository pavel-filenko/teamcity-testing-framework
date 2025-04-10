package com.example.teamcity.api;

import com.example.teamcity.api.generators.PropertiesGenerator;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.base.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
import com.example.teamcity.common.BuildInfo;
import com.example.teamcity.common.WireMock;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.example.teamcity.api.enums.Endpoint.*;
import static com.example.teamcity.api.enums.Endpoint.BUILD_TYPES;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

@Feature("Start build")
public class StartBuildTest extends BaseApiTest {

    @BeforeMethod(alwaysRun = true)
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .state(BuildInfo.BUILD_STATE_FINISHED)
                .status(BuildInfo.BUILD_STATUS_SUCCESS)
                .build();

        WireMock.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
    }

    @Test(description = "User should be able to start build (with WireMock)",
            groups = {"Regression"})
    public void userStartsBuildWithWireMockTest() {
        var checkedBuildQueueRequest = new CheckedRequests(Specifications.mockSpec());
        var build = Build.builder()
                .buildType(testData.getBuildType())
                .status(BuildInfo.BUILD_STATUS_SUCCESS)
                .state(BuildInfo.BUILD_STATE_FINISHED)
                .build();

        var createdBuild = checkedBuildQueueRequest.<Build>getRequest(BUILD_QUEUE).create(build);

        softy.assertThat(createdBuild)
                .usingRecursiveComparison()
                .ignoringFields("buildType")
                .isEqualTo(build);
    }

    @Test(
            description = "User should be able to start 'Hello, World!' build (with WireMock)",
            groups = {"Regression"}
    )
    public void userStartsHelloWorldBuildWithWireMockTest() {
        superUserCheckRequests.getRequest(USERS).create(testData.getUser());
        var userCheckRequests = new CheckedRequests(Specifications.authSpec(testData.getUser()));
        testData.getBuildType()
                .getSteps()
                .getStep()
                .getFirst()
                .setProperties(PropertiesGenerator.generatePropertiesWithEcho());
        var build = Build.builder()
                .status(BuildInfo.BUILD_STATUS_SUCCESS)
                .state(BuildInfo.BUILD_STATE_FINISHED)
                .buildTypeId(testData.getBuildType().getId())
                .build();

        userCheckRequests.<Project>getRequest(PROJECT).create(testData.getProject());
        userCheckRequests.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType());
        userCheckRequests.<Build>getRequest(BUILD_QUEUE).create(Build.builder()
                .buildTypeId(testData.getBuildType().getId())
                .build());

        var userMockedRequests = new CheckedRequests(Specifications.mockSpec());

        var createdBuild = userMockedRequests.<Build>getRequest(BUILD_QUEUE).create(build);

        softy.assertThat(createdBuild)
                .usingRecursiveComparison()
                .ignoringFields("buildType", "id", "buildTypeId")
                .isEqualTo(build);
    }

    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }
}
