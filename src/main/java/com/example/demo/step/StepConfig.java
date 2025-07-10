package com.example.demo.step;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StepConfig {


    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Step sampleStep() {
        return stepBuilderFactory.get("sampleStep")
            .<String, String>chunk(10)
            .reader(() -> "Hello") // 簡易なReader
            .processor((ItemProcessor<String, String>) item -> item.toUpperCase()) // 簡易なProcessor
            .writer(items -> items.forEach(System.out::println)) // 簡易なWriter
            .build();
    }

}
