package com.example.demo.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class JobRunner implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job sampleJob;

    //@Scheduled(initialDelay = 10000, fixedDelay = 60000) // Runs every 60 seconds
    @Override
    public void run(String... args) throws Exception {
        
        JobParameters params = new JobParametersBuilder()
            .addString("create_time", String.valueOf(System.currentTimeMillis())) // 一意なキーと値
            .toJobParameters();

        jobLauncher.run(sampleJob, params);
    }

}
