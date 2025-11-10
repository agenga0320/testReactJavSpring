package com.example.demo.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job uploadCsvJob;

    public void runUploadCsvJob(String filename) throws Exception {
        
        JobParameters params = new JobParametersBuilder()
            .addString("file_name", filename) // 一意なキーと値
            .toJobParameters();

        jobLauncher.run(uploadCsvJob, params);
    }

}
