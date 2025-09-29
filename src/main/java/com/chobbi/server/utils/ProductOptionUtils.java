package com.chobbi.server.utils;

import com.chobbi.server.dto.OptionDto;
import com.chobbi.server.dto.TierDto;
import com.chobbi.server.entity.TierEntity;
import com.chobbi.server.entity.VariationOptionEntity;

import java.util.*;
import java.util.stream.Collectors;

public class ProductOptionUtils {

    // Utility class
    private ProductOptionUtils() {}

    /**
     * Gom các option value theo optionId
     */
    public static Map<Long, List<OptionDto>> groupOptions(
            List<VariationOptionEntity> options,
            boolean keepOrder
    ) {
        return options.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOptionsEntity().getTierEntity().getId(),
                        keepOrder ? LinkedHashMap::new : HashMap::new,
                        Collectors.mapping(
                                o -> new OptionDto(
                                        o.getOptionsEntity().getId(),
                                        o.getOptionsEntity().getName()
                                ),
                                Collectors.collectingAndThen(Collectors.toList(),
                                        list -> list.stream().distinct().toList())
                        )
                ));
    }

    /**
     * Xây map: optionId -> (valueId -> index)
     */
    public static Map<Long, Map<Long, Integer>> buildOptionsIndexMap(
            Map<Long, List<OptionDto>> optionValuesGrouped
    ) {
        Map<Long, Map<Long, Integer>> optionsIndexMap = new HashMap<>();
        optionValuesGrouped.forEach((optionId, values) -> {
            Map<Long, Integer> valueIndexMap = new HashMap<>();
            for (int i = 0; i < values.size(); i++) {
                valueIndexMap.put(values.get(i).getId(), i);
            }
            optionsIndexMap.put(optionId, valueIndexMap);
        });
        return optionsIndexMap;
    }

    /**
     * Lấy optionId -> optionName
     */
    private static Map<Long, String> extractTierNames(List<VariationOptionEntity> options) {
        return options.stream()
                .map(o -> o.getOptionsEntity().getTierEntity())
                .distinct()
                .collect(Collectors.toMap(TierEntity::getId, TierEntity::getName));
    }

    /**
     * Build danh sách ProductOptionDto
     */
    public static List<TierDto> buildListTierDto(List<VariationOptionEntity> options) {
        Map<Long, List<OptionDto>> optionValuesGrouped = groupOptions(options, false);
        Map<Long, String> tierNames = extractTierNames(options);

        return optionValuesGrouped.entrySet().stream()
                .map(e -> {
                    TierDto dto = new TierDto();
                    dto.setId(e.getKey());
                    dto.setName(tierNames.get(e.getKey()));
                    dto.setOptions(e.getValue());
                    return dto;
                }).toList();
    }
}

