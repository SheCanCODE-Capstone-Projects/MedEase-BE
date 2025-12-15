package com.springboot.medease.Security;

import com.springboot.medease.Models.QueueStatus;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.PartialIndexFilter;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class MongoQueueIndexesConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void ensureQueueIndexes() {
        // Collection name must match @Document(collection = "queues")
        var indexOps = mongoTemplate.indexOps("queues");

        // Fast FIFO reads for a queue bucket
        indexOps.ensureIndex(
                new Index()
                        .on("clinicId", Sort.Direction.ASC)
                        .on("serviceId", Sort.Direction.ASC)
                        .on("status", Sort.Direction.ASC)
                        .on("joinTime", Sort.Direction.ASC)
                        .named("idx_queue_bucket_status_jointime")
        );

        // Enforce: one ACTIVE entry per patient globally (WAITING or IN_PROGRESS)
        // Partial unique index: applies only when status in ["WAITING","IN_PROGRESS"]
        indexOps.ensureIndex(
                new Index()
                        .on("patientId", Sort.Direction.ASC)
                        .unique()
                        .partial(PartialIndexFilter.of(new Document("status",
                                new Document("$in", List.of(
                                        QueueStatus.WAITING.name(),
                                        QueueStatus.IN_PROGRESS.name()
                                ))
                        )))
                        .named("ux_patient_one_active_queue")
        );

        // Enforce: one IN_PROGRESS per doctor globally
        // Partial unique index: applies only when status == "IN_PROGRESS" and assignedDoctorId exists
        indexOps.ensureIndex(
                new Index()
                        .on("assignedDoctorId", Sort.Direction.ASC)
                        .unique()
                        .partial(PartialIndexFilter.of(new Document("status", QueueStatus.IN_PROGRESS.name())
                                .append("assignedDoctorId", new Document("$exists", true))))
                        .named("ux_doctor_one_in_progress")
        );
    }
}