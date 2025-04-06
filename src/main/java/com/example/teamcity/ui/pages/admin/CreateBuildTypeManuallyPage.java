package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.models.BuildType;

import static com.codeborne.selenide.Selenide.$;

public class CreateBuildTypeManuallyPage extends CreateBasePage {
    protected static final String CREATE_MANUALLY_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=%s";
    private static final String CREATE_BUILD_TYPE_MODE = "createBuildTypeMenu";

    private final SelenideElement buildTypeNameInput = $("#buildTypeName");
    private final SelenideElement buildTypeIdInput = $("input[id='buildTypeExternalId']");
    private final SelenideElement createBuildTypeButton = $("input[name='createBuildType']");
    private final SelenideElement buildTypeCreatedMessage = $("div[id*='buildTypeCreated']");
    private final SelenideElement buildTypeNameError = $("#error_buildTypeName");
    private final SelenideElement manuallyButton = $("a[data-hint-container-id='create-build-configuration']");

    public static CreateBuildTypeManuallyPage open(String targetProjectId) {
        return Selenide.open(CREATE_MANUALLY_URL.formatted(targetProjectId, CREATE_BUILD_TYPE_MODE), CreateBuildTypeManuallyPage.class);
    }

    private void baseCreateFormManually(BuildType buildType) {
        manuallyButton.click();
        buildTypeNameInput.val(buildType.getName());
        buildTypeIdInput.val(buildType.getId());
        createBuildTypeButton.click();
    }

    public void createFormManually(BuildType buildType) {
        baseCreateFormManually(buildType);
        buildTypeCreatedMessage.shouldBe(Condition.appear, BASE_WAITING);
    }

    public void createFormManuallyWithoutName(BuildType buildType) {
        buildType.setName("");
        baseCreateFormManually(buildType);
        buildTypeNameError.shouldBe(Condition.visible);
    }
}
