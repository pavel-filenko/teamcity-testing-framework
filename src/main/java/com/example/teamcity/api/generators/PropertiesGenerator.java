package com.example.teamcity.api.generators;

import com.example.teamcity.api.models.Properties;
import com.example.teamcity.api.models.Property;

import java.util.Arrays;

public class PropertiesGenerator {
    public static Properties generatePropertiesWithEcho() {
        return Properties.builder().property(Arrays.asList(Property.builder()
                .name("script.content")
                .value("echo 'Hello World!'")
                .build())).build();
    }
}
