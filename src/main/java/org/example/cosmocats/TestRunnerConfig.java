package org.example.cosmocats;

import org.example.cosmocats.service.CosmoCatService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRunnerConfig {
    private final CosmoCatService cosmoCatService;

    public TestRunnerConfig(CosmoCatService cosmoCatService) {
        this.cosmoCatService = cosmoCatService;
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("[Demo] Calling CosmoCatService.getCosmoCats() ...");
            try {
                var cats = cosmoCatService.getCosmoCats();
                System.out.println("[Demo] CosmoCats: " + cats);
            } catch (Exception ex) {
                System.err.println("[Demo] Exception calling getCosmoCats(): " + ex.getClass().getName() + " - " + ex.getMessage());
            }
        };
    }
}
