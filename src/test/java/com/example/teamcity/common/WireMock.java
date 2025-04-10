package com.example.teamcity.common;

import com.example.teamcity.api.config.Config;
import com.example.teamcity.api.models.BaseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import lombok.SneakyThrows;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.options;
import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static com.github.tomakehurst.wiremock.common.ContentTypes.CONTENT_TYPE;


public final class WireMock {
    public static WireMockServer wireMockServer;

    private WireMock() {}

    @SneakyThrows
    public static void setupServer(MappingBuilder mappingBuilder, int status, BaseModel model) {
        if (wireMockServer == null) {
            wireMockServer = new WireMockServer(8081);
            wireMockServer.url(Config.getProperty("host"));
            wireMockServer.start();
        }

        var jsonModel = new ObjectMapper().writeValueAsString(model);

        wireMockServer.stubFor(mappingBuilder
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withBody(jsonModel)
                )
        );
    }

    public static void stopServer() {
        if (wireMockServer != null) {
            wireMockServer.stop();
            wireMockServer = null;
        }
    }

}
