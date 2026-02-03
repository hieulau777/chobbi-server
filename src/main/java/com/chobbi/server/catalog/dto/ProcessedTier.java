package com.chobbi.server.catalog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Builder
public class ProcessedTier {
    private Long id; // Dùng cho Update
    private String name;
    private boolean hasImages;
    private Map<String, ProcessedOption> optionsMap; // Key: optionName
}