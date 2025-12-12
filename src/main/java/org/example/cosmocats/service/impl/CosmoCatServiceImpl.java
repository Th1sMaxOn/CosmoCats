package org.example.cosmocats.service.impl;

import org.example.cosmocats.feature.ToggleFeature;
import org.example.cosmocats.service.CosmoCatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CosmoCatServiceImpl implements CosmoCatService {

    @Override
    @ToggleFeature("cosmoCats")
    public List<String> getCosmoCats() {
        return List.of("Nebula Kitty", "Stellar Paws", "Luna Meow");
    }
}