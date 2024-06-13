package com.hanqf.controller;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.stabilityai.StabilityAiImageClient;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2024/5/23 18:06.
 */

@RestController
public class DemoController {

    @Autowired
    private StabilityAiImageClient imageClient;

    @GetMapping("/ai/image")
    public ImageResponse generateImage(@RequestParam(value = "message", defaultValue = "A light cream colored mini golden doodle") String message) {
        return imageClient.call(
                new ImagePrompt("A light cream colored mini golden doodle",
                        StabilityAiImageOptions.builder()
                                .withModel("stable-diffusion-v1-6")
                                .withStylePreset("cinematic")
                                .withN(1)
                                .withHeight(512)
                                .withWidth(512)
//                                .withResponseFormat("image/png")
                                .withResponseFormat("application/json")
                                .build())
        );
    }

}
