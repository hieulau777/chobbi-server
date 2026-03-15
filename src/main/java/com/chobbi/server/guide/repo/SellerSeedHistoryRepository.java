package com.chobbi.server.guide.repo;

import com.chobbi.server.guide.entity.SellerSeedHistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SellerSeedHistoryRepository extends MongoRepository<SellerSeedHistoryDocument, String> {

    boolean existsByAccountId(Long accountId);
}

