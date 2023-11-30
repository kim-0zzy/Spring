package com.example.demo.config;

import com.example.demo.repository.ResourcesRepository;
import com.example.demo.security.service.SecurityResourceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AppConfig {
// 5-5
    // SecurityResourcesService 를 위한 config 클래스.
    @Bean
    public SecurityResourceService securityResourceService(ResourcesRepository resourcesRepository){
        SecurityResourceService securityResourceService = new SecurityResourceService(resourcesRepository);
        return securityResourceService;
    }
}
