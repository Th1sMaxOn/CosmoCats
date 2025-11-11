package org.example.cosmocats.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = "feature.cosmoCats.enabled=true")
class CosmoCatFeatureEnabledTest {

    @Autowired
    CosmoCatService service;

    @Test
    void getCosmoCats_whenFeatureEnabled_returnsList() {
        List<String> cats = service.getCosmoCats();
        assertNotNull(cats);
        assertFalse(cats.isEmpty());
    }
}
