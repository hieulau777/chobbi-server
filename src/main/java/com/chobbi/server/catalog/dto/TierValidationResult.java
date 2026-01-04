package com.chobbi.server.catalog.dto;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
public class TierValidationResult {
    private final Map<String, Set<String>> normalizedTierOptions;
    private final int countCartesian;

    public TierValidationResult(Map<String, Set<String>> normalizedTierOptions, int countCartesian) {
        this.normalizedTierOptions = normalizedTierOptions;
        this.countCartesian = countCartesian;
    }
}
