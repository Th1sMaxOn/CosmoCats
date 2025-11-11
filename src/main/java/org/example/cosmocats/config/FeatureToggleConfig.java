package org.example.cosmocats.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "feature")
public class FeatureToggleConfig {
    private Map<String, Map<String, Boolean>> features;

    public Map<String, Map<String, Boolean>> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Map<String, Boolean>> features) {
        this.features = features;
    }
}
