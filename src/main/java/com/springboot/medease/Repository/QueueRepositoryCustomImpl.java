package com.springboot.medease.Repository;

import com.springboot.medease.Models.Queue;
import com.springboot.medease.Models.QueueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryCustomImpl implements QueueRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Queue> findAndAssignNextPatient(String clinicId, String serviceId, String doctorId) {
        // Build query to find the first waiting patient
        Query query = new Query();
        query.addCriteria(Criteria.where("clinicId").is(clinicId)
                .and("serviceId").is(serviceId)
                .and("status").is(QueueStatus.WAITING));
        query.with(Sort.by(Sort.Direction.ASC, "joinTime"));
        query.limit(1);

        // Build update to set status and assign doctor
        Update update = new Update();
        update.set("status", QueueStatus.IN_PROGRESS);
        update.set("assignedDoctorId", doctorId);

        // Atomically find and modify - this is thread-safe
        Queue result = mongoTemplate.findAndModify(query, update, Queue.class);

        return Optional.ofNullable(result);
    }
}
