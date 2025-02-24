package com.example.teamcity.api.requests.unchecked;

import com.example.teamcity.api.enums.Endpoint;
import com.example.teamcity.api.models.BaseModel;
import com.example.teamcity.api.models.Role;
import com.example.teamcity.api.requests.CrudInterface;
import com.example.teamcity.api.requests.Request;
import com.example.teamcity.api.requests.UserRolesInterface;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UncheckedBase extends Request implements CrudInterface, UserRolesInterface {

    public UncheckedBase(RequestSpecification spec, Endpoint endpoint) {
        super(spec, endpoint);
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
    public Response read(String id) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl() + "/id:" + id);
    }

    @Override
    public Response update(String id, BaseModel model) {
        return RestAssured
                .given()
                .body(model)
                .spec(spec)
                .put(endpoint.getUrl() + "/id:" + id);
    }

    @Override
    public Response delete(String id) {
        return RestAssured
                .given()
                .spec(spec)
                .delete(endpoint.getUrl() + "/id:" + id);
    }

    @Override
    public Response getUserRoles(String id) {
        return RestAssured
                .given()
                .spec(spec)
                .get(endpoint.getUrl() + "id:%s/roles".formatted(id));
    }

    @Override
    public Response addUserRole(String id, Role role) {
        return RestAssured
                .given()
                .spec(spec)
                .put(endpoint.getUrl() + "/id:%s/roles/%s/%s".formatted(id, role.getRoleId(), role.getScope()));
    }

    @Override
    public Response deleteUserRole(String id, Role role) {
        return RestAssured
                .given()
                .spec(spec)
                .delete(endpoint.getUrl() + "id:%s/roles/%s/%s".formatted(id, role.getRoleId(), role.getScope()));
    }
}
