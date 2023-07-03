package org.example.config;


import lombok.extern.slf4j.Slf4j;
import org.example.common.azure.AzureOpenAiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/24 15:00.
 */

@Slf4j
@Configuration
public class AzureOpenaiApiConfig {

    @Bean
    public AzureOpenAiService azureOpenAiService(){
        return new AzureOpenAiService();
    }
}
