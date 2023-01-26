package com.c3po;

import com.c3po.helper.environment.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Loader {
    @Bean
    public Configuration getConfiguration() {
        return Configuration.instance();
    }
}
