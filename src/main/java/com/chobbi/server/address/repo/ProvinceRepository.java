package com.chobbi.server.address.repo;

import com.chobbi.server.address.document.ProvinceDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProvinceRepository extends MongoRepository<ProvinceDocument, Integer> {

    Optional<ProvinceDocument> findByCode(Integer code);
}

