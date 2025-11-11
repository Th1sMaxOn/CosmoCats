package org.example.cosmocats.feature;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    private final Environment env;

    public FeatureToggleService(Environment env) {
        this.env = env;
    }

    public boolean isFeatureEnabled(String featureKey) {
        String prop = "feature." + featureKey + ".enabled";
        return env.getProperty(prop, Boolean.class, Boolean.FALSE);
    }
}
