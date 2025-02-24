package com.example.teamcity.api;

import com.example.teamcity.api.generators.PropertiesGenerator;
import com.example.teamcity.api.models.Build;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.api.requests.CheckedRequests;
import com.example.teamcity.api.spec.Specifications;
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

    @BeforeMethod
    public void setupWireMockServer() {
        var fakeBuild = Build.builder()
                .state("finished")
                .status("SUCCESS")
                .build();

        WireMock.setupServer(post(BUILD_QUEUE.getUrl()), HttpStatus.SC_OK, fakeBuild);
    }

    @Test(description = "User should be able to start build (with WireMock)",
            groups = {"Regression"})
    public void userStartsBuildWithWireMockTest() {
        var checkedBuildQueueRequest = new CheckedRequests(Specifications.mockSpec());

        var build = checkedBuildQueueRequest.<Build>getRequest(BUILD_QUEUE).create(Build.builder()
                .buildType(testData.getBuildType())
                .build());

        softy.assertEquals(build.getState(), "finished");
        softy.assertEquals(build.getStatus(), "SUCCESS");
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

        userCheckRequests.<Project>getRequest(PROJECT).create(testData.getProject());
        userCheckRequests.<BuildType>getRequest(BUILD_TYPES).create(testData.getBuildType());
        userCheckRequests.<Build>getRequest(BUILD_QUEUE).create(Build.builder()
                .buildTypeId(testData.getBuildType().getId())
                .build());

        var userMockedRequests = new CheckedRequests(Specifications.mockSpec());

        var build = userMockedRequests.<Build>getRequest(BUILD_QUEUE).create(Build.builder()
                .buildTypeId(testData.getBuildType().getId())
                .build());

        softy.assertEquals(build.getState(), "finished");
        softy.assertEquals(build.getStatus(), "SUCCESS");
    }

    @AfterMethod(alwaysRun = true)
    public void stopWireMockServer() {
        WireMock.stopServer();
    }

}
