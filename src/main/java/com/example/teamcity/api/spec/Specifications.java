package com.example.teamcity.api.spec;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.User;
import com.github.viclovsky.swagger.coverage.FileSystemOutputWriter;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageRestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.nio.file.Paths;
import java.util.List;

import static com.github.viclovsky.swagger.coverage.SwaggerCoverageConstants.OUTPUT_DIRECTORY;

public class Specifications {
    private static Specifications spec;

    private static RequestSpecBuilder reqBuilder() {
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
//        reqBuilder.addFilter(new RequestLoggingFilter());
//        reqBuilder.addFilter(new ResponseLoggingFilter());
        reqBuilder.addFilter(new SwaggerCoverageRestAssured(
                new FileSystemOutputWriter(
                        Paths.get("target/" + OUTPUT_DIRECTORY)
                )
        ));
        reqBuilder.addFilter(new AllureRestAssured());
        reqBuilder.setBaseUri("http://%s".formatted(Config.getProperty("host"))).build();
        reqBuilder.setContentType(ContentType.JSON);
        reqBuilder.setAccept(ContentType.JSON);
        reqBuilder.addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()));
        return reqBuilder;
    }

    public static RequestSpecification superUserAuthSpec() {
        BasicAuthScheme basicAuthScheme = new BasicAuthScheme();
        basicAuthScheme.setUserName("");
        basicAuthScheme.setPassword(Config.getProperty("superUserToken"));
        return reqBuilder()
                .setAuth(basicAuthScheme)
                .build();
    }

    public static RequestSpecification unauthSpec() {
        return reqBuilder().build();
    }

    public static RequestSpecification authSpec(User user) {
        BasicAuthScheme basicAuthScheme = new BasicAuthScheme();
        basicAuthScheme.setUserName(user.getUsername());
        basicAuthScheme.setPassword(user.getPassword());
        return reqBuilder()
                .setAuth(basicAuthScheme)
                .build();
    }

    public static RequestSpecification mockSpec() {
        return reqBuilder()
                .setBaseUri("http://%s".formatted(Config.getProperty("host").replaceAll(":\\d+", ":" + Config.getProperty("wireMockPort"))))
                .build();
    }
}
