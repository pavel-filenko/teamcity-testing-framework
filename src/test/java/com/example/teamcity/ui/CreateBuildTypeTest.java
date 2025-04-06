package com.example.teamcity.ui;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BuildType;
import com.example.teamcity.api.models.BuildTypes;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.admin.CreateBuildTypeManuallyPage;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateBuildTypeTest extends BaseUiTest {

    @Test(description = "User should be able to create build type",
            groups = {"Positive"})
    public void userCreatesBuildTypeManuallySuccessfullyTest() {
        // Подготовка окружения
        loginAs(testData.getUser());
        superUserCheckRequests.getRequest(Endpoint.PROJECT).create(testData.getProject());

        // Взаимодействие с UI
        CreateBuildTypeManuallyPage
                .open(testData.getProject().getId())
                .createFormManually(testData.getBuildType());

        // Проверка состояния на API
        // (корректность отправки данных с UI на бэк)
        var createdBuildType = superUserCheckRequests.<BuildType>getRequest(Endpoint.BUILD_TYPES).read("id:" + testData.getBuildType().getId());
        softy.assertThat(createdBuildType).usingRecursiveComparison()
                .ignoringFields("steps")
                .isEqualTo(testData.getBuildType());

        // Проверка состояния на UI
        // (корректность считывания данных и отображение данных на UI)
        var buildTypeExists = ProjectPage.open(testData.getProject().getId())
                .getBuildTypes()
                .stream().anyMatch(
                        buildType -> buildType.getName().getText().equals(testData.getBuildType().getName())
                );
        softy.assertThat(buildTypeExists).isTrue();
    }

    @Test(description = "User should not be able to create BuildType without name",
        groups = {"Negative"})
    public void userCannotCreateBuildTypeManuallyWithEmptyNameTest() {
        // Подготовка окружения
        loginAs(testData.getUser());
        superUserCheckRequests.getRequest(Endpoint.PROJECT).create(testData.getProject());
        var buildTypesAmount = superUserCheckRequests
                .<BuildTypes>getRequest(Endpoint.PROJECT_BUILD_TYPES)
                .read("id:" + testData.getProject().getId())
                .getBuildType()
                .size();

        // Взаимодействие с UI
        CreateBuildTypeManuallyPage
                .open(testData.getProject().getId())
                .createFormManuallyWithoutName(testData.getBuildType());

        // Проверка состояния на API
        // (корректность отправки данных с UI на бэк)
        softy.assertThat(superUserCheckRequests
                .<BuildTypes>getRequest(Endpoint.PROJECT_BUILD_TYPES)
                .read("id:" + testData.getProject().getId())
                .getBuildType()
                .size())
                .isEqualTo(buildTypesAmount);

        // Проверка состояния на UI
        // (корректность считывания данных и отображение данных на UI)
        var buildTypes = ProjectPage.open(testData.getProject().getId()).getBuildTypes();
        softy.assertThat(buildTypes).isEmpty();
    }
}
