package com.example.teamcity.ui.pages.admin;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public abstract class CreateBasePageFromRepository extends CreateBasePage {
    protected static final String CREATE_URL = "/admin/createObjectMenu.html?projectId=%s&showMode=%s";

    protected final SelenideElement inputUrl = $("#url");
    protected final SelenideElement submitButton = $(Selectors.byAttribute("value", "Proceed"));
    protected final SelenideElement buildTypeNameInput = $("#buildTypeName");
    protected final SelenideElement connectionSuccessfulMessage = $(".connectionSuccessful");
    protected final SelenideElement successfullyCreatedMessage = $("main > div.successMessage ");

    protected void baseCreateForm(String url) {
        inputUrl.val(url);
        submitButton.click();
        connectionSuccessfulMessage.should(Condition.appear, BASE_WAITING);
    }
}
