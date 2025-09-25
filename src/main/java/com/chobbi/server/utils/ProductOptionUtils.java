package com.chobbi.server.utils;

import com.chobbi.server.dto.ProductOptionDto;
import com.chobbi.server.dto.ProductOptionValueDto;
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
    public static Map<Long, List<ProductOptionValueDto>> groupOptions(
            List<VariationOptionEntity> options,
            boolean keepOrder
    ) {
        return options.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getOptionsEntity().getTierEntity().getId(),
                        keepOrder ? LinkedHashMap::new : HashMap::new,
                        Collectors.mapping(
                                o -> new ProductOptionValueDto(
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
            Map<Long, List<ProductOptionValueDto>> optionValuesGrouped
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
    public static Map<Long, String> extractOptionNames(List<VariationOptionEntity> options) {
        return options.stream()
                .map(o -> o.getOptionsEntity().getTierEntity())
                .distinct()
                .collect(Collectors.toMap(TierEntity::getId, TierEntity::getName));
    }

    /**
     * Build danh sách ProductOptionDto
     */
    public static List<ProductOptionDto> buildOptionDtos(List<VariationOptionEntity> options) {
        Map<Long, List<ProductOptionValueDto>> optionValuesGrouped = groupOptions(options, false);
        Map<Long, String> optionNames = extractOptionNames(options);

        return optionValuesGrouped.entrySet().stream()
                .map(e -> {
                    ProductOptionDto dto = new ProductOptionDto();
                    dto.setId(e.getKey());
                    dto.setName(optionNames.get(e.getKey()));
                    dto.setValues(e.getValue());
                    return dto;
                }).toList();
    }
}

