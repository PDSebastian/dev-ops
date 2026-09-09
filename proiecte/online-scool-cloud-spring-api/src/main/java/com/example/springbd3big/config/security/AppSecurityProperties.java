package com.example.springbd3big.config.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private List<String> publicPaths = new ArrayList<>();
    private List<ResourceRule> resourceRules = new ArrayList<>();
    private int passwordEncoderStrength = 10;

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public List<ResourceRule> getResourceRules() {
        return resourceRules;
    }

    public void setResourceRules(List<ResourceRule> resourceRules) {
        this.resourceRules = resourceRules;
    }

    public int getPasswordEncoderStrength() {
        return passwordEncoderStrength;
    }

    public void setPasswordEncoderStrength(int passwordEncoderStrength) {
        this.passwordEncoderStrength = passwordEncoderStrength;
    }

    public static class ResourceRule {
        private String pathPattern;
        private String readAuthority;
        private String writeAuthority;

        public String getPathPattern() {
            return pathPattern;
        }

        public void setPathPattern(String pathPattern) {
            this.pathPattern = pathPattern;
        }

        public String getReadAuthority() {
            return readAuthority;
        }

        public void setReadAuthority(String readAuthority) {
            this.readAuthority = readAuthority;
        }

        public String getWriteAuthority() {
            return writeAuthority;
        }

        public void setWriteAuthority(String writeAuthority) {
            this.writeAuthority = writeAuthority;
        }
    }
}
