package com.yuri.rover.controller;

import com.yuri.rover.dtos.QueryDTO;
import com.yuri.rover.dtos.UrlDTO;
import com.yuri.rover.service.TokenProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/")
public class TokenizerController {
    private final TokenProcessingService tokenProcessingService;

    public TokenizerController(TokenProcessingService tokenProcessingService) {
        this.tokenProcessingService = tokenProcessingService;
    }

    @PostMapping("/index")
    public ResponseEntity<String> indexWords(@RequestBody UrlDTO urlDTO) throws IOException, FileNotFoundException {
        tokenProcessingService.processTokens(urlDTO.getUrl());
        return ResponseEntity.ok("Okay");
    }

    @GetMapping("/query")
    public ResponseEntity<Set<String>> queryWebsites(@RequestParam String query) {
        Set<String> results = tokenProcessingService.queryTokens(query);
        return ResponseEntity.ok(results);
    }
}
