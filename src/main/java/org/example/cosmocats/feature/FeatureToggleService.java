package org.example.cosmocats.feature;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FeatureToggleService {

    // глобальний прапорець для нашої фічі "cosmo-cats"
    @Value("${feature.cosmo-cats.enabled:true}")
    private boolean cosmoCatsEnabled;

    /**
     * Перевіряємо, чи увімкнена фіча за ключем.
     * Зараз підтримуємо тільки "cosmo-cats".
     */
    public boolean isFeatureEnabled(String key) {
        if ("cosmo-cats".equals(key)) {
            return cosmoCatsEnabled;
        }
        // інші фічі вважаємо вимкненими (або тут можна повернути true — як захоче викладач)
        return false;
    }

    /**
     * Метод для тестів — дає можливість вимикати/вмикати фічу з юніт-тестів.
     */
    public void setFeatureEnabled(boolean enabled) {
        this.cosmoCatsEnabled = enabled;
    }
}
