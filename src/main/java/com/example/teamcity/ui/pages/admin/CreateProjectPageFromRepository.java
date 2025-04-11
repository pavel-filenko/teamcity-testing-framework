package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

public class CreateProjectPageFromRepository extends CreateBasePageFromRepository {

    private static final String PROJECT_SHOW_MODE = "createProjectMenu";

    private final SelenideElement projectNameInput = $("#projectName");

    public static CreateProjectPageFromRepository open(String projectId) {
        return Selenide.open(CREATE_URL.formatted(projectId, PROJECT_SHOW_MODE), CreateProjectPageFromRepository.class);
    }

    public CreateProjectPageFromRepository createForm(String url) {
        baseCreateForm(url);
        return this;
    }

    public void setupProject(String projectName, String buildTypeName) {
        projectNameInput.val(projectName);
        buildTypeNameInput.val(buildTypeName);
        submitButton.click();
        successfullyCreatedMessage.shouldBe(Condition.visible, BASE_WAITING);
    }
}
