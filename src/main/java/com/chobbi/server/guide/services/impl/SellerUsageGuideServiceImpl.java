package com.chobbi.server.guide.services.impl;

import com.chobbi.server.guide.dto.SellerUsageGuideDto;
import com.chobbi.server.guide.entity.SellerUsageGuideDocument;
import com.chobbi.server.guide.repo.SellerUsageGuideRepository;
import com.chobbi.server.guide.services.SellerUsageGuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SellerUsageGuideServiceImpl implements SellerUsageGuideService {

    private static final String TARGET_SELLER = "SELLER";

    private final SellerUsageGuideRepository repository;

    @Override
    public SellerUsageGuideDto getSellerGuide() {
        return repository.findByTarget(TARGET_SELLER)
                .map(this::toDto)
                .orElseGet(() -> SellerUsageGuideDto.builder()
                        .id(null)
                        .title(null)
                        .content(null)
                        .youtubeUrl(null)
                        .seedButtonEnabled(false)
                        .seedConfigJson(null)
                        .build());
    }

    @Override
    public SellerUsageGuideDto upsertSellerGuide(SellerUsageGuideDto dto) {
        SellerUsageGuideDocument document = repository.findByTarget(TARGET_SELLER)
                .orElseGet(() -> SellerUsageGuideDocument.builder()
                        .target(TARGET_SELLER)
                        .build());

        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setYoutubeUrl(dto.getYoutubeUrl());
        document.setSeedButtonEnabled(dto.isSeedButtonEnabled());
        document.setSeedConfigJson(dto.getSeedConfigJson());
        document.setUpdatedAt(Instant.now());

        SellerUsageGuideDocument saved = repository.save(document);
        return toDto(saved);
    }

    private SellerUsageGuideDto toDto(SellerUsageGuideDocument document) {
        return SellerUsageGuideDto.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .youtubeUrl(document.getYoutubeUrl())
                .seedButtonEnabled(document.isSeedButtonEnabled())
                .seedConfigJson(document.getSeedConfigJson())
                .build();
    }
}

