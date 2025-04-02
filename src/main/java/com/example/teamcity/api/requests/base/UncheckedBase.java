package com.example.teamcity.api.requests.base;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class UncheckedBase extends Request implements CrudInterface {

    UncheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
    }

    public String convertEndpoint(String url, String locator) {
        if (url.contains("%s")) {
            return url.formatted(locator);
        } else {
            return url + "/" + locator;
        }
    }

    @Override
    public Response create(BaseModel model) {
        return RestAssured
                .given()
                .spec(spec)
                .body(model)
                .post(endpoint.getUrl());
    }

    @Override
    public Response read(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .get(convertEndpoint(endpoint.getUrl(), locator));
    }

    @Override
    public Response update(String locator, BaseModel model) {
        return RestAssured
                .given()
                .spec(spec)
                .body(model)
                .put(convertEndpoint(endpoint.getUrl(), locator));
    }

    @Override
    public Response delete(String locator) {
        return RestAssured
                .given()
                .spec(spec)
                .delete(convertEndpoint(endpoint.getUrl(), locator));
    }
}
