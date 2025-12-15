
// ============================================
// FILE 5: src/main/java/com/fintech/platform/config/ModelMapperConfig.java
// ============================================
package com.fintech.platform.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}