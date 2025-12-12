package org.example.cosmocats.feature;

import org.example.cosmocats.config.FeatureToggleConfig;
import org.example.cosmocats.feature.exception.FeatureNotAvailableException;
import org.example.cosmocats.service.CosmoCatService;
import org.example.cosmocats.service.impl.CosmoCatServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.HashMap;
import java.util.List;

@SpringBootTest(classes = {
        CosmoCatServiceImpl.class,  // Сервіс, який тестимо
        FeatureToggleAspect.class,  // Аспект, який перехоплює виклик
        FeatureToggleService.class, // Сервіс, який тримає стан прапорців
        CosmoCatFeatureTest.TestConfig.class // Локальна заглушка конфіга
})
@EnableAspectJAutoProxy(proxyTargetClass = true)
class CosmoCatFeatureTest {

    @Autowired
    private CosmoCatService cosmoCatService;

    @Autowired
    private FeatureToggleService featureToggleService;

    @Test
    void getCosmoCats_whenFeatureEnabled_returnsList() {
        // Вмикаємо
        featureToggleService.setFeatureEnabled("cosmoCats", true);

        // Перевіряємо
        List<String> cats = cosmoCatService.getCosmoCats();
        Assertions.assertFalse(cats.isEmpty());
        Assertions.assertTrue(cats.contains("Nebula Kitty"));
    }

    @Test
    void getCosmoCats_whenFeatureDisabled_throwsException() {
        // Вимикаємо
        featureToggleService.setFeatureEnabled("cosmoCats", false);

        // Перевіряємо, що летить ексепшн
        Assertions.assertThrows(FeatureNotAvailableException.class, () -> {
            cosmoCatService.getCosmoCats();
        });
    }

    // Робимо підміну конфіга, щоб не тягнути application.yaml
    @TestConfiguration
    static class TestConfig {
        @Bean
        FeatureToggleConfig featureToggleConfig() {
            // Повертаємо пустий конфіг, все одно будемо сетати значення вручну в тестах
            return new FeatureToggleConfig(new HashMap<>());
        }
    }
}