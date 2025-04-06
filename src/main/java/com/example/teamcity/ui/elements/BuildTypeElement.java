package com.example.teamcity.ui.elements;

import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

@Getter
public class BuildTypeElement extends BasePageElement {
    private SelenideElement name;
    private SelenideElement runButton;

    public BuildTypeElement(SelenideElement element) {
        super(element);
        this.name = find(".MiddleEllipsis__searchable--uZ");
        this.runButton = find("button[data-test='run-build']");
    }
}
