package com.example.springbd3big.config.swagger;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.docs")
public class AppDocsProperties {

    private String title = "Online School Cloud API";
    private String version = "v1";
    private String description = "API documentation for Online School Cloud.";
    private String bearerSchemeName = "bearerAuth";
    private String defaultPassword = "password";
    private List<String> bootstrapUsers = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBearerSchemeName() {
        return bearerSchemeName;
    }

    public void setBearerSchemeName(String bearerSchemeName) {
        this.bearerSchemeName = bearerSchemeName;
    }

    public String getDefaultPassword() {
        return defaultPassword;
    }

    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    public List<String> getBootstrapUsers() {
        return bootstrapUsers;
    }

    public void setBootstrapUsers(List<String> bootstrapUsers) {
        this.bootstrapUsers = bootstrapUsers;
    }
}
