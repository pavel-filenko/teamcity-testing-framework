package com.example.teamcity.ui;

import com.codeborne.selenide.Condition;
import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.Project;
import com.example.teamcity.ui.pages.ProjectPage;
import com.example.teamcity.ui.pages.ProjectsPage;
import com.example.teamcity.ui.pages.admin.CreateProjectPageFromRepository;
import org.testng.annotations.Test;

import static io.qameta.allure.Allure.step;

@Test(groups = {"Regression"})
public class CreateProjectTest extends BaseUiTest {
    private static final String REPO_URL = "https://github.com/AlexPshe/spring-core-for-qa";
    private static final String ROOT_PROJECT_ID = "_Root";

    @Test(description = "User should be able to create project",
            groups = {"Positive"})
    public void userCreatesProjectSuccessfullyTest() {
        // Подготовка окружения
        loginAs(testData.getUser());

        // Взаимодействие с UI
        CreateProjectPageFromRepository.open(ROOT_PROJECT_ID)
                .createForm(REPO_URL)
                .setupProject(testData.getProject().getName(), testData.getBuildType().getName());

        // Проверка состояния на API
        // (корректность отправки данных с UI на бэк)
        var createdProject = superUserCheckRequests.<Project>getRequest(Endpoint.PROJECT).read("name:" + testData.getProject().getName());
        softy.assertThat(createdProject).isNotNull();

        // Проверка состояния на UI
        // (корректность считывания данных и отображение данных на UI)
        ProjectPage.open(createdProject.getId())
                .title.shouldHave(Condition.exactText(testData.getProject().getName()));

        var projectExists = ProjectsPage.open()
                .getProjects()
                .stream().anyMatch(
                        project -> project.getName().text().equals(
                                testData.getProject().getName()
                        )
                );
        softy.assertThat(projectExists).isTrue();
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
