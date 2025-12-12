package org.example.cosmocats.feature;

import org.example.cosmocats.config.FeatureToggleConfig;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FeatureToggleService {

    private final Map<String, Boolean> featureFlags;

    // Інжектимо конфіг через конструктор
    public FeatureToggleService(FeatureToggleConfig config) {
        // Копіюємо з конфіга в ConcurrentHashMap для потокобезпеки і мутабельності в рантаймі (тести)
        this.featureFlags = new ConcurrentHashMap<>(config.getFeatures());
    }

    public boolean isFeatureEnabled(String key) {
        return featureFlags.getOrDefault(key, false);
    }

    public void setFeatureEnabled(String key, boolean enabled) {
        featureFlags.put(key, enabled);
    }
}