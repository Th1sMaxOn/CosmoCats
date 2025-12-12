package org.example.cosmocats.web;

import org.example.cosmocats.service.CosmoCatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cats")
public class CosmoCatController {

    private final CosmoCatService service;

    public CosmoCatController(CosmoCatService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<String>> getCats() {
        List<String> cats = service.getCosmoCats();
        return ResponseEntity.ok(cats);
    }
}
