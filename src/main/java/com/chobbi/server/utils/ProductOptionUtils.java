package com.chobbi.server.utils;

import com.chobbi.server.dto.ProductOptionDto;
import com.chobbi.server.dto.ProductOptionValueDto;
import com.chobbi.server.entity.ProductOptionEntity;
import com.chobbi.server.entity.ProductVariantOptionEntity;

import java.util.*;
import java.util.stream.Collectors;

public class ProductOptionUtils {

    // Utility class
    private ProductOptionUtils() {}

    /**
     * Gom các option value theo optionId
     */
    public static Map<Long, List<ProductOptionValueDto>> groupOptionValues(
            List<ProductVariantOptionEntity> options,
            boolean keepOrder
    ) {
        return options.stream()
                .collect(Collectors.groupingBy(
                        o -> o.getProductOptionValueEntity().getProductOptionEntity().getId(),
                        keepOrder ? LinkedHashMap::new : HashMap::new,
                        Collectors.mapping(
                                o -> new ProductOptionValueDto(
                                        o.getProductOptionValueEntity().getId(),
                                        o.getProductOptionValueEntity().getValue()
                                ),
                                Collectors.collectingAndThen(Collectors.toList(),
                                        list -> list.stream().distinct().toList())
                        )
                ));
    }

    /**
     * Xây map: optionId -> (valueId -> index)
     */
    public static Map<Long, Map<Long, Integer>> buildOptionValueIndexMap(
            Map<Long, List<ProductOptionValueDto>> optionValuesGrouped
    ) {
        Map<Long, Map<Long, Integer>> optionValueIndexMap = new HashMap<>();
        optionValuesGrouped.forEach((optionId, values) -> {
            Map<Long, Integer> valueIndexMap = new HashMap<>();
            for (int i = 0; i < values.size(); i++) {
                valueIndexMap.put(values.get(i).getId(), i);
            }
            optionValueIndexMap.put(optionId, valueIndexMap);
        });
        return optionValueIndexMap;
    }

    /**
     * Lấy optionId -> optionName
     */
    public static Map<Long, String> extractOptionNames(List<ProductVariantOptionEntity> options) {
        return options.stream()
                .map(o -> o.getProductOptionValueEntity().getProductOptionEntity())
                .distinct()
                .collect(Collectors.toMap(ProductOptionEntity::getId, ProductOptionEntity::getName));
    }

    /**
     * Build danh sách ProductOptionDto
     */
    public static List<ProductOptionDto> buildOptionDtos(List<ProductVariantOptionEntity> options) {
        Map<Long, List<ProductOptionValueDto>> optionValuesGrouped = groupOptionValues(options, false);
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

