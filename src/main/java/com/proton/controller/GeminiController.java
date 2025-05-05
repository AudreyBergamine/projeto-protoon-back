package com.proton.controller;

import org.springframework.web.bind.annotation.*;

import com.proton.services.GeminiService;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/generate")
    public String generateContent(@RequestBody String prompt) {
        return geminiService.generateContent(prompt);
    }
}
