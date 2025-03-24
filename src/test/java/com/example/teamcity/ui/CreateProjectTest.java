package com.example.teamcity.ui;

import com.example.teamcity.ui.pages.admin.CreateProjectPage;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";

    @Test(description = "User should be able to create project",
            groups = {"Positive"})
    public void userCreatesProjectSuccessfullyTest() {
        // Подготовка окружения
        loginAs(testData.getUser());

        // Взаимодействие с UI
        CreateProjectPage.open("_Root")
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // Проверка состояния на API
        // (корректность отправки данных с UI на бэк)
        step("Check that all entities (project, build type) was successfully created with correct data on API level");

        // Проверка состояния на UI
        // (корректность считывания данных и отображение данных на UI)
        step("Check that project is visible on Project Page (http://localhost:8111/favorite/projects)");
    }

    @Test(description = "User should not be able to create project without name",
            groups = {"Negative"})
    public void userCreatesProjectWithoutNameTest() {
        // Подготовка окружения
        step("Login as user");
        step("Check number of projects");

        // Взаимодействие с UI
        step("Open `Create Object Page` (http://localhost:8111/admin/createObjectMenu.html)");
        step("Send all project parameters (repository URL)");
        step("Click `Proceed`");
        step("Set Project Name value empty");
        step("Click `Proceed`");

        // Проверка состояния на API
        // (корректность отправки данных с UI на бэк)
        step("Check that number of projects did not change");

        // Проверка состояния на UI
        // (корректность считывания данных и отображение данных на UI)
        step("Check that error appears `Project name must not be empty`");
    }

}
