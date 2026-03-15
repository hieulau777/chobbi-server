package com.chobbi.server.guide.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "seller_seed_history")
public class SellerSeedHistoryDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long accountId;

    private Instant seededAt;
}

