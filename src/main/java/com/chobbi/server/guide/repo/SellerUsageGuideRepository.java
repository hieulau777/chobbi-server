package com.chobbi.server.guide.repo;

import com.chobbi.server.guide.entity.SellerUsageGuideDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SellerUsageGuideRepository extends MongoRepository<SellerUsageGuideDocument, String> {

    Optional<SellerUsageGuideDocument> findByTarget(String target);
}

