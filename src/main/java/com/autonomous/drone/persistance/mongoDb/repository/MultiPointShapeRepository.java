package com.autonomous.drone.persistance.mongoDb.repository;

import com.autonomous.drone.persistance.mongoDb.domain.MultiPointShape;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RestResource(exported = false)
public interface MultiPointShapeRepository extends MongoRepository<MultiPointShape, String> {
}

