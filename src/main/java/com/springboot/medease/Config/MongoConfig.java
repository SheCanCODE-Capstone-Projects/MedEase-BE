package com.springboot.medease.Config;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoConfig implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;

    public MongoConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String @NonNull ... args) {
        if (!mongoTemplate.collectionExists("patients")) {
            mongoTemplate.createCollection("patients");
            System.out.println("Patients collection created successfully!");
        }
    }
}