package org.example.cosmocats.service;

import org.example.cosmocats.feature.ToggleFeature;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosmoCatService {

    @ToggleFeature("cosmoCats")
    public List<String> getCosmoCats() {
        return List.of("Nebula Kitty", "Stellar Paws", "Luna Meow");
    }
}
