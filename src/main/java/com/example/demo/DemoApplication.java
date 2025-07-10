package com.example.demo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.demo.service.JobRunner;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class DemoApplication {
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
		JobRunner jobRunner = context.getBean(JobRunner.class);
		try {
			jobRunner.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
